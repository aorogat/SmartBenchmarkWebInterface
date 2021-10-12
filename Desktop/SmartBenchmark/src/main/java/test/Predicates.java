package test;

import java.util.ArrayList;
import online.kg_extractor.knowledgegraph.DBpedia;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.VariableSet;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.subgraph.SingleEdgeGraph;

/**
 *
 * @author aorogat
 */
public class Predicates {

    static DBpedia dbpedia = DBpedia.getInstance("https://dbpedia.org/sparql");
    static String endpoint = dbpedia.getEndpoint();
    static ArrayList<VariableSet> predicates;
    static ArrayList<VariableSet> predicatesTriples;
    static ArrayList<Graph> result = new ArrayList<>();

    public static void main(String[] args) {

        String query = "SELECT DISTINCT ?p WHERE { "
                + "?s "
                + "?p "
                + "?o"
                + "}";
        predicates = dbpedia.runQuery(query);
        //printOneExampleForEachPredicate(predicates);
        //printPredicateWeight(predicates);
        printPredicateLabel(predicates);
    }

    public static void printPredicateWeight(ArrayList<VariableSet> predicates) {
        for (VariableSet predicate : predicates) {
            String query = "SELECT (count(?p) as ?count) WHERE { ?s ?p ?o . FILTER(?p=<" + predicate.toString().trim() + ">)}";
            predicatesTriples = dbpedia.runQuery(query);
            for (VariableSet predicateTriples : predicatesTriples) {
                String oneResult = predicateTriples.getVariables().get(0).toString();
                System.out.println(predicate.toString().trim() + "\t" + oneResult);
            }
        }
    }
    
    public static void printPredicateLabel(ArrayList<VariableSet> predicates) {
        for (VariableSet predicate : predicates) {
            String query = "SELECT DISTINCT ?p ?l WHERE { ?p rdfs:label ?l. "
                    + "FILTER(?p=<" + predicate.toString().trim() + ">). "
                    + "FILTER langMatches( lang(?l), \"EN\" )."
                    + "}";
            predicatesTriples = dbpedia.runQuery(query);
            //Print results
            for (VariableSet predicateTriples : predicatesTriples) {
                System.out.println(predicateTriples.toString());
            }
        }
    }
    
    

    
    public static void printOneExampleForEachPredicate(ArrayList<VariableSet> predicates) {
        for (VariableSet predicate : predicates) {
            String query = "SELECT ?s ?o WHERE { ?s <" + predicate.toString().trim() + "> ?o . } LIMIT 1";
            predicatesTriples = dbpedia.runQuery(query);
            for (VariableSet predicateTriples : predicatesTriples) {
                String oneResult = predicateTriples.getVariables().get(0).toString()
                        + "\t"
                        + predicate.toString().trim()
                        + "\t"
                        + predicateTriples.getVariables().get(1).toString();
                oneResult = oneResult
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
                        .trim().replace('_', ' ');
                System.out.println(oneResult);
            }
        }
    }

}
