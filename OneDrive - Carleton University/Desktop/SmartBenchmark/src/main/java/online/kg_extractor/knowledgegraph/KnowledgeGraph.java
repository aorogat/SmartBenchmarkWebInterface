package online.kg_extractor.knowledgegraph;

import java.util.ArrayList;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.VariableSet;

/**
 *
 * @author aorogat
 */
public abstract class KnowledgeGraph {

    protected String name = "";
    protected static KnowledgeGraph instance = null;
    protected String queryString;
    protected String endpoint;
    protected String[] unwantedProperties;

    public abstract ArrayList<VariableSet> runQuery(String queryString);

    public abstract String[] getUnwantedProperties();

    public String getName() {
        return name;
    }

    public static KnowledgeGraph getInstance() {
        return instance;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getUnwantedPropertiesString() {
        String unwantedPropertiesString = "";
        for (String unwantedProperty : this.getUnwantedProperties()) {
            unwantedPropertiesString += "" + unwantedProperty + ", ";
        }
        unwantedPropertiesString = unwantedPropertiesString.substring(0, unwantedPropertiesString.length() - 2);
        return unwantedPropertiesString;
    }

}
