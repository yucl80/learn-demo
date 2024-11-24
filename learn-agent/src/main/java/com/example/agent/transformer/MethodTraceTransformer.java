package com.example.agent.transformer;

import com.example.agent.context.TaskContext;
import com.example.agent.context.TraceContext;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MethodTraceTransformer implements ClassFileTransformer {
    private static final Set<String> EXCLUDE_PACKAGES = new HashSet<>(Arrays.asList(
        "java/", "javax/", "sun/", "com/sun/",
        "jdk/", "org/objectweb/asm/",
        "com/example/agent/context/"  // 只排除context包，允许跟踪test包
    ));

    private static final Set<String> ASYNC_CLASSES = new HashSet<>(Arrays.asList(
        "java/util/concurrent/ThreadPoolExecutor",
        "java/util/concurrent/ScheduledThreadPoolExecutor",
        "java/util/concurrent/ForkJoinPool",
        "java/util/concurrent/CompletableFuture"
    ));

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        
        if (className == null || shouldExclude(className)) {
            return classfileBuffer;
        }

        try {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            boolean isAsyncClass = ASYNC_CLASSES.contains(className);
            ClassVisitor cv = new MethodTraceClassVisitor(cw, className, isAsyncClass);
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            return cw.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return classfileBuffer;
        }
    }

    private boolean shouldExclude(String className) {
        return EXCLUDE_PACKAGES.stream().anyMatch(className::startsWith);
    }

    private static class MethodTraceClassVisitor extends ClassVisitor {
        private final String className;
        private final boolean isAsyncClass;

        public MethodTraceClassVisitor(ClassVisitor cv, String className, boolean isAsyncClass) {
            super(Opcodes.ASM9, cv);
            this.className = className;
            this.isAsyncClass = isAsyncClass;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor,
                                       String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            
            if (mv != null && !name.equals("<clinit>")) {
                if (isAsyncClass && isAsyncMethod(name, descriptor)) {
                    return new AsyncMethodAdapter(mv, access, name, descriptor, className);
                } else if (!name.equals("<init>")) {
                    return new MethodTraceAdviceAdapter(mv, access, name, descriptor, className);
                }
            }
            return mv;
        }

        private boolean isAsyncMethod(String name, String descriptor) {
            return (name.equals("execute") && descriptor.startsWith("(Ljava/lang/Runnable;")) ||
                   (name.equals("submit") && (
                       descriptor.startsWith("(Ljava/lang/Runnable;") ||
                       descriptor.startsWith("(Ljava/util/concurrent/Callable;")
                   )) ||
                   (name.equals("supplyAsync") && descriptor.startsWith("(Ljava/util/function/Supplier;")) ||
                   (name.equals("runAsync") && descriptor.startsWith("(Ljava/lang/Runnable;"));
        }
    }

    private static class AsyncMethodAdapter extends AdviceAdapter {
        private final String className;
        private final String methodName;
        private final String methodDescriptor;

        protected AsyncMethodAdapter(MethodVisitor mv, int access, String name,
                                   String descriptor, String className) {
            super(Opcodes.ASM9, mv, access, name, descriptor);
            this.className = className;
            this.methodName = name;
            this.methodDescriptor = descriptor;
        }

        @Override
        protected void onMethodEnter() {
            // 获取当前线程信息
            invokeStatic(Type.getType("Ljava/lang/Thread;"),
                new Method("currentThread", "()Ljava/lang/Thread;"));
            invokeVirtual(Type.getType("Ljava/lang/Thread;"),
                new Method("getName", "()Ljava/lang/String;"));

            // 创建提交点信息
            push(className + "." + methodName);

            // 创建TaskContext
            newInstance(Type.getType("Lcom/example/agent/context/TaskContext;"));
            dup();
            swap();
            invokeConstructor(Type.getType("Lcom/example/agent/context/TaskContext;"),
                new Method("<init>", "(Ljava/lang/String;Ljava/lang/String;)V"));

            // 保存TaskContext到本地变量
            int contextVar = newLocal(Type.getType("Lcom/example/agent/context/TaskContext;"));
            storeLocal(contextVar);

            // 包装任务
            if (methodDescriptor.startsWith("(Ljava/lang/Runnable;")) {
                wrapRunnable(contextVar);
            } else if (methodDescriptor.startsWith("(Ljava/util/concurrent/Callable;")) {
                wrapCallable(contextVar);
            } else if (methodDescriptor.startsWith("(Ljava/util/function/Supplier;")) {
                wrapSupplier(contextVar);
            }
        }

        private void wrapRunnable(int contextVar) {
            // 创建包装的Runnable
            newInstance(Type.getType("Lcom/example/agent/transformer/MethodTraceTransformer$ContextAwareRunnable;"));
            dup();
            loadArg(0);  // 原始Runnable
            loadLocal(contextVar);  // TaskContext
            invokeConstructor(Type.getType("Lcom/example/agent/transformer/MethodTraceTransformer$ContextAwareRunnable;"),
                new Method("<init>", "(Ljava/lang/Runnable;Lcom/example/agent/context/TaskContext;)V"));
            storeArg(0);
        }

        private void wrapCallable(int contextVar) {
            // 创建包装的Callable
            newInstance(Type.getType("Lcom/example/agent/transformer/MethodTraceTransformer$ContextAwareCallable;"));
            dup();
            loadArg(0);  // 原始Callable
            loadLocal(contextVar);  // TaskContext
            invokeConstructor(Type.getType("Lcom/example/agent/transformer/MethodTraceTransformer$ContextAwareCallable;"),
                new Method("<init>", "(Ljava/util/concurrent/Callable;Lcom/example/agent/context/TaskContext;)V"));
            storeArg(0);
        }

        private void wrapSupplier(int contextVar) {
            // 创建包装的Supplier
            newInstance(Type.getType("Lcom/example/agent/transformer/MethodTraceTransformer$ContextAwareSupplier;"));
            dup();
            loadArg(0);  // 原始Supplier
            loadLocal(contextVar);  // TaskContext
            invokeConstructor(Type.getType("Lcom/example/agent/transformer/MethodTraceTransformer$ContextAwareSupplier;"),
                new Method("<init>", "(Ljava/util/function/Supplier;Lcom/example/agent/context/TaskContext;)V"));
            storeArg(0);
        }
    }

    private static class MethodTraceAdviceAdapter extends AdviceAdapter {
        private final String className;
        private final String methodName;
        private final String methodDescriptor;

        protected MethodTraceAdviceAdapter(MethodVisitor mv, int access, String name,
                                         String descriptor, String className) {
            super(Opcodes.ASM9, mv, access, name, descriptor);
            this.className = className;
            this.methodName = name;
            this.methodDescriptor = descriptor;
        }

        @Override
        protected void onMethodEnter() {
            push(className.replace('/', '.'));
            push(methodName);
            push(methodDescriptor);
            
            invokeStatic(Type.getType(TraceContext.class),
                new Method("enterMethod", 
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"));
        }

        @Override
        protected void onMethodExit(int opcode) {
            if (opcode == ATHROW) {
                dup();
            } else {
                visitInsn(ACONST_NULL);
            }
            
            invokeStatic(Type.getType(TraceContext.class),
                new Method("exitMethod", "(Ljava/lang/Throwable;)V"));
        }
    }

    // 包装类定义
    public static class ContextAwareRunnable implements Runnable {
        private final Runnable delegate;
        private final TaskContext taskContext;

        public ContextAwareRunnable(Runnable delegate, TaskContext taskContext) {
            this.delegate = delegate;
            this.taskContext = taskContext;
        }

        @Override
        public void run() {
            TraceContext.setTaskContext(taskContext);
            try {
                delegate.run();
            } finally {
                TraceContext.setTaskContext(null);
            }
        }
    }

    public static class ContextAwareCallable<V> implements Callable<V> {
        private final Callable<V> delegate;
        private final TaskContext taskContext;

        public ContextAwareCallable(Callable<V> delegate, TaskContext taskContext) {
            this.delegate = delegate;
            this.taskContext = taskContext;
        }

        @Override
        public V call() throws Exception {
            TraceContext.setTaskContext(taskContext);
            try {
                return delegate.call();
            } finally {
                TraceContext.setTaskContext(null);
            }
        }
    }

    public static class ContextAwareSupplier<T> implements java.util.function.Supplier<T> {
        private final java.util.function.Supplier<T> delegate;
        private final TaskContext taskContext;

        public ContextAwareSupplier(java.util.function.Supplier<T> delegate, TaskContext taskContext) {
            this.delegate = delegate;
            this.taskContext = taskContext;
        }

        @Override
        public T get() {
            TraceContext.setTaskContext(taskContext);
            try {
                return delegate.get();
            } finally {
                TraceContext.setTaskContext(null);
            }
        }
    }
}
