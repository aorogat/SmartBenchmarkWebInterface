package online.nl_generation;

import java.util.ArrayList;
import offLine.scrapping.model.PredicatesLexicon;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.subgraph.ChainGraph;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import online.kg_extractor.model.subgraph.StarGraph;


public class Test{
    public static Graph graph;
    public static void main(String[] args) {
        PredicatesLexicon predicatesNL = new PredicatesLexicon();
        
        TriplePattern t0 = new TriplePattern(new Variable("s", "<http://dbpedia.org/resource/Baltic_Sea>", "Sea"),
                                             new Variable("o", "<http://dbpedia.org/resource/Bräkneån>", "river"),
                                             new Variable("l", "<http://dbpedia.org/ontology/riverMouth>", "_"));
        
        TriplePattern t1 = new TriplePattern(new Variable("s", "<http://dbpedia.org/resource/Bräkneån>", "river"),
                                             new Variable("o", "<http://dbpedia.org/resource/Sweden>", "country"),
                                             new Variable("l", "<http://dbpedia.org/property/mainRiver>", "_"));
        
        TriplePattern t11 = new TriplePattern(new Variable("s", "<http://dbpedia.org/resource/Sweden>", "country"),
                                             new Variable("o", "<http://dbpedia.org/resource/Northern_Europe>", "place"),
                                             new Variable("l", "<http://dbpedia.org/property/largestCountry>", "_"));
        
        TriplePattern t12 = new TriplePattern(new Variable("s", "<http://dbpedia.org/resource/Baltic_Sea>", "Sea"),
                                             new Variable("o", "<http://dbpedia.org/ontology/Sea>", "place"),
                                             new Variable("l", "rdf:type", "_"));
        
       TriplePattern t13 = new TriplePattern(new Variable("s", "<http://dbpedia.org/resource/Baltic_Sea>", "Sea"),
                                             new Variable("o", "<http://dbpedia.org/resource/Denmark>", "country"),
                                             new Variable("l", "<http://dbpedia.org/property/enclosedBy>", "_"));
       
       TriplePattern t14 = new TriplePattern(new Variable("s", "<http://dbpedia.org/resource/Baltic_Sea>", "Sea"),
                                             new Variable("o", "<http://dbpedia.org/resource/Poland>", "country"),
                                             new Variable("l", "<http://dbpedia.org/property/enclosedBy>", "_"));
       
       TriplePattern t15 = new TriplePattern(new Variable("s", "<http://dbpedia.org/resource/Baltic_Sea>", "Sea"),
                                             new Variable("o", "<http://dbpedia.org/resource/Germany>", "country"),
                                             new Variable("l", "<http://dbpedia.org/property/enclosedBy>", "_"));
        
        TriplePattern t2 = new TriplePattern(new Variable("s", "<http://dbpedia.org/resource/Barack_Obama>", "person"),
                                             new Variable("o", "<http://dbpedia.org/resource/United_States>", "a"),
                                             new Variable("l", "<http://dbpedia.org/property/lead>", "_"));
        
        
        //test single edge
        
        System.out.println("\033[1;32m"+"Testing Single-Edge Questions");
        System.out.println("\033[1;32m"+"===========================");
        testSingleEdge(t0);
        testSingleEdge(t2);
        
//        //test chain
        System.out.println("\033[1;32m"+"Testing Chain Questions: L=2");
        System.out.println("\033[1;32m"+"===========================");
        ArrayList<TriplePattern> chainTriples = new ArrayList<>();
        chainTriples.add(t0);
        chainTriples.add(t1);
        testChain(chainTriples);
        
        //System.out.print("\033[1;32m");//Green Color
        System.out.println("\033[1;32m"+"Testing Chain Questions: L=3");
        System.out.println("\033[1;32m"+"===========================");
        chainTriples.add(t11);
        testChain(chainTriples);
        
        
        //test star
        System.out.println("\033[1;32m"+"Testing Star Questions: n=2");
        System.out.println("\033[1;32m"+"===========================");
        ArrayList<TriplePattern> starTriples = new ArrayList<>();
        starTriples.add(t12);
        starTriples.add(t13);
        testStar(starTriples);
        
        System.out.println("\033[1;32m"+"Testing Star Questions: n=3");
        System.out.println("\033[1;32m"+"===========================");
        starTriples.add(t14);
        testStar(starTriples);
        
        System.out.println("\033[1;32m"+"Testing Star Questions: n=4");
        System.out.println("\033[1;32m"+"===========================");
        starTriples.add(t0);
        testStar(starTriples);
        
        System.out.println("\033[1;32m"+"Testing Star Questions: n=5");
        System.out.println("\033[1;32m"+"===========================");
        starTriples.add(t15);
        testStar(starTriples);
        
    }
    
    
    public static void testStar(ArrayList<TriplePattern> ts)
    {
        graph = new StarGraph(ts);
        ArrayList<GeneratedQuestion> qs = StarQuestion.generateQuestions((StarGraph) graph);
        qs.forEach(q -> {q.print();});
        System.out.println("");
        System.out.println("");
    }
    
    public static void testChain(ArrayList<TriplePattern> ts)
    {
        graph = new ChainGraph(ts);
        ArrayList<GeneratedQuestion> qs = ChainQuestion.generateQuestions((ChainGraph) graph);
        qs.forEach(q -> {q.print();});
        System.out.println("");
    }
    
    public static void testSingleEdge(TriplePattern t)
    {
        graph = new SingleEdgeGraph(t);
        ArrayList<GeneratedQuestion> qs = SingleEdgeQuestion.generateQuestions((SingleEdgeGraph) graph);
        qs.forEach(q -> {q.print();});
        System.out.println("");
    }
    
    
}
