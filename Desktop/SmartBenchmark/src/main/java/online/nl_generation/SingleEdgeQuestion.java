package online.nl_generation;

import java.util.ArrayList;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.subgraph.ChainGraph;
import online.kg_extractor.model.subgraph.Graph;

public class SingleEdgeQuestion {

    public static String somethingElse;
    public static String somethingElseWithoutPrefix;
    
    public static ArrayList<GeneratedQuestion> generateQuestions(SingleEdgeGraph g) {
        String s = g.getTriplePattern().getSource().getValue();
        String o = g.getTriplePattern().getDestination().getValue();
        String p = g.getTriplePattern().getLabel().getValue();
        
        

        return generateGeneralQuestions(g, s, p, o, o, "");
    }

    public static ArrayList<GeneratedQuestion> generateGeneralQuestions(Graph g, String S0, String P0, String O0, String last_O, String intermediateObjectsPredicates) {
        ArrayList<GeneratedQuestion> generatedQuestions = new ArrayList<>();
        
        //Add SELECT Questions
        generatedQuestions.add(new GeneratedQuestion(
                generateSELECTQuestions(S0, P0, O0, last_O, intermediateObjectsPredicates), 
                generateSELECTQuery(g)));
        
        
        //Add ASK Questions
        generatedQuestions.add(new GeneratedQuestion(
                generateASKWithCorrectAnswerQuestions(S0, P0, O0, last_O, intermediateObjectsPredicates), 
                generateASKWithCorrectAnswerQuery(g)));
        
        
        //Add ASK Questions with Incorrect answers
        generatedQuestions.add(new GeneratedQuestion(
                generateASKWithIncorrectAnswerQuestions(S0, P0, O0, last_O, intermediateObjectsPredicates), 
                generateASKWithIncorrectAnswerQuery(g)));
        
//        ArrayList<String> questions = new ArrayList<>();
//
//        questions.addAll(generateSELECTQuestions(S0, P0, O0, last_O, intermediateObjectsPredicates));
//
//        
//        System.out.println(generateSELECTQuery(g));
//        System.out.println(generateASKWithCorrectAnswerQuery(g));
//        System.out.println(generateASKWithIncorrectAnswerQuery(g));
//        
//        
//        questions.addAll(generateASKWithCorrectAnswerQuestions(S0, P0, O0, last_O, intermediateObjectsPredicates));
//        questions.addAll(generateASKWithIncorrectAnswerQuestions(S0, P0, O0, last_O, intermediateObjectsPredicates));

        return generatedQuestions;
    }

    public static String generateSELECTQuery(Graph g) {
        if (g instanceof SingleEdgeGraph) {
            SingleEdgeGraph singleEdgeGraph = (SingleEdgeGraph) g;
            String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern().replace(singleEdgeGraph.getTriplePattern().getSource().getValueWithPrefix(), "?Seed") + " .";
            return "SELECT DISTINCT ?Seed WHERE{\n\t" + triple + "\n}";
        }
        else if (g instanceof ChainGraph) {
            ChainGraph chainGraph = (ChainGraph) g;
            String triples = "";
            ArrayList<TriplePattern> chain = chainGraph.getChain();
            for (TriplePattern triple : chain) {
                triples +=  "\n\t" + triple.toQueryTriplePattern() + ". ";
            }
            triples = triples.replace(chainGraph.getChain().get(0).getSource().getValueWithPrefix(), "?Seed");
            int i = 0;
            for (TriplePattern triple : chain) {
                triples = triples.replace(chainGraph.getChain().get(i).getSource().getValueWithPrefix(), "?S"+i++);
            }
            return "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        }
        return "";
    }

    public static ArrayList<String> generateSELECTQuestions(String S0, String P0, String O0, String last_O, String intermediateObjectsPredicates) {

        //Get NL representation for predicates
        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P0, KGOntology.getType(S0), KGOntology.getType(O0));

        String objectReplacement = intermediateObjectsPredicates + last_O;

        GeneratedQuestion generatedQuestion = new GeneratedQuestion();
        ArrayList<String> questions = new ArrayList<>();
        String query = generatedQuestion.getQuery();

        if (KGOntology.isSubtypeOf(predicateNL.getSubject_type(), "place")) {
            questions.add("What is the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
            questions.add("Where " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP() + "?");
            //ignore this as it is used to ask about o instead of s
            //questions.add("What is the " + predicateNL.getPredicate_o_s_NP() + " of " + s + "?");
        } else if (KGOntology.isSubtypeOf(predicateNL.getSubject_type(), "person")) {
            questions.add("Who is the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
            questions.add("Whom " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP() + "?");
            questions.add("Whose the " + predicateNL.getPredicate_o_s_NP() + " of " + objectReplacement + "?");
        }

        //if seed is place S=Baltic Sea, O=Braknean, P=rivermouth, we ask about S
        //("riverMouth", "sea", "river", "river mouth", "eventually flows into")
        // add to each branch:::: questions.add("What is the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");
        //Request
        questions.add(Request.getRequestPrefix() + " the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
        //questions.add(Request.getRequestPrefix() + ", what is the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");

        return questions;
    }

    
    public static String generateASKWithCorrectAnswerQuery(Graph g) {
        if (g instanceof SingleEdgeGraph) {
            SingleEdgeGraph singleEdgeGraph = (SingleEdgeGraph) g;
            String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern() + " .";
            return "ASK WHERE{\n\t" + triple + "\n}";
        }
        else if (g instanceof ChainGraph) {
            ChainGraph chainGraph = (ChainGraph) g;
            String triples = "";
            ArrayList<TriplePattern> chain = chainGraph.getChain();
            for (TriplePattern triple : chain) {
                triples +=  "\n\t" + triple.toQueryTriplePattern() + ". ";
            }
            
            int i = 0;
            for (TriplePattern triple : chain) {
                if(i==0){i++;continue;}
                triples = triples.replace(chainGraph.getChain().get(i).getSource().getValueWithPrefix(), "?S"+i++);
            }
            return "ASK WHERE{" + triples + "\n}";
        }
        return "";
    }
    
    public static ArrayList<String> generateASKWithCorrectAnswerQuestions(String S0, String P0, String O0, String last_O, String intermediateObjectsPredicates) {
        ArrayList<String> questions = new ArrayList<>();
        //Get NL representation for predicates
        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P0, KGOntology.getType(S0), KGOntology.getType(O0));

        String objectReplacement = intermediateObjectsPredicates + last_O;

        //Yes
        questions.add("Is " + S0 + " the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
        questions.add("Does " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP() + " " + S0 + "?");

        return questions;
    }

    public static String generateASKWithIncorrectAnswerQuery(Graph g) {
        somethingElse = "<http://dbpedia.org/resource/Red_Sea>"; //replace s with other instance of the same type
        somethingElseWithoutPrefix = "Red Sea";
        
        if (g instanceof SingleEdgeGraph) {
            SingleEdgeGraph singleEdgeGraph = (SingleEdgeGraph) g;
            String triple = singleEdgeGraph.getTriplePattern().toQueryTriplePattern()
                    .replace(singleEdgeGraph.getTriplePattern().getSource().getValueWithPrefix(), somethingElse) + " .";
            return "ASK WHERE{\n\t" + triple + "\n}";
        }
        else if (g instanceof ChainGraph) {
            ChainGraph chainGraph = (ChainGraph) g;
            String triples = "";
            ArrayList<TriplePattern> chain = chainGraph.getChain();
            for (TriplePattern triple : chain) {
                triples +=  "\n\t" + triple.toQueryTriplePattern() + ". ";
            }
            triples = triples.replace(chain.get(0).getSource().getValueWithPrefix(), somethingElse);
            int i = 0;
            for (TriplePattern triple : chain) {
                if(i==0){i++;continue;}
                triples = triples.replace(chainGraph.getChain().get(i).getSource().getValueWithPrefix(), "?S"+i++);
            }
            return "ASK WHERE{" + triples + "\n}";
        }
        return "";
    }
    
    public static ArrayList<String> generateASKWithIncorrectAnswerQuestions(String S0, String P0, String O0, String last_O, String intermediateObjectsPredicates) {
        somethingElse = "<http://dbpedia.org/resource/Red_Sea>"; //replace s with other instance of the same type
        somethingElseWithoutPrefix = "Red Sea";
        ArrayList<String> questions = new ArrayList<>();
        //Get NL representation for predicates
        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P0, KGOntology.getType(S0), KGOntology.getType(O0));

        String objectReplacement = intermediateObjectsPredicates + last_O;

        

        //NO
        questions.add("Is " + somethingElseWithoutPrefix + " the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
        questions.add("Does " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP() + " " + somethingElseWithoutPrefix + "?");

        return questions;
    }
}
