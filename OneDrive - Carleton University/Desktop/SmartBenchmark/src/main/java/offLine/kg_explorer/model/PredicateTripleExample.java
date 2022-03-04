package offLine.kg_explorer.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import database.Database;
import offLine.kg_explorer.explorer.Explorer;
import offLine.scrapping.wikipedia.NLP;
import offLine.scrapping.wikipedia.Wikipedia;

/**
 *
 * @author aorogat
 */
public class PredicateTripleExample {

    private String subject;
    private String object;
    private String subjectURI;
    private String objectURI;
    private String predicateLabel;
    private String predicateURI;
    private ArrayList<String> nlsSuggestions = new ArrayList<>();
    private ArrayList<Predicate_NLP_Representation> nlsSuggestionsObjects = new ArrayList<>();
    private Explorer explorer;
    
    
    

    public PredicateTripleExample(String subjectURI, String objectURI, String subject, String object, String predicateLabel, Explorer explorer) {
        this.subjectURI = subjectURI;
        this.objectURI = objectURI;
        this.subject = subject;
        this.object = object;
        this.explorer = explorer;
        this.predicateLabel = predicateLabel;
        try {
            System.out.println(subjectURI + "\t" + objectURI + "\t" + predicateLabel + "\t");
            nlsSuggestions.addAll(Wikipedia.getNLSentences(subject, object, true)); // add sentences from subject page
            nlsSuggestions.addAll(Wikipedia.getNLSentences(subject, object, false)); // add sentences from object page
            //Remove Duplicates
            nlsSuggestions = new ArrayList<>(new HashSet<>(nlsSuggestions));
            fillNlsSuggestionsObjects();
        } catch (Exception e) {

        }
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getSubjectWithoutPrefix() {
        return explorer.removePrefix(subject);
    }

    public String getObjectWithoutPrefix() {
        return explorer.removePrefix(object);
    }

    public void setNlsSuggestions(ArrayList<String> nlsSuggestions) {
        this.nlsSuggestions = nlsSuggestions;
    }

    public String getPredicateLabel() {
        return predicateLabel;
    }

    public void setPredicateLabel(String predicateLabel) {
        this.predicateLabel = predicateLabel;
    }

    public String getSubjectURI() {
        return subjectURI;
    }

    public void setSubjectURI(String subjectURI) {
        this.subjectURI = subjectURI;
    }

    public String getObjectURI() {
        return objectURI;
    }

    public void setObjectURI(String objectURI) {
        this.objectURI = objectURI;
    }

    public Explorer getExplorer() {
        return explorer;
    }

    public void setExplorer(Explorer explorer) {
        this.explorer = explorer;
    }

    private void fillNlsSuggestionsObjects() throws IOException {
        nlsSuggestionsObjects.clear();

        for (String nlsSuggestion : nlsSuggestions) {
            String pattern = "";
            String sub = subject.toLowerCase();
            String obj = object.toLowerCase();
            String sType = "[s{" + Entity.getDirectType(explorer, subjectURI) + "}]";
            String oType = "[o{" + Entity.getDirectType(explorer, objectURI) + "}]";

            if (sub.contains(obj)) {
                pattern = nlsSuggestion.replaceAll(sub, sType).replaceAll(obj, oType);
            } else if (obj.contains(sub)) {
                pattern = nlsSuggestion.replaceAll(obj, oType).replaceAll(sub, sType);
            } else {
                pattern = nlsSuggestion.replaceAll(sub, sType).replaceAll(obj, oType);
            }
            if (!(pattern.contains(sType) && pattern.contains(oType))) {
                continue;
            }

            String reducedPattern = NLP.getCandidatePhrases(pattern, predicateLabel, sType, oType).toString();
            //Only add the NL if the reducedPattern has a value
            if (!"".equals(reducedPattern)) {
                nlsSuggestionsObjects.add(new Predicate_NLP_Representation(nlsSuggestion, pattern, reducedPattern, predicateLabel, sType, oType));
            }
        }
    }

    public ArrayList<Predicate_NLP_Representation> getNlsSuggestionsObjects() throws IOException {
        return nlsSuggestionsObjects;
    }

//    public void sortNlsSuggestionsObjects() {
//        try {
//            nlsSuggestionsObjects.sort((s1, s2) -> (Math.abs(s1.getReducedPattern().replace(s1.getsType(), "").replace(s1.getoType(), "").length() - predicateLabel.length())
//                    - Math.abs(s2.getReducedPattern().replace(s2.getsType(), "").replace(s2.getoType(), "").length() - predicateLabel.length())));
//        } catch (Exception e) {
//        }
//    }

    @Override
    public String toString() {
        String s = "";

        //order nlsSuggestions
//        sortNlsSuggestionsObjects();
        s = subject + "___" + predicateLabel + "___" + object + "\t";
        for (Predicate_NLP_Representation nlsSuggestion : nlsSuggestionsObjects) {
            s += "Sentence: " + nlsSuggestion.getSentence()
                    + "\tPattern: " + nlsSuggestion.getPattern() + "\tReduced Pattern: " + nlsSuggestion.getReducedPattern() + "\t";
        }
        return s;
    }

    public ArrayList<String> getNlsSuggestions() {
        return nlsSuggestions;
    }

    public String getPredicateURI() {
        return predicateURI;
    }

    public void setPredicateURI(String predicateURI) {
        this.predicateURI = predicateURI;
    }
    
    

}
