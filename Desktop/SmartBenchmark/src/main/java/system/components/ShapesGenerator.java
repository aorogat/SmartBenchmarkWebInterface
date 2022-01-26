package system.components;

import java.util.ArrayList;
import offLine.scrapping.model.PredicatesLexicon;
import online.kg_extractor.model.NodeType;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.subgraph.ChainGraph;
import online.kg_extractor.model.subgraph.CycleGraph;
import online.kg_extractor.model.subgraph.FlowerGraph;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import online.kg_extractor.model.subgraph.StarGraph;
import online.kg_extractor.model.subgraph.TreeGraph;
import online.nl_generation.ChainQuestion;
import online.nl_generation.CycleGeneralQuestion;
import online.nl_generation.CycleQuestion;
import online.nl_generation.FlowerQuestion;
import online.nl_generation.GeneratedQuestion;
import online.nl_generation.SingleEdgeQuestion;
import online.nl_generation.StarQuestion;
import online.nl_generation.TreeQuestion;
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
            testSingleEdge(branch);
            testChain(branch, 2);
            testChain(branch, 3);
            testStar(branch, 2);
            testStar(branch, 3);
            testStar(branch, 4);
            testStar(branch, 5);
            testStar(branch, 6);
            testTree(branch, 2);
            testTree(branch, 3);
            testTree(branch, 4);
            testCycle(branch);
            testCycleGeneral(branch);
            testFlower(branch, 2);
            testFlower(branch, 3);
            testFlower(branch, 4);

        }
    }

    public static void testSingleEdge(Branch branch) {
        System.out.println("============================= Single-Edge Questions ==================================");
        TriplePattern t0 = new TriplePattern(new Variable("s", branch.s, branch.s_type),
                new Variable("o", branch.o, branch.o_type),
                new Variable("l", branch.p, "_"));
        SingleEdgeGraph singleEdgeGraph = new SingleEdgeGraph(t0);
        String graphString = singleEdgeGraph.toString();
        if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
            SingleEdgeQuestion singleEdgeQuestion = new SingleEdgeQuestion(singleEdgeGraph, branch.s_type, branch.o_type);
            generatedQuestions = singleEdgeQuestion.getAllPossibleQuestions();
            for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                generatedQuestion.print();
            }
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
        System.out.println("============================= Star (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase
        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {
            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
                StarQuestion starQuestion = new StarQuestion(currentStarGraph);
                generatedQuestions = starQuestion.getAllPossibleQuestions();
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs >= 3) {
                        break;
                    }
                }
                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                    generatedQuestion.print();
                }
            }

        }
    }

    public static void testTree(Branch branch, int n) {
        System.out.println("============================= Tree (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> rootStarGraphs = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase
        int succededGraphs = 0;
        for (StarGraph currentStarGraph : rootStarGraphs) {
            try {
                ArrayList<StarGraph> tree_starGraphs = new ArrayList<>();
                tree_starGraphs.add(currentStarGraph);
                //add other
                int[] star2_ends = new int[]{NodeType.URI};
                StarGraph secondaryStarGraph = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, currentStarGraph.getStar().get(0).getObject().getValueWithPrefix(),
                        star2_ends, 1, 1, 6).get(0);
                tree_starGraphs.add(secondaryStarGraph);

                TreeGraph treeGraph = new TreeGraph(tree_starGraphs);

                String graphString = treeGraph.toString();
                if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                    TreeQuestion treeQuestion = new TreeQuestion(treeGraph);

                    generatedQuestions = treeQuestion.getAllPossibleQuestions();
                    if (generatedQuestions.size() > 0) {
                        succededGraphs++;
                        if (succededGraphs >= 3) {
                            break;
                        }
                    }
                    System.out.println(treeGraph.getSeedType());
                    System.out.println(graphString);
                    for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                        generatedQuestion.print();
                    }
//
                }

            } catch (Exception e) {
                System.out.println("");
            }
        }

    }

    public static void testCycle(Branch branch) {
        System.out.println("============================= Cycle Questions ==================================");
        //Cycle 
        CycleGraph cycleGraph = new CycleGraph();
        ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions
        int succededGraphs = 0;
        for (CycleGraph currecntCycleGraph : graphs) {
            String graphString = currecntCycleGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                System.out.println(graphString);
                CycleQuestion question = new CycleQuestion(currecntCycleGraph);
                question.generateQuestions();
                generatedQuestions = question.getAllPossibleQuestions();
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs >= 3) {
                        break;
                    }
                }
                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                    generatedQuestion.print();
                }

            }

        }
    }

    public static void testCycleGeneral(Branch branch) {
        System.out.println("============================= Cycle General Questions ==================================");
        //Cycle 
        CycleGraph cycleGraph = new CycleGraph();
        ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions
        int succededGraphs = 0;
        for (CycleGraph currecntCycleGraph : graphs) {
            String graphString = currecntCycleGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                System.out.println(graphString);
                CycleGeneralQuestion question = new CycleGeneralQuestion(currecntCycleGraph);
                question.generateQuestions();
                generatedQuestions = question.getAllPossibleQuestions();
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs >= 3) {
                        break;
                    }
                }
                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                    generatedQuestion.print();
                }

            }

        }
    }

    public static void testFlower(Branch branch, int n) {
        System.out.println("============================= Flower (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> rootStarGraphs = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase

        for (StarGraph currentStarGraph : rootStarGraphs) {
            try {
                String starPredicates = "";
                for (TriplePattern tp : currentStarGraph.getStar()) {
                    starPredicates += "<" + tp.getPredicate().getValueWithPrefix() + ">, ";
                }

                CycleGraph cycleGraph = new CycleGraph();
                cycleGraph.setUnwantedPropertiesString(starPredicates);

                ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions
                int succededGraphs = 0;
                for (CycleGraph currecntCycleGraph : graphs) {
                    FlowerGraph flowerGraph = new FlowerGraph(currentStarGraph, currecntCycleGraph);
                    String graphString = flowerGraph.toString();
                    if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                        System.out.println(graphString);
                        FlowerQuestion question = new FlowerQuestion(flowerGraph);
                        generatedQuestions = question.getAllPossibleQuestions();
                        if (generatedQuestions.size() > 0) {
                            succededGraphs++;
                            if (succededGraphs >= 3) {
                                break;
                            }
                        }
                        for (GeneratedQuestion generatedQuestion : generatedQuestions) {
                            generatedQuestion.print();
                        }
                    }

                }
            } catch (Exception e) {
//                e.printStackTrace();
                System.out.print(".");
            }
        }

    }

}
