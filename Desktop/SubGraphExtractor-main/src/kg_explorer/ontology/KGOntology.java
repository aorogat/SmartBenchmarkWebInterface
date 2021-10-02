package kg_explorer.ontology;

public class KGOntology 
{

    //Define ontology as tree
    
    public KGOntology() {
        //Load ontolog tree
    }
    
    
    public static String getType(String node)
    {
        if(node.equals(node.equals("Baltic Sea")))
            return "place";
        else if(node.equals(node.equals("Bräkneån")))
            return "place";
        else if(node.equals(node.equals("Barack Obama")))
            return "person";
        else if(node.equals(node.equals("United States")))
            return "place";
        return "";
    }
    
    public static boolean isSubtypeOf(String child, String parent)
    {
        if(parent.equals("person")){
            if(child.equals("writer"))
                return true;
            else if(child.equals("instructor"))
                return true;
        }
        else if(parent.equals("place")){
            if(child.equals("river"))
                return true;
            else if(child.equals("sea"))
                return true;
        }
        return true;
    }
    
    
}
