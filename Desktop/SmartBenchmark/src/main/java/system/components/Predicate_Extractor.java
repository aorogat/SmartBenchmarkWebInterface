package system.components;

import java.util.ArrayList;
import java.util.HashSet;
import offLine.kg_explorer.explorer.Database;
import offLine.kg_explorer.model.ListOfPredicates;
import offLine.kg_explorer.model.Predicate;
import offLine.kg_explorer.model.PredicateContext;
import offLine.kg_explorer.model.PredicateTripleExample;
import online.kg_extractor.knowledgegraph.KnowledgeGraph;
import online.kg_extractor.model.VariableSet;
import online.kg_extractor.model.subgraph.Graph;
import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class Predicate_Extractor {

    private static int numberOfNLExamples = 100;
    private static int minContextWeight = 20;
    int counter = 0;
    public static KnowledgeGraph kg;
    public static String endpoint;
    public static ArrayList<VariableSet> predicatesVariableSet_entity = new ArrayList<>();
    public static ArrayList<VariableSet> predicatesVariableSet_number = new ArrayList<>();
    public static ArrayList<VariableSet> predicatesVariableSet_date = new ArrayList<>();

    public static ArrayList<VariableSet> predicatesTriplesVarSets;
    public static ArrayList<Graph> result = new ArrayList<>();
    protected ArrayList<Predicate> predicateList = new ArrayList<>();

    public Predicate_Extractor() {
        kg = KG_Settings.knowledgeGraph;
    }

    public static void main(String[] args) {
        Predicate_Extractor extractor = new Predicate_Extractor();
        extractor.exploreAllPredicates();
    }

    public void exploreAllPredicates() {
        ListOfPredicates predicats;
        int offset = 1000;
        int from = 0;
        boolean firstIteration = true;

        predicats = getLexiconLHS(from, offset);
        predicats.printHeader();
        predicats.print();
    }

    public ListOfPredicates getLexiconLHS(int from, int length) {

        predicatesVariableSet_entity = fillPredicatesURI_EntityObjects(from, length);
        predicatesVariableSet_number = fillPredicatesURI_NumberObjects(from, length);
        predicatesVariableSet_date = fillPredicatesURI_DateObjects(from, length);

        //get predicates LHS (e.g., uri, label, contexts)
        int i = 0;
        Predicate predicateObject = new Predicate(KG_Settings.explorer);
        ArrayList<PredicateContext> contexts;
        ListOfPredicates predicates = new ListOfPredicates(predicateList);

        for (VariableSet predicate : predicatesVariableSet_entity) {
            System.out.println("###################" + ++counter + ": New Predicate: " + predicate.toString().trim() + " ################### ");

            String uri = predicate.toString().trim();
            String predi = KG_Settings.explorer.removePrefix(predicate.toString().trim());
            String lab = getPredicateLabel(uri);
            contexts = getPredicatesContext_EntityObjects("<" + uri + ">");

            for (PredicateContext context : contexts) {
                predicateObject = new Predicate(KG_Settings.explorer);
                predicateObject.setPredicateURI(uri);
                predicateObject.setPredicate(predi);
                predicateObject.setLabel(lab);

                predicateObject.setPredicateContext(context);
                predicateObject.print();
                predicates.getPredicates().add(predicateObject);
                try {
                    Database.storePredicates(predicateObject);
                } catch (Exception e) {
                    System.out.println("XXXXXXXXXX NOT SOTRED XXXXXXXXXXXXX");
                }
            }

        }

        //numbers
        for (VariableSet predicate : predicatesVariableSet_number) {
            System.out.println("###################" + ++counter + ": New Predicate: " + predicate.toString().trim() + " ################### ");

            String uri = predicate.toString().trim();
            String predi = KG_Settings.explorer.removePrefix(predicate.toString().trim());
            String lab = getPredicateLabel(uri);
            contexts = getPredicatesContext_NumberObjects("<" + uri + ">");

            for (PredicateContext context : contexts) {
                predicateObject = new Predicate(KG_Settings.explorer);
                predicateObject.setPredicateURI(uri);
                predicateObject.setPredicate(predi);
                predicateObject.setLabel(lab);

                predicateObject.setPredicateContext(context);
                predicateObject.print();
                predicates.getPredicates().add(predicateObject);
                try {
                    Database.storePredicates(predicateObject);
                } catch (Exception e) {
                    System.out.println("XXXXXXXXXX NOT SOTRED XXXXXXXXXXXXX");
                }
            }

        }

        //dates
        for (VariableSet predicate : predicatesVariableSet_date) {
            System.out.println("###################" + ++counter + ": New Predicate: " + predicate.toString().trim() + " ################### ");

            String uri = predicate.toString().trim();
            String predi = KG_Settings.explorer.removePrefix(predicate.toString().trim());
            String lab = getPredicateLabel(uri);
            contexts = getPredicatesContext_DateObjects("<" + uri + ">");

            for (PredicateContext context : contexts) {
                predicateObject = new Predicate(KG_Settings.explorer);
                predicateObject.setPredicateURI(uri);
                predicateObject.setPredicate(predi);
                predicateObject.setLabel(lab);

                predicateObject.setPredicateContext(context);
                predicateObject.print();
                predicates.getPredicates().add(predicateObject);
                try {
                    Database.storePredicates(predicateObject);
                } catch (Exception e) {
                    System.out.println("XXXXXXXXXX NOT SOTRED XXXXXXXXXXXXX");
                }
            }

        }

        predicates.setPredicates(predicateList);
        return predicates;
    }

    public ArrayList<VariableSet> fillPredicatesURI_EntityObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();
        predicateList.clear();

        int predicatesSizeOld = 0;
        int predicatesSizeNew = 1;

        //get predicates URI from KG
        do {
            predicatesSizeOld = predicatesVariableSet.size();
            predicatesVariableSet.addAll(getPredicateList_EntityObjects(from, length));

            predicatesSizeNew = predicatesVariableSet.size();
            from += length;
            System.out.println("Predicates size = " + predicatesSizeNew);
            System.out.println(predicatesVariableSet.toString());
        } while (predicatesSizeNew > predicatesSizeOld);
        System.out.println("Predicates size = " + predicatesSizeNew);
        System.out.println(predicatesVariableSet.toString());

        return predicatesVariableSet;
    }

    public ArrayList<VariableSet> fillPredicatesURI_NumberObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();
        predicateList.clear();

        int predicatesSizeOld = 0;
        int predicatesSizeNew = 1;

        //get predicates URI from KG
        do {
            predicatesSizeOld = predicatesVariableSet.size();
            predicatesVariableSet.addAll(getPredicateList_NumberObjects(from, length));

            predicatesSizeNew = predicatesVariableSet.size();
            from += length;
            System.out.println("Predicates size = " + predicatesSizeNew);
            System.out.println(predicatesVariableSet.toString());
        } while (predicatesSizeNew > predicatesSizeOld);
        System.out.println("Predicates size = " + predicatesSizeNew);
        System.out.println(predicatesVariableSet.toString());

        return predicatesVariableSet;
    }

    public ArrayList<VariableSet> fillPredicatesURI_DateObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();
        predicateList.clear();

        int predicatesSizeOld = 0;
        int predicatesSizeNew = 1;

        //get predicates URI from KG
        do {
            predicatesSizeOld = predicatesVariableSet.size();
            predicatesVariableSet.addAll(getPredicateList_DateObjects(from, length));

            predicatesSizeNew = predicatesVariableSet.size();
            from += length;
            System.out.println("Predicates size = " + predicatesSizeNew);
            System.out.println(predicatesVariableSet.toString());
        } while (predicatesSizeNew > predicatesSizeOld);
        System.out.println("Predicates size = " + predicatesSizeNew);
        System.out.println(predicatesVariableSet.toString());

        return predicatesVariableSet;
    }

    private ArrayList<VariableSet> getPredicateList_EntityObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        String query = "SELECT DISTINCT ?p WHERE { "
                + "?s ?p ?o. ?o ?t ?l. " //Get only if ?o is entity
                + " FILTER (?p NOT IN(" + unwantedPropertiesString + "))."
                + "} LIMIT " + length + " OFFSET " + from;
        predicatesVariableSet.addAll(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    private ArrayList<VariableSet> getPredicateList_NumberObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();

        predicateList.clear();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();

        //get predicates where the object is number
        String query = "select ?p where {\n"
                + "  ?p a owl:DatatypeProperty.\n"
                + "     {?p rdfs:range xsd:integer} UNION {?p rdfs:range xsd:float}UNION {?p rdfs:range xsd:double}UNION {?p rdfs:range xsd:decimal}.\n"
                + " FILTER (?p NOT IN(" + unwantedPropertiesString + "))."
                + "} LIMIT " + length + " OFFSET " + from;
        predicatesVariableSet.addAll(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

    private ArrayList<VariableSet> getPredicateList_DateObjects(int from, int length) {
        ArrayList<VariableSet> predicatesVariableSet = new ArrayList<>();

        predicateList.clear();

        //get predicates where the object is entity
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();

        //get predicates where the object is number
        String query = "select ?p where {\n"
                + "  ?p a owl:DatatypeProperty ;\n"
                + "     rdfs:range xsd:date .\n"
                + " FILTER (?p NOT IN(" + unwantedPropertiesString + "))."
                + "} LIMIT " + length + " OFFSET " + from;
        predicatesVariableSet.addAll(kg.runQuery(query));

        //Remove duplicates
        predicatesVariableSet = new ArrayList<>(new HashSet<>(predicatesVariableSet));
        return predicatesVariableSet;
    }

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

    public ArrayList<PredicateTripleExample> getOneTripleExample(String predicate, String sType, String oType, String lable, int noOfExamples) {
        String query = "";
        ArrayList<PredicateTripleExample> predicateTriples = predicateTriples = new ArrayList<>();
        try {
            if (oType.equals("Number")) {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"
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
                        //Get only dbpedia types
                        + "  FILTER strstarts(str(<" + sType + ">  ), str(dbo:)).\n"
                        + "FILTER isNumeric(?o)."
                        + "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            } else if (oType.equals("Date")) {
                query = "SELECT DISTINCT ?s ?o WHERE { \n"
                        + "?s <" + predicate.trim() + "> ?o .\n"
                        + "?s rdf:type <" + sType + ">. \n"
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
                        //Get only dbpedia types
                        + "  FILTER strstarts(str(<" + sType + ">  ), str(dbo:)).\n"
                        + " FILTER (datatype(?o) = xsd:dateTime ). \n"
                        + "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            } else {
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
                        //Get only dbpedia types
                        + "  FILTER strstarts(str(<" + sType + ">  ), str(dbo:)).\n"
                        + "  FILTER strstarts(str(<" + oType + "> ), str(dbo:)).\n"
                        + "\n"
                        + "\n"
                        + "} LIMIT " + noOfExamples;
            }
            predicatesTriplesVarSets = kg.runQuery(query);
            if (predicatesTriplesVarSets.size() > noOfExamples) {
                predicatesTriplesVarSets = new ArrayList<>(predicatesTriplesVarSets.subList(0, noOfExamples));
            }
            for (VariableSet predicate1 : predicatesTriplesVarSets) {
                String s = predicate1.getVariables().get(0).toString();
                String o = predicate1.getVariables().get(1).toString();
                PredicateTripleExample predicateTriple = new PredicateTripleExample("<" + s + ">", "<" + o + ">", KG_Settings.explorer.removePrefix(s), KG_Settings.explorer.removePrefix(o), lable, KG_Settings.explorer);
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

    public static ArrayList<PredicateContext> getPredicatesContext_EntityObjects(String predicateURI) {
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
                + "  FILTER (strstarts(str(?s_type ), str(dbo:))).\n"
                + "  FILTER (strstarts(str(?o_type ), str(dbo:))).\n"
                //You can filter out types like (agent, ....)
                //                + "  FILTER (?s_type NOT IN (dbo:Agent, dbo:Settlement)).\n"
                //                + "  FILTER (?o_type NOT IN (dbo:Agent, dbo:Settlement)).\n"
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

    public static ArrayList<PredicateContext> getPredicatesContext_NumberObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  (count(?s) as ?count)\n"
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
                + "  FILTER (strstarts(str(?s_type ), str(dbo:))).\n"
                + "} GROUP BY ?s_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = "Number";
            String weightString = predicate.getVariables().get(1).getValueWithPrefix();
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

    public static ArrayList<PredicateContext> getPredicatesContext_DateObjects(String predicateURI) {
        String unwantedPropertiesString = kg.getUnwantedPropertiesString();
        long weight = 0;
        String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
                + "PREFIX schema: <http://schema.org/> \n"
                + " \n"
                + "SELECT DISTINCT ?s_type  (count(?s) as ?count)\n"
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
                + "  FILTER (strstarts(str(?s_type ), str(dbo:))).\n"
                + "} GROUP BY ?s_type "
                + "  ORDER By (str(?s_type))\n";
        predicatesTriplesVarSets = kg.runQuery(query);
        //remove duplicates as sometimes Distinct does not work in the KGMS
        predicatesTriplesVarSets = new ArrayList<>(new HashSet<>(predicatesTriplesVarSets));

        ArrayList<PredicateContext> predicateContexts = new ArrayList<>();
        for (VariableSet predicate : predicatesTriplesVarSets) {
            String stype = predicate.getVariables().get(0).getValueWithPrefix();
            String otype = "Date";
            String weightString = predicate.getVariables().get(1).getValueWithPrefix();
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
