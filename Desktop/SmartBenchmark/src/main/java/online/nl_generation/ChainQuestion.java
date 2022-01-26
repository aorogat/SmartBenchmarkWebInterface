package online.nl_generation;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.subgraph.ChainGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import settings.KG_Settings;

public class ChainQuestion {

    //Forward direction    (?S0)__(P0)__(O0)___(P1)___(O1)___....__(O_Final)
    // s_o_PN_series = [p0_s_o][p1_s_o][p2_s_o] ... [pn_s_o]  (((Wh.. [s_o_PN_series] [O_Final]?)))
    // o_s_PN_series = [p0_o_s][p1_o_s][p2_o_s] ... [pn_o_s]  (((Wh.. [o_s_PN_series] [S0]?)))
    ChainGraph chainGraph;
    String S0_Seed; //seed
    String P0;
    String O0;

    private String S0_withPrefix;
    private String P0_withPrefix;
    private String O0_withPrefix;

    String O_Final; //last object in the chain

    String P1_to_n_SO_PN_series = "";
    String P1_to_n_OS_PN_series = "";

    String P1_to_n_SO_PN_series_without_verb = "";

    private String S0_type_withPrefix;
    private String O0_type_withPrefix;

    private String P0_SO_VP;
    private String P0_SO_NP;
    private String P0_OS_VP;
    private String P0_OS_NP;

    private String P0_SO_NP_without_verb = "";
    private String P0_OS_NP_without_verb = "";

    PredicateNLRepresentation predicateNL_for_P0;

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

        initialize_forward_chain();
        initialize_backward_chain();

    }

    private void initialize_forward_chain() {
        missingPredicateRepresentation_s_o_NP = false;
        S0_Seed = chainGraph.getChain().get(0).getSubject().getValue(); //seed
        P0 = chainGraph.getChain().get(0).getPredicate().getValue();
        O0 = chainGraph.getChain().get(0).getObject().getValue();
        O_Final = chainGraph.getChain().get(chainGraph.getChain().size() - 1).getObject().getValue(); //last object in the chain

        S0_withPrefix = chainGraph.getChain().get(0).getSubject().getValueWithPrefix();
        P0_withPrefix = chainGraph.getChain().get(0).getPredicate().getValueWithPrefix();
        O0_withPrefix = chainGraph.getChain().get(0).getObject().getValueWithPrefix();

        S0_type_withPrefix = chainGraph.getChain().get(0).getS_type();
        O0_type_withPrefix = chainGraph.getChain().get(0).getO_type();

        somethingElse = SPARQL.getSimilarEntity(KG_Settings.explorer, S0_withPrefix, this.S0_type_withPrefix);
        somethingElseWithoutPrefix = KG_Settings.explorer.removePrefix(somethingElse);

        predicateNL_for_P0 = PredicatesLexicon.getPredicateNL(P0_withPrefix, S0_type_withPrefix, O0_type_withPrefix);

        P0_SO_NP = "";
        P0_SO_VP = "";

        P0_OS_NP = "";
        P0_OS_VP = "";

        if (predicateNL_for_P0 == null) {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        P0_SO_NP = predicateNL_for_P0.getPredicate_s_O_NP();
        P0_SO_VP = predicateNL_for_P0.getPredicate_s_O_VP();

        P0_OS_NP = predicateNL_for_P0.getPredicate_o_s_NP();
        P0_OS_VP = predicateNL_for_P0.getPredicate_o_s_VP();

        P1_to_n_SO_PN_series = "";
        P1_to_n_OS_PN_series = "";

        ArrayList<TriplePattern> chain = chainGraph.getChain();
        query_GP_triples = "";
        for (TriplePattern triple : chain) {
            query_GP_triples += "\n\t" + triple.toQueryTriplePattern() + ". ";
        }

        for (int i = 1; i < chainGraph.getChain().size(); i++) {
            TriplePattern triple = chainGraph.getChain().get(i);
            String s = triple.getSubject().getValue();
            String p = triple.getPredicate().getValue();
            String o = triple.getObject().getValue();
            String s_type = triple.getS_type();
            String o_type = triple.getO_type();
            String p_withPrefix = triple.getPredicate().getValueWithPrefix();
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p_withPrefix, s_type, o_type);
            String s_o_NP = "";
            String o_s_NP = "";
            if (predicateNL != null) {
                s_o_NP = predicateNL.getPredicate_s_O_NP();
                o_s_NP = predicateNL.getPredicate_o_s_NP();
            }
            //NL representation of intermediate predicates
            if (s_o_NP != null && !s_o_NP.equals("")) {
                P1_to_n_SO_PN_series += " " + PhraseRepresentationProcessing.NP_without_verb(s_o_NP);
            } else {
                missingPredicateRepresentation_s_o_NP = true;
            }
            if (o_s_NP != null && !o_s_NP.equals("")) {
                P1_to_n_OS_PN_series += " " + PhraseRepresentationProcessing.NP_without_verb(o_s_NP);
            } else {
                missingPredicateRepresentation_s_o_NP = true;
            }
        }
//        s_o_PN_series = s_o_PN_series.trim();
//        s_o_PN_series_without_verb = s_o_PN_series.replaceFirst("(is |are |was |were )", "");

        if (predicateNL_for_P0 == null) {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        P0_SO_NP = predicateNL_for_P0.getPredicate_o_s_NP();
        P0_OS_NP = predicateNL_for_P0.getPredicate_s_O_NP();

        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

        generateAllPossibleChainQuestions();
    }

    //Chain take the revers direction
    private void initialize_backward_chain() {
        missingPredicateRepresentation_s_o_NP = false;
        int length = chainGraph.getChain().size() - 1;
        S0_Seed = chainGraph.getChain().get(length).getObject().getValue(); //seed //Swap
        P0 = chainGraph.getChain().get(length).getPredicate().getValue();
        O0 = chainGraph.getChain().get(length).getSubject().getValue(); //Swap
        O_Final = chainGraph.getChain().get(0).getSubject().getValue(); //last object in the chain //Swap

        S0_withPrefix = chainGraph.getChain().get(length).getObject().getValueWithPrefix(); //Swap
        P0_withPrefix = chainGraph.getChain().get(length).getPredicate().getValueWithPrefix();
        O0_withPrefix = chainGraph.getChain().get(length).getSubject().getValueWithPrefix(); //Swap

        S0_type_withPrefix = chainGraph.getChain().get(length).getO_type(); //Swap
        O0_type_withPrefix = chainGraph.getChain().get(length).getS_type(); //Swap

        somethingElse = SPARQL.getSimilarEntity(KG_Settings.explorer, S0_withPrefix, this.S0_type_withPrefix);
        somethingElseWithoutPrefix = KG_Settings.explorer.removePrefix(somethingElse);

        predicateNL_for_P0 = PredicatesLexicon.getPredicateNL(P0_withPrefix, O0_type_withPrefix, S0_type_withPrefix); //Swap

        P0_SO_NP = "";
        P0_SO_VP = "";

        P0_OS_NP = "";
        P0_OS_VP = "";

        if (predicateNL_for_P0 == null) {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        P0_SO_NP = predicateNL_for_P0.getPredicate_o_s_NP();
        P0_SO_VP = predicateNL_for_P0.getPredicate_o_s_VP();

        P0_OS_NP = predicateNL_for_P0.getPredicate_s_O_NP();
        P0_OS_VP = predicateNL_for_P0.getPredicate_s_O_VP();

        ArrayList<TriplePattern> chain = chainGraph.getChain();
        query_GP_triples = "";

        for (int i = length; i >= 0; i--) {
            TriplePattern triple = chain.get(i);
            query_GP_triples += "\n\t" + triple.toQueryTriplePattern() + ". ";
        }

        P1_to_n_SO_PN_series = "";
        for (int i = length - 1; i >= 0; i--) {
            TriplePattern triple = chainGraph.getChain().get(i);
            String s = triple.getObject().getValue();
            String p = triple.getPredicate().getValue();
            String o = triple.getSubject().getValue();
            String s_type = triple.getO_type();
            String o_type = triple.getS_type();
            String p_withPrefix = triple.getPredicate().getValueWithPrefix();
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p_withPrefix, o_type, s_type);
            String s_o_NP = "";
            if (predicateNL != null) {
                s_o_NP = predicateNL.getPredicate_o_s_NP(); //////////////replaced
            }
            //NL representation of intermediate predicates
            if (s_o_NP != null && !s_o_NP.equals("")) {
                if (i < length) {
                    s_o_NP = PhraseRepresentationProcessing.NP_without_verb(s_o_NP);
                }
                P1_to_n_SO_PN_series += " " + s_o_NP;
            } else {
                missingPredicateRepresentation_s_o_NP = true;
            }
        }
        P1_to_n_SO_PN_series = P1_to_n_SO_PN_series.trim();
        P1_to_n_SO_PN_series_without_verb = PhraseRepresentationProcessing.NP_without_verb___first(P1_to_n_SO_PN_series);

        if (P0_SO_NP != null) {
            P0_SO_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb___first(P0_SO_NP);
        }
        if (P0_OS_NP != null) {
            P0_OS_NP_without_verb = PhraseRepresentationProcessing.NP_without_verb___first(P0_OS_NP);
        }

        if (predicateNL_for_P0 == null) {
            missingPredicateRepresentation_s_o_NP = true;
            return;
        }

        selectQuery = generateSELECTQuery();
        countQuery = generateCountQuery();
        askQuery_correct = generateAskQuery_Correct();
        askQuery_wrong = generateAskQuery_Wrong();

        generateAllPossibleChainQuestions();
    }

    public void generateAllPossibleChainQuestions() {
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
    }

    public String generateSELECTQuery() {
        String triples = query_GP_triples.replaceFirst("<" + S0_withPrefix + ">", "?Seed");

        if (KG_Settings.Triple_NP_Direction == KG_Settings.LABEL_NP_SO) {
            for (int i = 0; i < chainGraph.getChain().size() - 1; i++) {
                triples = triples.replace("<" + chainGraph.getChain().get(i).getSubject().getValueWithPrefix() + ">", "?S" + i);
            }
        } else if (KG_Settings.Triple_NP_Direction == KG_Settings.LABEL_NP_OS) {
            for (int i = chainGraph.getChain().size() - 1; i >= 0; i--) {
                triples = triples.replace("<" + chainGraph.getChain().get(i).getObject().getValueWithPrefix() + ">", "?S" + i);
            }
        }

        return "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
    }

    public String generateCountQuery() {

        if (missingPredicateRepresentation_s_o_NP) {
            return null;
        }

        String triples = query_GP_triples.replaceFirst("<" + chainGraph.getChain().get(0).getSubject().getValueWithPrefix() + ">", "?Seed");

        if (KG_Settings.Triple_NP_Direction == KG_Settings.LABEL_NP_SO) {
            for (int i = 0; i < chainGraph.getChain().size() - 1; i++) {
                triples = triples.replace("<" + chainGraph.getChain().get(i).getSubject().getValueWithPrefix() + ">", "?S" + i);
            }
        } else if (KG_Settings.Triple_NP_Direction == KG_Settings.LABEL_NP_OS) {
            for (int i = chainGraph.getChain().size() - 1; i >= 0; i--) {
                triples = triples.replace("<" + chainGraph.getChain().get(i).getObject().getValueWithPrefix() + ">", "?S" + i);
            }
        }

        return "SELECT COUNT(?Seed) WHERE{" + triples + "\n}";
    }

    public String generateAskQuery_Correct() {

        if (missingPredicateRepresentation_s_o_NP) {
            return null;
        }

        String triples = query_GP_triples;

        if (KG_Settings.Triple_NP_Direction == KG_Settings.LABEL_NP_SO) {
            for (int i = 1; i < chainGraph.getChain().size() - 1; i++) {
                triples = triples.replace("<" + chainGraph.getChain().get(i).getSubject().getValueWithPrefix() + ">", "?S" + i);
            }
        } else if (KG_Settings.Triple_NP_Direction == KG_Settings.LABEL_NP_OS) {
            for (int i = chainGraph.getChain().size() - 2; i >= 0; i--) {
                triples = triples.replace("<" + chainGraph.getChain().get(i).getObject().getValueWithPrefix() + ">", "?S" + i);
            }
        }

        return "ASK WHERE{" + triples + "\n}";
    }

    public String generateAskQuery_Wrong() {

        if (missingPredicateRepresentation_s_o_NP) {
            return null;
        }

        String triples = query_GP_triples;

        if (KG_Settings.Triple_NP_Direction == KG_Settings.LABEL_NP_SO) {
            for (int i = 1; i < chainGraph.getChain().size() - 1; i++) {
                triples = triples.replace("<" + chainGraph.getChain().get(i).getSubject().getValueWithPrefix() + ">", "?S" + i);
            }
        } else if (KG_Settings.Triple_NP_Direction == KG_Settings.LABEL_NP_OS) {
            for (int i = chainGraph.getChain().size() - 2; i >= 0; i--) {
                triples = triples.replace("<" + chainGraph.getChain().get(i).getObject().getValueWithPrefix() + ">", "?S" + i);
            }
        }

        triples = triples.replace(S0_withPrefix, somethingElse);
        return "ASK WHERE{" + triples + "\n}";
    }

    private void generateQuestionSELECT_e_of_type_Person() {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P0_SO_VP != null) {
            String question = "Who " + P0_SO_VP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHO, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_SO_NP != null) {
            String question = "Who " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHO, GeneratedQuestion.SH_CHAIN));
            question = Request.getRequestPrefix() + " " + P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_VP != null) {
            String question = "Whom " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_VP + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHOM, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_NP != null) {
            String question = "Whose " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_NP + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHOSE, GeneratedQuestion.SH_CHAIN));
        }

    }

    private void generateQuestionSELECT_e_of_type_Place() {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P1_to_n_SO_PN_series != null) {
            String question = "What " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));
            question = Request.getRequestPrefix() + " " + P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_VP != null) {
            String question = "Where " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_VP + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHERE, GeneratedQuestion.SH_CHAIN));
        }

    }

    private void generateQuestionSELECT_e_of_type_Entity() {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P0_OS_VP != null) {
            String question = "What " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_VP + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));
        }
        if (P1_to_n_SO_PN_series != null) {
            String question = "What " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));
            question = Request.getRequestPrefix() + " " + P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_SO_VP != null) {
            String question = "What " + P0_SO_VP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));
        }

    }

    private void generateQuestionSELECT_e_of_type_Number() {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P1_to_n_SO_PN_series != null) {
            String question = "What " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));
            question = Request.getRequestPrefix() + " " + P0_SO_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_NP != null) {
            String question = "How " + P0_OS_NP + " is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_HOW_ADJ, GeneratedQuestion.SH_CHAIN));
        }
    }

    private void generateQuestionSELECT_e_of_type_Date() {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P0_OS_VP != null) {
            String question = "When did " + P1_to_n_SO_PN_series + " " + O_Final + " " + P0_OS_VP + "?";
        }
        if (P1_to_n_SO_PN_series != null) {
            String question = "What " + P0_SO_NP + " " + P1_to_n_SO_PN_series + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_WHAT, GeneratedQuestion.SH_CHAIN));
            question = Request.getRequestPrefix() + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, selectQuery, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_CHAIN));
        }
    }

    private void generateQuestionAsk_Correct() {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("is/are ")) {
            String question = "Is " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("is ")) {
            String question = "Is " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("are ")) {
            String question = "Are " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("was ")) {
            String question = "Was " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("were ")) {
            String question = "Were " + S0_Seed + " " + P0_SO_NP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        }

        if (P0_OS_NP != null && P0_OS_NP.startsWith("is/are ")) {
            P0_OS_NP = P0_OS_NP.replace("is/are ", "");
            String question = "Is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("is ")) {
            P0_OS_NP = P0_OS_NP.replace("is ", "");
            String question = "Is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("are ")) {
            P0_OS_NP = P0_OS_NP.replace("are ", "");
            String question = "Are " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("was ")) {
            P0_OS_NP = P0_OS_NP.replace("was ", "");
            String question = "Was " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("were ")) {
            P0_OS_NP = P0_OS_NP.replace("were ", "");
            String question = "Were " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + S0_Seed + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        }

        if (P0_SO_VP != null) {
            String question = "Does " + S0_Seed + " " + P0_SO_VP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_VP != null) {
            String question = "Does " + O_Final + " " + P0_OS_VP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_correct, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_CHAIN));
        }
    }

    private void generateQuestionAsk_Wrong() {

        if (missingPredicateRepresentation_s_o_NP) {
            return;
        }

        //Generate Question
        String s_o_NP_Auxiliary_Verb = "";

        if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("is/are ")) {
            String question = "Is " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("is ")) {
            String question = "Is " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("are ")) {
            String question = "Are " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("was ")) {
            String question = "Was " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P1_to_n_SO_PN_series != null && P1_to_n_SO_PN_series.startsWith("were ")) {
            String question = "Were " + somethingElseWithoutPrefix + " " + P0_OS_NP_without_verb + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        }

        if (P0_OS_NP != null && P0_OS_NP.startsWith("is/are ")) {
            P0_OS_NP = P0_OS_NP.replace("is/are ", "");
            String question = "Is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("is ")) {
            P0_OS_NP = P0_OS_NP.replace("is ", "");
            String question = "Is " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("are ")) {
            P0_OS_NP = P0_OS_NP.replace("are ", "");
            String question = "Are " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("was ")) {
            P0_OS_NP = P0_OS_NP.replace("was ", "");
            String question = "Was " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        } else if (P0_OS_NP != null && P0_OS_NP.startsWith("were ")) {
            P0_OS_NP = P0_OS_NP.replace("were ", "");
            String question = "Were " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_OS_NP + " " + somethingElseWithoutPrefix + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_CHAIN));
        }

        if (P0_SO_VP != null) {
            String question = "Does " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + " " + P0_SO_VP + " " + somethingElseWithoutPrefix + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_CHAIN));
        }
        if (P0_OS_VP != null) {
            String question = "Does " + somethingElseWithoutPrefix + " " + P0_OS_VP + " " + P1_to_n_SO_PN_series_without_verb + " " + O_Final + "?";
            allPossibleQuestions.add(new GeneratedQuestion(S0_withPrefix, S0_type_withPrefix, question, askQuery_wrong, chainGraph.toString(), chainGraph.getChain().size(), GeneratedQuestion.QT_YES_NO_DO, GeneratedQuestion.SH_CHAIN));
        }
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

}
