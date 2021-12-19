package settings;

import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Explorer;

/**
 *
 * @author aorogat
 */
public class KG_Settings 
{
    public static String name = "DBpedia";
    public static String url = "https://dbpedia.org/sparql";
    public static Explorer explorer = new DBpediaExplorer(url);
    
}
