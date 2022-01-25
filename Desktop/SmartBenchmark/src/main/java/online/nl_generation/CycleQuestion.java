package online.nl_generation;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.ontology.KGOntology;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import online.kg_extractor.model.subgraph.CycleGraph;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class CycleQuestion {

    CycleGraph cycleGraph;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();
    String FCs_AND;
    String FCs_OR;

    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";

    String GPs_ASK; //for Graph Patterns
    String seed_withPrefix;
    String seed_without_prefix;
    String seed_type_withPrefix;
    String seed_type_without_prefix;

    public static final int FORWARD = 1;
    public static final int BACKWARD = 2;
    int direction = 1;

    public CycleQuestion(CycleGraph cycleGraph) {
        this.cycleGraph = cycleGraph;

        seed_withPrefix = cycleGraph.getPath_1().getSubject().getValueWithPrefix();
        seed_without_prefix = cycleGraph.getPath_1().getSubject().getValue();

        seed_type_withPrefix = cycleGraph.getPath_1().getS_type();

        somethingElse = SPARQL.getSimilarEntity(KG_Settings.explorer, cycleGraph.getPath_1().getSubject().getValueWithPrefix(), cycleGraph.getPath_1().getS_type());
        somethingElseWithoutPrefix = KG_Settings.explorer.removePrefix(somethingElse);

//        FCs_AND = factConstraints_toString(cycleGraph, CoordinatingConjunction.AND);
//        FCs_OR = factConstraints_toString(cycleGraph, CoordinatingConjunction.OR);
        direction = FORWARD;
        selectQuestions(CoordinatingConjunction.AND);
//        countQuestions(CoordinatingConjunction.AND);
//        askQuestions_true_answer(CoordinatingConjunction.AND);
//        askQuestions_false_answer(CoordinatingConjunction.AND);

        selectQuestions(CoordinatingConjunction.OR);
//        countQuestions(CoordinatingConjunction.OR);
//        askQuestions_true_answer(CoordinatingConjunction.OR);
//        askQuestions_false_answer(CoordinatingConjunction.OR);

        direction = BACKWARD;
        selectQuestions(CoordinatingConjunction.AND);
        selectQuestions(CoordinatingConjunction.OR);
    }

    public String selectQuery(CycleGraph cycleGraph, String coordinatingConjunction) {
        String query = "";
        String triples = "";
        String t1 = cycleGraph.getPath_1().toQueryTriplePattern();
        String t2 = cycleGraph.getPath_2().toQueryTriplePattern();

        if (coordinatingConjunction.equals(CoordinatingConjunction.AND)) {
            triples = "\n\t" + t1 + "."
                    + "\n\t" + t2 + ".";
            if (direction == FORWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">", "?Seed");
            } else if (direction == BACKWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + ">", "?Seed");
            }
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.OR)) {
            triples += "\n\t{" + t1 + "} UNION \n\t {" + t2 + "}";
            if (direction == FORWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">", "?Seed");
            } else if (direction == BACKWARD) {
                triples = triples.replace("<" + cycleGraph.getPath_1().getObject().getValueWithPrefix() + ">", "?Seed");
            }
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        }
        return query;
    }

    public String askQuery_true_answer(CycleGraph cycleGraph, String coordinatingConjunction) {
        return selectQuery(cycleGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + cycleGraph.getPath_1().getSubject().getValueWithPrefix() + ">");
    }

    public String askQuery_false_answer(CycleGraph cycleGraph, String coordinatingConjunction) {
        return selectQuery(cycleGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + somethingElse + ">");
    }

    public String countQuery(CycleGraph cycleGraph, String coordinatingConjunction) {
        return selectQuery(cycleGraph, coordinatingConjunction).replace("SELECT DISTINCT ?Seed WHERE{", "SELECT (COUNT (?Seed) AS ?count) WHERE{");
    }

    public void selectQuestions(String coordinatingConjunction) {

        ArrayList<String> questions = new ArrayList<>();
        String whQuestion = selectWh_Questions(coordinatingConjunction, "NP");
        if (whQuestion != null && !whQuestion.contains("null")) {
            questions.add(whQuestion);
        }

        whQuestion = selectWh_Questions(coordinatingConjunction, "VP");
        if (whQuestion != null && !whQuestion.contains("null")) {
            questions.add(whQuestion);
        }

        if (questions.size() >= 1) {
            String selectQuery = selectQuery(cycleGraph, coordinatingConjunction);

            GeneratedQuestion generatedQuestion = new GeneratedQuestion(questions, selectQuery, cycleGraph.toString());
            allPossibleQuestions.add(generatedQuestion);
        }
    }

    public String selectWh_Questions(String coordinatingConjunction, String phrase) {
        String FCs = "";
        if (direction == FORWARD) {
            if (KGOntology.isSubtypeOf(seed_type_withPrefix, KG_Settings.Person)) {
                if (phrase.equals("NP") || phrase.equals("VP")) {
                    FCs = factConstraints_toString_VP_forward(cycleGraph, coordinatingConjunction);
                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "Who " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(seed_type_withPrefix, KG_Settings.Place)) {
                if (phrase.equals("NP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);
                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {
                    FCs = factConstraints_toString_VP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "Where " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(seed_type_withPrefix, KG_Settings.Date)) {
                if (phrase.equals("NP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "When " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(seed_type_withPrefix, KG_Settings.Number)) {
                if (phrase.equals("NP")) {
                    FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "What " + FCs + "?";
                }
            } else {
                FCs = factConstraints_toString_NP_forward(cycleGraph, coordinatingConjunction);

                if (FCs == null || FCs.contains("null")) {
                    return null;
                }
                return "What " + FCs + "?";
            }

        } else if (direction == BACKWARD) {
            String O_type = SPARQL.getType(KG_Settings.explorer, cycleGraph.getPath_1().getObject().getValueWithPrefix());
            if (KGOntology.isSubtypeOf(O_type, KG_Settings.Person)) {
                if (phrase.equals("NP") || phrase.equals("VP")) {

                    FCs = factConstraints_toString_VP_reverse(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "Who " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(O_type, KG_Settings.Place)) {
                if (phrase.equals("NP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {

                    FCs = factConstraints_toString_VP_reverse(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "Where " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(O_type, KG_Settings.Date)) {
                if (phrase.equals("NP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "What " + FCs + "?";
                } else if (phrase.equals("VP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "When " + FCs + "?";
                }
            } else if (KGOntology.isSubtypeOf(O_type, KG_Settings.Number)) {
                if (phrase.equals("NP")) {

                    FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                    if (FCs == null || FCs.contains("null")) {
                        return null;
                    }
                    return "What " + FCs + "?";
                }
            } else {

                FCs = factConstraints_toString_NP_revers(cycleGraph, coordinatingConjunction);

                if (FCs == null || FCs.contains("null")) {
                    return null;
                }
                return "What " + FCs + "?";
            }

        }

        return null;
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

    public static String factConstraints_toString_VP_forward(CycleGraph cycleGraph, String coorinatingConjunction) {

        PredicateNLRepresentation predicateNL_Path1 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_1().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_1().getS_type(),
                cycleGraph.getPath_1().getO_type());

        PredicateNLRepresentation predicateNL_Path2 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_2().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_2().getS_type(),
                cycleGraph.getPath_2().getO_type());

        if (predicateNL_Path1 != null && predicateNL_Path2 != null) {
            String p1_SO_VP = predicateNL_Path1.getPredicate_s_O_VP();
            String p2_SO_VP = predicateNL_Path2.getPredicate_s_O_VP();
            
            String p1_OS_VP = predicateNL_Path1.getPredicate_o_s_VP();
            String p2_OS_VP = predicateNL_Path2.getPredicate_o_s_VP();

            if (p1_SO_VP != null && p2_SO_VP != null) {
                return p1_SO_VP + " " + coorinatingConjunction + " " + p2_SO_VP + " " + cycleGraph.getPath_1().getObject().getValue();
            }
            else if (p1_OS_VP != null && p2_OS_VP != null) {
                return cycleGraph.getPath_1().getObject().getValue() + " " + p1_OS_VP + " " + coorinatingConjunction + " " + p2_OS_VP;
            }

        }
        return null;
    }

    public static String factConstraints_toString_VP_reverse(CycleGraph cycleGraph, String coorinatingConjunction) {

        PredicateNLRepresentation predicateNL_Path1 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_1().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_1().getS_type(),
                cycleGraph.getPath_1().getO_type());

        PredicateNLRepresentation predicateNL_Path2 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_2().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_2().getS_type(),
                cycleGraph.getPath_2().getO_type());

        if (predicateNL_Path1 != null && predicateNL_Path2 != null) {

            String p1_OS_VP = predicateNL_Path1.getPredicate_o_s_VP();
            String p2_OS_VP = predicateNL_Path2.getPredicate_o_s_VP();

            if (p1_OS_VP != null && p2_OS_VP != null) {
                return p1_OS_VP + " " + coorinatingConjunction + " " + p2_OS_VP + " " + cycleGraph.getPath_1().getSubject().getValue();
            }

        }
        return null;
    }

    public static String factConstraints_toString_NP_forward(CycleGraph cycleGraph, String coorinatingConjunction) {

        PredicateNLRepresentation predicateNL_Path1 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_1().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_1().getS_type(),
                cycleGraph.getPath_1().getO_type());

        PredicateNLRepresentation predicateNL_Path2 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_2().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_2().getS_type(),
                cycleGraph.getPath_2().getO_type());

        if (predicateNL_Path1 != null && predicateNL_Path2 != null) {
            String p1_SO_NP = predicateNL_Path1.getPredicate_s_O_NP();
            String p2_SO_NP = predicateNL_Path2.getPredicate_s_O_NP();
            
            String p1_OS_NP = predicateNL_Path1.getPredicate_o_s_NP();
            String p2_OS_NP = predicateNL_Path2.getPredicate_o_s_NP();

            if (p1_SO_NP != null && p2_SO_NP != null) {
                return PhraseRepresentationProcessing.NP_without_Preposition(p1_SO_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_without_verb___first(p2_SO_NP) + " " + cycleGraph.getPath_1().getObject().getValue();
            }
            else if (p1_OS_NP != null && p2_OS_NP != null) {
                String stype = cycleGraph.getPath_1().getS_type();
                if(KGOntology.isSubtypeOf(stype, KG_Settings.Place))
                    return cycleGraph.getPath_1().getObject().getValue() + " is his/her " + PhraseRepresentationProcessing.NP_only(p1_OS_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_only(p2_OS_NP);
                else
                    return cycleGraph.getPath_1().getObject().getValue() + " is its " + PhraseRepresentationProcessing.NP_only(p1_OS_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_only(p2_OS_NP);
               
            }

        }
        return null;
    }

    public static String factConstraints_toString_NP_revers(CycleGraph cycleGraph, String coorinatingConjunction) {

        PredicateNLRepresentation predicateNL_Path1 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_1().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_1().getS_type(),
                cycleGraph.getPath_1().getO_type());

        PredicateNLRepresentation predicateNL_Path2 = PredicatesLexicon.getPredicateNL(cycleGraph.getPath_2().getPredicate().getValueWithPrefix(),
                cycleGraph.getPath_2().getS_type(),
                cycleGraph.getPath_2().getO_type());

        if (predicateNL_Path1 != null && predicateNL_Path2 != null) {

            String p1_OS_NP = predicateNL_Path1.getPredicate_o_s_NP();
            String p2_OS_NP = predicateNL_Path2.getPredicate_o_s_NP();

            if (p1_OS_NP != null && p2_OS_NP != null) {
                return PhraseRepresentationProcessing.NP_without_Preposition(p1_OS_NP) + " " + coorinatingConjunction + " " + PhraseRepresentationProcessing.NP_without_verb___first(p2_OS_NP) + " " + cycleGraph.getPath_1().getSubject().getValue();
            }
        }
        return null;
    }

}
