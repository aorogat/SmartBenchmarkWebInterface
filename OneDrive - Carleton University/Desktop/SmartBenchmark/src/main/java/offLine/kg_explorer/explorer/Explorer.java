package offLine.kg_explorer.explorer;

import java.util.ArrayList;
import java.util.List;
import offLine.kg_explorer.model.ListOfPredicates;
import offLine.kg_explorer.model.Predicate;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;
import online.kg_extractor.model.VariableSet;
import online.kg_extractor.model.subgraph.Graph;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public abstract class Explorer {

    public static KnowledgeGraph kg;
    protected static Explorer instance = null;
    public static String endpoint;
    public static ArrayList<VariableSet> predicatesVariableSet;
    public static ArrayList<VariableSet> predicatesTriplesVarSets;
    public static ArrayList<Graph> result = new ArrayList<>();
    protected ArrayList<Predicate> predicateList = new ArrayList<>();

    public abstract ListOfPredicates explore(int from, int length);

    public String removePrefix(String node) {
        if (node == null) {
            return node;
        }
        
        if(node.equals("true")||node.equals("false")||node.equals(KG_Settings.Number)||node.equals(KG_Settings.Date))
            return node;

        String s = "";
        s = SPARQL.getNodeLabel(this, node);
         
        return s;
    }

    public Explorer() {
        predicatesVariableSet = new ArrayList<>();
    }

}
