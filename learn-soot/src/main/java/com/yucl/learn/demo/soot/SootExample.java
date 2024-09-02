package com.yucl.learn.demo.soot;

import soot.*;
import soot.options.Options;

public class SootExample {
    public static void main(String[] args) {
        // 设置 Soot 的类路径
        Options.v().set_soot_classpath("path/to/classes");

        // 指定要分析的主类
        Options.v().set_main_class("com.example.MainClass");

        // 保持原始字节码中的行号信息
        Options.v().set_keep_line_number(true);

        // 设置 Soot 输出格式（Jimple）
        Options.v().set_output_format(Options.output_format_jimple);

        // 初始化 Soot
        Scene.v().loadNecessaryClasses();

        // 运行 Soot PackManager 进行分析
        PackManager.v().runPacks();

        // 输出结果
        PackManager.v().writeOutput();
    }
}

