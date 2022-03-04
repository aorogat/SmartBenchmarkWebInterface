package settings;

import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Explorer;
import offLine.kg_explorer.explorer.DBpedia;
import offLine.kg_explorer.explorer.GEOExplorer;
import offLine.kg_explorer.explorer.KnowledgeGraph;
import offLine.kg_explorer.explorer.MAKG;
import offLine.kg_explorer.explorer.MAKGExplorer;

/**
 *
 * @author aorogat
 */
public class Settings {
    //DBpedia
//    public static String benchmarkName = "Smart_1";
//    
//    public static String databaseName = "dbpedia";
//    public static String databaseURL = "jdbc:postgresql://localhost:5432/";
//    public static String databaseUser = "postgres";
//    public static String databasePassword = "admin";
//    
//    
//    public static String requiredTypePrefix = "dbo:";
//    public static String unwantedTypes = "dbo:Agent, dbo:Settlement";
//    
//    
//    public static final byte LABEL_NP_SO = 1;
//    public static final byte LABEL_NP_OS = 2;
//    
//    public static final int maxAnswerCardinalityAllowed = 500;
//    public static final int SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value
//
//    public static String name = "DBpedia";
//    public static String url = "https://dbpedia.org/sparql";
//    public static String default_graph_uri = "http%3A%2F%2Fdbpedia.org";
//    public static DBpediaExplorer explorer = new DBpediaExplorer(url);
//    public static DBpedia knowledgeGraph = new DBpedia(url);
//    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
//
//    //Seed types
//    public static String Person = "dbo:Person";
//    public static String Place = "dbo:Place";
//    public static String Number = "Number";
//    public static String Date = "Date";
//    
//    
//    public static String popularityFilter = " ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
//    public static String popularityORDER = " ORDER BY DESC(?len)\n ";  //used in SPARQL Class
    
    
    
    
    //MAKG
//    public static String benchmarkName = "MAKG_Smart_1";
//    
//    public static String databaseName = "makg";
//    public static String databaseURL = "jdbc:postgresql://localhost:5432/";
//    public static String databaseUser = "postgres";
//    public static String databasePassword = "admin";
//    
//    
//    public static String requiredTypePrefix = null;//"dbo:";
//    public static String unwantedTypes = null;//"dbo:Agent, dbo:Settlement";
//    
//    
//    public static final byte LABEL_NP_SO = 1;
//    public static final byte LABEL_NP_OS = 2;
//    
//    public static final int maxAnswerCardinalityAllowed = 500;
//    public static final int SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value
//
//    public static String name = "MAKG";
//    public static String url = "https://makg.org/sparql";
//    public static String default_graph_uri = "";
//    public static MAKGExplorer explorer = new MAKGExplorer(url);
//    public static MAKG knowledgeGraph = new MAKG(url);
//    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)
//
//    //Seed types
//    public static String Person = "<http://mag.graph/class/Author>";
//    public static String Place = "<http://mag.graph/class/ConferenceInstance>";
//    public static String Number = "Number";
//    public static String Date = "Date";
//    public static String Literal = "Literal";
//    
//    
//    public static String popularityFilter = "";//" ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
//    public static String popularityORDER = "";//" ORDER BY DESC(?len)\n ";  //used in SPARQL Class
//    
    
    
    
    
    
    //Geodata
    public static String benchmarkName = "Geo_Smart_1";
    
    public static String databaseName = "geodata";
    public static String databaseURL = "jdbc:postgresql://localhost:5432/";
    public static String databaseUser = "postgres";
    public static String databasePassword = "admin";
    
    
    public static String requiredTypePrefix = null;//"dbo:";
    public static String unwantedTypes = null;//"dbo:Agent, dbo:Settlement";
    
    
    public static final byte LABEL_NP_SO = 1;
    public static final byte LABEL_NP_OS = 2;
    
    public static final int maxAnswerCardinalityAllowed = 500;
    public static final int SET_QUESTION_TOP_ENTITY = 5; //get random value between 1 and this value

    public static String name = "GEO";
    public static String url = "http://linkedgeodata.org/sparql/";
    public static String default_graph_uri = "";
    public static GEOExplorer explorer = new GEOExplorer(url);
    public static MAKG knowledgeGraph = new MAKG(url);
    public static int Triple_NP_Direction = LABEL_NP_OS; //This means noun labels in dbpedia represent the fact (O is label of S)

    //Seed types
    public static String Person = "<http://mag.graph/class/Author>";
    public static String Place = "<http://linkedgeodata.org/ontology/Amenity>";
    public static String Number = "Number";
    public static String Date = "Date";
    public static String Literal = "Literal";
    
    
    public static String popularityFilter = "";//" ?s dbo:wikiPageLength ?len. ";  //used in SPARQL Class
    public static String popularityORDER = "";//" ORDER BY DESC(?len)\n ";  //used in SPARQL Class
    
    

}
