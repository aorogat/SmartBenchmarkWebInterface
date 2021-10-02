package nl_generation;

import java.util.ArrayList;
import kg_explorer.ontology.KGOntology;
import kg_extractor.model.subgraph.SingleEdgeGraph;
import scrapping.model.PredicateNLRepresentation;
import scrapping.model.PredicatesLexicon;

public class SingleEdgeQuestion {

    public static ArrayList<String> generateQuestions(SingleEdgeGraph g) {
        String s = g.getTriplePattern().getSource().getValue();
        String o = g.getTriplePattern().getDestination().getValue();
        String p = g.getTriplePattern().getLabel().getValue();

        return generateGeneralQuestions(s, p, o, o, "");
    }

    public static ArrayList<String> generateGeneralQuestions(String S0, String P0, String O0, String last_O, String intermediateObjectsPredicates) {
        ArrayList<String> questions = new ArrayList<>();
        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P0, KGOntology.getType(S0), KGOntology.getType(O0));

        String objectReplacement = intermediateObjectsPredicates + last_O;

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

        String somethingElse = "Red Sea"; //replace s with other instance of the same type
        //Yes
        questions.add("Is " + S0 + " the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
        questions.add("Does " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP() + " " + S0 + "?");

        //NO
        questions.add("Is " + somethingElse + " the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
        questions.add("Does " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP() + " " + somethingElse + "?");

        return questions;
    }
}
