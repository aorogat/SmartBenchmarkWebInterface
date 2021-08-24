package kg_explorer.model;

import java.util.ArrayList;
import kg_explorer.explorer.Explorer;
import scrapping.wikipedia.Wikipedia;

/**
 *
 * @author aorogat
 */
public class PredicateTriple {

    private String subject;
    private String object;
    private String subjectURI;
    private String objectURI;
    private String predicateLabel;
    private ArrayList<String> nlsSuggestions = new ArrayList<>();
    private Explorer explorer;

    public PredicateTriple(String subjectURI, String objectURI, String subject, String object, String predicateLabel, Explorer explorer) {
        this.subjectURI = subjectURI;
        this.objectURI = objectURI;
        this.subject = subject;
        this.object = object;
        this.explorer = explorer;
        this.predicateLabel = predicateLabel;
        try {
            nlsSuggestions.addAll(Wikipedia.getNLSentences(subject, object, true)); // add sentences from subject page
            nlsSuggestions.addAll(Wikipedia.getNLSentences(subject, object, false)); // add sentences from object page
        } catch (Exception e) {
            nlsSuggestions.add("UNKNOWN");
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

    public ArrayList<String> getNlsSuggestions() {
        nlsSuggestions.sort((s1, s2) -> Math.abs(s1.length()) - Math.abs(s2.length()));
        return nlsSuggestions;
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
    
    

    public String toString() {
        String s = subject + "___" + predicateLabel + "___" + object + "\t";
        for (String nlsSuggestion : nlsSuggestions) {
            s += "(((((" + nlsSuggestion + ")))))" +"  ["
                    +"\t"+nlsSuggestion.replaceAll(subject.toLowerCase(), "[s("+Entity.getType(explorer, subjectURI)+")]") 
                                  .replaceAll(object.toLowerCase(), "[o("+Entity.getType(explorer, objectURI)+")]") + "\t";
        }
        return s;
    }

}
