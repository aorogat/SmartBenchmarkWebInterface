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

    public static String name = "DBpedia";
    public static String url = "https://dbpedia.org/sparql";
    public static Explorer explorer = new DBpediaExplorer(url);
    public static DBpedia knowledgeGraph = new DBpedia(url);

    //Seed types
    public static String Person = "dbo:Person";
    public static String Place = "dbo:Place";
    public static String Number = "Number";
    public static String Date = "Date";

}
