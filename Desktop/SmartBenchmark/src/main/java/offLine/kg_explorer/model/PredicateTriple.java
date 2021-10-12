package offLine.kg_explorer.model;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.Explorer;
import offLine.scrapping.wikipedia.NLP;
import offLine.scrapping.wikipedia.Wikipedia;

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
    private ArrayList<NlsSuggestion> nlsSuggestionsObjects = new ArrayList<>();
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

    public ArrayList<NlsSuggestion> getNlsSuggestionsObjects() {
        nlsSuggestionsObjects.clear();
        
        for (String nlsSuggestion : nlsSuggestions) {
            String pattern = "";
            String sub = subject.toLowerCase();
            String obj = object.toLowerCase();
            String sType = "[s{"+Entity.getDirectType(explorer, subjectURI)+"}]";
            String oType = "[o{"+Entity.getDirectType(explorer, objectURI)+"}]";
            
            if(sub.contains(obj))
            {
                pattern = nlsSuggestion.replaceAll(sub, sType).replaceAll(obj, oType);
            }
            else if(obj.contains(sub))
            {
                pattern = nlsSuggestion.replaceAll(obj, oType).replaceAll(sub, sType);
            }
            else
                pattern = nlsSuggestion.replaceAll(sub, sType).replaceAll(obj, oType);
            
            String reducedPattern = NLP.summarySentence(pattern);
            nlsSuggestionsObjects.add(new NlsSuggestion(nlsSuggestion, pattern, reducedPattern, predicateLabel, sType, oType));
        }
        return nlsSuggestionsObjects;
    }

    public void sortNlsSuggestionsObjects() {
        nlsSuggestionsObjects.sort((s1, s2) -> (Math.abs(s1.getReducedPattern().replace(s1.getsType(), "").replace(s1.getoType(), "").length() - predicateLabel.length()) - 
                                                Math.abs(s2.getReducedPattern().replace(s2.getsType(), "").replace(s2.getoType(), "").length() - predicateLabel.length())));
    }
    
    

    @Override
    public String toString() {
        String s = "";
        //ignore triple example without NLSs
//        if(nlsSuggestions.size()==0)
//            return s;
        //NLSs from strings to objects
        getNlsSuggestionsObjects();
        //order nlsSuggestions
        sortNlsSuggestionsObjects();
        s = subject + "___" + predicateLabel + "___" + object + "\t";
        for (NlsSuggestion nlsSuggestion : nlsSuggestionsObjects) {
            s += "Sentence: " + nlsSuggestion.getSentence() +
                    "\tPattern: "+ nlsSuggestion.getPattern() + "\tReduced Pattern: " + nlsSuggestion.getReducedPattern() + "\t";
        }
        return s;
    }

}
