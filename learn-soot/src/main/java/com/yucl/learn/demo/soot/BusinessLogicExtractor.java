package com.yucl.learn.demo.soot;

import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.util.Chain;

import java.util.Map;

public class BusinessLogicExtractor extends BodyTransformer {
    @Override
    protected void internalTransform(Body body, String phase, Map<String, String> options) {
        // 获取方法的Jimple表示
        Chain<Unit> units = body.getUnits();
        for (Unit unit : units) {
            if (unit instanceof InvokeStmt) {
                // 如果单元是一个调用语句，打印信息
                InvokeStmt invokeStmt = (InvokeStmt) unit;
                System.out.println("调用方法: " + invokeStmt.getInvokeExpr().getMethod().getSignature());
            }
        }
    }

    public static void main(String[] args) {
        // 设置 Soot 环境
        Options.v().set_soot_classpath("path/to/classes");
        Options.v().set_keep_line_number(true);
        Options.v().set_output_format(Options.output_format_jimple);
        Scene.v().loadNecessaryClasses();

        // 添加自定义Transform到Jimple pack
        PackManager.v().getPack("jtp").add(new Transform("jtp.businessLogicExtractor", new BusinessLogicExtractor()));

        // 运行 Soot
        PackManager.v().runPacks();
        PackManager.v().writeOutput();
    }


}
