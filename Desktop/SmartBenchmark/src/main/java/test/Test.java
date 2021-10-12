package test;

import java.util.ArrayList;
import online.kg_extractor.knowledgegraph.DBpedia;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;
import online.kg_extractor.model.NodeType;
import online.kg_extractor.model.subgraph.ChainGraph;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import online.kg_extractor.model.subgraph.StarGraph;

/**
 *
 * @author aorogat
 */
public class Test {

    public static void main(String[] args) {
        //Define KG
        DBpedia dbpedia = DBpedia.getInstance("https://dbpedia.org/sparql");

        //Generate a subgraph with specific shape
//        String seed = "<http://dbpedia.org/resource/Galileo_Galilei> ";
        String seed = "<http://dbpedia.org/resource/Berlin> ";

        testSingleEdge(dbpedia, seed);
////        testChain(dbpedia, seed);
//        testStar(dbpedia, seed);
        
        
        
    }
    
    public static void testSingleEdge(KnowledgeGraph kg, String seed)
    {
        SingleEdgeGraph singleEdgeGraph = new SingleEdgeGraph();
        //Test Single Edge //////////////////////////////////////
        ArrayList<Graph> single_graphs = singleEdgeGraph.generate(kg, seed, NodeType.SUBJECT_ENTITY,NodeType.ANY, true, true);
        //Print the result
        System.out.println("Size: "+single_graphs.size());
        for (Graph graph : single_graphs) {
            System.out.println(graph.toString());
        }
        ///////////////////////////////////////////////////////////
    }
    
    public static void testChain(KnowledgeGraph kg, String seed)
    {
        ChainGraph chainGraph = new ChainGraph();
        //Test Chain
        ArrayList<Graph> chain_graphs = chainGraph.generate(kg, seed, NodeType.SUBJECT_ENTITY, NodeType.ANY, 3, true);

        System.out.println("Size: " + chain_graphs.size());
        for (Graph graph : chain_graphs) {
            System.out.println(graph.toString());
        }
    }
    
    public static void testStar(KnowledgeGraph kg, String seed)
    {
        //Test Star
        StarGraph starGraph = new StarGraph();
        ArrayList<Graph> star_graphs = starGraph.generate(kg, seed, new int[]{NodeType.LITERAL, NodeType.NUMBER, NodeType.NUMBER}, 4, 1);

        System.out.println("Size: " + star_graphs.size());
        for (Graph graph : star_graphs) {
            System.out.println(graph.toString());
        }
        //////////////////////////////////////////
    }
}
