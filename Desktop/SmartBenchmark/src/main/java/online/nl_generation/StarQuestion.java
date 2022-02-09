package online.nl_generation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import offLine.kg_explorer.explorer.SPARQL;
import online.kg_extractor.model.TriplePattern;
import online.kg_extractor.model.subgraph.StarGraph;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import settings.KG_Settings;

public class StarQuestion {

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

        if (starGraph.getStar().size() == 1) {
            selectQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.AND);
            askQuestions_true_answer(CoordinatingConjunction.AND);
            askQuestions_false_answer(CoordinatingConjunction.AND);

        } else if (starGraph.getStar().size() == 2 && starGraph.getStar().size() == starPredicates.size()) { //no repeated predicates
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

            askQuestions_false_answer(CoordinatingConjunction.AND);

        } else if (starGraph.getStar().size() == 2 && starGraph.getStar().size() != starPredicates.size()) { //repeated predicates
            selectQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.AND);
            askQuestions_true_answer(CoordinatingConjunction.AND);
            askQuestions_false_answer(CoordinatingConjunction.AND);

        } else if (starGraph.getStar().size() > 2 && starGraph.getStar().size() == starPredicates.size()) { //no repeated predicates
            selectQuestions(CoordinatingConjunction.AND);
            selectQuestions(CoordinatingConjunction.OR);

            countQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.OR);

            askQuestions_true_answer(CoordinatingConjunction.AND);

            askQuestions_false_answer(CoordinatingConjunction.AND);
        } else if (starGraph.getStar().size() > 2 && starGraph.getStar().size() != starPredicates.size()) { //repeated predicates
            selectQuestions(CoordinatingConjunction.AND);
            countQuestions(CoordinatingConjunction.AND);
            askQuestions_true_answer(CoordinatingConjunction.AND);
            askQuestions_false_answer(CoordinatingConjunction.AND);
        }
    }

    public String askQuery_true_answer(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + starGraph.getStar().get(0).getSubject().getValueWithPrefix() + ">");
    }

    public String askQuery_false_answer(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction)
                .replace("SELECT DISTINCT ?Seed WHERE{", "ASK WHERE{")
                .replace("?Seed", "<" + somethingElse + ">");
    }

    public String countQuery(StarGraph starGraph, String coordinatingConjunction) {
        return selectQuery(starGraph, coordinatingConjunction).replace("SELECT DISTINCT ?Seed WHERE{", "SELECT (COUNT (?Seed) AS ?count) WHERE{");
    }

    public void countQuestions(String coordinatingConjunction) {
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
            String countQuery = countQuery(starGraph, coordinatingConjunction);
            String whichQuestion = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", "How many");
            String question = whichQuestion;
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, countQuery, starGraph.toString(), starGraph.getStar().size()+1, GeneratedQuestion.QT_HOW_MANY, GeneratedQuestion.SH_STAR));
        }
    }

    public void askQuestions_true_answer(String coordinatingConjunction) {
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
            String whichQuestion = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", "Is " + starGraph.getStar().get(0).getSubject().getValue());
            String question = whichQuestion;
            String askQuery = askQuery_true_answer(starGraph, coordinatingConjunction);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, askQuery, starGraph.toString(), starGraph.getStar().size()+1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_STAR));
        }
    }

    public void askQuestions_false_answer(String coordinatingConjunction) {
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
            String whichQuestion = selectWhichQuestions(coordinatingConjunction).replaceFirst("Which", "Is " + somethingElseWithoutPrefix)
                    .replace(FCs, FCs);
            String question = whichQuestion;
            String askQuery = askQuery_false_answer(starGraph, coordinatingConjunction);
                        allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, askQuery, starGraph.toString(), starGraph.getStar().size()+1, GeneratedQuestion.QT_YES_NO_IS, GeneratedQuestion.SH_STAR));

//            allPossibleQuestions.add(new GeneratedQuestion(question, askQuery, starGraph.toString()));
        }
    }

    public void selectQuestions(String coordinatingConjunction) {
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
            String whichQuestion = selectWhichQuestions(coordinatingConjunction);
            String question = whichQuestion;
            String selectQuery = selectQuery(starGraph, coordinatingConjunction);
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, selectQuery, starGraph.toString(), starGraph.getStar().size()+1, GeneratedQuestion.QT_WHICH, GeneratedQuestion.SH_STAR));

            String requestQuestion = whichQuestion.replaceFirst("Which ", Request.getRequestPrefix().trim() + " which ");
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, selectQuery, starGraph.toString(), starGraph.getStar().size()+1, GeneratedQuestion.QT_REQUEST, GeneratedQuestion.SH_STAR));
            
            requestQuestion = whichQuestion.replaceFirst("Which ", "");
            allPossibleQuestions.add(new GeneratedQuestion(starGraph.getStar().get(0).getSubject().getValueWithPrefix(), starGraph.getStar().get(0).getS_type(), question, selectQuery, starGraph.toString(), starGraph.getStar().size()+1, GeneratedQuestion.QT_TOPICAL_PRUNE, GeneratedQuestion.SH_STAR));
        }
    }

     public String selectWhichQuestions(String coordinatingConjunction) {
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

    public String selectQuery(StarGraph starGraph, String coordinatingConjunction) {
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

        ArrayList<String> processedPredicates = new ArrayList<>();
        for (TriplePattern branch : branches) {
            String p_with_Prefix = branch.getPredicate().getValueWithPrefix();
            String p = branch.getPredicate().getValue();
            String s_type = branch.getS_type();
            String o_type = branch.getO_type();
            ArrayList<String> objects = new ArrayList<>(starPredicates.get(p));
            String O = objectListToString(objects);

            if (processedPredicates.contains(p)) {
                continue;
            }

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
                    if (SPARQL.isASubtypeOf(KG_Settings.explorer, starGraph.getSeedType(), KG_Settings.Person)) {
                        FCs_Representation.add(" his/her " + p_OS_NP + " is " + O);
                    } else {
                        FCs_Representation.add(" its " + p_OS_NP + " is " + O);
                    }
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

    public String getFCs_with_T_COO_is_AND() {
        return T + FCs_AND;
    }

    public String getFCs_with_T_COO_is_OR() {
        return T + FCs_OR;
    }

    public String getFCs_with_T_COO_is_AND_NOT() {
        return T + FCs_AND_NOT;
    }

    public String getFCs_with_T_COO_is_OR_NOT() {
        return T + FCs_OR_NOT;
    }

    public String getFCs_with_T_COO_is_NOT_NOT() {
        return T + FCs_NOT_NOT;
    }

}
