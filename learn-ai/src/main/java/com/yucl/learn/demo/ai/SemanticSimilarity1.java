package com.yucl.learn.demo.ai;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;

import java.util.Properties;

public class SemanticSimilarity1 {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        String sentence1 = "The quick brown fox jumps over the lazy dog.";
        String sentence2 = "The fast brown fox jumps over the lazy dog.";

        Annotation annotation1 = new Annotation(sentence1);
        Annotation annotation2 = new Annotation(sentence2);

        pipeline.annotate(annotation1);
        pipeline.annotate(annotation2);

        SemanticGraph graph1 = annotation1.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
        SemanticGraph graph2 = annotation2.get(CoreAnnotations.SentencesAnnotation.class).get(0).get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);


       /* double similarity = graph1.semanticSimilarity(graph2);*/

       // System.out.println("The semantic similarity between the two sentences is: " + similarity);
    }
}
