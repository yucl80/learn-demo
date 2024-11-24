package com.example.agent;

import com.example.agent.transformer.MethodTraceTransformer;
import java.lang.instrument.Instrumentation;

public class MethodTraceAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("Method Trace Agent is starting...");
        inst.addTransformer(new MethodTraceTransformer(), true);
    }
}
