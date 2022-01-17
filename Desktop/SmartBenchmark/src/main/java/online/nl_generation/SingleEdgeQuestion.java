package online.nl_generation;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import settings.KG_Settings;
import system.components.Predicate_Representation_Extractor;

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
    
    void intialize_Seed_is_S(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix)
    {
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
    
    void intialize_Seed_is_O(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix)
    {
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
        
        s_o_NP_without_verb= null;
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

        // generateCountQuery(); //Not possible here  (Require a type branch)
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
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern()
                .replace(S_withPrefix, somethingElse) + " .";
        return "ASK WHERE{\n\t" + triple + "\n}";
    }

    private void generateQuestionSELECT_e_of_type_Person() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_VP != null) {
            questions.add("Who " + s_o_VP + " " + O + "?");
        }
        if (s_o_NP != null) {
            questions.add("Who " + s_o_NP + " " + O + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?");
        }
        if (o_s_VP != null) {
            questions.add("Whom " + O + " " + o_s_VP + "?");
        }
        if (o_s_NP != null) {
            questions.add("Whose " + O + " " + o_s_NP_only + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, singleEdgeGraph.toString()));
    }

    private void generateQuestionSELECT_e_of_type_Place() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_NP != null) {
            questions.add("What " + s_o_NP + " " + O + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?");
        }
        if (o_s_VP != null) {
            questions.add("Where " + O + " " + o_s_VP + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, singleEdgeGraph.toString()));
    }

    private void generateQuestionSELECT_e_of_type_Entity() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

//        if (o_s_VP != null) {
//            questions.add("What " + O + " " + o_s_VP + "?");
//        }
        if (s_o_NP != null) {
            questions.add("What " + s_o_NP + " " + O + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?");
        }
        if (s_o_VP != null) {
            questions.add("What " + s_o_VP + " " + O + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, singleEdgeGraph.toString()));
    }

    private void generateQuestionSELECT_e_of_type_Number() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_NP != null) {
            questions.add("What " + s_o_NP + " " + O + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?");
        }
        if (o_s_NP != null) {
            questions.add("How " + o_s_NP + " is " + O + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, singleEdgeGraph.toString()));
    }

    private void generateQuestionSELECT_e_of_type_Date() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (o_s_VP != null) {
            questions.add("When did " + O + " " + o_s_VP + "?");
        }
        if (s_o_NP != null) {
            questions.add("What " + s_o_NP + " " + O + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_NP_without_verb + " " + O + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, singleEdgeGraph.toString()));
    }

    private void generateQuestionAsk_Correct() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_NP != null && s_o_NP.startsWith("is/are ")) {
            s_o_NP_without_verb = s_o_NP.replace("is/are ", "");
            questions.add("Is " + S + " " + s_o_NP_without_verb + " " + O + "?");
        } else if (s_o_NP != null && s_o_NP.startsWith("is ")) {
            s_o_NP_without_verb = s_o_NP.replace("is ", "");
            questions.add("Is " + S + " " + s_o_NP_without_verb + " " + O + "?");
        } else if (s_o_NP != null && s_o_NP.startsWith("are ")) {
            s_o_NP_without_verb = s_o_NP.replace("are ", "");
            questions.add("Are " + S + " " + s_o_NP_without_verb + " " + O + "?");
        } else if (s_o_NP != null && s_o_NP.startsWith("was ")) {
            s_o_NP_without_verb = s_o_NP.replace("was ", "");
            questions.add("Was " + S + " " + s_o_NP_without_verb + " " + O + "?");
        } else if (s_o_NP != null && s_o_NP.startsWith("were ")) {
            s_o_NP_without_verb = s_o_NP.replace("were ", "");
            questions.add("Were " + S + " " + s_o_NP_without_verb + " " + O + "?");
        }

//        if (o_s_NP != null && o_s_NP.startsWith("is/are ")) {
//            o_s_NP_without_verb = o_s_NP.replace("is/are ", "");
//            questions.add("Is " + O + " " + o_s_NP_without_verb + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("is ")) {
//            o_s_NP_without_verb = o_s_NP.replace("is ", "");
//            questions.add("Is " + O + " " + o_s_NP_without_verb + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("are ")) {
//            o_s_NP_without_verb = o_s_NP.replace("are ", "");
//            questions.add("Are " + O + " " + o_s_NP_without_verb + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("was ")) {
//            o_s_NP_without_verb = o_s_NP.replace("was ", "");
//            questions.add("Was " + O + " " + o_s_NP_without_verb + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("were ")) {
//            o_s_NP_without_verb = o_s_NP.replace("were ", "");
//            questions.add("Were " + O + " " + o_s_NP_without_verb + " " + S + "?");
//        }

        if (s_o_VP != null) {
            questions.add("Does " + S + " " + s_o_VP + " " + O + "?");
        }
//        if (o_s_VP != null) {
//            questions.add("Does " + O + " " + o_s_VP + " " + S + "?");
//        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, askQuery_correct, singleEdgeGraph.toString()));
    }

    private void generateQuestionAsk_Wrong() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        String s_o_NP_Auxiliary_Verb = "";

         if (s_o_NP != null && s_o_NP.startsWith("is/are ")) {
            s_o_NP_without_verb = s_o_NP.replace("is/are ", "");
            questions.add("Is " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?");
        } else if (s_o_NP != null && s_o_NP.startsWith("is ")) {
            s_o_NP_without_verb = s_o_NP.replace("is ", "");
            questions.add("Is " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?");
        } else if (s_o_NP != null && s_o_NP.startsWith("are ")) {
            s_o_NP_without_verb = s_o_NP.replace("are ", "");
            questions.add("Are " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?");
        } else if (s_o_NP != null && s_o_NP.startsWith("was ")) {
            s_o_NP_without_verb = s_o_NP.replace("was ", "");
            questions.add("Was " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?");
        } else if (s_o_NP != null && s_o_NP.startsWith("were ")) {
            s_o_NP_without_verb = s_o_NP.replace("were ", "");
            questions.add("Were " + somethingElseWithoutPrefix + " " + s_o_NP_without_verb + " " + O + "?");
        }

//        if (o_s_NP != null && o_s_NP.startsWith("is/are ")) {
//            o_s_NP_without_verb = o_s_NP.replace("is/are ", "");
//            questions.add("Is " + somethingElseWithoutPrefix + " " + o_s_NP_without_verb + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("is ")) {
//            o_s_NP_without_verb = o_s_NP.replace("is ", "");
//            questions.add("Is " + somethingElseWithoutPrefix + " " + o_s_NP_without_verb + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("are ")) {
//            o_s_NP_without_verb = o_s_NP.replace("are ", "");
//            questions.add("Are " + somethingElseWithoutPrefix + " " + o_s_NP_without_verb + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("was ")) {
//            o_s_NP_without_verb = o_s_NP.replace("was ", "");
//            questions.add("Was " + somethingElseWithoutPrefix + " " + o_s_NP_without_verb + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("were ")) {
//            o_s_NP_without_verb = o_s_NP.replace("were ", "");
//            questions.add("Were " + somethingElseWithoutPrefix + " " + o_s_NP_without_verb + " " + S + "?");
//        }

        if (s_o_VP != null) {
            questions.add("Does " + somethingElseWithoutPrefix + " " + s_o_VP + " " + O + "?");
        }
//        if (o_s_VP != null) {
//            questions.add("Does " + O + " " + o_s_VP + " " + somethingElseWithoutPrefix + "?");
//        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, askQuery_wrong, singleEdgeGraph.toString()));
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

    
    
    
}
