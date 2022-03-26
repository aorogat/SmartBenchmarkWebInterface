package system.components;

import database.Database;
import database.DatabaseIntializer;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aorogat
 */
public class SmartBench {

    public static void main(String[] args) {
        try {
            DatabaseIntializer.intialize();

            //OFFLINE
            //Step 1
//            Predicate_Extractor extractor = new Predicate_Extractor();
//            extractor.exploreAllPredicates();

            //Step 2
//            NLP_Pattern_Extractor.extractNLPPatterns();

            //Step 3
//            Predicate_Representation_Extractor.fill_from_Labels_VP_and_NP_S_O();
//            Predicate_Representation_Extractor.fill_from_Labels_VP_O_S();
//            Predicate_Representation_Extractor.fill_from_text_corpus_VP();
//            Predicate_Representation_Extractor.fill_from_text_corpus_NP();
            Database.populateLexicon();
            
            
            //Online
            ShapesGenerator.generateShapes(); //generate and save them and prune and save again
            
            
            
        } catch (IOException ex) {
            Logger.getLogger(SmartBench.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
