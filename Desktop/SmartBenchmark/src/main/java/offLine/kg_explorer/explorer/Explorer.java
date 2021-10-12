package offLine.kg_explorer.explorer;

import java.util.ArrayList;
import java.util.List;
import offLine.kg_explorer.model.ListOfPredicates;
import offLine.kg_explorer.model.Predicate;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;
import online.kg_extractor.model.VariableSet;
import online.kg_extractor.model.subgraph.Graph;

/**
 *
 * @author aorogat
 */
public abstract class Explorer 
{
    
    public static KnowledgeGraph kg;
    protected static Explorer instance = null;
    public static String endpoint;
    public static ArrayList<VariableSet> predicates;
    public static ArrayList<VariableSet> predicatesTriples;
    public static ArrayList<Graph> result = new ArrayList<>();
    protected ArrayList<Predicate> predicateList = new ArrayList<>();
    
    public abstract ListOfPredicates explore(int from, int length);
    
    public abstract String removePrefix(String node);
     
}
