/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offLine.kg_explorer.explorer;

import static org.apache.jena.assembler.JA.FileManager;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;

/**
 *
 * @author ayaab
 */
public class DBpediaLocally {
/**
         * The Constant tdbDirectory.
         */
    public static final String tdbDirectory = "C:\\TDBLoadGeoCoordinatesAndLabels";

    /**
     * The Constant dbdump0.
     */
    public static final String dbdump0 = "C:\\Users\\Public\\Documents\\TDB\\dbpedia_3.8\\dbpedia_3.8.owl";

    /**
     * The Constant dbdump1.
     */
    public static final String dbdump1 = "C:\\Users\\Public\\Documents\\TDB\\geo_coordinates_en\\geo_coordinates_en.nt";

    public static void main(String[] args) {
        


        Dataset tdbModel = TDBFactory.createDataset(tdbDirectory);


/*Incrementally read data to the Model, once per run , RAM > 6 GB*/
//    FileManager.get()
//
//    .readModel( tdbModel, dbdump0);
//    FileManager.get()
//
//    .readModel( tdbModel, dbdump1, "N-TRIPLES");
//    FileManager.get ()
//
//    .readModel( tdbModel, dbdump2, "N-TRIPLES");
//    FileManager.get ()
//
//    .readModel( tdbModel, dbdump3, "N-TRIPLES");
//    FileManager.get ()
//
//    .readModel( tdbModel, dbdump4, "N-TRIPLES");
//    FileManager.get ()
//
//    .readModel( tdbModel, dbdump5, "N-TRIPLES");
//    FileManager.get ()
//
//    .readModel( tdbModel, dbdump6, "N-TRIPLES");
//    tdbModel.close ();
}
}
