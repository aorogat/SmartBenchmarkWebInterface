/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offLine.kg_explorer.explorer;

import offLine.kg_explorer.model.ListOfPredicates;

/**
 *
 * @author aorogat
 */
public class Test {

    public static void main(String[] args) {
        String dbpediaURL = "https://dbpedia.org/sparql";
        DBpediaExplorer dBpediaExplorer = DBpediaExplorer.getInstance(dbpediaURL);
        
        
        ListOfPredicates predicats;
        int offset = 50;
        int from = 0;
        boolean firstIteration = true;
        
        dBpediaExplorer.getPredicatesContext("dbo:riverMouth");
        
//        do {
//            predicats = dBpediaExplorer.explore(from, offset);
//            if (firstIteration) {
//                predicats.printHeader();
//            }
//            predicats.print();
//            from += offset;
//            firstIteration = false;
//            System.gc(); //force Java garbage collection 
//        } while (predicats.getPredicates().size() > 0);
    }
}
