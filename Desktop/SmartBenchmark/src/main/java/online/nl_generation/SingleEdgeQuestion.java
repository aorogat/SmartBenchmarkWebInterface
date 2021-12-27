package online.nl_generation;

import java.util.ArrayList;
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
    
    
    
    private String selectQuery;
    private String countQuery;
    private String askQuery_correct;
    private String askQuery_wrong;
    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";
    
    
    
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();

    public SingleEdgeQuestion(SingleEdgeGraph singleEdgeGraph, String S_type_withPrefix, String O_type_withPrefix) {
        this.singleEdgeGraph = singleEdgeGraph;
        this.S_type_withPrefix = S_type_withPrefix;
        this.O_type_withPrefix = O_type_withPrefix;

        S = singleEdgeGraph.getTriplePattern().getSubject().getValue();
        P = singleEdgeGraph.getTriplePattern().getPredicate().getValue();
        O = singleEdgeGraph.getTriplePattern().getObject().getValue();
        
        S_withPrefix = singleEdgeGraph.getTriplePattern().getSubject().getValueWithPrefix();
        P_withPrefix = singleEdgeGraph.getTriplePattern().getPredicate().getValueWithPrefix();
        O_withPrefix = singleEdgeGraph.getTriplePattern().getObject().getValueWithPrefix();

        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P_withPrefix, S_type_withPrefix, O_type_withPrefix);
        s_o_VP = predicateNL.getPredicate_s_O_VP();
        s_o_NP = predicateNL.getPredicate_s_O_NP();
        o_s_VP = predicateNL.getPredicate_o_s_VP();
        o_s_NP = predicateNL.getPredicate_o_s_NP();
        
        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();
        
        generateAllPossibleSingleEdgeQuestions();
    }

    public ArrayList<GeneratedQuestion> generateAllPossibleSingleEdgeQuestions() {
        allPossibleQuestions.clear();
        generateQuestionAsk_Correct();
        generateQuestionAsk_Wrong();
        
        // generateCountQuery(); //Not possible here  (Require a type branch)
        if (KGOntology.isSubtypeOf(S_type_withPrefix, KG_Settings.Person)) {
            generateQuestionSELECT_e_of_type_Person();
        }
        else if (KGOntology.isSubtypeOf(S_type_withPrefix, KG_Settings.Place)) {
            generateQuestionSELECT_e_of_type_Place();
        }
        else if (KGOntology.isSubtypeOf(S_type_withPrefix, KG_Settings.Date)) {
            generateQuestionSELECT_e_of_type_Date();
        }
        else if (KGOntology.isSubtypeOf(S_type_withPrefix, KG_Settings.Number)) {
            generateQuestionSELECT_e_of_type_Number();
        }
        
        else {
            generateQuestionSELECT_e_of_type_Entity();
        }
        return allPossibleQuestions;
    }

    public String generateSELECTQuery() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern().replace("<"+singleEdgeGraph.getTriplePattern().getSubject().getValueWithPrefix()+">", "?Seed") + " .";
        return "SELECT DISTINCT ?Seed WHERE{\n\t" + triple + "\n}";
    }

    public String generateCountQuery() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern().replace("<"+singleEdgeGraph.getTriplePattern().getSubject().getValueWithPrefix()+">", "?Seed") + " .";
        return "SELECT COUNT(?Seed) WHERE{\n\t" + triple + "\n}";
    }

    public String generateAskQuery_Correct() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern() + " .";
        return "ASK WHERE{\n\t" + triple + "\n}";
    }

    public String generateAskQuery_Wrong() {
        String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern()
                .replace(singleEdgeGraph.getTriplePattern().getSubject().getValueWithPrefix(), somethingElse) + " .";
        return "ASK WHERE{\n\t" + triple + "\n}";
    }

    private void generateQuestionSELECT_e_of_type_Person() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_VP != null) {
            questions.add("Who " + O + " " + s_o_VP + "?");
        }
        if (s_o_NP != null) {
            questions.add("Who is " + s_o_NP + " " + O + "?");
        }
        if (o_s_VP != null) {
            questions.add("Whom " + O + " " + o_s_VP + "?");
        }
        if (o_s_NP != null) {
            questions.add("Whose " + o_s_NP + " " + O + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery));
    }

    private void generateQuestionSELECT_e_of_type_Place() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_NP != null) {
            questions.add("What " + s_o_NP + " " + O + "?");
        }
        if (o_s_VP != null) {
            questions.add("Where " + O + " " + o_s_VP + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery));
    }

    private void generateQuestionSELECT_e_of_type_Entity() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (o_s_VP != null) {
            questions.add("What " + O + " " + o_s_VP + "?");
        }
        if (s_o_NP != null) {
            questions.add("What " + s_o_NP + " " + O + "?");
        }
        if (s_o_VP != null) {
            questions.add("What " + s_o_VP + " " + O + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery));
    }

    private void generateQuestionSELECT_e_of_type_Number() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_NP != null) {
            questions.add("What " + s_o_NP + " " + O + "?");
        }
        if (o_s_NP != null) {
            questions.add("How " + o_s_NP + " is " + O + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery));
    }

    private void generateQuestionSELECT_e_of_type_Date() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_VP != null) {
            questions.add("Who " + O + " " + s_o_VP + "?");
        }
        if (s_o_NP != null) {
            questions.add("Who " + s_o_NP + " " + O + "?");
        }
        if (o_s_VP != null) {
            questions.add("Whom " + O + " " + o_s_VP + "?");
        }
        if (o_s_NP != null) {
            questions.add("Whose " + o_s_NP + " " + O + "?");
        }

        questions.add("When did " + O + " " + o_s_VP + "?");
        questions.add("What " + s_o_NP + " " + O + "?");

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery));
    }

    
    private void generateQuestionAsk_Correct() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_NP != null) {
             questions.add("Is " + S + " " + s_o_NP + " " + O + "?");
        }
        if (o_s_NP != null) {
             questions.add("Is " + O + " " + o_s_NP + " " + O + "?");
        }
        if (s_o_VP != null) {
             questions.add("Does " + S + " " + s_o_VP + " " + O + "?");
        }
        if (o_s_VP != null) {
             questions.add("Does " + O + " " + o_s_VP + " " + O + "?");
        }
        
        allPossibleQuestions.add(new GeneratedQuestion(questions, askQuery_correct));
    }
    
    private void generateQuestionAsk_Wrong() {
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_NP != null) {
             questions.add("Is " + S + " " + s_o_NP + " " + somethingElseWithoutPrefix + "?");
        }
        if (o_s_NP != null) {
             questions.add("Is " + O + " " + o_s_NP + " " + somethingElseWithoutPrefix + "?");
        }
        if (s_o_VP != null) {
             questions.add("Does " + S + " " + s_o_VP + " " + somethingElseWithoutPrefix + "?");
        }
        if (o_s_VP != null) {
             questions.add("Does " + O + " " + o_s_VP + " " + somethingElseWithoutPrefix + "?");
        }
        
        allPossibleQuestions.add(new GeneratedQuestion(questions, askQuery_wrong));
    }
    
    
}
