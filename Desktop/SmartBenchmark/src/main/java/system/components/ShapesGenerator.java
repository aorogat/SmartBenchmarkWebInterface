package system.components;

import java.util.ArrayList;
import offLine.scrapping.model.PredicatesLexicon;
import online.kg_extractor.model.NodeType;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import online.nl_generation.GeneratedQuestion;
import online.nl_generation.SingleEdgeQuestion;
import static online.nl_generation.Test.graph;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class ShapesGenerator {

    ArrayList<SingleEdgeGraph> singleEdgeGraphs = new ArrayList<>();

    public static void main(String[] args) {
        PredicatesLexicon lexicon = new PredicatesLexicon();
        RandomSeedGenerator.generateSeedList();

         
        
        ArrayList<Branch> branchs = RandomSeedGenerator.branchs;
        for (Branch branch : branchs) {
            TriplePattern t0 = new TriplePattern(new Variable("s", branch.s, branch.s_type),
                                             new Variable("o",  branch.o, branch.o_type),
                                             new Variable("l", branch.p, "_"));
        SingleEdgeGraph singleEdgeGraph = new SingleEdgeGraph(t0);
            SingleEdgeQuestion singleEdgeQuestion = new SingleEdgeQuestion(singleEdgeGraph, branch.s_type, branch.o_type);
            ArrayList<GeneratedQuestion> generatedQuestions =  singleEdgeQuestion.generateAllPossibleSingleEdgeQuestions();
            for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                generatedQuestion.print();
            }
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
}
