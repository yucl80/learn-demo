package com.yucl.learn.demo.nebula;

public class Test {
    private static final String BATCH_INSERT_TEMPLATE               = "INSERT %s `%s`(%s) VALUES %s" ;
    private static final String  INSERT_VALUE_TEMPLATE               = "%s: (%s)";
    private static final String  INSERT_VALUE_TEMPLATE_WITH_POLICY   = "%s(\"%s\"): (%s)";
    private static final String  ENDPOINT_TEMPLATE                   = "%s(\"%s\")";
    private static final String  EDGE_VALUE_WITHOUT_RANKING_TEMPLATE = "%s->%s: (%s)";
    private static final String  EDGE_VALUE_TEMPLATE                 = "%s->%s@%d: (%s)";



}
