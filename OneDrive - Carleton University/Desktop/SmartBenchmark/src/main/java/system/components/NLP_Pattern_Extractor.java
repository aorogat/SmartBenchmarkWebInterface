package system.components;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import offLine.kg_explorer.explorer.DBpediaExplorer;
import database.Database;
import offLine.kg_explorer.explorer.Explorer;
import offLine.kg_explorer.model.Predicate;
import offLine.kg_explorer.model.PredicateTripleExample;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class NLP_Pattern_Extractor {
    
    
    
    
    public static void main(String[] args) throws SQLException {
        extractNLPPatterns();
    }
    
    public static void extractNLPPatterns(){
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
