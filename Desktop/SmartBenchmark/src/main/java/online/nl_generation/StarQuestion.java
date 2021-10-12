package online.nl_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.subgraph.StarGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;

public class StarQuestion {

    public static Map<String, HashSet<String>> starPredicates = new HashMap<>();
    public static String seed;
    public static String type = "";

    public static ArrayList<GeneratedQuestion> generateQuestions(StarGraph g) {

        seed = g.getStar().get(0).getSource().getValue();

        //get type - Fill starPredicates map
        for (TriplePattern triple : g.getStar()) {
            String typeLabel = triple.getLabel().getValue();
            String s = triple.getSource().getValue();
            String o = triple.getDestination().getValue();

            if (typeLabel.equals("rdf:type") || typeLabel.equals("a")) {
                type = triple.getDestination().getValue();
                continue;
            }
            if (!starPredicates.containsKey(typeLabel)) {
                HashSet<String> objects = new HashSet<>();
                objects.add(o);
                starPredicates.put(typeLabel, objects);
            } else {
                starPredicates.get(typeLabel).add(o);
            }
        }

        ArrayList<GeneratedQuestion> questions = new ArrayList<>();

        questions.add(generate_WithAnd_Question(g, QuestionType.which));// also include request
        questions.add(generate_WithAnd_Question(g, QuestionType.how_many));
        questions.add(generate_WithAnd_Question(g, QuestionType.yes_no));

        if (starPredicates.size() > 1) {
            questions.add(generate_WithOr_Question(g, QuestionType.which));
            questions.add(generate_WithOr_Question(g, QuestionType.how_many));
            questions.add(generate_WithOr_Question(g, QuestionType.yes_no));
        }

        //To remove duplicates
        return new ArrayList<>(new HashSet<>(questions));
    }

    private static GeneratedQuestion generate_WithAnd_Question(StarGraph g, int questionType) {
        ArrayList<String> questionStrings = new ArrayList<>();
        String seedValue = g.getStar().get(0).getSource().getValue();
        String seedValueWithPrefix = g.getStar().get(0).getSource().getValueWithPrefix();

        //Construct the Question Strings
        switch (questionType) {
            case QuestionType.which:
            case QuestionType.request:
                questionStrings.add("Which " + type + starPredicates_NP_ToString("and") + "?");
                questionStrings.add("Which " + type + starPredicates_VP_ToString("and") + "?");
                String requestPrefix = Request.getRequestPrefix();
                questionStrings.add(requestPrefix + " " + type + " that" + starPredicates_NP_ToString("and") + "?");
                questionStrings.add(requestPrefix + " " + type + " that" + starPredicates_VP_ToString("and") + "?");
                break;
            case QuestionType.how_many:
                questionStrings.add("How many " + type + starPredicates_NP_ToString("and") + "?");
                questionStrings.add("How many " + type + starPredicates_VP_ToString("and") + "?");
                break;
            case QuestionType.yes_no:
                questionStrings.add("Is " + seedValue + starPredicates_NP_ToString("and").replace(" is ", " ") + "?");
                questionStrings.add("Does " + seedValue + starPredicates_VP_ToString_Yes_No_direction("and") + "?");
                break;
            default:;
        }

        //Construct the Query
        String query = "";
        String triples = "";
        ArrayList<TriplePattern> star = g.getStar();
        for (TriplePattern triple : star) {
            triples += "\n\t" + triple.toQueryTriplePattern() + ". ";
        }

        triples = triples.replace(seedValueWithPrefix, "?Seed");
        switch (questionType) {
            case QuestionType.which:
            case QuestionType.request:
                query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
                break;
            case QuestionType.how_many:
                query = "SELECT (COUNT(DISTINCT ?Seed) as ?count) WHERE{" + triples + "\n}";
                break;
            case QuestionType.yes_no:
                query = "ASK WHERE{" + triples.replace("?Seed", seedValueWithPrefix) + "\n}";
                break;
            default:
                query = "";
        }
        return new GeneratedQuestion(questionStrings, query);
    }

    private static GeneratedQuestion generate_WithOr_Question(StarGraph g, int questionType) {
        String seedValue = g.getStar().get(0).getSource().getValue();
        String seedValueWithPrefix = g.getStar().get(0).getSource().getValueWithPrefix();
        Map<String, HashSet<String>> starPredicatesTriples = new HashMap<>();
        ArrayList<String> questionStrings = new ArrayList<>();
        String query = "";

        //Construct the Question Strings
        switch (questionType) {
            case QuestionType.which:
            case QuestionType.request:
                questionStrings.add("Which " + type + starPredicates_NP_ToString("or") + "?");
                questionStrings.add("Which " + type + starPredicates_VP_ToString("or") + "?");
                String requestPrefix = Request.getRequestPrefix();
                questionStrings.add(requestPrefix + " " + type + " that" + starPredicates_NP_ToString("or") + "?");
                questionStrings.add(requestPrefix + " " + type + " that" + starPredicates_VP_ToString("or") + "?");
                break;
            case QuestionType.how_many:
                questionStrings.add("How many " + type + starPredicates_NP_ToString("or") + "?");
                questionStrings.add("How many " + type + starPredicates_VP_ToString("or") + "?");
                break;
            case QuestionType.yes_no:
                questionStrings.add("Is " + seedValue + starPredicates_NP_ToString("or").replace(" is ", " ") + "?");
                questionStrings.add("Does " + type + starPredicates_VP_ToString_Yes_No_direction("or") + "?");
                break;
            default:;
        }

        String triples = "";
        ArrayList<TriplePattern> star = g.getStar();

        for (TriplePattern triple : star) {
            String typeLabel = triple.getLabel().getValueWithPrefix();
            String s = triple.getSource().getValueWithPrefix();
            String tripleString = triple.toQueryTriplePattern();
            if (!starPredicatesTriples.containsKey(typeLabel)) {
                HashSet<String> objects = new HashSet<>();
                objects.add(tripleString);
                starPredicatesTriples.put(typeLabel, objects);
            } else {
                starPredicatesTriples.get(typeLabel).add(tripleString);
            }
        }

        //iterate over the map
        //get type triple
        String typeTriple = "";
        for (Map.Entry<String, HashSet<String>> entry : starPredicatesTriples.entrySet()) {
            ArrayList<String> sharedpredicateTriples = new ArrayList<>(entry.getValue());
            if (entry.getKey().equals("rdf:type") || entry.getKey().equals("a")) {
                typeTriple = sharedpredicateTriples.get(0);
            } 
        }
        
        for (Map.Entry<String, HashSet<String>> entry : starPredicatesTriples.entrySet()) {
            String triplesGroup = "\n\t\t" + typeTriple + ".";
            ArrayList<String> sharedpredicateTriples = new ArrayList<>(entry.getValue());
            if (!(entry.getKey().equals("rdf:type") || entry.getKey().equals("a"))) {
                for (String triple : sharedpredicateTriples) {
                    triplesGroup += "\n\t\t" + triple + ". ";
                }
                triples += "\n\t{" + triplesGroup + "\n\t} UNION ";
            }
        }

        triples = triples.subSequence(0, triples.length() - 6) + " ";

        triples = triples.replace(seedValueWithPrefix, "?Seed");

        switch (questionType) {
            case QuestionType.which:
            case QuestionType.request:
                query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
                break;
            case QuestionType.how_many:
                query = "SELECT (COUNT(DISTINCT ?Seed) as ?count) WHERE{" + triples + "\n}";
                break;
            case QuestionType.yes_no:
                query = "ASK WHERE{" + triples.replace("?Seed", seedValueWithPrefix) + "\n}";
                break;
            default:
                query = "";
        }

        return new GeneratedQuestion(questionStrings, query);
    }

    private static String starPredicates_NP_ToString(String conjenction) {
        String starString = "";
        conjenction = ", " + conjenction;
        String objectsList = "";
        boolean firstIteration = true;
        for (String p : starPredicates.keySet()) {
            if (starPredicates.size() <= 1) {
                conjenction = "";
            }
            if (!firstIteration) {
                starString += conjenction;
            }
            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
            String o = objects.get(0);
            objectsList = objectListToString(objects);
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p, KGOntology.getType(seed), KGOntology.getType(o));
            starString += " is the " + predicateNL.getPredicate_s_O_NP() + " of " + objectsList;

            firstIteration = false;
        }
        return starString;
    }

    private static String starPredicates_VP_ToString(String conjenction) {
        String starString = "";
        conjenction = ", " + conjenction;
        boolean firstIteration = true;
        String objectsList = "";
        for (String p : starPredicates.keySet()) {
            if (starPredicates.size() <= 1) {
                conjenction = "";
            }
            if (!firstIteration) {
                starString += conjenction;
            }
            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
            objectsList = objectListToString(objects);
            String o = objects.get(0);
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p, KGOntology.getType(seed), KGOntology.getType(o));
            starString += " " + objectsList + " " + predicateNL.getPredicate_o_s_VP();
            firstIteration = false;
        }
        return starString;
    }
    
    private static String starPredicates_VP_ToString_Yes_No_direction(String conjenction) {
        String starString = "";
        conjenction = ", " + conjenction;
        boolean firstIteration = true;
        String objectsList = "";
        for (String p : starPredicates.keySet()) {
            if (starPredicates.size() <= 1) {
                conjenction = "";
            }
            if (!firstIteration) {
                starString += conjenction;
            }
            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
            objectsList = objectListToString(objects);
            String o = objects.get(0);
            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p, KGOntology.getType(seed), KGOntology.getType(o));
            starString += " " + predicateNL.getPredicate_o_s_VP() + " " + objectsList;
            firstIteration = false;
        }
        return starString;
    }

    public static String objectListToString(ArrayList<String> objects) {
        String objectsList = "";
        if (objects.size() == 1) {
            objectsList = objects.get(0);
        } else if (objects.size() == 2) {
            String o1 = objects.get(0);
            String o2 = objects.get(1);
            objectsList = "both " + o1 + " and " + o2;
        } else if (objects.size() > 2) {

            //represent objectsList
            objectsList += objects.get(0);
            for (int i = 1; i < objects.size() - 1; i++) {
                objectsList += ", " + objects.get(i);
            }
            objectsList += " and " + objects.get(objects.size() - 1);
        }
        return objectsList;
    }

}
