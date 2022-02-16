package settings;

import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Explorer;
import online.kg_extractor.knowledgegraph.DBpedia;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;

/**
 *
 * @author aorogat
 */
public class KG_Settings {
    
    public static final byte LABEL_NP_SO = 1;
    public static final byte LABEL_NP_OS = 2;
    
    public static final int maxAnswerCardinalityAllowed = 500;
    public static final int SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value

    public static String name = "DBpedia";
    public static String url = "https://dbpedia.org/sparql";
    public static DBpediaExplorer explorer = new DBpediaExplorer(url);
    public static DBpedia knowledgeGraph = new DBpedia(url);
    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)

    //Seed types
    public static String Person = "dbo:Person";
    public static String Place = "dbo:Place";
    public static String Number = "Number";
    public static String Date = "Date";
    
    
    public static String popularityFilter = " ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
     public static String popularityORDER = " ORDER BY DESC(?len)\n ";  //used in SPARQL Class

}
