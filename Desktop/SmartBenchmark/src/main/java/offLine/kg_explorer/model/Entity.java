package offLine.kg_explorer.model;

import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Explorer;

/**
 *
 * @author aorogat
 */
public class Entity {

    public Entity() {
    }

    public static String getDirectType(Explorer explorer, String entity) {
        //run a quern on the KG of the explorer to get the entity type
        String kgFilteration = "";
        if (explorer instanceof DBpediaExplorer) 
            kgFilteration = "FILTER strstarts(str(?directType), str(dbo:))";
            
        String query = "";
        try {
            query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                    + "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                    + "  PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                    + "  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                    + "  PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                    + "  SELECT ?directType { \n"
                    + "    " + entity + " rdf:type ?directType .\n"
                    + "    FILTER NOT EXISTS {\n"
                    + "      " + entity + " rdf:type ?type .\n"
                    + "      ?type rdfs:subClassOf ?directType .\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type owl:equivalentClass ?directType .\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      ?directType rdfs:subClassOf ?superType .\n"
                    + "    }.\n"
                    + "    " + kgFilteration + "\n"
                    + "  }";
            //query = "SELECT ?type WHERE { " + entity + " rdf:type ?type. FILTER strstarts(str(?type), str(dbo:))}";
            explorer.predicatesTriples = explorer.kg.runQuery(query);
            return explorer.removePrefix(explorer.predicatesTriples.get(0).getVariables().get(0).toString());
        } catch (Exception e) {
            try {
                query = "SELECT ?type WHERE { " + entity + " rdf:type ?type.}";
                explorer.predicatesTriples = explorer.kg.runQuery(query);
                return explorer.removePrefix(explorer.predicatesTriples.get(0).getVariables().get(0).toString());
            } catch (Exception ex) {
                return "";
            }
        }
    }
}
