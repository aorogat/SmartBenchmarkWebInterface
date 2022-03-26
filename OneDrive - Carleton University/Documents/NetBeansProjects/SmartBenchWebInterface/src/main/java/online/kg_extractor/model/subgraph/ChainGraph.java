package online.kg_extractor.model.subgraph;

import java.util.ArrayList;
import java.util.HashSet;
import offLine.kg_explorer.explorer.KnowledgeGraph;
import online.kg_extractor.model.NodeType;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.VariableSet;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class ChainGraph extends Graph {

    private ArrayList<TriplePattern> chain;

    //Constructors
    public ChainGraph() {
    }

    public ChainGraph(ArrayList<TriplePattern> chain) {
        this.chain = chain;
    }

//    public ArrayList<Graph> generateFromLexicon(KnowledgeGraph knowledgeGraph, String seed, int seedType, int endType, int chainLength, boolean uniqueProperties
//    {
//        
//    }
    public ArrayList<Graph> generate(KnowledgeGraph knowledgeGraph, String seed, int seedType, int endType, int chainLength, boolean uniqueProperties) {
        if (!seed.startsWith("<")) {
            seed = "<" + seed + ">";
        }
        if (seedType == NodeType.SUBJECT_ENTITY) {
            return generate_SUBJECT_ENTITY(knowledgeGraph, seed, endType, seedType, chainLength, uniqueProperties);
        } else if (seedType == NodeType.OBJECT_ENTITY) {
            return generate_OBJECT_ENTITY(knowledgeGraph, seed, endType, seedType, chainLength, uniqueProperties);
        } else {
            return new ArrayList<>();
        }
    }

    private ArrayList<Graph> generate_SUBJECT_ENTITY(KnowledgeGraph knowledgeGraph, String seed, int endType, int seedType, int chainLength, boolean uniqueProperties) {
        if (chainLength <= 0) {
            return null;
        } else if (chainLength == 1) {
            return new SingleEdgeGraph().generate(knowledgeGraph, seed, seedType, endType, false, false);
        }

        String endpoint = knowledgeGraph.getEndpoint();

        ArrayList<VariableSet> queryResult;
        ArrayList<Graph> result = new ArrayList<>();

        //Assume the chain as follow O0--P1--O1  O1--P2--O2 ....
        String vars = "";
        String triples = "";
        String filter = "";
        String lastVar = "";
        String lastPredicate = "";

        //Predicate not in the unwanted list of the current KG
        String[] unwantedPropertiesList = knowledgeGraph.getUnwantedProperties();
        String unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();
        if (endType == NodeType.TYPE) {
            unwantedPropertiesString = unwantedPropertiesString.replace("rdf:type,", "");
        }
        for (int i = 0; i < chainLength; i++) {
            filter += "FILTER (?p" + (i + 1) + " NOT IN(" + unwantedPropertiesString + ")). ";
            if (endType == NodeType.TYPE && (i + 1) == chainLength) {
                break;
            }
            if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
                filter += "FILTER strstarts(str(?p" + (i + 1) + " ), str(" + Settings.requiredTypePrefix + ")). ";
            }
        }

        for (int i = 0; i < chainLength; i++) {
            vars += "?o" + i + " " + "?p" + (i + 1) + " " + "?o" + (i + 1) + " ";
            triples += "?o" + i + " " + "?p" + (i + 1) + " " + "?o" + (i + 1) + " . ";
            lastVar = "?o" + (i + 1);
            lastPredicate = "?p" + (i + 1);
            filter += "FILTER NOT EXISTS { ?o" + i + "  ?p" + (i + 1) + " ?t" + (i + 1) + ". FILTER(?t" + (i + 1) + " != ?o" + (i + 1) + ")}. ";
            if (uniqueProperties) {
                filter += "FILTER NOT EXISTS { ?o" + i + "  ?p" + (i + 1) + " ?m" + (i + 1) + ". FILTER(?m" + (i + 1) + " != ?o" + (i + 1) + ")}. ";
            }
            for (int j = 0; j < i; j++) {
                filter += "FILTER (?o" + i + "!=?o" + j + "). ";
            }
        }

        vars = vars.replace("?o0", seed);
        triples = triples.replace("?o0", seed);
        filter = filter.replace("?o0", seed);

        //Return the chain in which the 1st node is the seed
        if (endType == NodeType.URI) {
            filter += "FILTER isIRI(" + lastVar + "). ";
        } else if (endType == NodeType.NUMBER) {
            filter += "FILTER isNumeric(" + lastVar + "). ";
        } else if (endType == NodeType.LITERAL) {
            filter += "FILTER isLiteral(" + lastVar + "). ";
        } else if (endType == NodeType.DATE) {
            filter += "FILTER ( datatype(" + lastVar + ") = xsd:dateTime ). ";
        } else if (endType == NodeType.TYPE) {
            filter += "FILTER (" + lastPredicate + " = rdf:type). ";
        }

        String query = "SELECT REDUCED " + vars + " WHERE { "
                + triples
                + " " + filter
                + "}";
        queryResult = knowledgeGraph.runQuery(query);

        for (VariableSet queryResult1 : queryResult) {
            chain = new ArrayList<TriplePattern>();

            for (int i = 0; i < chainLength * 3; i = i + 3) {
                TriplePattern triplePattern = new TriplePattern(
                        queryResult1.getVariables().get(i),
                        queryResult1.getVariables().get(i + 2),
                        queryResult1.getVariables().get(i + 1));
                chain.add(triplePattern);
            }
            result.add(new ChainGraph(chain));
        }

        return result;
    }

    private ArrayList<Graph> generate_OBJECT_ENTITY(KnowledgeGraph knowledgeGraph, String seed, int endType, int seedType, int chainLength, boolean uniqueProperties) {
        if (chainLength <= 0) {
            return null;
        } else if (chainLength == 1) {
            return new SingleEdgeGraph().generate(knowledgeGraph, seed, seedType, endType, false, false);
        }

        String endpoint = knowledgeGraph.getEndpoint();

        ArrayList<VariableSet> queryResult;
        ArrayList<Graph> result = new ArrayList<>();

        //Assume the chain as follow O0--P1--O1  O1--P2--O2 ....
        String vars = "";
        String triples = "";
        String filter = "";

        //Predicate not in the unwanted list of the current KG
        String[] unwantedPropertiesList = knowledgeGraph.getUnwantedProperties();
        String unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();
        for (int i = 0; i < chainLength; i++) {
            filter += "FILTER (?p" + (i + 1) + " NOT IN(" + unwantedPropertiesString + ")). ";
            for (int j = 0; j < i; j++) {
                filter += "FILTER (?o" + i + "!=?o" + j + "). ";
            }
        }

        for (int i = 0; i < chainLength; i++) {
            vars += "?o" + i + " " + "?p" + (i + 1) + " " + "?o" + (i + 1) + " ";
            triples += "?o" + i + " " + "?p" + (i + 1) + " " + "?o" + (i + 1) + " . ";
        }

        vars = vars.replace("?o0", seed);
        triples = triples.replace("?o0", seed);

        //Return the chain in which the 1st node is the seed
        if (seedType == NodeType.SUBJECT_ENTITY) {

            if (endType == NodeType.URI) {
                filter += "FILTER isIRI(?s). ";
            } else if (endType == NodeType.NUMBER) {
                filter += "FILTER isNumeric(?s). ";
            } else if (endType == NodeType.LITERAL) {
                filter += "FILTER isLiteral(?s). ";
            } else if (endType == NodeType.DATE) {
                filter += "FILTER ( datatype(?s) = xsd:dateTime ). ";
            }

            String query = "SELECT  REDUCED " + vars + " WHERE { "
                    + triples
                    + " " + filter
                    + "}";
            queryResult = knowledgeGraph.runQuery(query);

            for (VariableSet queryResult1 : queryResult) {
                chain = new ArrayList<TriplePattern>();

                for (int i = 0; i < chainLength * 3; i = i + 3) {
                    TriplePattern triplePattern = new TriplePattern(
                            queryResult1.getVariables().get(i),
                            queryResult1.getVariables().get(i + 2),
                            queryResult1.getVariables().get(i + 1));
                    chain.add(triplePattern);
                }
                result.add(new ChainGraph(chain));
            }
        }
        return new ArrayList<>(new HashSet<>(result));
    }

    public String toString() {
        String s = chain.get(0).toString();
        for (int i = 1; i < chain.size(); i++) {
            s += chain.get(i).toStringNotSubject();
        }
        return s;
    }

    public ArrayList<TriplePattern> getChain() {
        return chain;
    }

    public void setChain(ArrayList<TriplePattern> chain) {
        this.chain = chain;
    }

}
