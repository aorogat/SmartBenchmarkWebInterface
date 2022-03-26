package offLine.kg_explorer.explorer;

/**
 *
 * @author aorogat
 */
//Singleton Class
public class MAKG extends KnowledgeGraph {

    public MAKG(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
        if (instance == null) {
            instance = new MAKG(endpoint);
            return (MAKG) instance;
        } else {
            return (MAKG) instance;
        }
    }

    @Override
    public String[] getUnwantedProperties() {
        unwantedProperties = new String[]{
            "rdfs:label",
            "rdfs:subPropertyOf",
            "rdfs:comment",
            "rdfs:label",
            "rdfs:seeAlso",
            "rdf:type",
            "rdfs:subClassOf",
            "rdfs:range",
            "rdfs:domain",
           
            
            "owl:sameAs",
            "owl:equivalentProperty",
            "owl:differentFrom",
            "owl:versionInfo",
            "owl:disjointWith",
            "owl:equivalentClass",
            
            "foaf:name",
            "foaf:primaryTopic",
        
            "<http://purl.org/linguistics/gold/hypernym>",
            "<http://xmlns.com/foaf/0.1/name>",
            "<http://purl.org/dc/terms/title>"
        
        
        };
        return unwantedProperties;
    }

    
}
