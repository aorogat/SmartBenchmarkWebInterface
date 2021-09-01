package kg_explorer.explorer;

import java.util.ArrayList;
import kg_explorer.model.ListOfPredicates;
import kg_explorer.model.Predicate;
import kg_explorer.model.PredicateTriple;
import kg_extractor.knowledgegraph.DBpedia;
import kg_extractor.model.VariableSet;

/**
 *
 * @author aorogat
 */
public class DBpediaExplorer extends Explorer {

    
    
    private DBpediaExplorer(String url) {
        kg = DBpedia.getInstance(url);
        endpoint = kg.getEndpoint();
    }

    public static DBpediaExplorer getInstance(String url) {
        if (instance == null) {
            instance = new DBpediaExplorer(url);
            return (DBpediaExplorer) instance;
        } else {
            return (DBpediaExplorer) instance;
        }
    }

    @Override
    public ListOfPredicates explore(int from, int length) {
        predicateList.clear();

        getPredicateList(from, length);

        for (VariableSet predicate : predicates) {
            Predicate predicateObject = new Predicate(this);
            predicateObject.setPredicateURI(predicate.toString().trim());
            predicateObject.setPredicate(removePrefix(predicate.toString().trim()));
            predicateObject.setLabel(getPredicateLabel(predicate.toString().trim()));
            predicateObject.setWeight(getPredicateWeight(predicate.toString().trim()));
            predicateObject.setTripleExamples(getOneTripleExample(predicate.toString().trim(), predicateObject.getLabel(),10));
            predicateList.add(predicateObject);
        }
        ListOfPredicates predicaes = new ListOfPredicates(predicateList);
        return predicaes;
    }

    private void getPredicateList(int from, int length) {
        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        String query = "SELECT DISTINCT ?p WHERE { "
                + "?s ?p ?o. ?o ?t ?l. " //Get only if ?o is entity
                + " FILTER (?p NOT IN(" + unwantedPropertiesString + ")). "
                + "} LIMIT "+length+" OFFSET "+from;
        predicates = kg.runQuery(query);
        
        //get predicates where the object is number
        
        
        //get predicates where the object is date
        
        
        //..
    }

    private String getPredicateLabel(String predicate) {
        String query = "";
        //get labels
        try {
            query = "SELECT DISTINCT ?l WHERE { ?p rdfs:label ?l. "
                    + "FILTER(?p=<" + predicate.trim() + ">). "
                    + "FILTER langMatches( lang(?l), \"EN\" )."
                    + "}";
            predicatesTriples = kg.runQuery(query);
            return predicatesTriples.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return (predicate.trim());
        }
    }

    private long getPredicateWeight(String predicate) {
        String query = "";
        //get weights
        try {
            query = "SELECT (count(?p) as ?count) WHERE { ?s ?p ?o . FILTER(?p=<" + predicate.trim() + ">)}";
            predicatesTriples = kg.runQuery(query);
            return Long.valueOf(predicatesTriples.get(0).getVariables().get(0).toString());
        } catch (Exception e) {
            return -1;
        }
    }

    @SuppressWarnings("empty-statement")
    private ArrayList<PredicateTriple> getOneTripleExample(String predicate, String lable, int noOfExamples) {
        String query = "";
        ArrayList<PredicateTriple> predicateTriples = predicateTriples = new ArrayList<>();
        try {
            query = "SELECT DISTINCT ?s ?o WHERE { ?s <" + predicate.trim() + "> ?o . ?o ?t ?l. } LIMIT " + (noOfExamples-1); //only those with entity object
            //query = "SELECT DISTINCT ?s ?o WHERE { ?s <" + predicate.trim() + "> ?o .} LIMIT " + (noOfExamples-1);
            predicatesTriples = kg.runQuery(query);
            for (VariableSet predicate1 : predicatesTriples) {
                String s = predicate1.getVariables().get(0).toString();
                String o = predicate1.getVariables().get(1).toString();
                PredicateTriple predicateTriple = new PredicateTriple("<" + s + ">", "<" + o + ">", removePrefix(s), removePrefix(o), lable, this);
                predicateTriples.add(predicateTriple);
            }
            return predicateTriples;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public String removePrefix(String node) {
        node = node.replace("http://dbpedia.org/resource/", "")
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
                .replace("http://www.w3.org/2003/01/geo/wgs84 pos", "")
                .replace("http://en.wikipedia.org/", "")
                .replace("http://purl.org/linguistics/gold/", "")
                //.replace("","")
                .replace(">", "")
                .trim().replace('_', ' ');
        return node;
    }

}
