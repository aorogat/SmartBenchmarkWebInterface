package online.nl_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.ontology.KGOntology;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.subgraph.StarGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import settings.KG_Settings;

public class StarQuestion {

    //public static Map<String, HashSet<String>> starPredicates = new HashMap<>();
    //public static String seed;
    //public static String seed_with_Prefix;
    //public static String type = "";
    //public static String type_with_Prefix = "";
    StarGraph starGraph;
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();
    String T;
    String FCs_AND;
    String FCs_OR;
    String FCs_AND_NOT;
    String FCs_OR_NOT;
    String FCs_NOT_NOT;
    
    private String somethingElse = "http://AnnyOther";
    private String somethingElseWithoutPrefix = "AnnyOther";

    String GPs_ASK; //for Graph Patterns

    public StarQuestion(StarGraph starGraph) {
        this.starGraph = starGraph;
        
        somethingElse = SPARQL.getSimilarEntity(KG_Settings.explorer, starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type());
        somethingElseWithoutPrefix = KG_Settings.explorer.removePrefix(somethingElse);

        T = KG_Settings.explorer.removePrefix(starGraph.getSeedType()).toLowerCase();
        
        Map<String, HashSet<String>> starPredicates = new HashMap<>();

        //Fill starPredicates map to make star as (p1, O1.1, O1.2,...), ..... (P2, O2.1,O2.2,...)
        for (TriplePattern triple : starGraph.getStar()) {
            String p = triple.getPredicate().getValue();
            String s = triple.getSubject().getValue();
            String o = triple.getObject().getValue();

            if (!starPredicates.containsKey(p)) {
                HashSet<String> objects = new HashSet<>();
                objects.add(o);
                starPredicates.put(p, objects);
            } else {
                starPredicates.get(p).add(o);
            }
        }

        FCs_AND = factConstraints_toString(starGraph, CoordinatingConjunction.AND, starPredicates);
        FCs_OR = factConstraints_toString(starGraph, CoordinatingConjunction.OR, starPredicates);
        FCs_AND_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.AND_NOT, starPredicates);
        FCs_OR_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.OR_NOT, starPredicates);
        FCs_NOT_NOT = factConstraints_toString(starGraph, CoordinatingConjunction.NOT_NOT, starPredicates);

        if (starPredicates.size() == 1) {
            selectQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.AND);
            askQuestions_true_answer(CoordinatingConjunction.AND);
            askQuestions_false_answer(CoordinatingConjunction.AND);
            
        } else if (starPredicates.size() == 2) {
            selectQuestions(CoordinatingConjunction.AND);
            selectQuestions(CoordinatingConjunction.OR);
            selectQuestions(CoordinatingConjunction.AND_NOT);
            selectQuestions(CoordinatingConjunction.OR_NOT);
            selectQuestions(CoordinatingConjunction.NOT_NOT);

            countQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.OR);
            countQuestions(CoordinatingConjunction.AND_NOT);
            countQuestions(CoordinatingConjunction.OR_NOT);
            countQuestions(CoordinatingConjunction.NOT_NOT);

            askQuestions_true_answer(CoordinatingConjunction.AND);
//            askQuestions_true_answer(CoordinatingConjunction.OR);
//            askQuestions_true_answer(CoordinatingConjunction.AND_NOT);
//            askQuestions_true_answer(CoordinatingConjunction.OR_NOT);
//            askQuestions_true_answer(CoordinatingConjunction.NOT_NOT);
            
            askQuestions_false_answer(CoordinatingConjunction.AND);
//            askQuestions_false_answer(CoordinatingConjunction.OR);
//            askQuestions_false_answer(CoordinatingConjunction.AND_NOT);
//            askQuestions_false_answer(CoordinatingConjunction.OR_NOT);
//            askQuestions_false_answer(CoordinatingConjunction.NOT_NOT);

        } else if (starPredicates.size() > 2) {
            selectQuestions(CoordinatingConjunction.AND);
            selectQuestions(CoordinatingConjunction.OR);

            countQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.OR);

            askQuestions_true_answer(CoordinatingConjunction.AND);
//            askQuestions_true_answer(CoordinatingConjunction.OR);
            
            askQuestions_false_answer(CoordinatingConjunction.AND);
//            askQuestions_false_answer(CoordinatingConjunction.OR);
        }
    }

    private String askQuery_true_answer(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + starGraph.getStar().get(0).getSubject().getValueWithPrefix() + ">");
    }
    
    private String askQuery_false_answer(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + somethingElse + ">");
    }

    private String countQuery(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction).replace("SELECT DISTINCT ?Seed WHERE{", "SELECT (COUNT (?Seed) AS ?count) WHERE{");
    }

    private void countQuestions(String coordinatingConjunction) {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }
        if (FCs != null) {
            ArrayList<String> questions = new ArrayList<>();
            String whichQuestion = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", "How many");
            questions.add(whichQuestion);

            String countQuery = countQuery(starGraph, coordinatingConjunction);

            GeneratedQuestion generatedQuestion = new GeneratedQuestion(questions, countQuery, starGraph.toString());
            allPossibleQuestions.add(generatedQuestion);
        }
    }

    private void askQuestions_true_answer(String coordinatingConjunction) {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }
        if (FCs != null) {
            ArrayList<String> questions = new ArrayList<>();
            String whichQuestion = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", "Is " + starGraph.getStar().get(0).getSubject().getValue());
            questions.add(whichQuestion);

            String askQuery = askQuery_true_answer(starGraph, coordinatingConjunction);

            GeneratedQuestion generatedQuestion = new GeneratedQuestion(questions, askQuery, starGraph.toString());
            allPossibleQuestions.add(generatedQuestion);
        }
    }
    
    
    private void askQuestions_false_answer(String coordinatingConjunction) {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }
        if (FCs != null) {
            ArrayList<String> questions = new ArrayList<>();
            String whichQuestion = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", "Is " + somethingElseWithoutPrefix)
                    .replace(FCs, FCs);
            questions.add(whichQuestion);

            String askQuery = askQuery_false_answer(starGraph, coordinatingConjunction);

            GeneratedQuestion generatedQuestion = new GeneratedQuestion(questions, askQuery, starGraph.toString());
            allPossibleQuestions.add(generatedQuestion);
        }
    }

    private void selectQuestions(String coordinatingConjunction) {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }
        if (FCs != null) {
            ArrayList<String> questions = new ArrayList<>();
            String whichQuestion = selectWhichQuestions(coordinatingConjunction);
            questions.add(whichQuestion);

            String requestQuestion = whichQuestion.replace("Which ", Request.getRequestPrefix().trim() + " which ");
            questions.add(requestQuestion);

            String selectQuery = selectQuery(starGraph, coordinatingConjunction);

            GeneratedQuestion generatedQuestion = new GeneratedQuestion(questions, selectQuery, starGraph.toString());
            allPossibleQuestions.add(generatedQuestion);
        }
    }

    private String selectWhichQuestions(String coordinatingConjunction) {
        String FCs = "";
        switch (coordinatingConjunction) {
            case CoordinatingConjunction.AND:
                FCs = FCs_AND;
                break;
            case CoordinatingConjunction.AND_NOT:
                FCs = FCs_AND_NOT;
                break;
            case CoordinatingConjunction.NOT_NOT:
                FCs = FCs_NOT_NOT;
                break;
            case CoordinatingConjunction.OR:
                FCs = FCs_OR;
                break;
            case CoordinatingConjunction.OR_NOT:
                FCs = FCs_OR_NOT;
                break;
            default:
        }

        if (FCs != null) {
            String whichQuestion = "Which " + T + FCs + "?";
            return whichQuestion;
        }
        return null;
    }

    private String selectQuery(StarGraph starGraph, String coordinatingConjunction) {
        String query = "";
        String triples = "";
        ArrayList<TriplePattern> star = starGraph.getStar();
        String T = starGraph.getSeedType();
        triples += "\n\t?Seed \t rdf:type \t <" + T + "> . ";

        if (coordinatingConjunction.equals(CoordinatingConjunction.AND)) {
            for (TriplePattern triple : star) {
                triples += "\n\t" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ". ";
            }
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.OR)) {
            for (TriplePattern triple : star) {
                triples += "\n\t{" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ".} UNION ";
            }
            triples = triples.substring(0, triples.length() - "UNION ".length());
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.AND_NOT)) {
            for (TriplePattern triple : star) {
                triples += "\n\t{" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ".} MINUS ";
            }
            triples = triples.substring(0, triples.length() - "MINUS ".length());
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.NOT_NOT)) {
            triples += "\n\tMINUS{ ";
            for (TriplePattern triple : star) {
                triples += "\n\t\t{" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ".} UNION ";
            }
            triples = triples.substring(0, triples.length() - "UNION ".length());
            triples += "\n\t} ";
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        } else if (coordinatingConjunction.equals(CoordinatingConjunction.OR_NOT)) {
            triples += "\n\tMINUS{ ";
            triples += "\n\t\t {{ ?Seed \t rdf:type \t <" + T + "> .} MINUS{ " + star.get(0).toQueryTriplePattern().replace("<" + star.get(0).getSubject().getValueWithPrefix() + ">", "?Seed") + "}} . ";
            int k = 0;
            for (TriplePattern triple : star) {
                k++;
                if (k == 1) {
                    continue;
                }
                triples += "\n\t\t" + triple.toQueryTriplePattern().replace("<" + triple.getSubject().getValueWithPrefix() + ">", "?Seed") + ". ";
            }
            triples += "\n\t} ";
            query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
        }
        return query;
    }

//    public static ArrayList<GeneratedQuestion> getAllPossibleQuestions(StarGraph g) {
//
//        seed = g.getStar().get(0).getSubject().getValue();
//        seed_with_Prefix = g.getStar().get(0).getSubject().getValueWithPrefix();
//
//        //get type - Fill starPredicates map
////        for (TriplePattern triple : g.getStar()) {
////            String typeLabel = triple.getPredicate().getValue();
////            String s = triple.getSubject().getValue();
////            String o = triple.getObject().getValue();
////
////            if (typeLabel.equals("rdf:type") || typeLabel.equals("a")) {
////                type = triple.getObject().getValue();
////                continue;
////            }
////            if (!starPredicates.containsKey(typeLabel)) {
////                HashSet<String> objects = new HashSet<>();
////                objects.add(o);
////                starPredicates.put(typeLabel, objects);
////            } else {
////                starPredicates.get(typeLabel).add(o);
////            }
////        }
//        ArrayList<GeneratedQuestion> questions = new ArrayList<>();
//
//        questions.add(generate_WithAnd_Question(g, QuestionType.which));// also include request
//        questions.add(generate_WithAnd_Question(g, QuestionType.how_many));
//        questions.add(generate_WithAnd_Question(g, QuestionType.yes_no));
//
//        if (starPredicates.size() > 1) {
//            questions.add(generate_WithOr_Question(g, QuestionType.which));
//            questions.add(generate_WithOr_Question(g, QuestionType.how_many));
//            questions.add(generate_WithOr_Question(g, QuestionType.yes_no));
//        }
//
//        //To remove duplicates
//        return new ArrayList<>(new HashSet<>(questions));
//    }
//    private static GeneratedQuestion generate_WithAnd_Question(StarGraph g, int questionType) {
//        ArrayList<String> questionStrings = new ArrayList<>();
//        String seedValue = g.getStar().get(0).getSubject().getValue();
//        String seedValueWithPrefix = g.getStar().get(0).getSubject().getValueWithPrefix();
//
//        //Construct the Question Strings
//        switch (questionType) {
//            case QuestionType.which:
//            case QuestionType.request:
//                questionStrings.add("Which " + type + starPredicates_NP_ToString("and") + "?");
//                questionStrings.add("Which " + type + starPredicates_VP_ToString("and") + "?");
//                String requestPrefix = Request.getRequestPrefix();
//                questionStrings.add(requestPrefix + " " + type + " that" + starPredicates_NP_ToString("and") + "?");
//                questionStrings.add(requestPrefix + " " + type + " that" + starPredicates_VP_ToString("and") + "?");
//                break;
//            case QuestionType.how_many:
//                questionStrings.add("How many " + type + starPredicates_NP_ToString("and") + "?");
//                questionStrings.add("How many " + type + starPredicates_VP_ToString("and") + "?");
//                break;
//            case QuestionType.yes_no:
//                questionStrings.add("Is " + seedValue + starPredicates_NP_ToString("and").replace(" is ", " ") + "?");
//                questionStrings.add("Does " + seedValue + starPredicates_VP_ToString_Yes_No_direction("and") + "?");
//                break;
//            default:;
//        }
//
//        //Construct the Query
//        String query = "";
//        String triples = "";
//        ArrayList<TriplePattern> star = g.getStar();
//        for (TriplePattern triple : star) {
//            triples += "\n\t" + triple.toQueryTriplePattern() + ". ";
//        }
//
//        triples = triples.replace(seedValueWithPrefix, "?Seed");
//        switch (questionType) {
//            case QuestionType.which:
//            case QuestionType.request:
//                query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
//                break;
//            case QuestionType.how_many:
//                query = "SELECT (COUNT(DISTINCT ?Seed) as ?count) WHERE{" + triples + "\n}";
//                break;
//            case QuestionType.yes_no:
//                query = "ASK WHERE{" + triples.replace("?Seed", seedValueWithPrefix) + "\n}";
//                break;
//            default:
//                query = "";
//        }
//        return new GeneratedQuestion(questionStrings, query, g.toString());
//    }
//    private static GeneratedQuestion generate_WithOr_Question(StarGraph g, int questionType) {
//        String seedValue = g.getStar().get(0).getSubject().getValue();
//        String seedValueWithPrefix = g.getStar().get(0).getSubject().getValueWithPrefix();
//        Map<String, HashSet<String>> starPredicatesTriples = new HashMap<>();
//        ArrayList<String> questionStrings = new ArrayList<>();
//        String query = "";
//
//        //Construct the Question Strings
//        switch (questionType) {
//            case QuestionType.which:
//            case QuestionType.request:
//                questionStrings.add("Which " + type + starPredicates_NP_ToString("or") + "?");
//                questionStrings.add("Which " + type + starPredicates_VP_ToString("or") + "?");
//                String requestPrefix = Request.getRequestPrefix();
//                questionStrings.add(requestPrefix + " " + type + " that" + starPredicates_NP_ToString("or") + "?");
//                questionStrings.add(requestPrefix + " " + type + " that" + starPredicates_VP_ToString("or") + "?");
//                break;
//            case QuestionType.how_many:
//                questionStrings.add("How many " + type + starPredicates_NP_ToString("or") + "?");
//                questionStrings.add("How many " + type + starPredicates_VP_ToString("or") + "?");
//                break;
//            case QuestionType.yes_no:
//                questionStrings.add("Is " + seedValue + starPredicates_NP_ToString("or").replace(" is ", " ") + "?");
//                questionStrings.add("Does " + type + starPredicates_VP_ToString_Yes_No_direction("or") + "?");
//                break;
//            default:;
//        }
//
//        String triples = "";
//        ArrayList<TriplePattern> star = g.getStar();
//
//        for (TriplePattern triple : star) {
//            String typeLabel = triple.getPredicate().getValueWithPrefix();
//            String s = triple.getSubject().getValueWithPrefix();
//            String tripleString = triple.toQueryTriplePattern();
//            if (!starPredicatesTriples.containsKey(typeLabel)) {
//                HashSet<String> objects = new HashSet<>();
//                objects.add(tripleString);
//                starPredicatesTriples.put(typeLabel, objects);
//            } else {
//                starPredicatesTriples.get(typeLabel).add(tripleString);
//            }
//        }
//
//        //iterate over the map
//        //get type triple
//        String typeTriple = "";
//        for (Map.Entry<String, HashSet<String>> entry : starPredicatesTriples.entrySet()) {
//            ArrayList<String> sharedpredicateTriples = new ArrayList<>(entry.getValue());
//            if (entry.getKey().equals("rdf:type") || entry.getKey().equals("a")) {
//                typeTriple = sharedpredicateTriples.get(0);
//            }
//        }
//
//        for (Map.Entry<String, HashSet<String>> entry : starPredicatesTriples.entrySet()) {
//            String triplesGroup = "\n\t\t" + typeTriple + ".";
//            ArrayList<String> sharedpredicateTriples = new ArrayList<>(entry.getValue());
//            if (!(entry.getKey().equals("rdf:type") || entry.getKey().equals("a"))) {
//                for (String triple : sharedpredicateTriples) {
//                    triplesGroup += "\n\t\t" + triple + ". ";
//                }
//                triples += "\n\t{" + triplesGroup + "\n\t} UNION ";
//            }
//        }
//
//        triples = triples.subSequence(0, triples.length() - 6) + " ";
//
//        triples = triples.replace(seedValueWithPrefix, "?Seed");
//
//        switch (questionType) {
//            case QuestionType.which:
//            case QuestionType.request:
//                query = "SELECT DISTINCT ?Seed WHERE{" + triples + "\n}";
//                break;
//            case QuestionType.how_many:
//                query = "SELECT (COUNT(DISTINCT ?Seed) as ?count) WHERE{" + triples + "\n}";
//                break;
//            case QuestionType.yes_no:
//                query = "ASK WHERE{" + triples.replace("?Seed", seedValueWithPrefix) + "\n}";
//                break;
//            default:
//                query = "";
//        }
//
//        return new GeneratedQuestion(questionStrings, query, g.toString());
//    }
//    private static String starPredicates_NP_ToString(String conjenction) {
//        try {
//            String starString = "";
//            conjenction = ", " + conjenction;
//            String objectsList = "";
//            boolean firstIteration = true;
//            for (String p : starPredicates.keySet()) {
//                if (starPredicates.size() <= 1) {
//                    conjenction = "";
//                }
//                if (!firstIteration) {
//                    starString += conjenction;
//                }
//                ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
//                String o = objects.get(0);
//                objectsList = objectListToString(objects);
//                PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p, KGOntology.getType(seed), KGOntology.getType(o));
//                starString += " is the " + predicateNL.getPredicate_s_O_NP() + " of " + objectsList;
//
//                firstIteration = false;
//            }
//            return starString;
//        } catch (Exception e) {
//            return "";
//        }
//    }
//    private static String starPredicates_VP_ToString(String conjenction) {
//        try {
//            String starString = "";
//            conjenction = ", " + conjenction;
//            boolean firstIteration = true;
//            String objectsList = "";
//            for (String p : starPredicates.keySet()) {
//                if (starPredicates.size() <= 1) {
//                    conjenction = "";
//                }
//                if (!firstIteration) {
//                    starString += conjenction;
//                }
//                ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
//                objectsList = objectListToString(objects);
//                String o = objects.get(0);
//                PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p, KGOntology.getType(seed), KGOntology.getType(o));
//                starString += " " + objectsList + " " + predicateNL.getPredicate_o_s_VP();
//                firstIteration = false;
//            }
//            return starString;
//        } catch (Exception e) {
//            return "";
//        }
//    }
//    private static String starPredicates_VP_ToString_Yes_No_direction(String conjenction) {
//        try {
//            String starString = "";
//            conjenction = ", " + conjenction;
//            boolean firstIteration = true;
//            String objectsList = "";
//            for (String p : starPredicates.keySet()) {
//                if (starPredicates.size() <= 1) {
//                    conjenction = "";
//                }
//                if (!firstIteration) {
//                    starString += conjenction;
//                }
//                ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
//                objectsList = objectListToString(objects);
//                String o = objects.get(0);
//                PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p, KGOntology.getType(seed), KGOntology.getType(o));
//                starString += " " + predicateNL.getPredicate_o_s_VP() + " " + objectsList;
//                firstIteration = false;
//            }
//            return starString;
//        } catch (Exception e) {
//            return "";
//        }
//    }
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

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }

    public static String factConstraints_toString(StarGraph starGraph, String coorinatingConjunction, Map<String, HashSet<String>> starPredicates) {
        ArrayList<String> FCs_Representation = new ArrayList<>();
        ArrayList<TriplePattern> branches = starGraph.getStar();
        

//        for (String p : starPredicates.keySet()) {
//
//            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
//            String objectsList = objectListToString(objects);
//            String p_with_Prefix = branch.getPredicate().getValueWithPrefix();
//            String s_type = branch.getS_type();
//            String o_type = branch.getO_type();
//            String O = branch.getObject().getValue();
//
//            if (starPredicates.size() <= 1) {
//                conjenction = "";
//            }
//            if (!firstIteration) {
//                starString += conjenction;
//            }
//            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
//            objectsList = objectListToString(objects);
//            String o = objects.get(0);
//            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p, KGOntology.getType(seed), KGOntology.getType(o));
//            starString += " " + predicateNL.getPredicate_o_s_VP() + " " + objectsList;
//            firstIteration = false;
//        }

        ArrayList<String> processedPredicates = new ArrayList<>();
        for (TriplePattern branch : branches) {
            String p_with_Prefix = branch.getPredicate().getValueWithPrefix();
            String p = branch.getPredicate().getValue();
            String s_type = branch.getS_type();
            String o_type = branch.getO_type();
//            String O = branch.getObject().getValue();
            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
            String O = objectListToString(objects);
            
            if(processedPredicates.contains(p))
                continue;
            
            processedPredicates.add(p);

            PredicateNLRepresentation predicateNL = PredicatesLexicon.getPredicateNL(p_with_Prefix, s_type, o_type);
            if (predicateNL != null) {
                if (predicateNL.getPredicate_s_O_NP() != null) {
                    String p_SO_NP = predicateNL.getPredicate_s_O_NP();
                    FCs_Representation.add(" " + p_SO_NP + " " + O);
                } else if (predicateNL.getPredicate_s_O_VP() != null) {
                    String p_SO_VP = predicateNL.getPredicate_s_O_VP();
                    FCs_Representation.add(" " + p_SO_VP + " " + O);
                } else if (predicateNL.getPredicate_o_s_VP() != null) {
                    String p_OS_VP = predicateNL.getPredicate_o_s_VP();
                    FCs_Representation.add(" " + O + " " + p_OS_VP);
                } else if (predicateNL.getPredicate_o_s_NP() != null) {
                    String p_OS_NP = PhraseRepresentationProcessing.NP_only(predicateNL.getPredicate_o_s_NP());
                    FCs_Representation.add(" its " + p_OS_NP + " is " + O);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        String FCs = "";
        switch (FCs_Representation.size()) {
            case 0:
                return null;
            case 1:
                FCs = FCs_Representation.get(0);
                break;
            case 2:
                if (coorinatingConjunction.equals(CoordinatingConjunction.AND)) {
                    FCs = FCs_Representation.get(0) + " and" + FCs_Representation.get(1);
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.OR)) {
                    FCs = " either" + FCs_Representation.get(0) + " or" + FCs_Representation.get(1);
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.AND_NOT)) {
                    FCs = FCs_Representation.get(0) + " but" + FCs_Representation.get(1).replaceAll("\\bis\\b", "is not")
                            .replaceAll("\\bare\\b", "are not")
                            .replaceAll("\\bwas\\b", "was not")
                            .replaceAll("\\bwere\\b", "were not");
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.OR_NOT)) {
                    FCs = FCs_Representation.get(0) + " or" + FCs_Representation.get(1).replaceAll("\\bis\\b", "is not")
                            .replaceAll("\\bare\\b", "are not")
                            .replaceAll("\\bwas\\b", "was not")
                            .replaceAll("\\bwere\\b", "were not");
                } else if (coorinatingConjunction.equals(CoordinatingConjunction.NOT_NOT)) {
                    FCs = " neither" + FCs_Representation.get(0) + " nor" + FCs_Representation.get(1);
                }
                break;
            default:
                if (coorinatingConjunction.equals("and")) {
                    for (int i = 0; i < FCs_Representation.size() - 1; i++) {
                        FCs += FCs_Representation.get(i) + " ,";
                    }
                    FCs += "and" + FCs_Representation.get(FCs_Representation.size() - 1);
                } else if (coorinatingConjunction.equals("or")) {
                    for (int i = 0; i < FCs_Representation.size() - 1; i++) {
                        FCs += FCs_Representation.get(i) + " ,";
                    }
                    FCs += "or" + FCs_Representation.get(FCs_Representation.size() - 1);
                }
                break;
        }
        return FCs;
    }

}
