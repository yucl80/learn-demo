package com.example.agent.context;

import java.util.*;
import java.util.concurrent.*;

public class TraceContext {
    private static final ThreadLocal<Stack<MethodTraceInfo>> TRACE_STACK = ThreadLocal.withInitial(Stack::new);
    private static final ThreadLocal<String> THREAD_ID = ThreadLocal.withInitial(() -> 
        Thread.currentThread().getName() + "-" + Thread.currentThread().getId());
    private static final ThreadLocal<TaskContext> TASK_CONTEXT = new ThreadLocal<>();
    
    private static final ConcurrentHashMap<String, List<MethodTraceInfo>> THREAD_TRACE_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, List<MethodTraceInfo>> TASK_TRACE_MAP = new ConcurrentHashMap<>();
    
    public static void setTaskContext(TaskContext taskContext) {
        TASK_CONTEXT.set(taskContext);
    }
    
    public static void enterMethod(String className, String methodName, String methodDescriptor) {
        Stack<MethodTraceInfo> stack = TRACE_STACK.get();
        long startTime = System.currentTimeMillis();
        
        // 构建缩进
        String indent = "  ".repeat(stack.size());
        
        // 获取任务上下文
        TaskContext taskContext = TASK_CONTEXT.get();
        String taskInfo = "";
        if (taskContext != null) {
            taskInfo = String.format(" [Task: %s, Parent: %s, From: %s]",
                taskContext.getTaskId(),
                taskContext.getParentThreadId(),
                taskContext.getSubmissionPoint());
        }
        
        System.out.printf("%s[%s%s] Enter: %s.%s%s%n", 
            indent, Thread.currentThread().getName(), taskInfo,
            className, methodName, methodDescriptor);
        
        MethodTraceInfo traceInfo = new MethodTraceInfo(className, methodName, methodDescriptor, startTime);
        
        if (taskContext != null) {
            traceInfo.setTaskContext(taskContext);
        }
        
        if (!stack.isEmpty()) {
            traceInfo.setParent(stack.peek());
        }
        
        stack.push(traceInfo);
    }
    
    public static void exitMethod(Throwable throwable) {
        Stack<MethodTraceInfo> stack = TRACE_STACK.get();
        if (stack.isEmpty()) {
            return;
        }
        
        MethodTraceInfo traceInfo = stack.pop();
        traceInfo.setEndTime(System.currentTimeMillis());
        
        // 构建缩进
        String indent = "  ".repeat(stack.size());
        
        // 获取任务上下文
        TaskContext taskContext = TASK_CONTEXT.get();
        String taskInfo = "";
        if (taskContext != null) {
            taskInfo = String.format(" [Task: %s, Parent: %s, From: %s]",
                taskContext.getTaskId(),
                taskContext.getParentThreadId(),
                taskContext.getSubmissionPoint());
        }
        
        if (throwable != null) {
            traceInfo.setException(throwable);
            System.out.printf("%s[%s%s] Exit with exception: %s.%s%s [duration=%dms] Exception: %s%n",
                indent, Thread.currentThread().getName(), taskInfo,
                traceInfo.getClassName(), traceInfo.getMethodName(), traceInfo.getMethodDescriptor(),
                traceInfo.getDuration(), throwable.getClass().getName());
        } else {
            System.out.printf("%s[%s%s] Exit: %s.%s%s [duration=%dms]%n",
                indent, Thread.currentThread().getName(), taskInfo,
                traceInfo.getClassName(), traceInfo.getMethodName(), traceInfo.getMethodDescriptor(),
                traceInfo.getDuration());
        }
        
        if (stack.isEmpty()) {
            recordTrace(traceInfo);
        }
    }
    
    private static void recordTrace(MethodTraceInfo traceInfo) {
        String threadId = THREAD_ID.get();
        THREAD_TRACE_MAP.computeIfAbsent(threadId, k -> new CopyOnWriteArrayList<>()).add(traceInfo);
        
        TaskContext taskContext = traceInfo.getTaskContext();
        if (taskContext != null) {
            TASK_TRACE_MAP.computeIfAbsent(taskContext.getTaskId(), k -> new CopyOnWriteArrayList<>()).add(traceInfo);
        }
    }
    
    public static void printTaskTrace(String taskId) {
        List<MethodTraceInfo> traces = TASK_TRACE_MAP.get(taskId);
        if (traces != null) {
            System.out.println("\n=== Task Trace for " + taskId + " ===");
            for (MethodTraceInfo trace : traces) {
                printTraceInfo(trace, "");
            }
        }
    }
    
    private static void printTraceInfo(MethodTraceInfo traceInfo, String indent) {
        System.out.printf("%s%s.%s%s [duration=%dms]%n",
            indent,
            traceInfo.getClassName(),
            traceInfo.getMethodName(),
            traceInfo.getMethodDescriptor(),
            traceInfo.getDuration());
        
        if (traceInfo.getException() != null) {
            System.out.printf("%s  Exception: %s%n",
                indent,
                traceInfo.getException().getClass().getName());
        }
        
        for (MethodTraceInfo child : traceInfo.getChildren()) {
            printTraceInfo(child, indent + "  ");
        }
    }
    
    public static class MethodTraceInfo {
        private final String className;
        private final String methodName;
        private final String methodDescriptor;
        private final long startTime;
        private long endTime;
        private Throwable exception;
        private MethodTraceInfo parent;
        private TaskContext taskContext;
        private final List<MethodTraceInfo> children = new ArrayList<>();
        
        public MethodTraceInfo(String className, String methodName, String methodDescriptor, long startTime) {
            this.className = className;
            this.methodName = methodName;
            this.methodDescriptor = methodDescriptor;
            this.startTime = startTime;
        }
        
        public void setParent(MethodTraceInfo parent) {
            this.parent = parent;
            parent.children.add(this);
        }
        
        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }
        
        public void setException(Throwable exception) {
            this.exception = exception;
        }
        
        public void setTaskContext(TaskContext taskContext) {
            this.taskContext = taskContext;
        }
        
        public String getClassName() { return className; }
        public String getMethodName() { return methodName; }
        public String getMethodDescriptor() { return methodDescriptor; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public Throwable getException() { return exception; }
        public List<MethodTraceInfo> getChildren() { return children; }
        public MethodTraceInfo getParent() { return parent; }
        public TaskContext getTaskContext() { return taskContext; }
        public String getFullMethodSignature() {
            return className + "." + methodName + methodDescriptor;
        }
        public long getDuration() {
            return endTime - startTime;
        }
    }
    
    public static class ExceptionInfo {
        private final MethodTraceInfo traceInfo;
        private final Throwable throwable;
        
        public ExceptionInfo(MethodTraceInfo traceInfo, Throwable throwable) {
            this.traceInfo = traceInfo;
            this.throwable = throwable;
        }
        
        public MethodTraceInfo getTraceInfo() {
            return traceInfo;
        }
        
        public Throwable getThrowable() {
            return throwable;
        }
    }
}
