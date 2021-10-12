package online.kg_extractor.model;

/**
 *
 * @author aorogat
 */
public class Variable {

    private String name;
    private String value;
    private String type;

    public Variable(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getValueWithPrefix() {
        return value;
    }

    public String getValue() {
        return value
                .replace("http://dbpedia.org/resource/", "")
                .replace("http://dbpedia.org/ontology/", "")
                .replace("http://dbpedia.org/property/", "")
                .replace("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "type")
                .replace("<", "")
                .replace("http://dbpedia.org/class/yago/", "yago:")
                .replace("http://umbel.org/umbel/rc/", "umbel:")
                .replace("http://www.w3.org/ns/prov", "")
                .replace("http://www.w3.org/2002/07/owl", "")
                .replace("http://www.w3.org/2000/01/rdf-schema", "")
                .replace("http://www.w3.org/1999/02/22-rdf-syntax-ns", "")
                .replace("ttp://xmlns.com/foaf/0.1/", "")
                .replace("ttp://www.w3.org/2004/02/skos/", "")
                .replace("http://purl.org/dc/terms/", "")
                .replace("http://purl.org/dc/elements/1.1/", "")
//                .replace("", "")
//                .replace("", "")
//                .replace("", "")
                .replace(">", "")
                .trim().replace('_', ' ')
                ;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value.replace("\n", " - ");
    }
}
