package offLine.kg_explorer.explorer;

import java.util.ArrayList;
import java.util.HashSet;
import static offLine.kg_explorer.explorer.Explorer.kg;
import static offLine.kg_explorer.explorer.Explorer.predicatesTriplesVarSets;
import offLine.kg_explorer.model.PredicateContext;
import offLine.kg_explorer.model.PredicateTripleExample;
import online.kg_extractor.model.VariableSet;

/**
 *
 * @author aorogat
 */
public class SPARQL {
    public static String getPredicateLabel(String predicate) {
        String query = "";
        //get labels
        try {
            query = "SELECT DISTINCT ?l WHERE { ?p rdfs:label ?l. "
                    + "FILTER(?p=<" + predicate.trim() + ">). "
                    + "FILTER langMatches( lang(?l), \"EN\" )."
                    + "}";
            predicatesTriplesVarSets = kg.runQuery(query);
            return predicatesTriplesVarSets.get(0).getVariables().get(0).toString();
        } catch (Exception e) {
            return (predicate.trim());
        }
    }

    public static long getPredicateWeight(String predicate, String sType, String oType) {
        String query = "";
        //get weights
        try {
            query = "SELECT (count(?p) as ?count) WHERE { ?s ?p ?o . "
                    + "?s rdf:type <" + sType + ">. "
                    + "?o rdf:type <" + oType + ">. "
                    + "FILTER(?p=<" + predicate.trim() + ">)."
                    + ""
                    + "    FILTER NOT EXISTS {\n"
                    + "      ?s rdf:type ?type1 .\n"
                    + "      ?type1 rdfs:subClassOf <" + sType + ">.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      <" + sType + "> rdfs:subClassOf ?superType1 .\n"
                    + "      ?s rdf:type ?superType1 .\n"
                    + "    }.\n"
                    + "\n"
                    + "   ?o      rdf:type              <" + oType + ">.\n"
                    + "    FILTER NOT EXISTS {\n"
                    + "      ?o rdf:type ?type2 .\n"
                    + "      ?type2 rdfs:subClassOf <" + oType + ">.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type2 owl:equivalentClass <" + oType + ">.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      <" + oType + "> rdfs:subClassOf ?superType2 .\n"
                    + "      ?o rdf:type ?superType2 .\n"
                    + "    }.\n"
                    + "  FILTER strstarts(str(<" + sType + ">  ), str(dbo:)).\n"
                    + "  FILTER strstarts(str(<" + oType + "> ), str(dbo:)).\n"
                    + ""
                    + "}";
            predicatesTriplesVarSets = kg.runQuery(query);
            return Long.valueOf(predicatesTriplesVarSets.get(0).getVariables().get(0).toString());
        } catch (Exception e) {
            return -1;
        }
    }

    public static ArrayList<PredicateTripleExample> getOneTripleExample(String predicate, String sType, String oType, String lable, int noOfExamples) {
        String query = "";
        ArrayList<PredicateTripleExample> predicateTriples = predicateTriples = new ArrayList<>();
        try {
//            query = "SELECT DISTINCT ?s ?o WHERE { ?s <" + predicate.trim() + "> ?o . ?o ?t ?l. } LIMIT " + (noOfExamples - 1); //only those with entity object
            query = "SELECT DISTINCT ?s ?o WHERE { \n"
                    + "?s <" + predicate.trim() + "> ?o .\n"
                    + "?s rdf:type <" + sType + ">. \n"
                    + "?o rdf:type <" + oType + ">. \n"
                    + "\n"
                    + "\n"
                    + "    FILTER NOT EXISTS {\n"
                    + "      ?s rdf:type ?type1 .\n"
                    + "      ?type1 rdfs:subClassOf <" + sType + ">.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type1 owl:equivalentClass <" + sType + ">.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      <" + sType + "> rdfs:subClassOf ?superType1 .\n"
                    + "      ?s rdf:type ?superType1 .\n"
                    + "    }.\n"
                    + "\n"
                    + "\n"
                    + "\n"
                    + "    FILTER NOT EXISTS {\n"
                    + "      ?o rdf:type ?type2 .\n"
                    + "      ?type2 rdfs:subClassOf <" + oType + ">.\n"
                    + "      FILTER NOT EXISTS {\n"
                    + "         ?type2 owl:equivalentClass <" + oType + ">.\n"
                    + "      }\n"
                    + "    }.\n"
                    + "    FILTER EXISTS {\n"
                    + "      <" + oType + "> rdfs:subClassOf ?superType2 .\n"
                    + "      ?o rdf:type ?superType2 .\n"
                    + "    }.\n"
                    + "\n"
                    + "\n"
                    //Get only s and o with only one predicate between them /////// NOT WORKING ?! Why ///////
                    //                    + "    FILTER NOT EXISTS {\n"
                    //                    + "      ?s ?pp ?o .\n"
                    //                    + "      FILTER(?pp = <" + predicate.trim() + ">).\n"
                    //                    + "    }.\n"
                    + "\n"
                    + "\n"
                    + "\n"
                    //Get only dbpedia types
                    + "  FILTER strstarts(str(<" + sType + ">  ), str(dbo:)).\n"
                    + "  FILTER strstarts(str(<" + oType + "> ), str(dbo:)).\n"
                    + "\n"
                    + "\n"
                    + "} LIMIT " + noOfExamples;
            predicatesTriplesVarSets = kg.runQuery(query);
            if (predicatesTriplesVarSets.size() > noOfExamples) {
                predicatesTriplesVarSets = new ArrayList<>(predicatesTriplesVarSets.subList(0, noOfExamples));
            }
            for (VariableSet predicate1 : predicatesTriplesVarSets) {
                String s = predicate1.getVariables().get(0).toString();
                String o = predicate1.getVariables().get(1).toString();
                PredicateTripleExample predicateTriple = new PredicateTripleExample("<" + s + ">", "<" + o + ">", removePrefix(s), removePrefix(o), lable, this);
                predicateTriples.add(predicateTriple);
                //To speed up the system. break after one VP.
//                if(predicateTriple.getNlsSuggestionsObjects().size()>=1)
//                    break;
            }
            return predicateTriples;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<PredicateContext> getPredicatesContext(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  ?o_type  (count(?s) as ?count)\n"
                //                + "SELECT DISTINCT SAMPLE(?s) SAMPLE(?o) ?s_type    ?o_type \n"
                + "WHERE{\n"
                + "?s      " + predicateURI + "      ?o.\n"
                + "?s      rdf:type              ?s_type.\n"
                + "    FILTER NOT EXISTS {\n"
                + "      ?s rdf:type ?type1 .\n"
                + "      ?type1 rdfs:subClassOf ?s_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type1 owl:equivalentClass ?s_type.\n"
                + "      }\n"
                + "    }.\n"
                + "    FILTER EXISTS {\n"
                + "      ?s_type rdfs:subClassOf ?superType1 .\n"
                + "      ?s rdf:type ?superType1 .\n"
                + "    }.\n"
                + "\n"
                + "   ?o      rdf:type              ?o_type.\n"
                + "    FILTER NOT EXISTS {\n"
                + "      ?o rdf:type ?type2 .\n"
                + "      ?type2 rdfs:subClassOf ?o_type.\n"
                + "      FILTER NOT EXISTS {\n"
                + "         ?type2 owl:equivalentClass ?o_type.\n"
                + "      }\n"
                + "    }.\n"
                + "    FILTER EXISTS {\n"
                + "      ?o_type rdfs:subClassOf ?superType2 .\n"
                + "      ?o rdf:type ?superType2 .\n"
                + "    }.\n"
                + "  FILTER strstarts(str(?s_type ), str(dbo:)).\n"
                + "  FILTER strstarts(str(?o_type ), str(dbo:)).\n"
                //You can filter out types like (agent, ....)
                + "  FILTER (?s_type NOT IN (dbo:Agent, dbo:Settlement)).\n"
                + "  FILTER (?o_type NOT IN (dbo:Agent, dbo:Settlement)).\n"
                + "} GROUP BY ?s_type  ?o_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = predicate.getVariables().get(1).getValueWithPrefix();
            String weightString = predicate.getVariables().get(2).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
//            weight = getPredicateWeight(predicateURI.replace("<", "").replace(">", ""), stype, otype);
            weight = Long.parseLong(weightString);
            predicateContexts.add(new PredicateContext(stype, otype, weight));
//            System.out.println(predicate.toString());
        }
        System.out.println("Predicate Context list size before filteration: " + predicateContexts.size());
        predicateContexts = filterOutNoisyContexts(predicateContexts);
        System.out.println("Predicate Context list size after filteration: " + predicateContexts.size());
        return predicateContexts;

    }

    public static ArrayList<PredicateContext> filterOutNoisyContexts(ArrayList<PredicateContext> contexts) {
        ArrayList<PredicateContext> newContexts = new ArrayList<>();
        double mean = 0;
        double sum = 0;

        for (PredicateContext context : contexts) {
            sum += context.getWeight();
        }
        mean = sum / (double) contexts.size();

        for (PredicateContext context : contexts) {
            if (context.getWeight() >= mean) {
                newContexts.add(context);
            }
        }
        return newContexts;
    }

}
