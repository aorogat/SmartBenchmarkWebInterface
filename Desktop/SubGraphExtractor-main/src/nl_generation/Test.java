package nl_generation;

import java.util.ArrayList;
import kg_extractor.model.NodeType;
import kg_extractor.model.TriplePattern;
import kg_extractor.model.Variable;
import kg_extractor.model.subgraph.ChainGraph;
import kg_extractor.model.subgraph.Graph;
import kg_extractor.model.subgraph.SingleEdgeGraph;
import scrapping.model.PredicatesLexicon;

public class Test {
    static Graph graph;
    
    public static void main(String[] args) {
        //Load predicates (output of offline phase)
        PredicatesLexicon predicatesNL = new PredicatesLexicon();
        
        TriplePattern t0 = new TriplePattern(new Variable("s", "Baltic Sea", "place"),
                                             new Variable("o", "Bräkneån", "place"),
                                             new Variable("l", "riverMouth", "place"));
        
        TriplePattern t1 = new TriplePattern(new Variable("s", "Bräkneån", "place"),
                                             new Variable("o", "Sweden", "country"),
                                             new Variable("l", "mainRiver", "place"));
        
        TriplePattern t11 = new TriplePattern(new Variable("s", "Sweden", "country"),
                                             new Variable("o", "Northern Europe", "place"),
                                             new Variable("l", "largestCountry", "_"));
        
        TriplePattern t2 = new TriplePattern(new Variable("s", "Barack Obama", "a"),
                                             new Variable("o", "United States", "a"),
                                             new Variable("l", "lead", "a"));
        
        
        //test single edge
        testSingleEdge(t0);
        testSingleEdge(t2);
        
        //test chain
        ArrayList<TriplePattern> chainTriples = new ArrayList<>();
        chainTriples.add(t0);
        chainTriples.add(t1);
        testChain(chainTriples);
        
        chainTriples.add(t11);
        testChain(chainTriples);
        
        
    }
    
    
    public static void testChain(ArrayList<TriplePattern> ts)
    {
        graph = new ChainGraph(ts);
        ArrayList<String> qs = chainQuestion.generateQuestions((ChainGraph) graph);
        for (String q : qs) {
            System.out.println(q);
        }
        System.out.println("");
    }
    
    public static void testSingleEdge(TriplePattern t)
    {
        graph = new SingleEdgeGraph(t);
        ArrayList<String> qs = SingleEdgeQuestion.generateQuestions((SingleEdgeGraph) graph);
        for (String q : qs) {
            System.out.println(q);
        }
        System.out.println("");
    }
    
    
}
