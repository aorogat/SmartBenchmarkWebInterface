package system.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import online.nl_generation.Benchmark;
import online.nl_generation.GeneratedQuestion;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class Pruner {

    static Map<String, ArrayList<GeneratedQuestion>> categorizedQuestions_by_properties = new HashMap<>();
    static List<GeneratedQuestion> prunedQuestions = new ArrayList<>();

    static List<GeneratedQuestion> prunedQuestions_1 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_2 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_3 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_4 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_5 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_6 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_7 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_8 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_9 = new ArrayList<>();
    static List<GeneratedQuestion> prunedQuestions_10 = new ArrayList<>();

    static int size_benchmark = 2000;
    static int size_1;
    static int size_2;
    static int size_3;
    static int size_4;
    static int size_5;
    static int size_6;
    static int size_7;
    static int size_8;
    static int size_9;
    static int size_10;

    static int ASK_REQUIRED;
    static int ASK_CURRENT;

    static int LIMIT_REQUIRED;
    static int LIMIT_CURRENT;

    static int UNION_REQUIRED;
    static int UNION_CURRENT;

    static int MINUS_REQUIRED;
    static int MINUS_CURRENT;

    static int CYCLE_REQUIRED;
    static int CYCLE_CURRENT;

    static int FLOWER_REQUIRED;
    static int FLOWER_CURRENT;

    static int WHOM_REQUIRED;
    static int WHOM_CURRENT;

    static int WHOSE_REQUIRED;
    static int WHOSE_CURRENT;

    static int WHERE_REQUIRED;
    static int WHERE_CURRENT;

    static int WHEN_REQUIRED;
    static int WHEN_CURRENT;

    static int HOW_MANY_REQUIRED;
    static int HOW_MANY_CURRENT;
    
    static int TOPICAL_REQUIRED;
    static int TOPICAL_CURRENT;
    
    
    static int REQUEST_REQUIRED;
    static int REQUEST_CURRENT;

    static int MODIFIED_REQUIRED;
    static int MODIFIED_CURRENT;

    public static void main(String[] args) {
        prune("Smart_1_2000.json");
    }

    public static void prune(String benchmarkName) {

        size_1 = (int) (0.34 * (double) size_benchmark);
        size_2 = (int) (0.22 * (double) size_benchmark);
        size_3 = (int) (0.15 * (double) size_benchmark);
        size_4 = (int) (0.10 * (double) size_benchmark);
        size_5 = (int) (0.07 * (double) size_benchmark);
        size_6 = (int) (0.05 * (double) size_benchmark);
        size_7 = (int) (0.03 * (double) size_benchmark);
        size_8 = (int) (0.02 * (double) size_benchmark);
        size_9 = (int) (0.01 * (double) size_benchmark);
        size_10 = (int) (0.01 * (double) size_benchmark);

        ASK_REQUIRED = (int) (0.10 * (double) size_benchmark);
        LIMIT_REQUIRED = (int) (0.05 * (double) size_benchmark);
        UNION_REQUIRED = (int) (0.05 * (double) size_benchmark);
        MINUS_REQUIRED = (int) (0.03 * (double) size_benchmark);

        CYCLE_REQUIRED = (int) (0.02 * (double) size_benchmark);
        FLOWER_REQUIRED = (int) (0.04 * (double) size_benchmark);
        WHOM_REQUIRED = (int) (0.02 * (double) size_benchmark);
        WHOSE_REQUIRED = (int) (0.02 * (double) size_benchmark);
        WHEN_REQUIRED = (int) (0.05 * (double) size_benchmark);
        WHERE_REQUIRED = (int) (0.05 * (double) size_benchmark);

        HOW_MANY_REQUIRED = (int) (0.10 * (double) size_benchmark);
        TOPICAL_REQUIRED = (int) (0.10 * (double) size_benchmark);
        REQUEST_REQUIRED = (int) (0.10 * (double) size_benchmark);

        MODIFIED_REQUIRED = (int) (0.03 * (double) size_benchmark);

        try {
            // create object mapper instance
            ObjectMapper mapper = new ObjectMapper();

            // convert JSON array to list of books
            List<GeneratedQuestion> generatedQuestions = Arrays.asList(mapper.readValue(Paths.get(benchmarkName).toFile(), GeneratedQuestion[].class));
            categorizeQuestions(generatedQuestions);

            //prune
            //first fill rare questions
            for (Map.Entry<String, ArrayList<GeneratedQuestion>> entry : categorizedQuestions_by_properties.entrySet()) {
//                System.out.println("");
//                System.out.println("");
//                System.out.println("");
//                System.out.println(" ====================  Category: " + entry.getKey() + "(" + entry.getValue().size() + "[" + GeneratedQuestion.round(100 * entry.getValue().size() / (double) generatedQuestions.size(), 2) + "%]) ========================== ");

                for (GeneratedQuestion gq1 : entry.getValue()) {

                    if (gq1.getShapeType().toLowerCase().contains("cycle")
                            || gq1.getShapeType().toLowerCase().contains("flower")
                            || gq1.getQuestionType().toLowerCase().contains("when")
                            || gq1.getQuestionType().toLowerCase().contains("where")
                            || gq1.getQuestionType().toLowerCase().contains("whom")
                            || gq1.getQuestionType().toLowerCase().contains("whose")
                            || gq1.getShapeType().toUpperCase().contains("MODIFIED")) {

                        if (gq1.getShapeType().toLowerCase().contains("cycle")) {
                            CYCLE_CURRENT++;
                            if (CYCLE_CURRENT > CYCLE_REQUIRED) {
                                continue;
                            }
                        } else if (gq1.getShapeType().toLowerCase().contains("flower")) {
                            FLOWER_CURRENT++;
                            if (FLOWER_CURRENT > FLOWER_REQUIRED) {
                                continue;
                            }
                        } else if (gq1.getQuestionType().toLowerCase().contains("when")) {
                            WHEN_CURRENT++;
                            if (WHEN_CURRENT > WHEN_REQUIRED) {
                                continue;
                            }
                        } else if (gq1.getQuestionType().toLowerCase().contains("where")) {
                            WHERE_CURRENT++;
                            if (WHERE_CURRENT > WHERE_REQUIRED) {
                                continue;
                            }
                        } else if (gq1.getQuestionType().toLowerCase().contains("whom")) {
                            WHOM_CURRENT++;
                            if (WHOM_CURRENT > WHOM_REQUIRED) {
                                continue;
                            }
                        } else if (gq1.getQuestionType().toLowerCase().contains("whose")) {
                            WHOSE_CURRENT++;
                            if (WHOSE_CURRENT > WHOSE_REQUIRED) {
                                continue;
                            }
                        } else if (gq1.getShapeType().toUpperCase().contains("MODIFIED")) {
                            MODIFIED_CURRENT++;
                            if (MODIFIED_CURRENT > MODIFIED_REQUIRED) {
                                continue;
                            }
                        }

                        if (gq1.getQustionComplexity() > 0 && gq1.getQustionComplexity() <= 0.1) {
                            if (prunedQuestions_1.size() < size_1) {
                                prunedQuestions_1.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.1 && gq1.getQustionComplexity() <= 0.2) {
                            if (prunedQuestions_2.size() < size_2) {
                                prunedQuestions_2.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.2 && gq1.getQustionComplexity() <= 0.3) {
                            if (prunedQuestions_3.size() < size_3) {
                                prunedQuestions_3.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.3 && gq1.getQustionComplexity() <= 0.4) {
                            if (prunedQuestions_4.size() < size_4) {
                                prunedQuestions_4.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.4 && gq1.getQustionComplexity() <= 0.5) {
                            if (prunedQuestions_5.size() < size_5) {
                                prunedQuestions_5.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.5 && gq1.getQustionComplexity() <= 0.6) {
                            if (prunedQuestions_6.size() < size_6) {
                                prunedQuestions_6.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.6 && gq1.getQustionComplexity() <= 0.7) {
                            if (prunedQuestions_7.size() < size_7) {
                                prunedQuestions_7.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.7 && gq1.getQustionComplexity() <= 0.8) {
                            if (prunedQuestions_8.size() < size_8) {
                                prunedQuestions_8.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.8 && gq1.getQustionComplexity() <= 0.9) {
                            if (prunedQuestions_9.size() < size_9) {
                                prunedQuestions_9.add(gq1);
                            }
                        } else if (gq1.getQustionComplexity() > 0.9 && gq1.getQustionComplexity() <= 1) {
                            if (prunedQuestions_10.size() < size_10) {
                                prunedQuestions_10.add(gq1);
                            }
                        }
                        if (gq1.getQuestionType().toLowerCase().contains("yes")) {
                            ASK_CURRENT++;
                            if (ASK_CURRENT > ASK_REQUIRED) {
                                continue;
                            }
                        }

                        if (gq1.getQuestionType().toLowerCase().contains("how-many")) {
                            HOW_MANY_CURRENT++;
                            if (HOW_MANY_CURRENT > HOW_MANY_REQUIRED) {
                                continue;
                            }
                        }
                    }
                }
            }

            //GET BASED ON QUESTION COMPLEXITY DISTRIBUTION
            for (Map.Entry<String, ArrayList<GeneratedQuestion>> entry : categorizedQuestions_by_properties.entrySet()) {

                for (GeneratedQuestion gq1 : entry.getValue()) {
                    if (gq1.getQuestionType().toLowerCase().contains("yes")) {
                        ASK_CURRENT++;
                        if (ASK_CURRENT > ASK_REQUIRED) {
                            continue;
                        }
                    }
                    
                    if (gq1.getQuestionType().toLowerCase().contains("topical")) {
                        TOPICAL_CURRENT++;
                        if (TOPICAL_CURRENT > TOPICAL_REQUIRED) {
                            continue;
                        }
                    }
                    
                    if (gq1.getQuestionType().toLowerCase().contains("request")) {
                        REQUEST_CURRENT++;
                        if (REQUEST_CURRENT > REQUEST_REQUIRED) {
                            continue;
                        }
                    }

                    if (gq1.getQuestionType().toLowerCase().contains("how-many")) {
                        HOW_MANY_CURRENT++;
                        if (HOW_MANY_CURRENT > HOW_MANY_REQUIRED) {
                            continue;
                        }
                    }

                    if (gq1.getQuery().toLowerCase().contains("limit ")) {
                        LIMIT_CURRENT++;
                        if (LIMIT_CURRENT > LIMIT_REQUIRED) {
                            continue;
                        }
                    }
                    if (gq1.getQuery().toLowerCase().contains("union")) {
                        UNION_CURRENT++;
                        if (UNION_CURRENT > UNION_REQUIRED) {
                            continue;
                        }
                    }
                    if (gq1.getQuery().toLowerCase().contains("minus")) {
                        MINUS_CURRENT++;
                        if (MINUS_CURRENT > MINUS_REQUIRED) {
                            continue;
                        }
                    }

                    if (gq1.getQustionComplexity() > 0 && gq1.getQustionComplexity() <= 0.1) {
                        if (prunedQuestions_1.size() < size_1) {
                            prunedQuestions_1.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.1 && gq1.getQustionComplexity() <= 0.2) {
                        if (prunedQuestions_2.size() < size_2) {
                            prunedQuestions_2.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.2 && gq1.getQustionComplexity() <= 0.3) {
                        if (prunedQuestions_3.size() < size_3) {
                            prunedQuestions_3.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.3 && gq1.getQustionComplexity() <= 0.4) {
                        if (prunedQuestions_4.size() < size_4) {
                            prunedQuestions_4.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.4 && gq1.getQustionComplexity() <= 0.5) {
                        if (prunedQuestions_5.size() < size_5) {
                            prunedQuestions_5.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.5 && gq1.getQustionComplexity() <= 0.6) {
                        if (prunedQuestions_6.size() < size_6) {
                            prunedQuestions_6.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.6 && gq1.getQustionComplexity() <= 0.7) {
                        if (prunedQuestions_7.size() < size_7) {
                            prunedQuestions_7.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.7 && gq1.getQustionComplexity() <= 0.8) {
                        if (prunedQuestions_8.size() < size_8) {
                            prunedQuestions_8.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.8 && gq1.getQustionComplexity() <= 0.9) {
                        if (prunedQuestions_9.size() < size_9) {
                            prunedQuestions_9.add(gq1);
                        }
                    } else if (gq1.getQustionComplexity() > 0.9 && gq1.getQustionComplexity() <= 1) {
                        if (prunedQuestions_10.size() < size_10) {
                            prunedQuestions_10.add(gq1);
                        }
                    }

                }
//                GeneratedQuestion gq1 = entry.getValue().get(0);

                prunedQuestions.add(entry.getValue().get(0));

//                for (GeneratedQuestion generatedQuestion : entry.getValue()) {
//                    generatedQuestion.print();
//                }
            }

            prunedQuestions.addAll(prunedQuestions_1);
            prunedQuestions.addAll(prunedQuestions_2);
            prunedQuestions.addAll(prunedQuestions_3);
            prunedQuestions.addAll(prunedQuestions_4);
            prunedQuestions.addAll(prunedQuestions_5);
            prunedQuestions.addAll(prunedQuestions_6);
            prunedQuestions.addAll(prunedQuestions_7);
            prunedQuestions.addAll(prunedQuestions_8);
            prunedQuestions.addAll(prunedQuestions_9);
            prunedQuestions.addAll(prunedQuestions_10);

            System.out.println("0.1: " + prunedQuestions_1.size());
            System.out.println("0.2: " + prunedQuestions_2.size());
            System.out.println("0.3: " + prunedQuestions_3.size());
            System.out.println("0.4: " + prunedQuestions_4.size());
            System.out.println("0.5: " + prunedQuestions_5.size());
            System.out.println("0.6: " + prunedQuestions_6.size());
            System.out.println("0.7: " + prunedQuestions_7.size());
            System.out.println("0.8: " + prunedQuestions_8.size());
            System.out.println("0.9: " + prunedQuestions_9.size());
            System.out.println("0.10: " + prunedQuestions_10.size());

            for (GeneratedQuestion generatedQuestion : prunedQuestions) {
                if (generatedQuestion.getQuestionString().toLowerCase().startsWith("is ")) {
                    generatedQuestion.setQuestionString(generatedQuestion.getQuestionString().replace("whose", "its"));
                }
                generatedQuestion.print();
            }

            System.out.println("No of Questions:" + prunedQuestions.size());
            BenchmarkJsonWritter.save(new Benchmark((ArrayList<GeneratedQuestion>) prunedQuestions), benchmarkName.replace(".json", "") + "_pruned");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void categorizeQuestions(List<GeneratedQuestion> generatedQuestions) {
        for (GeneratedQuestion generatedQuestion : generatedQuestions) {
            String k
                    = //                        generatedQuestion.getSeedType_withPrefix() + "-" +
                    generatedQuestion.getShapeType() + "-"
                    + generatedQuestion.getQuestionType() + "-T="
                    + generatedQuestion.getNoOfTriples();
            if (generatedQuestion.getQuery().toLowerCase().contains("group by")) {
                k += "-group by";
            }
            if (generatedQuestion.getQuery().toLowerCase().contains("union")) {
                k += "-union";
            }
            if (generatedQuestion.getQuery().toLowerCase().contains("minus")) {
                k += "-minus";
            }

            if (categorizedQuestions_by_properties.containsKey(k)) {
                categorizedQuestions_by_properties.get(k).add(generatedQuestion);
                categorizedQuestions_by_properties.put(k, categorizedQuestions_by_properties.get(k));
            } else {
                ArrayList<GeneratedQuestion> list = new ArrayList<>();
                list.add(generatedQuestion);
                categorizedQuestions_by_properties.put(k, list);
            }
        }

        System.out.println("No.of Categories: " + categorizedQuestions_by_properties.size());
    }

}
