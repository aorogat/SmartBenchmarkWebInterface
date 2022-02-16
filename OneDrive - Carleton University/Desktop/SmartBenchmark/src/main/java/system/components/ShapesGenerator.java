package system.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
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
import online.nl_generation.Benchmark;
import online.nl_generation.ChainQuestion;
import online.nl_generation.CycleGeneralQuestion;
import online.nl_generation.CycleQuestion;
import online.nl_generation.FlowerQuestion;
import online.nl_generation.GeneratedQuestion;
import online.nl_generation.SingleEdgeQuestion;
import online.nl_generation.StarQuestion;
import online.nl_generation.StarQuestionWithGroupBy;
import online.nl_generation.StarSetQuestion;
import online.nl_generation.TreeQuestion;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class ShapesGenerator {

    ArrayList<SingleEdgeGraph> singleEdgeGraphs = new ArrayList<>();

    static Random random = new Random();

    static PredicatesLexicon lexicon = new PredicatesLexicon();

    static ChainGraph chainGraph = new ChainGraph();
    static Benchmark benchmark = new Benchmark();

    static ArrayList<Branch> branchs = RandomSeedGenerator.branchs;
    static HashSet<GeneratedQuestion> generatedQuestions = new HashSet<>();

    public static void main(String[] args) {
        RandomSeedGenerator.generateSeedList();
        int currentSize = 0;
        int oldSize = 0;
        //Single-Edge Questions
        System.out.println("=========================== Generated Questions tell now: " + generatedQuestions.size() + " ===========================");
        int i = 0;
        for (Branch branch : branchs) {
            System.out.println("++++++++++++++++  Seed " + ++i + " of " + branchs.size() + " +++++++ Seed: " + branch.s);
            if (branch.o_type.equals(KG_Settings.Number) || branch.o_type.equals(KG_Settings.Date)) {
                //Single-Edge
                try {
                    testSingleEdge(branch);
                } catch (Exception e) {
                }
                try {
                    testStarSet(branch, 1); //must be 1 for now
                } catch (Exception e) {
                }

            } else {

//                Single-Edge
                try {
                    testSingleEdge(branch);
                } catch (Exception e) {
                }
                try {
                    oldSize = generatedQuestions.size();
                    testChain(branch, 2);
                    currentSize = generatedQuestions.size();
                    if (currentSize > oldSize) {
                        testChain(branch, 3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    testCycle(branch);
                } catch (Exception e) {
                }
                try {
                    testCycleGeneral(branch);
                } catch (Exception e) {
                }

                try {
                    oldSize = generatedQuestions.size();
                    testStar(branch, 1);
                    currentSize = generatedQuestions.size();
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testStar(branch, 2);
                        testStarWithGroupBy(branch, 2);
                        currentSize = generatedQuestions.size();
                    }
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testStar(branch, 3);
                        currentSize = generatedQuestions.size();
                    }
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testStar(branch, 4);
                        currentSize = generatedQuestions.size();
                    }
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testStar(branch, 5);
                        currentSize = generatedQuestions.size();
                    }
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testStar(branch, 6);
                        currentSize = generatedQuestions.size();
                    }
                } catch (Exception e) {
                }
                try {
                    oldSize = generatedQuestions.size();
                    testTree(branch, 2);
                    currentSize = generatedQuestions.size();
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testTree(branch, 3);
                        currentSize = generatedQuestions.size();
                    }
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testTree(branch, 4);
                        currentSize = generatedQuestions.size();
                    }
                } catch (Exception e) {
                }
                try {
                    oldSize = generatedQuestions.size();
                    testFlower(branch, 2);
                    currentSize = generatedQuestions.size();
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testFlower(branch, 3);
                        currentSize = generatedQuestions.size();
                    }
                    if (currentSize > oldSize) {
                        oldSize = generatedQuestions.size();
                        testFlower(branch, 4);
                        currentSize = generatedQuestions.size();
                    }
                } catch (Exception e) {
                }
                try {
                    testStarSet(branch, 1); //must be 1 for now
                } catch (Exception e) {
                }
            }
        }

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("============================== Benchmark =============================");
        System.out.println("======================================================================");

        benchmark.generatedBenchmark = new ArrayList<>();
        for (GeneratedQuestion generatedQuestion : generatedQuestions) {
            generatedQuestion.print();
            if(generatedQuestion.getAnswerCardinality()>0)
                benchmark.generatedBenchmark.add(generatedQuestion);
        }
        BenchmarkJsonWritter.save(benchmark);

    }

    public static void testSingleEdge(Branch branch) {
        System.out.println("============================= Single-Edge Questions ==================================");

        //Numbers and dates are only supported in single-edge
        TriplePattern t0 = new TriplePattern(
                new Variable("s", branch.s, branch.s_type),
                new Variable("o", branch.o, branch.o_type),
                new Variable("l", branch.p, "_"), branch.s_type, branch.o_type);
        SingleEdgeGraph singleEdgeGraph = new SingleEdgeGraph(t0);

        String graphString = singleEdgeGraph.toString();
        if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
            SingleEdgeQuestion singleEdgeQuestion = new SingleEdgeQuestion(singleEdgeGraph, branch.s_type, branch.o_type);
            generatedQuestions.addAll(singleEdgeQuestion.getAllPossibleQuestions());
//            for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                generatedQuestion.print();
//            }
        }
    }

    public static void testChain(Branch branch, int n) {
        System.out.println("============================= Chain (L=" + n + ") Questions ==================================");
        //Chain - Length 2
//            ArrayList<Graph> chainGraphs = chainGraph.generate(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.URI, n, true); //For one answer questions
        ArrayList<Graph> chainGraphs = new ArrayList<>();
        ArrayList<Graph> URIsEnd = chainGraph.generate(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.URI, n, false);
        if (URIsEnd.size() > 5) {
            chainGraphs.add(URIsEnd.get(random.nextInt(URIsEnd.size()))); //For one or many answers questions
        } else if (URIsEnd.size() > 0) {
            chainGraphs.add(URIsEnd.get(0)); //For one or many answers questions
        }
        ArrayList<Graph> NumberEnd = chainGraph.generate(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.NUMBER, n, false);
        if (NumberEnd != null) {
            if (NumberEnd.size() > 5) {
                chainGraphs.add(NumberEnd.get(random.nextInt(NumberEnd.size()))); //For one or many answers questions
            } else if (NumberEnd.size() > 0) {
                chainGraphs.add(NumberEnd.get(0)); //For one or many answers questions
            }
        }

        ArrayList<Graph> DatesEnd = chainGraph.generate(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.DATE, n, false);
        if (DatesEnd != null) {
            if (DatesEnd.size() > 5) {
                chainGraphs.add(DatesEnd.get(random.nextInt(DatesEnd.size()))); //For one or many answers questions
            } else if (DatesEnd.size() > 0) {
                chainGraphs.add(DatesEnd.get(0)); //For one or many answers questions
            }
            int succededGraphs = 0;
            for (Graph chainGraph1 : chainGraphs) {
                chainGraph1 = (ChainGraph) chainGraph1;
                String graphString = chainGraph1.toString();
                if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                    System.out.println(graphString);
                    ChainQuestion chainQuestion = new ChainQuestion((ChainGraph) chainGraph1);
                    generatedQuestions.addAll(chainQuestion.getAllPossibleQuestions());
                    if (generatedQuestions.size() > 0) {
                        succededGraphs++;
                        if (succededGraphs > 0) {
                            return;
                        }
                    }
//                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                    generatedQuestion.print();
//                }

                }

            }
        }
    }

    public static void testStar(Branch branch, int n) {
        System.out.println("============================= Star (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase
        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.NUMBER};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase
        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.DATE};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase
        ends = new int[]{NodeType.URI, NodeType.NUMBER, NodeType.DATE};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {
            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
                StarQuestion starQuestion = new StarQuestion(currentStarGraph);
                generatedQuestions.addAll(starQuestion.getAllPossibleQuestions());
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) {
                        return;
                    }
                }
//                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                    generatedQuestion.print();
//                }
            }
        }

    }

    public static void testTree(Branch branch, int n) {
        System.out.println("============================= Tree (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> rootStarGraphs = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.NUMBER};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.DATE};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.DATE, NodeType.NUMBER};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

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

                    generatedQuestions.addAll(treeQuestion.getAllPossibleQuestions());
                    if (generatedQuestions.size() > 0) {
                        succededGraphs++;
                        if (succededGraphs > 0) {
                            return;
                        }
                    }
                    System.out.println(treeGraph.getSeedType());
                    System.out.println(graphString);
//                    for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                        generatedQuestion.print();
//                    }
//
                }

            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("");
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
                generatedQuestions.addAll(question.getAllPossibleQuestions());
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) {
                        return;
                    }
                }
//                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                    generatedQuestion.print();
//                }

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
                generatedQuestions.addAll(question.getAllPossibleQuestions());
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) {
                        return;
                    }
                }
//                for (GeneratedQuestion generatedQuestion : generatedQuestions) {
//                    generatedQuestion.print();
//                }

            }

        }
    }

    public static void testFlower(Branch branch, int n) {
        System.out.println("============================= Flower (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> rootStarGraphs = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.NUMBER};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.DATE};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.NUMBER, NodeType.DATE};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : rootStarGraphs) {
            try {
                String starPredicates = "";
                for (TriplePattern tp : currentStarGraph.getStar()) {
                    starPredicates += "<" + tp.getPredicate().getValueWithPrefix() + ">, ";
                }

                CycleGraph cycleGraph = new CycleGraph();
                cycleGraph.setUnwantedPropertiesString(starPredicates);

                ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions
                
                for (CycleGraph currecntCycleGraph : graphs) {
                    FlowerGraph flowerGraph = new FlowerGraph(currentStarGraph, currecntCycleGraph);
                    String graphString = flowerGraph.toString();
                    if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                        System.out.println(graphString);
                        FlowerQuestion question = new FlowerQuestion(flowerGraph);
                        generatedQuestions.addAll(question.getAllPossibleQuestions());
                        if (generatedQuestions.size() > 0) {
                            succededGraphs++;
                            if (succededGraphs > 0) {
                                return;
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print(".");
            }
        }

    }

    public static void testStarSet(Branch branch, int n) {
        System.out.println("============================= Star Set (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.NUMBER};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 2);//try 10 graphs because probability of failure increase
        ends = new int[]{NodeType.DATE};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {
            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
                StarSetQuestion starQuestion = new StarSetQuestion(currentStarGraph, currentStarGraph.getSeedType());
                generatedQuestions.addAll(starQuestion.getAllPossibleQuestions());
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) { // the number must cover all types size
                        return;
                    }
                }
            }

        }
    }

    public static void testStarWithGroupBy(Branch branch, int n) {
        System.out.println("============================= Star With Group By (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY_All_predicates_are_the_same(KG_Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {
            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
                StarQuestionWithGroupBy starQuestion = new StarQuestionWithGroupBy(currentStarGraph);
                generatedQuestions.addAll(starQuestion.getAllPossibleQuestions());
                if (generatedQuestions.size() > 0) {
                    succededGraphs++;
                    if (succededGraphs > 0) {
                        return;
                    }
                }
            }
        }

    }

}
