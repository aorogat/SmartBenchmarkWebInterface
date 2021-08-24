/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kg_explorer.explorer;

import java.util.ArrayList;
import kg_explorer.model.ListOfPredicates;
import kg_explorer.model.Predicate;
import kg_extractor.knowledgegraph.DBpedia;

/**
 *
 * @author aorogat
 */
public class Test {
    public static void main(String[] args) {
        String dbpediaURL = "https://dbpedia.org/sparql";
        DBpediaExplorer dBpediaExplorer = DBpediaExplorer.getInstance(dbpediaURL);
        
        ListOfPredicates predicats =  dBpediaExplorer.explore();
        predicats.print();
    }
}
