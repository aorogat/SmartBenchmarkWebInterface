package nl_generation;

import java.util.ArrayList;
import kg_explorer.ontology.KGOntology;
import kg_extractor.model.subgraph.SingleEdgeGraph;
import scrapping.model.PredicateNLRepresentation;
import scrapping.model.PredicatesLexicon;

public class SingleEdgeQuestion {
    public static ArrayList<String> generateQuestions(SingleEdgeGraph g)
    {
        String s = g.getTriplePattern().getSource().getValue();
        String o = g.getTriplePattern().getDestination().getValue();
        String p = g.getTriplePattern().getLabel().getValue();
        
//        System.out.println("s: " + s);
//        System.out.println("o: " + o);
//        System.out.println("p: " + p);
        
        ArrayList<String> questions = new ArrayList<>();
        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p,KGOntology.getType(s),KGOntology.getType(o));
        
        
        if(KGOntology.isSubtypeOf(predicateNL.getSubject_type(), "place"))
        {
            questions.add("What is the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");
            questions.add("Where " + o + " " + predicateNL.getPredicate_o_s_VP()+ "?");
            //ignore this as it is used to ask about o instead of s
            //questions.add("What is the " + predicateNL.getPredicate_o_s_NP() + " of " + s + "?");
        }
        else if(KGOntology.isSubtypeOf(predicateNL.getSubject_type(), "person"))
        {
            questions.add("Who is the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");
            questions.add("Whom " + o + " " + predicateNL.getPredicate_o_s_VP()+ "?");
            questions.add("Whose the " + predicateNL.getPredicate_o_s_NP() + " of " + o + "?");
        }
        
        //if seed is place S=Baltic Sea, O=Braknean, P=rivermouth, we ask about S
        //("riverMouth", "sea", "river", "river mouth", "eventually flows into")
       // add to each branch:::: questions.add("What is the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");
        
        //Request
        questions.add(Request.getRequestPrefix() + " the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");
        //questions.add(Request.getRequestPrefix() + ", what is the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");
        
        String somethingElse = "Red Sea"; //replace s with other instance of the same type
        //Yes
        questions.add("Is " + s + " the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");
        questions.add("Does " + o + " " + predicateNL.getPredicate_o_s_VP()+ " " + s + "?");
        
        //NO
        questions.add("Is " + somethingElse + " the " + predicateNL.getPredicate_s_O_NP() + " of " + o + "?");
        questions.add("Does " + o + " " + predicateNL.getPredicate_o_s_VP()+ " " + somethingElse + "?");
        
        
        return questions;
    }
}
