package online.kg_extractor.model;

/**
 *
 * @author aorogat
 */
public class TriplePattern {
    private Variable subject;
    private Variable object;
    private Variable predicate;

    public TriplePattern(Variable source, Variable destination, Variable label) {
        this.subject = source;
        this.object = destination;
        this.predicate = label;
    }

    
    public Variable getSubject() {
        return subject;
    }

    public void setSubject(Variable source) {
        this.subject = source;
    }

    public Variable getObject() {
        return object;
    }

    public void setObject(Variable destination) {
        this.object = destination;
    }

    public Variable getPredicate() {
        return predicate;
    }

    public void setPredicate(Variable label) {
        this.predicate = label;
    }

    public String toString(){
        String s = subject.getValue() + " ____" + predicate.getValue()+"____ "+object.getValue();
        return s;
    }
    
    public String toStringNotSubject(){
        String s = " ____" + predicate.getValue()+"____ "+object.getValue();
        return s;
    }
    
    public String toQueryTriplePattern(){
        String s = "<"+subject + ">\t<" + predicate + ">\t<" + object + ">";
        return s;
    }
    
    
}
