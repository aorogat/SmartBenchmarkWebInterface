package online.kg_extractor.model.subgraph;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.SPARQL;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;
import online.kg_extractor.model.NodeType;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.VariableSet;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class StarGraph extends Graph {

    private ArrayList<TriplePattern> star;
    private String seedType;

    //Constructors
    public StarGraph() {
    }

    public StarGraph(ArrayList<TriplePattern> star) {
        this.star = star;
    }

    public StarGraph(ArrayList<TriplePattern> star, String seedType) {
        this.star = star;
        this.seedType = seedType;
    }

    public ArrayList<StarGraph> generate_SUBJECT_ENTITY(
            KnowledgeGraph knowledgeGraph, String seed,
            int[] endsType,
            int noOfBranches, int maxChainLength,
            int noOfGraphsRequired) {

        String endpoint = knowledgeGraph.getEndpoint();

        ArrayList<VariableSet> queryResult;
        ArrayList<StarGraph> result = new ArrayList<>();
        seedType = SPARQL.getType(KG_Settings.explorer, seed);

        //Assume the star as follow S0--P0--O0  
        //                          S0--P1--O1
        //                              .... 
        //where S0 is the seed
        String vars = "";
        String triples = "";
        String filter = "";
        String lastVar = "";

        for (int i = 0; i < noOfBranches; i++) {
            vars += " " + "?p" + i + " " + "?o" + i + " ";
            triples += "?s0" + " " + "?p" + i + " " + "?o" + i + " . ";

            //Predicate not in the unwanted list of the current KG
            String[] unwantedPropertiesList = knowledgeGraph.getUnwantedProperties();
            String unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();
            filter += "FILTER (?p" + i + " NOT IN(" + unwantedPropertiesString + ")). ";

            filter += "FILTER strstarts(str(?p" + i + " ), str(dbo:)). ";

            //Predicates not equal each other
            for (int j = 0; j < i; j++) {
                filter += "FILTER (?o" + i + "!=?o" + j + "). ";
//                filter += "FILTER (?p" + i + "!=?p" + j + "). ";
            }
        }

        vars = vars.replace("?s0", "<" + seed + ">");
        triples = triples.replace("?s0", "<" + seed + ">");
        filter = filter.replace("?s0", "<" + seed + ">");

        for (int i = 0; i < endsType.length; i++) {
            if (i > noOfBranches - 1) {
                break;
            }
            if (endsType[i] == NodeType.URI) {
                filter += "FILTER isIRI(?o" + i + "). ";
            } else if (endsType[i] == NodeType.NUMBER) {
                filter += "FILTER isNumeric(?o" + i + "). ";
            } else if (endsType[i] == NodeType.LITERAL) {
                filter += "FILTER isLiteral(?o" + i + "). ";
            } else if (endsType[i] == NodeType.DATE) {
                filter += "FILTER ( datatype(?o" + i + ") = xsd:dateTime ). ";
            }
        }

        String query = "SELECT " + vars + " WHERE { "
                + triples
                + " " + filter
                + "} LIMIT " + noOfGraphsRequired;
        queryResult = knowledgeGraph.runQuery(query);

        for (VariableSet queryResult1 : queryResult) {
            star = new ArrayList<TriplePattern>();

            for (int i = 0; i < noOfBranches * 2; i = i + 2) {
                TriplePattern triplePattern = new TriplePattern(
                        new Variable("?so", seed, "URI"),
                        queryResult1.getVariables().get(i + 1),
                        queryResult1.getVariables().get(i));
                star.add(triplePattern);
            }

            boolean foundGraph = false;
            for (Graph graph : result) {
                if (isGraph1EqualGraph2((StarGraph) graph, new StarGraph(star))) {
                    foundGraph = true;
                }
            }
            if (!foundGraph) {
                result.add(new StarGraph(star, seedType));
            }

        }

        return result;
    }

//    public ArrayList<StarGraph> generate_SUBJECT_ENTITY_Last_P_Repeated_n_times(
//            KnowledgeGraph knowledgeGraph, String seed,
//            int[] endsType,
//            int noOfBranches, int maxChainLength, int n,
//            int noOfGraphsRequired) {
//
//        String endpoint = knowledgeGraph.getEndpoint();
//
//        ArrayList<VariableSet> queryResult;
//        ArrayList<StarGraph> result = new ArrayList<>();
//        seedType = SPARQL.getType(KG_Settings.explorer, seed);
//
//        //Assume the star as follow S0--P0--O0  
//        //                          S0--P1--O1
//        //                              .... 
//        //where S0 is the seed
//        String vars = "";
//        String triples = "";
//        String filter = "";
//        String lastVar = "";
//
//        for (int i = 0; i < noOfBranches - 1; i++) {
//            vars += " " + "?p" + i + " " + "?o" + i + " ";
//            triples += "?s0" + " " + "?p" + i + " " + "?o" + i + " . ";
//
//            //Predicate not in the unwanted list of the current KG
//            String[] unwantedPropertiesList = knowledgeGraph.getUnwantedProperties();
//            String unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();
//            filter += "FILTER (?p" + i + " NOT IN(" + unwantedPropertiesString + ")). ";
//
//            filter += "FILTER strstarts(str(?p" + i + " ), str(dbo:)). ";
//
//            //Predicates not equal each other
//            for (int j = 0; j < i; j++) {
//                filter += "FILTER (?p" + i + "!=?p" + j + "). ";
//            }
//        }
//        int i = noOfBranches - 1;
//        String[] unwantedPropertiesList = knowledgeGraph.getUnwantedProperties();
//        String unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();
//        //Predicate not in the unwanted list of the current KG
//        filter += "FILTER strstarts(str(?p" + i + " ), str(dbo:)). ";
//        filter += "FILTER (?p" + i + " NOT IN(" + unwantedPropertiesString + ")). ";
//        for (int k = 0; k < n; k++) {
//            vars += " " + "?p" + i + " " + "?o" + i + "" + k + " ";
//            triples += "?s0" + " " + "?p" + i + " " + "?o" + i + "" + k + " . ";
//
//        }
//
//        vars = vars.replace("?s0", "<" + seed + ">");
//        triples = triples.replace("?s0", "<" + seed + ">");
//        filter = filter.replace("?s0", "<" + seed + ">");
//
//        for (int t = 0; t < endsType.length; t++) {
//            if (t > noOfBranches - 1) {
//                break;
//            }
//            if (endsType[t] == NodeType.URI) {
//                filter += "FILTER isIRI(?o" + t + "). ";
//            } else if (endsType[t] == NodeType.NUMBER) {
//                filter += "FILTER isNumeric(?o" + t + "). ";
//            } else if (endsType[t] == NodeType.LITERAL) {
//                filter += "FILTER isLiteral(?o" + t + "). ";
//            } else if (endsType[t] == NodeType.DATE) {
//                filter += "FILTER ( datatype(?o" + t + ") = xsd:dateTime ). ";
//            }
//        }
//
//        String query = "SELECT " + vars + " WHERE { "
//                + triples
//                + " " + filter
//                + "} LIMIT " + noOfGraphsRequired;
//        queryResult = knowledgeGraph.runQuery(query);
//
//        for (VariableSet queryResult1 : queryResult) {
//            star = new ArrayList<TriplePattern>();
//
//            for (int t = 0; t < noOfBranches * 2; t = t + 2) {
//                TriplePattern triplePattern = new TriplePattern(
//                        new Variable("?so", seed, "URI"),
//                        queryResult1.getVariables().get(t + 1),
//                        queryResult1.getVariables().get(t));
//                star.add(triplePattern);
//            }
//
//            boolean foundGraph = false;
//            for (Graph graph : result) {
//                if (isGraph1EqualGraph2((StarGraph) graph, new StarGraph(star))) {
//                    foundGraph = true;
//                }
//            }
//            if (!foundGraph) {
//                result.add(new StarGraph(star, seedType));
//            }
//
//        }
//
//        return result;
//    }

//    public ArrayList<Graph> generate_SUBJECT_ENTITY_With_Type_Branch(
//            KnowledgeGraph knowledgeGraph, String seed,
//            int[] endsType,
//            int noOfBranches, int maxChainLength,
//            int noOfGraphsRequired) {
//
//        String endpoint = knowledgeGraph.getEndpoint();
//
//        ArrayList<VariableSet> queryResult;
//        ArrayList<Graph> result = new ArrayList<>();
//
//        //Assume the star as follow S0--P0--O0  
//        //                          S0--P1--O1
//        //                              .... 
//        //where S0 is the seed
//        String vars = "";
//        String triples = "";
//        String filter = "";
//        String lastVar = "";
//
//        for (int i = 0; i < noOfBranches; i++) {
//            vars += " " + "?p" + i + " " + "?o" + i + " ";
//            triples += "?s0" + " " + "?p" + i + " " + "?o" + i + " . \n ";
//
//            //Predicate not in the unwanted list of the current KG
//            String[] unwantedPropertiesList = knowledgeGraph.getUnwantedProperties();
//            String unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();
//            if (i != 0) {
//                filter += "FILTER (?p" + i + " NOT IN(" + unwantedPropertiesString + ")). \n ";
//                filter += "FILTER strstarts(str(?p" + i + " ), str(dbo:)). \n ";
//            } else {
//                filter += "FILTER (?p" + i + " = rdf:type). \n ";
//                filter += "FILTER strstarts(str(?o" + i + " ), str(dbo:)). \n "
//                        + "    FILTER NOT EXISTS {\n"
//                        + "      ?s" + i + " rdf:type ?type1 .\n"
//                        + "      ?type1 rdfs:subClassOf ?o" + i + ".\n"
//                        + "      FILTER NOT EXISTS {\n"
//                        + "         ?type1 owl:equivalentClass ?o" + i + ".\n"
//                        + "      }\n"
//                        + "    }.\n";
//            }
//
//            //Predicates not equal each other
//            for (int j = 0; j < i; j++) {
//                filter += "FILTER (?p" + i + "!=?p" + j + "). \n ";
//            }
//        }
//
//        vars = vars.replace("?s0", "<" + seed + ">");
//        triples = triples.replace("?s0", "<" + seed + ">");
//        filter = filter.replace("?s0", "<" + seed + ">");
//
//        for (int i = 0; i < endsType.length; i++) {
//            if (endsType[i] == NodeType.URI) {
//                filter += "FILTER isIRI(?o" + i + "). \n ";
//            } else if (endsType[i] == NodeType.NUMBER) {
//                filter += "FILTER isNumeric(?o" + i + "). \n ";
//            } else if (endsType[i] == NodeType.LITERAL) {
//                filter += "FILTER isLiteral(?o" + i + "). \n ";
//            } else if (endsType[i] == NodeType.DATE) {
//                filter += "FILTER ( datatype(?o" + i + ") = xsd:dateTime ). \n ";
//            }
//        }
//
//        String query = "SELECT " + vars + " WHERE { \n "
//                + triples
//                + " " + filter
//                + "} LIMIT " + noOfGraphsRequired;
//        queryResult = knowledgeGraph.runQuery(query);
//
//        for (VariableSet queryResult1 : queryResult) {
//            star = new ArrayList<TriplePattern>();
//
//            for (int i = 0; i < noOfBranches * 2; i = i + 2) {
//                TriplePattern triplePattern = new TriplePattern(
//                        new Variable("?so", seed, "URI"),
//                        queryResult1.getVariables().get(i + 1),
//                        queryResult1.getVariables().get(i));
//                star.add(triplePattern);
//            }
//
//            boolean foundGraph = false;
//            for (Graph graph : result) {
//                if (isGraph1EqualGraph2((StarGraph) graph, new StarGraph(star))) {
//                    foundGraph = true;
//                }
//            }
//            if (!foundGraph) {
//                result.add(new StarGraph(star));
//            }
//
//        }
//
//        return result;
//    }
//
//    public ArrayList<Graph> generate_OBJECT_ENTITY(
//            KnowledgeGraph knowledgeGraph, String seed,
//            int[] endsType,
//            int noOfBranches, int maxChainLength,
//            int noOfGraphsRequired) {
//
//        String endpoint = knowledgeGraph.getEndpoint();
//
//        ArrayList<VariableSet> queryResult;
//        ArrayList<Graph> result = new ArrayList<>();
//
//        //Assume the star as follow S0--P0--O0  
//        //                          S1--P1--O0
//        //                              .... 
//        //where S0 is the seed
//        String vars = "";
//        String triples = "";
//        String filter = "";
//        String lastVar = "";
//
//        for (int i = 0; i < noOfBranches; i++) {
//            vars += " " + "?p" + i + " " + "?s" + i + " ";
//            triples += "?s" + i + " " + "?p" + i + " " + "?o0" + " . ";
//
//            //Predicate not in the unwanted list of the current KG
//            String[] unwantedPropertiesList = knowledgeGraph.getUnwantedProperties();
//            String unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();
//            filter += "FILTER (?p" + i + " NOT IN(" + unwantedPropertiesString + ")). ";
//
//            filter += "FILTER strstarts(str(?p" + i + " ), str(dbo:)). ";
//
//            //Predicates not equal each other
//            for (int j = 0; j < i; j++) {
//                filter += "FILTER (?p" + i + "!=?p" + j + "). ";
//            }
//        }
//
//        vars = vars.replace("?o0", "<" + seed + ">");
//        triples = triples.replace("?o0", "<" + seed + ">");
//        filter = filter.replace("?o0", "<" + seed + ">");
//
//        for (int i = 0; i < endsType.length; i++) {
//            if (endsType[i] == NodeType.URI) {
//                filter += "FILTER isIRI(?s" + i + "). ";
//            } else if (endsType[i] == NodeType.NUMBER) {
//                filter += "FILTER isNumeric(?s" + i + "). ";
//            } else if (endsType[i] == NodeType.LITERAL) {
//                filter += "FILTER isLiteral(?s" + i + "). ";
//            } else if (endsType[i] == NodeType.DATE) {
//                filter += "FILTER ( datatype(?s" + i + ") = xsd:dateTime ). ";
//            }
//        }
//
//        String query = "SELECT " + vars + " WHERE { "
//                + triples
//                + " " + filter
//                + "} LIMIT " + noOfGraphsRequired;
//        queryResult = knowledgeGraph.runQuery(query);
//
//        for (VariableSet queryResult1 : queryResult) {
//            star = new ArrayList<TriplePattern>();
//
//            for (int i = 0; i < noOfBranches * 2; i = i + 2) {
//                TriplePattern triplePattern = new TriplePattern(
//                        queryResult1.getVariables().get(i + 1),
//                        new Variable("?oo", seed, "URI"),
//                        queryResult1.getVariables().get(i));
//                star.add(triplePattern);
//            }
//
//            boolean foundGraph = false;
//            for (Graph graph : result) {
//                if (isGraph1EqualGraph2((StarGraph) graph, new StarGraph(star))) {
//                    foundGraph = true;
//                }
//            }
//            if (!foundGraph) {
//                result.add(new StarGraph(star));
//            }
//
//        }
//
//        return result;
//    }
    public String toString() {
        String s = star.get(0).getSubject().getValue() + " ____ " + "type" + " ____ " + KG_Settings.explorer.removePrefix(seedType);
        for (int i = 0; i < star.size(); i++) {
            s += "\n" + star.get(i).toString();
        }
        return s + "\n";
    }

    public ArrayList<TriplePattern> getStar() {
        return star;
    }

    public void setStar(ArrayList<TriplePattern> star) {
        this.star = star;
    }

    public static boolean isGraphContainsATriplePattern(StarGraph starGraph, TriplePattern tp) {
        for (TriplePattern triplePattern : starGraph.getStar()) {
            if ((tp.getSubject().getValueWithPrefix().equals(triplePattern.getSubject().getValueWithPrefix())
                    && tp.getObject().getValueWithPrefix().equals(triplePattern.getObject().getValueWithPrefix())
                    && tp.getPredicate().getValueWithPrefix().equals(triplePattern.getPredicate().getValueWithPrefix()))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isGraph1EqualGraph2(StarGraph starGraph1, StarGraph starGraph2) {
        for (TriplePattern triplePattern : starGraph1.star) {
            if (!isGraphContainsATriplePattern(starGraph2, triplePattern)) {
                return false;
            }
        }
        return true;
    }

    public String getSeedType() {
        return seedType;
    }

    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

}
