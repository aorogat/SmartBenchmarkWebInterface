
package scrapping.model;

import java.util.ArrayList;
import kg_explorer.ontology.KGOntology;

public class PredicatesLexicon {
    public static ArrayList<PredicateNLRepresentation> predicatesNL;
    public static PredicateNLRepresentation predicateNL;

    public PredicatesLexicon() {
        predicatesNL = new ArrayList<>();
        //Baltic Sea___riverMouth___Bräkneån
        // S(place) is the river mouth of O  //NP
        // O eventually flows into S(sea)  //VP
        // O is the tributary of S(sea)   //NP
        // S destinate O  //VP
        predicatesNL.add(new PredicateNLRepresentation("riverMouth", "place", "river", 
                    "river mouth", "eventually flows into",
                    "tributary", "destinate"));
        
        predicatesNL.add(new PredicateNLRepresentation("mainRiver", "river", "country", 
                    "main river", "run through",
                    "", "country of"));
        
        // S is the president of O
        // O leaded by S
        // O is the country of S(Obama)
        // S leaded O
        predicatesNL.add(new PredicateNLRepresentation("lead", "person", "place", "president", "leaded by",
        "leaded country","leaded"));
    }
    
    
    
    public static PredicateNLRepresentation getPredicateNL(String predicate, String S_type, String O_type)
    {
        for(PredicateNLRepresentation p: predicatesNL)
        {
            if(p.getPredicate().equals(predicate) && 
               KGOntology.isSubtypeOf(S_type, p.getSubject_type()) &&
               KGOntology.isSubtypeOf(O_type, p.getObject_type()))
                return p;
        }
        return null;
    }
    
    
}
