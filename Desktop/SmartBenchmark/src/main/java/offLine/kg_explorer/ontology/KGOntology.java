package offLine.kg_explorer.ontology;

import offLine.kg_explorer.explorer.SPARQL;
import settings.KG_Settings;

public class KGOntology 
{

    //Define ontology as tree
    
    public KGOntology() {
        //Load ontolog tree
    }
    
    
    public static String getType(String node)
    {
        return SPARQL.getType(settings.KG_Settings.explorer, node);
//        if(node.equals("Baltic Sea"))
//            return "place";
//        else if(node.equals("Bräkneån"))
//            return "river";
//        else if(node.equals("Barack Obama"))
//            return "person";
//        else if(node.equals("United States"))
//            return "place";
//        else if(node.equals("Sweden"))
//            return "country";
//        else if(node.equals("Northern Europe"))
//            return "place";
//        else if(node.equals("Denmark"))
//            return "country";
//         else if(node.equals("Germany"))
//            return "country";
//        
//        return "OntologyUnknownType";
    }
    
    public static boolean isSubtypeOf(String child, String parent)
    {
        if(child==null || parent==null)
            return false;
        if(parent.equals(child))
            return true;
        else 
            if(SPARQL.isASubtypeOf(KG_Settings.explorer, child, parent))
                return true;
        else
                return false;
                        
    }
    
    
}
