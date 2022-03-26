package system.components;

import ca.carleton.smartbenchweb.MainBean;
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
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class ShapesGenerator {

    ArrayList<SingleEdgeGraph> singleEdgeGraphs = new ArrayList<>();
    static ArrayList<GeneratedQuestion> clearGeneratedQuestions = new ArrayList<>();

    static Random random = new Random();

    public static PredicatesLexicon lexicon = new PredicatesLexicon();

    static ChainGraph chainGraph = new ChainGraph();
    public static Benchmark benchmark = new Benchmark();

    static ArrayList<Branch> branchs;
    public static HashSet<GeneratedQuestion> generatedQuestions = new HashSet<>();

    public static void main(String[] args) {
        generateShapes();
    }

    public static void periodicCall(int i) {
        benchmark.generatedBenchmark = new ArrayList<>(generatedQuestions);
        if (branchs == null) {
            generateShapes();
        }
        int currentSize = 0;
        int oldSize = 0;
        if (i < branchs.size() - 1) {
            System.out.println("++++++++++++++++  Seed " + ++i + " of " + branchs.size() + " +++++++ Seed: " + branchs.get(i).s);
            MainBean.output += "\n" + "++++++  Seed " + i + " of " + branchs.size() + " ++++ Seed: " + branchs.get(i).s;

//                Single-Edge
            try {
                testSingleEdge(branchs.get(i));
            } catch (Exception e) {
            }
            try {
//                    oldSize = generatedQuestions.size();
                testChain(branchs.get(i), 2);
                
                currentSize = generatedQuestions.size();
                if (currentSize > oldSize) {
                    testChain(branchs.get(i), 3);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                testCycle(branchs.get(i));
            } catch (Exception e) {
            }
            try {
                testCycleGeneral(branchs.get(i));
            } catch (Exception e) {
            }

            try {
//                    oldSize = generatedQuestions.size();
                testStar(branchs.get(i), 1);
                currentSize = generatedQuestions.size();
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testStar(branchs.get(i), 2);
                    testStarWithGroupBy(branchs.get(i), 2);
                    currentSize = generatedQuestions.size();
                }
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testStar(branchs.get(i), 3);
                    currentSize = generatedQuestions.size();
                }
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testStar(branchs.get(i), 4);
                    currentSize = generatedQuestions.size();
                }
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testStar(branchs.get(i), 5);
                    currentSize = generatedQuestions.size();
                }
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testStar(branchs.get(i), 6);
                    currentSize = generatedQuestions.size();
                }
            } catch (Exception e) {
            }
            try {
//                    oldSize = generatedQuestions.size();
                testTree(branchs.get(i), 2);
                currentSize = generatedQuestions.size();
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testTree(branchs.get(i), 3);
                    currentSize = generatedQuestions.size();
                }
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testTree(branchs.get(i), 4);
                    currentSize = generatedQuestions.size();
                }
            } catch (Exception e) {
            }
            try {
//                    oldSize = generatedQuestions.size();
                testFlower(branchs.get(i), 2);
                currentSize = generatedQuestions.size();
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testFlower(branchs.get(i), 3);
                    currentSize = generatedQuestions.size();
                }
                if (currentSize > oldSize) {
                    oldSize = generatedQuestions.size();
                    testFlower(branchs.get(i), 4);
                    currentSize = generatedQuestions.size();
                }
            } catch (Exception e) {
            }
            try {
                testStarSet(branchs.get(i), 1); //must be 1 for now
            } catch (Exception e) {
            }

        }

        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("============================== Benchmark =============================");
//        MainBean.output += "\n" + "================== Benchmark ===============\n";
        System.out.println("======================================================================");

        benchmark.generatedBenchmark = new ArrayList<>();
        for (GeneratedQuestion generatedQuestion : generatedQuestions) {
            generatedQuestion.print();
            if (generatedQuestion.getAnswerCardinality() > 0) {
                benchmark.generatedBenchmark.add(generatedQuestion);
            }
        }

        
        for (GeneratedQuestion generatedQuestion : benchmark.generatedBenchmark) {
            if (!(generatedQuestion.getQuestionType().equals(GeneratedQuestion.QT_HOW_MANY)
                    && generatedQuestion.getAnswers().get(0).equals("0"))) {
                clearGeneratedQuestions.add(generatedQuestion);
            }
        }

        benchmark.generatedBenchmark = clearGeneratedQuestions;

    }

    public static void generateShapes() {
        RandomSeedGenerator.generateSeedList();
        branchs = RandomSeedGenerator.branchs;
        int currentSize = 0;
        int oldSize = 0;
        //Single-Edge Questions
        System.out.println("=========================== Generated Questions tell now: " + generatedQuestions.size() + " ===========================");
//        MainBean.output += "\n" + "====== Generated Questions tell now: " + generatedQuestions.size() + " =====\n";
        int i = 0;
//        for (Branch branch : branchs) {
//            System.out.println("++++++++++++++++  Seed " + ++i + " of " + branchs.size() + " +++++++ Seed: " + branch.s);
//            MainBean.output += "\n" + "++++++  Seed " + i + " of " + branchs.size() + " ++++ Seed: " + branch.s;
//
////                Single-Edge
//            try {
//                testSingleEdge(branch);
//            } catch (Exception e) {
//            }
//            try {
////                    oldSize = generatedQuestions.size();
//                testChain(branch, 2);
//                currentSize = generatedQuestions.size();
//                if (currentSize > oldSize) {
//                    testChain(branch, 3);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                testCycle(branch);
//            } catch (Exception e) {
//            }
//            try {
//                testCycleGeneral(branch);
//            } catch (Exception e) {
//            }
//
//            try {
////                    oldSize = generatedQuestions.size();
//                testStar(branch, 1);
//                currentSize = generatedQuestions.size();
//                if (currentSize > oldSize) {
//                    oldSize = generatedQuestions.size();
//                    testStar(branch, 2);
//                    testStarWithGroupBy(branch, 2);
//                    currentSize = generatedQuestions.size();
//                }
//                if (currentSize > oldSize) {
//                    oldSize = generatedQuestions.size();
//                    testStar(branch, 3);
//                    currentSize = generatedQuestions.size();
//                }
//                if (currentSize > oldSize) {
//                    oldSize = generatedQuestions.size();
//                    testStar(branch, 4);
//                    currentSize = generatedQuestions.size();
//                }
//                if (currentSize > oldSize) {
//                    oldSize = generatedQuestions.size();
//                    testStar(branch, 5);
//                    currentSize = generatedQuestions.size();
//                }
//                if (currentSize > oldSize) {
//                    oldSize = generatedQuestions.size();
//                    testStar(branch, 6);
//                    currentSize = generatedQuestions.size();
//                }
//            } catch (Exception e) {
//            }
//                try {
////                    oldSize = generatedQuestions.size();
//                    testTree(branch, 2);
//                    currentSize = generatedQuestions.size();
//                    if (currentSize > oldSize) {
//                        oldSize = generatedQuestions.size();
//                        testTree(branch, 3);
//                        currentSize = generatedQuestions.size();
//                    }
//                    if (currentSize > oldSize) {
//                        oldSize = generatedQuestions.size();
//                        testTree(branch, 4);
//                        currentSize = generatedQuestions.size();
//                    }
//                } catch (Exception e) {
//                }
//                try {
////                    oldSize = generatedQuestions.size();
//                    testFlower(branch, 2);
//                    currentSize = generatedQuestions.size();
//                    if (currentSize > oldSize) {
//                        oldSize = generatedQuestions.size();
//                        testFlower(branch, 3);
//                        currentSize = generatedQuestions.size();
//                    }
//                    if (currentSize > oldSize) {
//                        oldSize = generatedQuestions.size();
//                        testFlower(branch, 4);
//                        currentSize = generatedQuestions.size();
//                    }
//                } catch (Exception e) {
//                }
//            try {
//                testStarSet(branch, 1); //must be 1 for now
//            } catch (Exception e) {
//            }

//        }
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("============================== Benchmark =============================");
//        MainBean.output += "\n" + "================== Benchmark ===============\n";
        System.out.println("======================================================================");

        benchmark.generatedBenchmark = new ArrayList<>();
        for (GeneratedQuestion generatedQuestion : generatedQuestions) {
            generatedQuestion.print();
            if (generatedQuestion.getAnswerCardinality() > 0) {
                benchmark.generatedBenchmark.add(generatedQuestion);
            }
        }

        ArrayList<GeneratedQuestion> clearGeneratedQuestions = new ArrayList<>();
        for (GeneratedQuestion generatedQuestion : benchmark.generatedBenchmark) {
            if (!(generatedQuestion.getQuestionType().equals(GeneratedQuestion.QT_HOW_MANY)
                    && generatedQuestion.getAnswers().get(0).equals("0"))) {
                clearGeneratedQuestions.add(generatedQuestion);
            }
        }

        benchmark.generatedBenchmark = clearGeneratedQuestions;
        BenchmarkJsonWritter.save(benchmark, Settings.benchmarkName);
        Pruner.prune(Settings.benchmarkName + ".json");
    }

    public static void testSingleEdge(Branch branch) {
        System.out.println("============================= Single-Edge Questions ==================================");
        MainBean.output += "\n" + "===== Single-Edge Questions ====\n";

        //Numbers and dates are only supported in single-edge
        TriplePattern t0 = new TriplePattern(
                new Variable("s", branch.s, branch.s_type),
                new Variable("o", branch.o, branch.o_type),
                new Variable("l", branch.p, "_"), branch.s_type, branch.o_type);
        SingleEdgeGraph singleEdgeGraph = new SingleEdgeGraph(t0);

        String graphString = singleEdgeGraph.toString();
        if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
            System.out.println(graphString);
            MainBean.output += "\n" + graphString;
            SingleEdgeQuestion singleEdgeQuestion = new SingleEdgeQuestion(singleEdgeGraph, branch.s_type, branch.o_type);
            ArrayList<GeneratedQuestion> gq = singleEdgeQuestion.getAllPossibleQuestions();
            generatedQuestions.addAll(gq);
            for (GeneratedQuestion generatedQuestion : gq) {
                generatedQuestion.print();
            }
        }
    }

    public static void testChain(Branch branch, int n) {
        System.out.println("============================= Chain (L=" + n + ") Questions ==================================");
        MainBean.output += "\n" + "===== Chain (L=" + n + ") Questions ====\n";
        //Chain - Length 2
//            ArrayList<Graph> chainGraphs = chainGraph.generate(KG_Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.URI, n, true); //For one answer questions
        ArrayList<Graph> chainGraphs = new ArrayList<>();
        ArrayList<Graph> URIsEnd = chainGraph.generate(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.URI, n, false);
        if (URIsEnd.size() > 5) {
            chainGraphs.add(URIsEnd.get(random.nextInt(URIsEnd.size()))); //For one or many answers questions
        } else if (URIsEnd.size() > 0) {
            chainGraphs.add(URIsEnd.get(0)); //For one or many answers questions
        }
        ArrayList<Graph> NumberEnd = chainGraph.generate(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.NUMBER, n, false);
        if (NumberEnd != null) {
            if (NumberEnd.size() > 5) {
                chainGraphs.add(NumberEnd.get(random.nextInt(NumberEnd.size()))); //For one or many answers questions
            } else if (NumberEnd.size() > 0) {
                chainGraphs.add(NumberEnd.get(0)); //For one or many answers questions
            }
        }

        ArrayList<Graph> DatesEnd = chainGraph.generate(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, NodeType.DATE, n, false);
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
                    MainBean.output += "\n" + graphString;
                    ChainQuestion chainQuestion = new ChainQuestion((ChainGraph) chainGraph1);
                    ArrayList<GeneratedQuestion> gq = chainQuestion.getAllPossibleQuestions();
                    generatedQuestions.addAll(gq);
                    for (GeneratedQuestion generatedQuestion : gq) {
                        generatedQuestion.print();
                    }
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

    public static void testStar(Branch branch, int n) {
        System.out.println("============================= Star (n=" + n + " + 1 type branch) Questions for (" + branch.s + ")==================================");
        MainBean.output += "\n" + "===== Star (n=" + n + " + 1 type branch) Questions for (" + branch.s + ") ====\n";
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.LITERAL};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase
        ends = new int[]{NodeType.NUMBER};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase
        ends = new int[]{NodeType.DATE};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

//        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
//        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase
//        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.NUMBER};
//        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase
//        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.DATE};
//        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase
//        ends = new int[]{NodeType.URI, NodeType.NUMBER, NodeType.DATE};
//        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase
        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {
            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
                MainBean.output += "\n" + graphString;
                StarQuestion starQuestion = new StarQuestion(currentStarGraph);
                ArrayList<GeneratedQuestion> gq = starQuestion.getAllPossibleQuestions();
                generatedQuestions.addAll(gq);
                for (GeneratedQuestion generatedQuestion : gq) {
                    generatedQuestion.print();
                }
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
        MainBean.output += "\n" + "===== Tree (n=" + n + " + 1 type branch) Questions for (" + branch.s + ") ====\n";
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> rootStarGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.NUMBER};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.DATE};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.DATE, NodeType.NUMBER};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : rootStarGraphs) {
            try {
                ArrayList<StarGraph> tree_starGraphs = new ArrayList<>();
                tree_starGraphs.add(currentStarGraph);
                //add other
                int[] star2_ends = new int[]{NodeType.URI};
                StarGraph secondaryStarGraph = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, currentStarGraph.getStar().get(0).getObject().getValueWithPrefix(),
                        star2_ends, 1, 1, 6).get(0);
                tree_starGraphs.add(secondaryStarGraph);

                TreeGraph treeGraph = new TreeGraph(tree_starGraphs);

                String graphString = treeGraph.toString();
                if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                    TreeQuestion treeQuestion = new TreeQuestion(treeGraph);

                    ArrayList<GeneratedQuestion> gq = treeQuestion.getAllPossibleQuestions();
                    generatedQuestions.addAll(gq);
                    for (GeneratedQuestion generatedQuestion : gq) {
                        generatedQuestion.print();
                    }
                    if (generatedQuestions.size() > 0) {
                        succededGraphs++;
                        if (succededGraphs > 0) {
                            return;
                        }
                    }
                    System.out.println(treeGraph.getSeedType());
                    MainBean.output += "\n" + graphString;
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
        MainBean.output += "\n" + "===== Cycle Questions ====\n";
        //Cycle 
        CycleGraph cycleGraph = new CycleGraph();
        ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions
        int succededGraphs = 0;
        for (CycleGraph currecntCycleGraph : graphs) {
            String graphString = currecntCycleGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                System.out.println(graphString);
                MainBean.output += "\n" + graphString;
                CycleQuestion question = new CycleQuestion(currecntCycleGraph);
                question.generateQuestions();
                ArrayList<GeneratedQuestion> gq = question.getAllPossibleQuestions();
                generatedQuestions.addAll(gq);
                for (GeneratedQuestion generatedQuestion : gq) {
                    generatedQuestion.print();
                }
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
        MainBean.output += "\n" + "===== Cycle General Questions ====\n";
        //Cycle 
        CycleGraph cycleGraph = new CycleGraph();
        ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions
        int succededGraphs = 0;
        for (CycleGraph currecntCycleGraph : graphs) {
            String graphString = currecntCycleGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                System.out.println(graphString);
                MainBean.output += "\n" + graphString;
                CycleGeneralQuestion question = new CycleGeneralQuestion(currecntCycleGraph);
                question.generateQuestions();
                ArrayList<GeneratedQuestion> gq = question.getAllPossibleQuestions();
                generatedQuestions.addAll(gq);
                for (GeneratedQuestion generatedQuestion : gq) {
                    generatedQuestion.print();
                }
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
        MainBean.output += "\n" + "===== Flower (n=" + n + " + 1 type branch) Questions for (" + branch.s + ") ====\n";

        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> rootStarGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.NUMBER};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.URI, NodeType.DATE};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        ends = new int[]{NodeType.URI, NodeType.NUMBER, NodeType.DATE};
        rootStarGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : rootStarGraphs) {
            try {
                String starPredicates = "";
                for (TriplePattern tp : currentStarGraph.getStar()) {
                    starPredicates += "<" + tp.getPredicate().getValueWithPrefix() + ">, ";
                }

                CycleGraph cycleGraph = new CycleGraph();
                cycleGraph.setUnwantedPropertiesString(starPredicates);

                ArrayList<CycleGraph> graphs = cycleGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, NodeType.SUBJECT_ENTITY, 10); //For one or many answers questions

                for (CycleGraph currecntCycleGraph : graphs) {
                    FlowerGraph flowerGraph = new FlowerGraph(currentStarGraph, currecntCycleGraph);
                    String graphString = flowerGraph.toString();
                    if (!graphString.contains("UNKONWN") && !graphString.contains("null")) {
                        System.out.println(graphString);
                        MainBean.output += "\n" + graphString;
                        FlowerQuestion question = new FlowerQuestion(flowerGraph);
                        ArrayList<GeneratedQuestion> gq = question.getAllPossibleQuestions();
                        generatedQuestions.addAll(gq);
                        for (GeneratedQuestion generatedQuestion : gq) {
                            generatedQuestion.print();
                        }
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
        MainBean.output += "\n" + "===== Star Set (n=" + n + " + 1 type branch) Questions for (" + branch.s + ") ====\n";
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.NUMBER};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 2);//try 10 graphs because probability of failure increase
        ends = new int[]{NodeType.DATE};
        starGraphs.addAll(starGraph.generate_SUBJECT_ENTITY(Settings.knowledgeGraph, branch.s, ends, n, 1, 10));//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {
            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
                MainBean.output += "\n" + graphString;
                StarSetQuestion starQuestion = new StarSetQuestion(currentStarGraph, currentStarGraph.getSeedType());
                ArrayList<GeneratedQuestion> gq = starQuestion.getAllPossibleQuestions();
                generatedQuestions.addAll(gq);
                for (GeneratedQuestion generatedQuestion : gq) {
                    generatedQuestion.print();
                }
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
        MainBean.output += "\n" + "===== Star With Group By (n=" + n + " + 1 type branch) Questions for (" + branch.s + ") ====\n";
        StarGraph starGraph = new StarGraph();
        int[] ends = new int[]{NodeType.URI, NodeType.URI, NodeType.URI};
        ArrayList<StarGraph> starGraphs = starGraph.generate_SUBJECT_ENTITY_All_predicates_are_the_same(Settings.knowledgeGraph, branch.s, ends, n, 1, 10);//try 10 graphs because probability of failure increase

        int succededGraphs = 0;
        for (StarGraph currentStarGraph : starGraphs) {
            String graphString = currentStarGraph.toString();
            if (!graphString.contains("UNKONWN") && !graphString.contains("null")) { //if it contains null, this means one of the objects not belonging to contexts in the DB: i.e, its type not start with dbo:
                System.out.println(currentStarGraph.getSeedType());
                System.out.println(graphString);
                MainBean.output += "\n" + graphString;
                StarQuestionWithGroupBy starQuestion = new StarQuestionWithGroupBy(currentStarGraph);
                ArrayList<GeneratedQuestion> gq = starQuestion.getAllPossibleQuestions();
                generatedQuestions.addAll(gq);
                for (GeneratedQuestion generatedQuestion : gq) {
                    generatedQuestion.print();
                }
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
