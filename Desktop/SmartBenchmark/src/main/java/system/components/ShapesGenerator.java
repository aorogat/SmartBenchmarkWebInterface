package system.components;

import java.util.ArrayList;
import offLine.scrapping.model.PredicatesLexicon;
import online.kg_extractor.model.NodeType;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.subgraph.ChainGraph;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import online.kg_extractor.model.subgraph.StarGraph;
import online.nl_generation.ChainQuestion;
import online.nl_generation.GeneratedQuestion;
import online.nl_generation.SingleEdgeQuestion;
import online.nl_generation.StarQuestion;
import static online.nl_generation.Test.graph;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class ShapesGenerator {

    ArrayList<SingleEdgeGraph> singleEdgeGraphs = new ArrayList<>();

    static PredicatesLexicon lexicon = new PredicatesLexicon();

    static ChainGraph chainGraph = new ChainGraph();

    static ArrayList<Branch> branchs = RandomSeedGenerator.branchs;
    static ArrayList<GeneratedQuestion> generatedQuestions = new ArrayList<>();

    public static void main(String[] args) {
        RandomSeedGenerator.generateSeedList();

        //Single-Edge Questions
        for (Branch branch : branchs) {

            //Single-Edge
//            testSingleEdge(branch);
//            testChain(branch, 2);
//            testChain(branch, 3);
            testStar(branch, 2);

            //ArrayList<Graph> graphs = singleEdgeGraph.generate(KG_Settings.knowledgeGraph, "<" + seed.seed + ">", NodeType.SUBJECT_ENTITY, NodeType.ANY, false, false);
//            for (Graph graph : graphs) {
//                System.out.println(graph.toString());
//                graph = (SingleEdgeGraph) graph;
//                SingleEdgeQuestion singleEdgeQuestion = new SingleEdgeQuestion(singleEdgeGraph, S_type_withPrefix, O_type_withPrefix)
//                ArrayList<GeneratedQuestion> qs = singleEdgeQuestion.generateAllPossibleSingleEdgeQuestions();
//                qs.forEach(q -> {
//                    q.print();
//                });
//                System.out.println("");
//            }
        }
    }

    public static void testSingleEdge(Branch branch) {
        System.out.println("============================= Single-Edge Questions ==================================");
        TriplePattern t0 = new TriplePattern(new Variable("s", branch.s, branch.s_type),
                new Variable("o", branch.o, branch.o_type),
                new Variable("l", branch.p, "_"));
        SingleEdgeGraph singleEdgeGraph = new SingleEdgeGraph(t0);
        SingleEdgeQuestion singleEdgeQuestion = new SingleEdgeQuestion(singleEdgeGraph, branch.s_type, branch.o_type);
        generatedQuestions = singleEdgeQuestion.getAllPossibleQuestions();
        for (GeneratedQuestion generatedQuestion : generatedQuestions) {
            generatedQuestion.print();
        }
    }

    public static void testChain(Branch branch, int n) {
        System.out.println("============================= Chain (L=" + n + ") Questions ==================================");
        //Chain - Length 2
//            ArrayList<Graph> chainGraphs = chainGraph.generate(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.URI, n, true); //For one answer questions
        ArrayList<Graph> chainGraphs = chainGraph.generate(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.URI, n, false); //For one or many answers questions
        for (Graph chainGraph1 : chainGraphs) {
            chainGraph1 = (ChainGraph) chainGraph1;
            String graphString = chainGraph1.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                System.out.println(graphString);
                ChainQuestion chainQuestion = new ChainQuestion((ChainGraph) chainGraph1);
                generatedQuestions = chainQuestion.getAllPossibleQuestions();
                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                    generatedQuestion.print();
                }

            }

        }
    }

    public static void testStar(Branch branch, int n) {
        System.out.println("============================= Star (n=" + n + ") Questions for ("+branch.s+")==================================");
        //Chain - Length 2
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 50);
//        ArrayList<Graph> starGraphs = starGraph.generate_OBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 50);
        for (StarGraph currentStarGraph : starGraphs) {
            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
                StarQuestion starQuestion = new StarQuestion(currentStarGraph);
                    generatedQuestions = starQuestion.getAllPossibleQuestions();
                    for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                        generatedQuestion.print();
                    }
//
            }

        }
    }

}
