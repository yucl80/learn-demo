import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;

public class CommentRemover extends ASTVisitor {

    private ASTRewrite rewriter;

    public CommentRemover(AST ast) {
        this.rewriter = ASTRewrite.create(ast);
    }

    @Override
    public boolean visit(LineComment node) {
        rewriter.remove(node, null);
        return super.visit(node);
    }

    @Override
    public boolean visit(BlockComment node) {
        rewriter.remove(node, null);
        return super.visit(node);
    }

    @Override
    public boolean visit(Javadoc node) {
        rewriter.remove(node, null);
        return super.visit(node);
    }

    public String removeComments(String sourceCode) {
        // 省略解析代码、生成 AST 的步骤

        // 创建 CommentRemover 实例并访问 AST
        CommentRemover remover = new CommentRemover(ast);
        compilationUnit.accept(remover);

        // 生成修改后的代码
        TextEdit edits = rewriter.rewriteAST(new Document(sourceCode), null);
        try {
            edits.apply(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return document.get();
    }
}