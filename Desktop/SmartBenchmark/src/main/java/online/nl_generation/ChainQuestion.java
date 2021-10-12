package online.nl_generation;

import java.util.ArrayList;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.subgraph.ChainGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;

public class ChainQuestion {
    public static ArrayList<GeneratedQuestion> generateQuestions(ChainGraph g)
    {
        String S0 = g.getChain().get(0).getSource().getValue(); //seed
        String P0 = g.getChain().get(0).getLabel().getValue();
        String O0 = g.getChain().get(0).getDestination().getValue();
        String O_Final = g.getChain().get(g.getChain().size()-1).getDestination().getValue(); //last object in the chain
        
        //Questions will be as single edge except that we replace the object by P`_0 ... P`_n O_n
        String p_series = "";
        for (int i=1; i<g.getChain().size(); i++) {
            TriplePattern triple = g.getChain().get(i);
            String s = triple.getSource().getValue();
            String p = triple.getLabel().getValue();
            String o = triple.getDestination().getValue();
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p,KGOntology.getType(s),KGOntology.getType(o));
            //NL representation of intermediate predicates
            p_series += "the " + predicateNL.getPredicate_s_O_NP() + " of "; 
        }
        
        return SingleEdgeQuestion.generateGeneralQuestions(g, S0, P0, O0, O_Final, p_series);
    }

}
