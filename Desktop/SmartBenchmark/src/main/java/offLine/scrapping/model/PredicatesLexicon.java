
package offLine.scrapping.model;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.Database;
import offLine.kg_explorer.ontology.KGOntology;

public class PredicatesLexicon {
    public static ArrayList<PredicateNLRepresentation> predicatesNL;
    public static PredicateNLRepresentation predicateNL;

    public PredicatesLexicon() {
        //predicatesNL = new ArrayList<>();
        predicatesNL = Database.getPredicatesNLRepresentationLexicon();
        //Baltic Sea___riverMouth___Bräkneån
        // S(sea) is the river mouth of O(river)  //NP
        // O(river) eventually flows into S(sea)  //VP
        // O(river) is the tributary of S(sea)   //NP
        // S(sea) destinate O(river)  //VP
//        predicatesNL.add(new PredicateNLRepresentation("riverMouth", "place", "river", 
//                    "river mouth", "eventually flows into",
//                    "tributary", "destinate"));
//        
//        predicatesNL.add(new PredicateNLRepresentation("mainRiver", "river", "country", 
//                    "main river", "run through",
//                    "", "country of"));
//        predicatesNL.add(new PredicateNLRepresentation("largestCountry", "country", "place", 
//                    "largest country", "located in",
//                    "", ""));
//        
//        predicatesNL.add(new PredicateNLRepresentation("enclosedBy", "place", "country", 
//                    "edge", "encloses",
//                    "", "enclosed by"));
        
        
        // S is the president of O
        // O leaded by S
        // O is the country of S(Obama)
        // S leaded O
//        predicatesNL.add(new PredicateNLRepresentation("lead", "person", "place", "president", "leaded by",
//        "leaded country","leaded"));
    }
    
    
    
    public static PredicateNLRepresentation getPredicateNL(String predicate, String S_type, String O_type)
    {
        if(predicatesNL==null)
            predicatesNL = Database.getPredicatesNLRepresentationLexicon();
        for(PredicateNLRepresentation p: predicatesNL)
        {
            if(p.getPredicate()==null)
                continue;
            if(p.getPredicate().equals(predicate) && 
               S_type.equals(p.getSubject_type()) &&
               O_type.equals(p.getObject_type()))
                return p;
        }
        return null;
    }
    
    
}
