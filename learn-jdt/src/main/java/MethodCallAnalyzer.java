import org.eclipse.jdt.core.dom.*;
import java.util.*;
import java.util.stream.Collectors;

public class MethodCallAnalyzer {

    private final Map<String, List<String>> callGraph = new HashMap<>();
    private String currentClassName;

    public static void main(String[] args) {
        ASTParser parser = createASTParser(
                "D:\\workspaces\\learn-demo\\learn-jdt\\src\\main\\java\\com\\yucl\\learn\\demo\\jdt\\MethodCallAnalyzer2.java"); // 替换为实际的Java文件路径
        CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        new MethodCallAnalyzer().analyze(cu);
    }

    private static ASTParser createASTParser(String fileName) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(fileName.toCharArray());
        parser.setResolveBindings(true);
        return parser;
    }

    public void analyze(CompilationUnit cu) {
        cu.accept(new MethodCallVisitor());
        printCallGraph();
    }

    class MethodCallVisitor extends ASTVisitor {

        @Override
        public boolean visit(TypeDeclaration typeDeclaration) {
            currentClassName = typeDeclaration.getName().getIdentifier();
            return true;
        }

        @Override
        public void endVisit(TypeDeclaration typeDeclaration) {
            // 当类型声明结束时，重置当前类名
            currentClassName = null;
        }

        @Override
        public boolean visit(MethodDeclaration methodDeclaration) {
            // 记录方法开始和结束，用于生成方法调用签名
            String methodName = methodDeclaration.getName().getIdentifier();
            String methodSignature = currentClassName + "." + methodName + generateSignature(methodDeclaration);
            callGraph.computeIfAbsent(methodSignature, k -> new ArrayList<>());
            return true;
        }

        @Override
        public boolean visit(MethodInvocation node) {
            IMethodBinding methodBinding = node.resolveMethodBinding();
            if (methodBinding != null) {
                String calledMethodSignature = generateMethodSignature(methodBinding);
                String callerMethodSignature = getCurrentMethodSignature();
                if (calledMethodSignature != null && !calledMethodSignature.equals(callerMethodSignature)) {
                    callGraph.get(callerMethodSignature).add(calledMethodSignature);
                }
            }
            return true;
        }

        @Override
        public boolean visit(LambdaExpression node) {
            // Lambda表达式中的方法调用需要特别处理
            // 这里我们简单地访问Lambda体，但不会记录Lambda表达式自身的调用
            super.visit(node);
            return false;
        }
    }

    private String generateSignature(MethodDeclaration methodDeclaration) {
        IMethodBinding methodBinding = methodDeclaration.resolveBinding();
        return methodBinding != null ? getParameterString(methodBinding.getParameterTypes()) : "";
    }

    private String generateMethodSignature(IMethodBinding methodBinding) {
        return methodBinding.getReturnType().getQualifiedName() + " " + methodBinding.getName()
                + getParameterString(methodBinding.getParameterTypes());
    }

    private String getParameterString(ITypeBinding[] parameters) {
        return Arrays.stream(parameters)
                .map(this::getTypeName)
                .collect(Collectors.joining(", ", "(", ")"));
    }

    private String getTypeName(ITypeBinding typeBinding) {
        // 处理泛型、数组和复杂类型
        if (typeBinding.isParameterizedType()) {
            return typeBinding.getQualifiedName() +
                    "<" + Arrays.stream(typeBinding.getTypeArguments())
                            .map(this::getTypeName)
                            .collect(Collectors.joining(", "))
                    + ">";
        } else if (typeBinding.isArray()) {
            return getTypeName(typeBinding.getComponentType()) + "[]";
        } else {
            return typeBinding.getQualifiedName();
        }
    }

    private String getCurrentMethodSignature() {
        // 获取当前方法调用签名，如果当前不在方法内，则返回null
        return callGraph.containsKey(currentClassName) ? currentClassName : null;
    }

    private void printCallGraph() {
        callGraph.forEach((caller, callees) -> {
            System.out.println("Caller: " + caller);
            callees.forEach(System.out::println);
        });
    }
}