package offLine.scrapping.model;

import java.io.IOException;
import java.util.ArrayList;
import database.Database;
import java.util.logging.Level;
import java.util.logging.Logger;
import offLine.kg_explorer.explorer.Explorer;
import offLine.kg_explorer.model.Predicate;
import offLine.kg_explorer.model.PredicateContext;
import offLine.kg_explorer.ontology.KGOntology;
import settings.Settings;
import system.components.Predicate_Representation_Extractor;

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

    public static PredicateNLRepresentation getPredicateNL(String predicate, String S_type, String O_type) {
        if (predicatesNL == null) {
            predicatesNL = Database.getPredicatesNLRepresentationLexicon();
        }
        for (PredicateNLRepresentation p : predicatesNL) {
            if (p.getPredicate() == null) {
                continue;
            }
            if (p.getPredicate().equals(predicate)
                    && S_type.equals(p.getSubject_type())
                    && O_type.equals(p.getObject_type())) {
                return p;
            }
        }
        
//        Predicate predicate1 = new Predicate(Settings.explorer);
//        predicate1.setLabel(predicate);
//        predicate1.setPredicate(predicate);
//        predicate1.setPredicateURI(predicate);
//        predicate1.setPredicateContext(new PredicateContext(S_type, O_type, 0));
//        Predicate_Representation_Extractor.fill_from_Labels_VP_and_NP_S_O(predicate1);
//        try {
//            Database.populateLexicon();
//        } catch (IOException ex) {
//            Logger.getLogger(PredicatesLexicon.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        predicatesNL = Database.getPredicatesNLRepresentationLexicon();
//        for (PredicateNLRepresentation p : predicatesNL) {
//            if (p.getPredicate() == null) {
//                continue;
//            }
//            if (p.getPredicate().equals(predicate)
//                    && S_type.equals(p.getSubject_type())
//                    && O_type.equals(p.getObject_type())) {
//                return p;
//            }
//        }
        return null;
    }

}
