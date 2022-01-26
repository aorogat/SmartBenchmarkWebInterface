package online.nl_generation;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import settings.KG_Settings;

public class SingleEdgeQuestion {

    private SingleEdgeGraph singleEdgeGraph;

    private String S;
    private String P;
    private String O;

    private String S_withPrefix;
    private String P_withPrefix;
    private String O_withPrefix;

    private String S_type_withPrefix;
    private String O_type_withPrefix;

    private String s_o_VP;
    private String s_o_NP;
    private String o_s_VP;
    private String o_s_NP;
    private String s_o_NP_without_verb;
    private String o_s_NP_without_verb;

    private String s_o_NP_only;
    private String o_s_NP_only;

    private String selectQuery;
    private String countQuery;
    private String askQuery_correct;
    private String askQuery_wrong;
    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";

    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();

    public SingleEdgeQuestion(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) {

        intialize_Seed_is_S(singleEdgeGraph, S_type_withPrefix, O_type_withPrefix);
        intialize_Seed_is_O(singleEdgeGraph, S_type_withPrefix, O_type_withPrefix);
    }

    void intialize_Seed_is_S(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) {
        this.singleEdgeGraph = singleEdgeGraph;
        this.S_type_withPrefix = S_type_withPrefix;
        this.O_type_withPrefix = O_type_withPrefix;
        S = singleEdgeGraph.getTriplePattern().getSubject().getValue();
        P = singleEdgeGraph.getTriplePattern().getPredicate().getValue();
        O = singleEdgeGraph.getTriplePattern().getObject().getValue();

        S_withPrefix = singleEdgeGraph.getTriplePattern().getSubject().getValueWithPrefix();
        P_withPrefix = singleEdgeGraph.getTriplePattern().getPredicate().getValueWithPrefix();
        O_withPrefix = singleEdgeGraph.getTriplePattern().getObject().getValueWithPrefix();

        somethingElse = SPARQL.getSimilarEntity(KG_Settings.explorer, S_withPrefix, this.S_type_withPrefix);
        somethingElseWithoutPrefix = KG_Settings.explorer.removePrefix(somethingElse);

        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P_withPrefix, S_type_withPrefix, O_type_withPrefix);
        s_o_VP = predicateNL.getPredicate_s_O_VP();
        s_o_NP = predicateNL.getPredicate_s_O_NP();
        o_s_VP = predicateNL.getPredicate_o_s_VP();
        o_s_NP = predicateNL.getPredicate_o_s_NP();

        if (s_o_NP != null) {
            s_o_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb(s_o_NP);
            s_o_NP_only = PhraseRepresentationProcessing.NP_only(s_o_NP);
        }

        if (o_s_NP != null) {
            o_s_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb(o_s_NP);
            o_s_NP_only = PhraseRepresentationProcessing.NP_only(o_s_NP);
        }

        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

        generateAllPossibleSingleEdgeQuestions();
    }

    void intialize_Seed_is_O(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) {
        //Relace each s by o and o by s ////////////////////////////////////////////////////////
        this.singleEdgeGraph = singleEdgeGraph;
        this.O_type_withPrefix = S_type_withPrefix;
        this.S_type_withPrefix = O_type_withPrefix;
        O = singleEdgeGraph.getTriplePattern().getSubject().getValue();
        P = singleEdgeGraph.getTriplePattern().getPredicate().getValue();
        S = singleEdgeGraph.getTriplePattern().getObject().getValue();

        O_withPrefix = singleEdgeGraph.getTriplePattern().getSubject().getValueWithPrefix();
        P_withPrefix = singleEdgeGraph.getTriplePattern().getPredicate().getValueWithPrefix();
        S_withPrefix = singleEdgeGraph.getTriplePattern().getObject().getValueWithPrefix();

        somethingElse = SPARQL.getSimilarEntity(KG_Settings.explorer, S_withPrefix, this.S_type_withPrefix);
        somethingElseWithoutPrefix = KG_Settings.explorer.removePrefix(somethingElse);

        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P_withPrefix, S_type_withPrefix, O_type_withPrefix); //except this one

        o_s_VP = predicateNL.getPredicate_s_O_VP();
        o_s_NP = predicateNL.getPredicate_s_O_NP();
        s_o_VP = predicateNL.getPredicate_o_s_VP();
        s_o_NP = predicateNL.getPredicate_o_s_NP();
        ///////////////////////////////////////////////////////////////////////////////////////////

        s_o_NP_without_verb = null;
        o_s_NP_without_verb = null;
        s_o_NP_only = null;
        o_s_NP_only = null;

        if (s_o_NP != null) {
            s_o_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb(s_o_NP);
            s_o_NP_only = PhraseRepresentationProcessing.NP_only(s_o_NP);
        }

        if (o_s_NP != null) {
            o_s_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb(o_s_NP);
            o_s_NP_only = PhraseRepresentationProcessing.NP_only(o_s_NP);
        }

        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

        generateAllPossibleSingleEdgeQuestions();
    }

    public ArrayList<GeneratedQuestion> generateAllPossibleSingleEdgeQuestions() {
        generateQuestionAsk_Correct();
        generateQuestionAsk_Wrong();

        if (KGOntology.isSubtypeOf(S_type_withPrefix, KG_Settings.Person)) {
            generateQuestionSELECT_e_of_type_Person();
        } else if (KGOntology.isSubtypeOf(S_type_withPrefix, KG_Settings.Place)) {
            generateQuestionSELECT_e_of_type_Place();
        } else if (KGOntology.isSubtypeOf(S_type_withPrefix, KG_Settings.Date)) {
            generateQuestionSELECT_e_of_type_Date();
        } else if (KGOntology.isSubtypeOf(S_type_withPrefix, KG_Settings.Number)) {
            generateQuestionSELECT_e_of_type_Number();
        } else {
            generateQuestionSELECT_e_of_type_Entity();
        }
        return allPossibleQuestions;
    }

    public String generateSELECTQuery() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern().replace("<" + S_withPrefix + ">", "?Seed") + " .";
        return "SELECT DISTINCT ?Seed WHERE{\n\t" + triple + "\n}";
    }

    public String generateCountQuery() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern().replace("<" + S_withPrefix + ">", "?Seed") + " .";
        return "SELECT COUNT(?Seed) WHERE{\n\t" + triple + "\n}";
    }

    public String generateAskQuery_Correct() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern() + " .";
        return "ASK WHERE{\n\t" + triple + "\n}";
    }

    public String generateAskQuery_Wrong() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern();
        if (triple != null) {
            triple = triple.replace(S_withPrefix, somethingElse) + " .";
            return "ASK WHERE{\n\t" + triple + "\n}";
        }
        return null;
    }

    private void generateQuestionSELECT_e_of_type_Person() {
        //Generate Question
        if (s_o_VP != null) {
            String question = "Who " + s_o_VP + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHO, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
        if (s_o_NP != null) {
            String question = "Who " + s_o_NP + " " + O + "?";
                        allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHO, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
             question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
                         allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
        if (o_s_VP != null) {
            String question = "Whom " + O + " " + o_s_VP + "?";
                        allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHOM, GeneratedQuestion.SH_SINGLE_EDGE));

//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
        if (o_s_NP != null) {
            String question = "Whose " + O + " " + o_s_NP_only + "?";
                        allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHOSE, GeneratedQuestion.SH_SINGLE_EDGE));

//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
    }

    private void generateQuestionSELECT_e_of_type_Place() {
        //Generate Question
        if (s_o_NP != null) {
            String question = "What " + s_o_NP + " " + O + "?";
                        allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));

//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
             question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
                         allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));

//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
        if (o_s_VP != null) {
            String question = "Where " + O + " " + o_s_VP + "?";
                        allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHERE, GeneratedQuestion.SH_SINGLE_EDGE));

//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
    }

    private void generateQuestionSELECT_e_of_type_Entity() {
        //Generate Question
        if (s_o_NP != null) {
            String question = "What " + s_o_NP + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
             question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
             allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
        if (s_o_VP != null) {
            String question = "What " + s_o_VP + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
    }

    private void generateQuestionSELECT_e_of_type_Number() {
        if (s_o_NP != null) {
            String question = "What " + s_o_NP + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
             question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
             allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
        if (o_s_NP != null) {
            String question = "How " + o_s_NP + " is " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_HOW_ADJ, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
    }

    private void generateQuestionSELECT_e_of_type_Date() {
        //Generate Question
        if (o_s_VP != null) {
            String question = "When did " + O + " " + o_s_VP + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHEN, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
        if (s_o_NP != null) {
            String question = "What " + s_o_NP + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
             question = Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?";
             allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, selectQuery, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, selectQuery, singleEdgeGraph.toString()));
        }
    }

    private void generateQuestionAsk_Correct() {
        //Generate Question
        if (s_o_NP != null && s_o_NP.startsWith("is/are ")) {
            s_o_NP_without_verb = s_o_NP.replace("is/are ", "");
            String question = "Is " + S + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_correct, singleEdgeGraph.toString()));
        } else if (s_o_NP != null && s_o_NP.startsWith("is ")) {
            s_o_NP_without_verb = s_o_NP.replace("is ", "");
            String question = "Is " + S + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_correct, singleEdgeGraph.toString()));
        } else if (s_o_NP != null && s_o_NP.startsWith("are ")) {
            s_o_NP_without_verb = s_o_NP.replace("are ", "");
            String question = "Are " + S + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_correct, singleEdgeGraph.toString()));
        } else if (s_o_NP != null && s_o_NP.startsWith("was ")) {
            s_o_NP_without_verb = s_o_NP.replace("was ", "");
            String question = "Was " + S + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_correct, singleEdgeGraph.toString()));
        } else if (s_o_NP != null && s_o_NP.startsWith("were ")) {
            s_o_NP_without_verb = s_o_NP.replace("were ", "");
            String question = "Were " + S + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_correct, singleEdgeGraph.toString()));
        }

        if (s_o_VP != null) {
            String question = "Does " + S + " " + s_o_VP + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_correct, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_correct, singleEdgeGraph.toString()));
        }
    }

    private void generateQuestionAsk_Wrong() {
        //Generate Question
        String s_o_NP_Auxiliary_Verb = "";

        if (s_o_NP != null && s_o_NP.startsWith("is/are ")) {
            s_o_NP_without_verb = s_o_NP.replace("is/are ", "");
            String question = "Is " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_wrong, singleEdgeGraph.toString()));
        } else if (s_o_NP != null && s_o_NP.startsWith("is ")) {
            s_o_NP_without_verb = s_o_NP.replace("is ", "");
            String question = "Is " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_wrong, singleEdgeGraph.toString()));
        } else if (s_o_NP != null && s_o_NP.startsWith("are ")) {
            s_o_NP_without_verb = s_o_NP.replace("are ", "");
            String question = "Are " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_wrong, singleEdgeGraph.toString()));
        } else if (s_o_NP != null && s_o_NP.startsWith("was ")) {
            s_o_NP_without_verb = s_o_NP.replace("was ", "");
            String question = "Was " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_wrong, singleEdgeGraph.toString()));
        } else if (s_o_NP != null && s_o_NP.startsWith("were ")) {
            s_o_NP_without_verb = s_o_NP.replace("were ", "");
            String question = "Were " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_wrong, singleEdgeGraph.toString()));
        }
        if (s_o_VP != null) {
            String question = "Does " + somethingElseWithoutPrefix + " " + s_o_VP + " " + O + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S, S_type_withPrefix, question, askQuery_wrong, singleEdgeGraph.toString(), 1, GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_SINGLE_EDGE));
//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery_wrong, singleEdgeGraph.toString()));
        }
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

}
