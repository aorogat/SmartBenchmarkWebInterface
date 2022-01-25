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

            //Objects not equal each other
            for (int j = 0; j < i; j++) {
                filter += "FILTER (?o" + i + "!=?o" + j + "). ";
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
