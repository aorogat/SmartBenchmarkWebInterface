package nl_generation;

import java.util.ArrayList;
import kg_explorer.ontology.KGOntology;
import kg_extractor.model.TriplePattern;
import kg_extractor.model.subgraph.ChainGraph;
import scrapping.model.PredicateNLRepresentation;
import scrapping.model.PredicatesLexicon;

public class chainQuestion {
    public static ArrayList<String> generateQuestions(ChainGraph g)
    {
        String S0 = g.getChain().get(0).getSource().getValue(); //seed
        String P0 = g.getChain().get(0).getLabel().getValue();
        String O0 = g.getChain().get(0).getLabel().getValue();
        String O_Final = g.getChain().get(g.getChain().size()-1).getDestination().getValue(); //last object in the chain
        
        //Questions will be as single edge except that we replace the object by P`_0 ... P`_n O_n
        String p_series = "";
        for (int i=1; i<g.getChain().size(); i++) {
            TriplePattern triple = g.getChain().get(i);
            String s = triple.getSource().getValue();
            String p = triple.getLabel().getValue();
            String o = triple.getDestination().getValue();
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p,KGOntology.getType(s),KGOntology.getType(o));
            
            p_series += "the " + predicateNL.getPredicate_s_O_NP() + " of "; 
        }
        
        //This is the string that replace o in single edge questions
        String objectReplacement = p_series + O_Final;
        
        
        ArrayList<String> questions = new ArrayList<>();
        PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(P0,KGOntology.getType(S0),KGOntology.getType(O0));
        
        
        if(KGOntology.isSubtypeOf(predicateNL.getSubject_type(), "place"))
        {
            questions.add("What is the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
            questions.add("Where " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP()+ "?");
            //ignore this as it is used to ask about o instead of s
            //questions.add("What is the " + predicateNL.getPredicate_o_s_NP() + " of " + s + "?");
        }
        else if(KGOntology.isSubtypeOf(predicateNL.getSubject_type(), "person"))
        {
            questions.add("Who is the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
            questions.add("Whom " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP()+ "?");
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
        questions.add("Does " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP()+ " " + S0 + "?");
        
        //NO
        questions.add("Is " + somethingElse + " the " + predicateNL.getPredicate_s_O_NP() + " of " + objectReplacement + "?");
        questions.add("Does " + objectReplacement + " " + predicateNL.getPredicate_o_s_VP()+ " " + somethingElse + "?");
        
        
        return questions;
    }
}
