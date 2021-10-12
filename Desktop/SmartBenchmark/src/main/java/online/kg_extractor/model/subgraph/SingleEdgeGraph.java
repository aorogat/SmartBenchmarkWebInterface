package online.kg_extractor.model.subgraph;

import java.util.ArrayList;
import online.kg_extractor.knowledgegraph.DBpedia;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;
import online.kg_extractor.model.NodeType;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.VariableSet;

/**
 *
 * @author aorogat
 */
public class SingleEdgeGraph extends Graph {

    private TriplePattern triplePattern;

    //Constructors
    public SingleEdgeGraph() {
    }

    public SingleEdgeGraph(TriplePattern triplePattern) {
        this.triplePattern = triplePattern;
    }

    /**
     * Returns a Single-Edge subgraph.
     *
     * @param knowledgeGraph an object from a class inherits the KnowledgeGraph
     * abstract class.
     * @param seed an entity that represent the start or the end of the triple
     * based on the seedType value.
     * @param seedType the type of the seed accepts a constant value from the
     * NodeType class.
     * @param uniqueProperties if true, will eliminate all triples with repeated
     * property. For example, x_y_z and x_y_w, will be eliminated.
     * @param endedChain if true, returns a Single-Edge subgraph in which the
     * object is not participating as subject into any other triples. This means
     * that this triple pattern cannot participate into a longer chain in which
     * this triple is the 1st one.
     * @return a list of graph objects, the reference of type Graph and the
     * instance of type SingleEdge.
     */
    public ArrayList<Graph> generate(KnowledgeGraph knowledgeGraph, String seed, int seedType, int endType, boolean uniqueProperties, boolean endedChain) {
        if (endedChain) {
            return generateEndedSingleEdge(knowledgeGraph, seed, seedType, endType, uniqueProperties);
        }

        ArrayList<Graph> result = new ArrayList<>();
        String filter = "";

        //Predicate not in the unwanted list of the current KG
        String[] unwantedPropertiesList = knowledgeGraph.getUnwantedProperties();
        String unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();
        filter += "FILTER (?p NOT IN(" + unwantedPropertiesString + ")). ";

        if (seedType == NodeType.OBJECT_ENTITY) {
            if (uniqueProperties) {
                filter += "FILTER NOT EXISTS { ?s ?p ?m. FILTER(?m != " + seed + ")}. ";
            }

            if (endType == NodeType.IRI) {
                filter += "FILTER isIRI(?s). ";
            } else if (endType == NodeType.NUMBER) {
                filter += "FILTER isNumeric(?s). ";
            } else if (endType == NodeType.LITERAL) {
                filter += "FILTER isLiteral(?s). ";
            } else if (endType == NodeType.DATE) {
                filter += "FILTER ( datatype(?s) = xsd:dateTime ). ";
            }

            return generate_OBJECT_ENTITY(knowledgeGraph, seed, seedType, filter);
        } else if (seedType == NodeType.SUBJECT_ENTITY) {
            if (uniqueProperties) {
                filter += "FILTER NOT EXISTS { " + seed + " ?p ?m. FILTER(?m != ?o)}. ";
            }
            if (endType == NodeType.IRI) {
                filter += "FILTER isIRI(?o). ";
            } else if (endType == NodeType.NUMBER) {
                filter += "FILTER isNumeric(?o). ";
            } else if (endType == NodeType.LITERAL) {
                filter += "FILTER isLiteral(?o). ";
            } else if (endType == NodeType.DATE) {
                filter += "FILTER ( datatype(?o) = xsd:dateTime ). ";
            }

            return generate_SUBJECT_ENTITY(knowledgeGraph, seed, seedType, filter);
        }
        return result;
    }

    /**
     * Returns a Single-Edge subgraph in which the object is not participating
     * as subject into any other triples. This means that this triple pattern
     * cannot participate into a longer chain in which this triple is the 1st
     * one.
     *
     * @param knowledgeGraph an object from a class inherits the KnowledgeGraph
     * abstract class.
     * @param seed an entity that represent the start or the end of the triple
     * based on the seedType value.
     * @param seedType the type of the seed accepts a constant value from the
     * NodeType class.
     * @param uniqueProperties if true, will eliminate all triples with repeated
     * property. For example, x_y_z and x_y_w, will be eliminated.
     * @return a list of graph objects, the reference of type Graph and the
     * instance of type SingleEdge.
     */
    private ArrayList<Graph> generateEndedSingleEdge(KnowledgeGraph knowledgeGraph, String seed, int seedType, int endType, boolean uniqueProperties) {
        ArrayList<Graph> result = new ArrayList<>();
        String filter = "";

        if (seedType == NodeType.OBJECT_ENTITY) {
            filter = "FILTER NOT EXISTS { ?s ?x ?m }. ";

            if (uniqueProperties) {
                filter += "FILTER NOT EXISTS { ?s ?p ?t. FILTER(?t != " + seed + ")}. ";
            }
            if (endType == NodeType.IRI) {
                filter += "FILTER isIRI(?s). ";
            } else if (endType == NodeType.NUMBER) {
                filter += "FILTER isNumeric(?s). ";
            } else if (endType == NodeType.LITERAL) {
                filter += "FILTER isLiteral(?s). ";
            } else if (endType == NodeType.DATE) {
                filter += "FILTER ( datatype(?s) = xsd:dateTime ). ";
            }

            return generate_OBJECT_ENTITY(knowledgeGraph, seed, seedType, filter);
        } else if (seedType == NodeType.SUBJECT_ENTITY) {
            filter = "FILTER NOT EXISTS { ?o ?x ?m }. " + filter;
            if (uniqueProperties) {
                filter += "FILTER NOT EXISTS { " + seed + " ?p ?t. FILTER(?t != ?o)}. ";
            }
            if (endType == NodeType.IRI) {
                filter += "FILTER isIRI(?o). ";
            } else if (endType == NodeType.NUMBER) {
                filter += "FILTER isNumeric(?o). ";
            } else if (endType == NodeType.LITERAL) {
                filter += "FILTER isLiteral(?o). ";
            } else if (endType == NodeType.DATE) {
                filter += "FILTER ( datatype(?o) = xsd:date ||  datatype(?o) = xsd:dateTime ). ";
            }

            return generate_SUBJECT_ENTITY(knowledgeGraph, seed, seedType, filter);
        }
        return result;
    }

    private ArrayList<Graph> generate_OBJECT_ENTITY(KnowledgeGraph knowledgeGraph, String seed, int seedType, String filter) {
        String endpoint = knowledgeGraph.getEndpoint();

        ArrayList<VariableSet> queryResult;
        ArrayList<Graph> result = new ArrayList<>();
        String query = "SELECT ?s ?p WHERE { "
                + "?s "
                + "?p "
                + seed
                + " " + filter
                + "}";
        queryResult = knowledgeGraph.runQuery(query);
        for (VariableSet queryResult1 : queryResult) {
            triplePattern = new TriplePattern(queryResult1.getVariables().get(0),
                    new Variable("d", seed, "Entitiy"),
                    queryResult1.getVariables().get(1));
            result.add(new SingleEdgeGraph(triplePattern));
        }
        return result;
    }

    private ArrayList<Graph> generate_SUBJECT_ENTITY(KnowledgeGraph knowledgeGraph, String seed, int seedType, String filter) {
        String endpoint = knowledgeGraph.getEndpoint();
        ArrayList<VariableSet> queryResult;
        ArrayList<Graph> result = new ArrayList<>();

        String query = "SELECT ?p ?o WHERE { "
                + seed
                + "?p "
                + "?o"
                + " " + filter
                + "}";
        queryResult = knowledgeGraph.runQuery(query);
        for (VariableSet queryResult1 : queryResult) {
            triplePattern = new TriplePattern(
                    new Variable("d", seed, "Entitiy"),
                    queryResult1.getVariables().get(1),
                    queryResult1.getVariables().get(0));
            result.add(new SingleEdgeGraph(triplePattern));

        }
        return result;
    }

    public TriplePattern getTriplePattern() {
        return triplePattern;
    }

    
    
    public String toString() {
        String s = triplePattern.toString();
        return s;
    }
}
