package system.components;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Database;
import offLine.kg_explorer.explorer.Explorer;
import offLine.kg_explorer.model.Predicate;
import offLine.kg_explorer.model.PredicateTripleExample;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class NLP_Pattern_Extractor {
    
    
    
    
    public static void main(String[] args) throws SQLException {
//        String dbpediaURL = "https://dbpedia.org/sparql";
//        Explorer explorer = KG_Settings.explorer;
//        
        Predicate_Extractor extractor = new Predicate_Extractor();
        ArrayList<Predicate> predicates = Database.getPredicates();
        int i = 0;
        for (Predicate predicate : predicates) {
            predicate.setTripleExamples(extractor.getOneTripleExample(predicate.getPredicateURI().trim(),
                        predicate.getPredicateContext().getSubjectType(), 
                        predicate.getPredicateContext().getObjectType(), 
                        predicate.getLabel(), 
                        20));
            try {
                
                System.out.print(i++ + "\t");
                predicate.print();
                Database.storePredicates_NLP_Representation(predicate, (ArrayList<PredicateTripleExample>) predicate.getTripleExamples());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
}
