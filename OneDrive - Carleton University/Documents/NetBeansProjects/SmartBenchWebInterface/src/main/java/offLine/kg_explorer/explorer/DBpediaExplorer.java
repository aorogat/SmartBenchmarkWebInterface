package offLine.kg_explorer.explorer;

import database.Database;

/**
 *
 * @author aorogat
 */
public class DBpediaExplorer extends Explorer {

    

    public DBpediaExplorer(String url) {
        super();
        kg = DBpedia.getInstance(url);
        endpoint = kg.getEndpoint();
        Database.connect();
    }

    public static DBpediaExplorer getInstance(String url) {
        if (instance == null) {
            instance = new DBpediaExplorer(url);
            return (DBpediaExplorer) instance;
        } else {
            return (DBpediaExplorer) instance;
        }
    }

}
