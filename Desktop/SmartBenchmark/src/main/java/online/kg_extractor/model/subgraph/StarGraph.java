package online.kg_extractor.model.subgraph;

import java.util.ArrayList;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;
import online.kg_extractor.model.NodeType;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.VariableSet;

/**
 *
 * @author aorogat
 */
public class StarGraph extends Graph {

    private ArrayList<TriplePattern> star;

    //Constructors
    public StarGraph() {
    }

    public StarGraph(ArrayList<TriplePattern> star) {
        this.star = star;
    }

    public ArrayList<Graph> generate(
            KnowledgeGraph knowledgeGraph, String seed,
            int[] endsType,
            int noOfBranches, int maxChainLength) {
            return generate_SUBJECT_ENTITY(knowledgeGraph, seed, endsType, noOfBranches, maxChainLength);
        
    }

    
    private ArrayList<Graph> generate_SUBJECT_ENTITY(
            KnowledgeGraph knowledgeGraph, String seed,
            int[] endsType,
            int noOfBranches, int maxChainLength) {
        if (noOfBranches <= 2) {
            return null;
        }

        String endpoint = knowledgeGraph.getEndpoint();

        ArrayList<VariableSet> queryResult;
        ArrayList<Graph> result = new ArrayList<>();

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
            filter += "FILTER (?p" + i + " NOT IN("+unwantedPropertiesString+")). ";
            
            //Predicates not equal each other
            for (int j = 0; j < i; j++) {
                filter += "FILTER (?p" + i + "!=?p"+j+"). ";
            }
        }

        vars = vars.replace("?s0", seed);
        triples = triples.replace("?s0", seed);
        filter = filter.replace("?s0", seed);

        for (int i = 0; i < endsType.length; i++) {
            if (endsType[i] == NodeType.IRI) {
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
                + "}";
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
            result.add(new StarGraph(star));
        }

        return result;
    }


    public String toString() {
        String s = star.get(0).toString();
        for (int i = 1; i < star.size(); i++) {
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
    
    
}
