package kg_explorer.model;

import kg_explorer.explorer.Explorer;

/**
 *
 * @author aorogat
 */
public class Entity {

    public Entity() {
    }
    
    public static String getType(Explorer explorer, String entity)
    {
        //run a quern on the KG of the explorer to get the entity type
        String query = "";
        //get weights
        try {
            query = "SELECT ?type WHERE { "+entity+" rdf:type ?type. FILTER strstarts(str(?type), str(dbo:))}";
            explorer.predicatesTriples = explorer.kg.runQuery(query);
            return explorer.removePrefix(explorer.predicatesTriples.get(0).getVariables().get(0).toString());
        } catch (Exception e) {
            return "";
        }
    }
}
