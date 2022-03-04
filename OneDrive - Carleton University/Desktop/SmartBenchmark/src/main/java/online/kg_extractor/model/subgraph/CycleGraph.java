package online.kg_extractor.model.subgraph;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.explorer.KnowledgeGraph;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.VariableSet;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class CycleGraph { //Only support paths of length 1

    private TriplePattern path_1;
    private TriplePattern path_2;
    private String seedType;
    private String unwantedPropertiesString;

    //Constructors
    public CycleGraph() {

    }

    public CycleGraph(TriplePattern path_1, TriplePattern path_2) {
        this.path_1 = path_1;
        this.path_2 = path_2;
    }

    public CycleGraph(TriplePattern path_1, TriplePattern path_2, String seedType) {
        this.path_1 = path_1;
        this.path_2 = path_2;
        this.seedType = seedType;
    }

    public ArrayList<CycleGraph> generate_SUBJECT_ENTITY(
            KnowledgeGraph knowledgeGraph, String seed,
            int endType,
            int noOfGraphsRequired) {

        String endpoint = knowledgeGraph.getEndpoint();

        ArrayList<VariableSet> queryResult;
        ArrayList<CycleGraph> result = new ArrayList<>();
        seedType = SPARQL.getType(Settings.explorer, seed);

        //Assume the star as follow S0--P1.1--P1.2 .. --On.m  
        //                          S0--P2.1--P2.2 .. --On.m
        //where S0 is the seed
        String vars = "";
        String triples = "";
        String filter = "";
        String lastVar = "";

        vars += " " + "?p1 ?p2 ?o";
        triples += "\n\t <" + seed + "> ?p1 ?o.";
        triples += "\n\t <" + seed + "> ?p2 ?o.";
        if (Settings.requiredTypePrefix != null && !"".equals(Settings.requiredTypePrefix)) {
            filter += "\n\t FILTER strstarts(str(?p1), str(" + Settings.requiredTypePrefix + ")).";
            filter += "\n\t FILTER strstarts(str(?p2), str(" + Settings.requiredTypePrefix + ")).";
        }

        filter += "\n\t FILTER (?p1!=?p2). \n";

        unwantedPropertiesString = knowledgeGraph.getUnwantedPropertiesString();

        filter += "\n\t FILTER (?p1 NOT IN(" + unwantedPropertiesString + ")). ";
        filter += "\n\t FILTER (?p2 NOT IN(" + unwantedPropertiesString + ")). ";

        String query = "SELECT " + vars + " WHERE { "
                + triples
                + " " + filter
                + "} LIMIT " + noOfGraphsRequired;

        queryResult = knowledgeGraph.runQuery(query);

        for (VariableSet varSetTuple : queryResult) {
            path_1 = new TriplePattern(
                    new Variable("?s0", seed, "URI"),
                    varSetTuple.getVariables().get(2),
                    varSetTuple.getVariables().get(0));

            path_2 = new TriplePattern(
                    new Variable("?s0", seed, "URI"),
                    varSetTuple.getVariables().get(2),
                    varSetTuple.getVariables().get(1));

            CycleGraph c = new CycleGraph(path_1, path_2);
            boolean found = false;
            for (CycleGraph ci : result) {
                if (isGraphEqualTo(c, ci)) {
                    found = true;
                }
            }
            if (!found) {
                result.add(c);
            }

        }

        return result;
    }

    public String toString() {
        String s = "\n" + path_1.toString();
        s += "\n" + path_2.toString();

        return s + "\n";
    }

    public static boolean isGraphEqualTo(CycleGraph c1, CycleGraph c2) {
        if (c1.path_1.getPredicate().getValueWithPrefix().equals(c2.path_2.getPredicate().getValueWithPrefix())
                && c1.path_2.getPredicate().getValueWithPrefix().equals(c2.path_1.getPredicate().getValueWithPrefix())) {
            return true;
        } else if (c1.path_1.getPredicate().getValueWithPrefix().equals(c2.path_1.getPredicate().getValueWithPrefix())
                && c1.path_2.getPredicate().getValueWithPrefix().equals(c2.path_2.getPredicate().getValueWithPrefix())) {
            return true;
        }
        return false;
    }

    public TriplePattern getPath_1() {
        return path_1;
    }

    public void setPath_1(TriplePattern path_1) {
        this.path_1 = path_1;
    }

    public TriplePattern getPath_2() {
        return path_2;
    }

    public void setPath_2(TriplePattern path_2) {
        this.path_2 = path_2;
    }

    public String getSeedType() {
        return seedType;
    }

    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

    public String getUnwantedPropertiesString() {
        return unwantedPropertiesString;
    }

    public void setUnwantedPropertiesString(String unwantedPropertiesString) {
        this.unwantedPropertiesString = unwantedPropertiesString;
    }

}
