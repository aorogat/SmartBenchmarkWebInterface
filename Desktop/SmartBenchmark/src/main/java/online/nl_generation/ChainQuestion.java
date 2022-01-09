package online.nl_generation;

import java.util.ArrayList;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.subgraph.ChainGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import settings.KG_Settings;

public class ChainQuestion {

    ChainGraph chainGraph;
    String S0; //seed
    String P0;
    String O0;
    String O_Final; //last object in the chain
    String p_series = "";
    String s_o_PN_series = "";
    String s_o_PN_series_without_verb = "";

    private String S0_withPrefix;
    private String P0_withPrefix;
    private String O0_withPrefix;

    private String S0_type_withPrefix;
    private String O0_type_withPrefix;

    private String s0_o_VP;
    private String s0_o_NP;
    private String o0_s_VP;
    private String o0_s_NP;

    PredicateNLRepresentation predicateNL;
    
    //Query
    private String selectQuery;
    private String countQuery;
    private String askQuery_correct;
    private String askQuery_wrong;
    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";
    private String query_GP_triples;
    
    private boolean missingPredicateRepresentation_s_o_NP = false;

    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();

    public ChainQuestion(ChainGraph chainGraph) {
        this.chainGraph = chainGraph;
        S0 = chainGraph.getChain().get(0).getSubject().getValue(); //seed
        P0 = chainGraph.getChain().get(0).getPredicate().getValue();
        O0 = chainGraph.getChain().get(0).getObject().getValue();
        O_Final = chainGraph.getChain().get(chainGraph.getChain().size() - 1).getObject().getValue(); //last object in the chain

        S0_withPrefix = chainGraph.getChain().get(0).getSubject().getValueWithPrefix();
        P0_withPrefix = chainGraph.getChain().get(0).getPredicate().getValueWithPrefix();
        O0_withPrefix = chainGraph.getChain().get(0).getObject().getValueWithPrefix();

        S0_type_withPrefix = chainGraph.getChain().get(0).getS_type();
        O0_type_withPrefix = chainGraph.getChain().get(0).getO_type();

        predicateNL = PredicatesLexicon.getPredicateNL(P0_withPrefix, S0_type_withPrefix, O0_type_withPrefix);
        
        if(predicateNL==null)
        {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        s0_o_NP = predicateNL.getPredicate_s_O_NP();

        ArrayList<TriplePattern> chain = chainGraph.getChain();
        query_GP_triples = "";
        for (TriplePattern triple : chain) {
            query_GP_triples += "\n\t" + triple.toQueryTriplePattern() + ". ";
        }


        s_o_PN_series = "";
        for (int i = 0; i < chainGraph.getChain().size(); i++) {
            TriplePattern triple = chainGraph.getChain().get(i);
            String s = triple.getSubject().getValue();
            String p = triple.getPredicate().getValue();
            String o = triple.getObject().getValue();
            String s_type = triple.getS_type();
            String o_type = triple.getO_type();
            String p_withPrefix = triple.getPredicate().getValueWithPrefix();
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p_withPrefix, s_type, o_type);
            String s_o_NP = "";
            if(predicateNL!=null)
                s_o_NP = predicateNL.getPredicate_s_O_NP();
            //NL representation of intermediate predicates
            if(s_o_NP!=null && !s_o_NP.equals(""))
            {
                if(i>=1)
                    s_o_NP = s_o_NP.replaceFirst("(is |are |was |were )", "");
                s_o_PN_series += " " + s_o_NP;
            }
            else
            {
                missingPredicateRepresentation_s_o_NP = true;
            }
        }
        s_o_PN_series = s_o_PN_series.trim();
        s_o_PN_series_without_verb = s_o_PN_series.replaceFirst("(is |are |was |were )", "");
        
        
        
        
        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

    }


    public ArrayList<GeneratedQuestion> generateAllPossibleChainQuestions() {
        allPossibleQuestions.clear();
        generateQuestionAsk_Correct();
        generateQuestionAsk_Wrong();

        // generateCountQuery(); //Not possible here  (Require a type branch)
        if (KGOntology.isSubtypeOf(S0_type_withPrefix, KG_Settings.Person)) {
            generateQuestionSELECT_e_of_type_Person();
        } else if (KGOntology.isSubtypeOf(S0_type_withPrefix, KG_Settings.Place)) {
            generateQuestionSELECT_e_of_type_Place();
        } else if (KGOntology.isSubtypeOf(S0_type_withPrefix, KG_Settings.Date)) {
            generateQuestionSELECT_e_of_type_Date();
        } else if (KGOntology.isSubtypeOf(S0_type_withPrefix, KG_Settings.Number)) {
            generateQuestionSELECT_e_of_type_Number();
        } else {
            generateQuestionSELECT_e_of_type_Entity();
        }
        return allPossibleQuestions;
    }
    
     public String generateSELECTQuery() {
        String triples = query_GP_triples.replaceFirst("<" + chainGraph.getChain().get(0).getSubject().getValueWithPrefix() + ">", "?Seed");
        int i = 0;
        for (TriplePattern triple : chainGraph.getChain()) {
            triples = triples.replace("<" + chainGraph.getChain().get(i).getSubject().getValueWithPrefix() + ">", "?S" + i++);
        }
        return "SELECT DISTINCT ?Seed WHERE{\n\t" + triples + "\n}";
    }


    public String generateCountQuery() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return null;
        
        
        String triples = query_GP_triples.replaceFirst("<" + chainGraph.getChain().get(0).getSubject().getValueWithPrefix() + ">", "?Seed");
        int i = 0;
        for (TriplePattern triple : chainGraph.getChain()) {
            triples = triples.replace("<" + chainGraph.getChain().get(i).getSubject().getValueWithPrefix() + ">", "?S" + i++);
        }
        return "SELECT COUNT(?Seed) WHERE{\n\t" + triples + "\n}";
    }

    public String generateAskQuery_Correct() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return null;
        
        
        String triples = query_GP_triples;
        int i = 0;
        for (TriplePattern triple : chainGraph.getChain()) {
            if(i==0)
                i++;
            else
                triples = triples.replace("<" + chainGraph.getChain().get(i).getSubject().getValueWithPrefix() + ">", "?S" + i++);
        }
        return "ASK WHERE{\n\t" + triples + "\n}";
    }

    public String generateAskQuery_Wrong() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return null;
        
        
        String triples = query_GP_triples;
        int i = 0;
        for (TriplePattern triple : chainGraph.getChain()) {
            if(i==0)
                i++;
            else
                triples = triples.replace("<" + chainGraph.getChain().get(i).getSubject().getValueWithPrefix() + ">", "?S" + i++);
        }
        triples = triples.replace(S0_withPrefix, somethingElse);
        return "ASK WHERE{\n\t" + triples + "\n}";
    }

    private void generateQuestionSELECT_e_of_type_Person() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return;
        
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

//        if (s_o_VP != null) {
//            questions.add("Who " + s_o_VP + " " + O_Final + "?");
//        }
        if (s_o_PN_series != null) {
            questions.add("Who " + s_o_PN_series + " " + O_Final + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        }
//        if (o_s_VP != null) {
//            questions.add("Whom " + O_Final + " " + o_s_VP + "?");
//        }
//        if (o_s_NP != null) {
//            questions.add("Whose " + O_Final + " " + o_s_NP + "?");
//        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, chainGraph.toString()));
    }

    private void generateQuestionSELECT_e_of_type_Place() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return;
        
        
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_PN_series != null) {
            questions.add("What " + s_o_PN_series + " " + O_Final + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        }
//        if (o_s_VP != null) {
//            questions.add("Where " + O_Final + " " + o_s_VP + "?");
//        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, chainGraph.toString()));
    }

    private void generateQuestionSELECT_e_of_type_Entity() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return;
        
        
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

//        if (o_s_VP != null) {
//            questions.add("What " + O_Final + " " + o_s_VP + "?");
//        }
        if (s_o_PN_series != null) {
            questions.add("What " + s_o_PN_series + " " + O_Final + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        }
//        if (s_o_VP != null) {
//            questions.add("What " + s_o_VP + " " + O_Final + "?");
//        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, chainGraph.toString()));
    }

    private void generateQuestionSELECT_e_of_type_Number() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return;
        
        
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_PN_series != null) {
            questions.add("What " + s_o_PN_series + " " + O_Final + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        }
//        if (o_s_NP != null) {
//            questions.add("How " + o_s_NP + " is " + O_Final + "?");
//        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, chainGraph.toString()));
    }

    private void generateQuestionSELECT_e_of_type_Date() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return;
        
        
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

//        if (o_s_VP != null) {
//            questions.add("When did " + O_Final + " " + o_s_VP + "?");
//        }
        if (s_o_PN_series != null) {
            questions.add("What " + s_o_PN_series + " " + O_Final + "?");
            questions.add(Request.getRequestPrefix() + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, selectQuery, chainGraph.toString()));
    }

    private void generateQuestionAsk_Correct() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return;
        
        
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        if (s_o_PN_series != null && s_o_PN_series.startsWith("is/are ")) {
            questions.add("Is " + S0 + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        } else if (s_o_PN_series != null && s_o_PN_series.startsWith("is ")) {
            questions.add("Is " + S0 + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        } else if (s_o_PN_series != null && s_o_PN_series.startsWith("are ")) {
            questions.add("Are " + S0 + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        } else if (s_o_PN_series != null && s_o_PN_series.startsWith("was ")) {
            questions.add("Was " + S0 + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        } else if (s_o_PN_series != null && s_o_PN_series.startsWith("were ")) {
            questions.add("Were " + S0 + " " + s_o_PN_series_without_verb + " " + O_Final + "?");
        }

//        if (o_s_NP != null && o_s_NP.startsWith("is/are ")) {
//            o_s_NP = o_s_NP.replace("is/are ", "");
//            questions.add("Is " + O_Final + " " + o_s_NP + " " + S0 + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("is ")) {
//            o_s_NP = o_s_NP.replace("is ", "");
//            questions.add("Is " + O_Final + " " + o_s_NP + " " + S0 + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("are ")) {
//            o_s_NP = o_s_NP.replace("are ", "");
//            questions.add("Are " + O_Final + " " + o_s_NP + " " + S0 + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("was ")) {
//            o_s_NP = o_s_NP.replace("was ", "");
//            questions.add("Was " + O_Final + " " + o_s_NP + " " + S0 + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("were ")) {
//            o_s_NP = o_s_NP.replace("were ", "");
//            questions.add("Were " + O_Final + " " + o_s_NP + " " + S0 + "?");
//        }
//
//        if (s_o_VP != null) {
//            questions.add("Does " + S0 + " " + s_o_VP + " " + O_Final + "?");
//        }
//        if (o_s_VP != null) {
//            questions.add("Does " + O_Final + " " + o_s_VP + " " + S0 + "?");
//        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, askQuery_correct, chainGraph.toString()));
    }

    private void generateQuestionAsk_Wrong() {
        
        if(missingPredicateRepresentation_s_o_NP)
            return;
        
        
        //Generate Question
        ArrayList<String> questions = new ArrayList<>();

        String s_o_NP_Auxiliary_Verb = "";

        if (s_o_PN_series != null && s_o_PN_series.startsWith("is/are ")) {
            questions.add("Is " + S0 + " " + s_o_PN_series_without_verb + " " + somethingElseWithoutPrefix + "?");
        } else if (s_o_PN_series != null && s_o_PN_series.startsWith("is ")) {
            questions.add("Is " + S0 + " " + s_o_PN_series_without_verb + " " + somethingElseWithoutPrefix + "?");
        } else if (s_o_PN_series != null && s_o_PN_series.startsWith("are ")) {
            questions.add("Are " + S0 + " " + s_o_PN_series_without_verb + " " + somethingElseWithoutPrefix + "?");
        } else if (s_o_PN_series != null && s_o_PN_series.startsWith("was ")) {
            questions.add("Was " + S0 + " " + s_o_PN_series_without_verb + " " + somethingElseWithoutPrefix + "?");
        } else if (s_o_PN_series != null && s_o_PN_series.startsWith("were ")) {
            questions.add("Were " + S0 + " " + s_o_PN_series_without_verb + " " + somethingElseWithoutPrefix + "?");
        }

//        if (o_s_NP != null && o_s_NP.startsWith("is/are ")) {
//            o_s_NP = o_s_NP.replace("is/are ", "");
//            questions.add("Is " + somethingElseWithoutPrefix + " " + o_s_NP + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("is ")) {
//            o_s_NP = o_s_NP.replace("is ", "");
//            questions.add("Is " + somethingElseWithoutPrefix + " " + o_s_NP + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("are ")) {
//            o_s_NP = o_s_NP.replace("are ", "");
//            questions.add("Are " + somethingElseWithoutPrefix + " " + o_s_NP + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("was ")) {
//            o_s_NP = o_s_NP.replace("was ", "");
//            questions.add("Was " + somethingElseWithoutPrefix + " " + o_s_NP + " " + S + "?");
//        } else if (o_s_NP != null && o_s_NP.startsWith("were ")) {
//            o_s_NP = o_s_NP.replace("were ", "");
//            questions.add("Were " + somethingElseWithoutPrefix + " " + o_s_NP + " " + S + "?");
//        }
//
//        if (s_o_VP != null) {
//            questions.add("Does " + S + " " + s_o_VP + " " + somethingElseWithoutPrefix + "?");
//        }
//        if (o_s_VP != null) {
//            questions.add("Does " + O + " " + o_s_VP + " " + somethingElseWithoutPrefix + "?");
//        }

        allPossibleQuestions.add(new GeneratedQuestion(questions, askQuery_wrong, chainGraph.toString()));
    }

}
