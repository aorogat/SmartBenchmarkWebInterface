package offLine.nlp;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Database;
import offLine.kg_explorer.model.ListOfPredicates;
import offLine.kg_explorer.model.Predicate;
import offLine.kg_explorer.model.PredicateTripleExample;
import online.kg_extractor.knowledgegraph.DBpedia;

/**
 *
 * @author aorogat
 */
public class NLP_Pattern_Extractor {
    
    
    
    
    public static void main(String[] args) {
        String dbpediaURL = "https://dbpedia.org/sparql";
        DBpediaExplorer dBpediaExplorer = DBpediaExplorer.getInstance(dbpediaURL);
        
        ArrayList<Predicate> predicates = Database.getPredicates();
        for (Predicate predicate : predicates) {
            predicate.setTripleExamples(dBpediaExplorer.getOneTripleExample(predicate.getPredicateURI().trim(),
                        predicate.getPredicateContext().getSubjectType(), 
                        predicate.getPredicateContext().getObjectType(), 
                        predicate.getLabel(), 
                        5));
            Database.storePredicates_NLP_Representation(predicate, (ArrayList<PredicateTripleExample>) predicate.getTripleExamples());
        }
    }
    
    
}
