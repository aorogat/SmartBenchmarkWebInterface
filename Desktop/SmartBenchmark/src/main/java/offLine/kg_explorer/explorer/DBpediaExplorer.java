package offLine.kg_explorer.explorer;

import java.util.ArrayList;
import offLine.kg_explorer.model.ListOfPredicates;
import offLine.kg_explorer.model.Predicate;
import offLine.kg_explorer.model.PredicateContext;
import offLine.kg_explorer.model.PredicateTripleExample;
import online.kg_extractor.knowledgegraph.DBpedia;
import online.kg_extractor.model.VariableSet;

/**
 *
 * @author aorogat
 */
public class DBpediaExplorer extends Explorer {

    private DBpediaExplorer(String url) {
        super();
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

        int predicatesSizeOld = 0;
        int predicatesSizeNew = 1;

//        do {
            predicatesSizeOld = predicates.size();
            getPredicateList(from, length);
            predicatesSizeNew = predicates.size();
            from += length;
            System.out.println("Predicates size = " + predicatesSizeNew);
            System.out.println(predicates.toString());
//        } while (predicatesSizeNew > predicatesSizeOld);
        System.out.println("Predicates size = " + predicatesSizeNew);
        System.out.println(predicates.toString());

        int i = 0;
        Predicate predicateObject = new Predicate(this);
        ArrayList<PredicateContext> contexts;

        for (VariableSet predicate : predicates) {
            predicateObject.setPredicateURI(predicate.toString().trim());
            predicateObject.setPredicate(removePrefix(predicate.toString().trim()));
            predicateObject.setLabel(getPredicateLabel(predicate.toString().trim()));
            predicateObject.setWeight(getPredicateWeight(predicate.toString().trim()));
            contexts = getPredicatesContext("<"+predicate.toString().trim()+">");
            for (PredicateContext context : contexts) {
                predicateObject.setTripleExamples(getOneTripleExample(predicate.toString().trim(), context.getSubjectType(), context.getObjectType(), predicateObject.getLabel(), 10));
                predicateObject.setPredicateContext(context);
                predicateList.add(predicateObject);
                if (i++ % 10 == 0) {
                    ListOfPredicates predicaes = new ListOfPredicates(predicateList);
                    predicaes.print();
                }
            }

        }
        ListOfPredicates predicaes = new ListOfPredicates(predicateList);
        return predicaes;
    }

    private void getPredicateList(int from, int length) {
        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        String query = "SELECT DISTINCT ?p WHERE { "
                + "?s ?p ?o. ?o ?t ?l. " //Get only if ?o is entity
                //                + "?s ?p ?o. "
                + " FILTER (?p NOT IN(" + unwantedPropertiesString + "))."
                //                + " FILTER strstarts(str(?p ), str(dbo:)). "  //NOT working, system return nothing
                + "} LIMIT " + length + " OFFSET " + from;
        predicates.addAll(kg.runQuery(query));

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

    private ArrayList<PredicateTripleExample> getOneTripleExample(String predicate, String sType, String oType, String lable, int noOfExamples) {
        String query = "";
        ArrayList<PredicateTripleExample> predicateTriples = predicateTriples = new ArrayList<>();
        try {
//            query = "SELECT DISTINCT ?s ?o WHERE { ?s <" + predicate.trim() + "> ?o . ?o ?t ?l. } LIMIT " + (noOfExamples - 1); //only those with entity object
            query = "SELECT DISTINCT ?s ?o WHERE { ?s <" + predicate.trim() + "> ?o ."
                    + "?s rdf:type <" + sType + ">. "
                    + "?o rdf:type <" + oType + ">. } LIMIT " + noOfExamples;
            predicatesTriples = kg.runQuery(query);
            for (VariableSet predicate1 : predicatesTriples) {
                String s = predicate1.getVariables().get(0).toString();
                String o = predicate1.getVariables().get(1).toString();
                PredicateTripleExample predicateTriple = new PredicateTripleExample("<" + s + ">", "<" + o + ">", removePrefix(s), removePrefix(o), lable, this);
                predicateTriples.add(predicateTriple);
            }
            return predicateTriples;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public ArrayList<PredicateContext> getPredicatesContext(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type    ?o_type \n"
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
                + "} ORDER By (str(?s_type))\n";
        predicatesTriples = kg.runQuery(query);
        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriples) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = predicate.getVariables().get(1).getValueWithPrefix();
//            System.out.println("stype:" + stype + ",  " + "otype:" + otype);
            predicateContexts.add(new PredicateContext(stype, otype));
//            System.out.println(predicate.toString());
        }
        return predicateContexts;

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
