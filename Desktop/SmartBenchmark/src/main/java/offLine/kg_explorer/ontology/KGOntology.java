package offLine.kg_explorer.ontology;

public class KGOntology 
{

    //Define ontology as tree
    
    public KGOntology() {
        //Load ontolog tree
    }
    
    
    public static String getType(String node)
    {
        if(node.equals("Baltic Sea"))
            return "place";
        else if(node.equals("Bräkneån"))
            return "river";
        else if(node.equals("Barack Obama"))
            return "person";
        else if(node.equals("United States"))
            return "place";
        else if(node.equals("Sweden"))
            return "country";
        else if(node.equals("Northern Europe"))
            return "place";
        else if(node.equals("Denmark"))
            return "country";
         else if(node.equals("Germany"))
            return "country";
        
        return "OntologyUnknownType";
    }
    
    public static boolean isSubtypeOf(String child, String parent)
    {
        if(parent.equals(child))
            return true;
        else if(parent.equals("person")){
            if(child.equals("writer"))
                return true;
            else if(child.equals("instructor"))
                return true;
            else if(child.equals("person"))
                return true;
        }
        else if(parent.equals("place")){
            if(child.equals("river"))
                return true;
            else if(child.equals("sea"))
                return true;
            else if(child.equals("place"))
                return true;
            else if(child.equals("country"))
                return true;
        }
        return false;
    }
    
    
}
