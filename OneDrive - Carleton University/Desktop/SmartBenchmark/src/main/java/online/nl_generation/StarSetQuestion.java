package online.nl_generation;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import online.kg_extractor.model.subgraph.StarGraph;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class StarSetQuestion {

    StarGraph starGraph;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();
    String T;
    String T_withprefix;

    public StarSetQuestion(StarGraph starGraph, String T_prefix) {
        this.starGraph = starGraph;
        this.T = KG_Settings.explorer.removePrefix(T_prefix);
        this.T_withprefix = T_prefix;
        String P = starGraph.getStar().get(0).getPredicate().getValue();
        String P_withPrefix = starGraph.getStar().get(0).getPredicate().getValueWithPrefix();

        String S = starGraph.getStar().get(0).getSubject().getValue();
        String S_withPrefix = starGraph.getStar().get(0).getSubject().getValueWithPrefix();

        String O = starGraph.getStar().get(0).getObject().getValue();
        String O_withPrefix = starGraph.getStar().get(0).getObject().getValueWithPrefix();

        String S_Type = starGraph.getStar().get(0).getS_type_without_prefix();
        String S_Type_withPrefix = starGraph.getStar().get(0).getS_type();

        String O_Type = starGraph.getStar().get(0).getO_type_without_prefix();
        String O_Type_withPrefix = starGraph.getStar().get(0).getO_type();

        String compareEntityTop_withPrefix = SPARQL.getTopEntity(T_withprefix, P_withPrefix, true);
        String compareEntityDown_withPrefix = SPARQL.getTopEntity(T_withprefix, P_withPrefix, false);

        String compareEntityDown = KG_Settings.explorer.removePrefix(compareEntityDown_withPrefix);
        String compareEntityTop = KG_Settings.explorer.removePrefix(compareEntityTop_withPrefix);

        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P_withPrefix, S_Type_withPrefix, O_Type_withPrefix);
        if (predicateNL == null) {
            return;
        }
        String so_NP = PhraseRepresentationProcessing.NP_only(predicateNL.getPredicate_s_O_NP());
        String so_VP = predicateNL.getPredicate_s_O_VP();
        String os_NP = PhraseRepresentationProcessing.NP_only(predicateNL.getPredicate_o_s_NP());
        String os_VP = predicateNL.getPredicate_o_s_VP();

        
        String lessNL = "";
        String greaterNL = "";
        String equalNL = "";

        if (O_Type_withPrefix.equals(KG_Settings.Date)) {
            lessNL = "before";
            greaterNL = "after";
            equalNL = "as the same time as";
        } else if (O_Type_withPrefix.equals(KG_Settings.Number)) {
            lessNL = "less than";
            greaterNL = "greater than";
            equalNL = "equals";
        }

        // < date
        String question = null;
        String query = null;
        if (so_VP != null) {
            question = "Which " + T + " " + so_VP + " " + lessNL + " " + compareEntityDown.trim() + "?";
        } else if (so_NP != null) {
            so_NP = so_NP.replaceAll("\\(.*\\)", "");
            question = "Which " + T + " has " + so_NP + " " + lessNL + " " + compareEntityDown + "'s " + so_NP.replaceAll("\\(.*\\)", "").trim() + "?";
        } else if (os_NP != null) {
            os_NP = os_NP.replaceAll("\\(.*\\)", "");
            question = "Which " + T + " has " + os_NP + " " + lessNL + " " + compareEntityDown + "'s " + os_NP.replaceAll("\\(.*\\)", "").trim() + "?";
        }
        query = "SELECT ?Seed WHERE \n"
                + "{\n"
                + "     ?Seed rdf:type <" + T_withprefix + ">. \n"
                + "     <" + compareEntityDown_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
                + "     <" + compareEntityDown_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
                + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n<?NN)"
                + "\n}";

        if (question != null && query != null) {
            if (!question.contains("null")) {
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET));
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which ", ""), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET));
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which", Request.getRequestPrefix()), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET));
            }
        }

        //Modefied Question compare to constant
        question = null;
        query = null;
        if (so_VP != null) {
            question = "Which " + T + " " + so_VP + " " + lessNL + " " + O_withPrefix + "?";
        } else if (so_NP != null) {
            so_NP = so_NP.replaceAll("\\(.*\\)", "");
            question = "Which " + T + " has " + so_NP + " " + lessNL + " " + O_withPrefix + "?";
        } else if (os_NP != null) {
            os_NP = os_NP.replaceAll("\\(\\?\\)", "");
            question = "Which " + T + " has " + os_NP + " " + lessNL + " " + O_withPrefix + "?";
        }
        query = "SELECT ?Seed WHERE \n"
                + "{\n"
                + "     ?Seed rdf:type <" + T_withprefix + ">. \n"
                + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n<"+O_withPrefix+")"
                + "\n}";

        if (question != null && query != null) {
            if (!question.contains("null")) {
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_STAR_MODIFIED));
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which ", ""), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_STAR_MODIFIED));
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which", Request.getRequestPrefix()), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_STAR_MODIFIED));
            }
        }
        
        // > date
        question = null;
        query = null;
        if (so_VP != null) {
            question = "Which " + T + " " + so_VP + " " + greaterNL + " " + compareEntityTop.trim() + "?";
        } else if (so_NP != null) {
            question = "Which " + T + " has " + so_NP + " " + greaterNL + " " + compareEntityTop + "'s " + so_NP.replaceAll("\\(.*\\)", "").trim() + "?";
        } else if (os_NP != null) {
            question = "Which " + T + " has " + os_NP + " " + greaterNL + " " + compareEntityTop + "'s " + os_NP.replaceAll("\\(.*\\)", "").trim() + "?";
        }
        query = "SELECT ?Seed WHERE \n"
                + "{\n"
                + "     ?Seed rdf:type <" + T_withprefix + ">. "
                + "     <" + compareEntityTop_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
                + "     <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
                + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n>?NN)"
                + "\n}";

        if (question != null && query != null) {
            if (!question.contains("null")) {
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET));
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which ", ""), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET));
                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which", Request.getRequestPrefix()), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET));
            }
        }

        //Modefied Question - first after
        query = null;
        question = null;
        if (O_Type_withPrefix.equals(KG_Settings.Number)) {
            if (so_NP != null) {
                so_NP = so_NP.replaceAll("\\(.*\\)", "");
                question = "Which " + T + " after " + compareEntityTop + " has the most " + so_NP.trim() + "?";
            } else if (os_NP != null) {
                os_NP = os_NP.replaceAll("\\(\\?\\)", "");
                question = "Which " + T + " after " + compareEntityTop + " has the most " + os_NP.trim() + "?";
            }
            query = "SELECT ?Seed WHERE \n"
                    + "{\n"
                    + "     ?Seed rdf:type <" + T_withprefix + ">. \n"
                    + "     <" + compareEntityTop_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
                    + "     <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
                    + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n<?NN)"
                    + "\n}" + " \nORDER BY DESC(?n)\n"
                    + "LIMIT 1\n"
                    + "OFFSET 0";
            if (question != null && query != null) {
                if (!question.contains("null")) {
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET_MODIFIED));
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which ", ""), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET_MODIFIED));
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which", Request.getRequestPrefix()), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET_MODIFIED));
                }
            }
        }
        
        //Modefied Question - second after
        query = null;
        question = null;
        if (O_Type_withPrefix.equals(KG_Settings.Number)) {
            if (so_NP != null) {
                so_NP = so_NP.replaceAll("\\(.*\\)", "");
                question = "Which " + T + " after " + compareEntityTop + " has the second most " + so_NP.trim() + "?";
            } else if (os_NP != null) {
                os_NP = os_NP.replaceAll("\\(\\?\\)", "");
                question = "Which " + T + " after " + compareEntityTop + " has the second most " + os_NP.trim() + "?";
            }
            query = "SELECT ?Seed WHERE \n"
                    + "{\n"
                    + "     ?Seed rdf:type <" + T_withprefix + ">. \n"
                    + "     <" + compareEntityTop_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
                    + "     <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
                    + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n<?NN)"
                    + "\n}" + " \nORDER BY DESC(?n)\n"
                    + "LIMIT 1\n"
                    + "OFFSET 1";
            if (question != null && query != null) {
                if (!question.contains("null")) {
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET_MODIFIED));
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which ", ""), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET_MODIFIED));
                    allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which", Request.getRequestPrefix()), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET_MODIFIED));
                }
            }
        }


        // = date
//        question = null;
//        query = null;
//        if (so_VP != null) {
//            question = "Which " + T + " " + so_VP + " " + equalNL + " " + compareEntityTop.trim() + "?";
//        } else if (so_NP != null) {
//            question = "Which " + T + " has " + so_NP + " " + equalNL + " " + compareEntityTop + "'s " + so_NP.replaceAll("\\(.*\\)", "").trim() + "?";
//        } else if (os_NP != null) {
//            question = "Which " + T + " has " + os_NP + " " + equalNL + " " + compareEntityTop + "'s " + os_NP.replaceAll("\\(.*\\)", "").trim() + "?";
//        }
//        query = "SELECT ?Seed WHERE \n"
//                + "{\n"
//                + "  {\n"
//                + "   ?Seed rdf:type <" + T_withprefix + ">. \n"
//                + "     <" + compareEntityTop_withPrefix + "> rdf:type <" + T_withprefix + ">. \n"
//                + "     <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN. \n"
//                + "     ?Seed <" + P_withPrefix + "> ?n. FILTER (?n=?NN). \n"
//                + "  }\n"
//                + "  MINUS \n"
//                + "  { <" + compareEntityTop_withPrefix + "> <" + P_withPrefix + "> ?NN } \n"
//                + "}";
//
//        if (question != null && query != null) {
//            if (!question.contains("null")) {
//                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_SET));
//                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which ", ""), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_SET));
//                allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question.replaceFirst("Which", Request.getRequestPrefix()), query, starGraph.toString(), 2 * (starGraph.getStar().size() + 1), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SET));
//            }
//        }
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

}
