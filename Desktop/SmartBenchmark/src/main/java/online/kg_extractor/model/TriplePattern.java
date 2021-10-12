package online.kg_extractor.model;

/**
 *
 * @author aorogat
 */
public class TriplePattern {
    private Variable source;
    private Variable destination;
    private Variable label;

    public TriplePattern(Variable source, Variable destination, Variable label) {
        this.source = source;
        this.destination = destination;
        this.label = label;
    }

    
    public Variable getSource() {
        return source;
    }

    public void setSource(Variable source) {
        this.source = source;
    }

    public Variable getDestination() {
        return destination;
    }

    public void setDestination(Variable destination) {
        this.destination = destination;
    }

    public Variable getLabel() {
        return label;
    }

    public void setLabel(Variable label) {
        this.label = label;
    }

    public String toString(){
        String s = source.getValue() + " ____" + label.getValue()+"____ "+destination.getValue();
        return s;
    }
    
    public String toStringNotSubject(){
        String s = " ____" + label.getValue()+"____ "+destination.getValue();
        return s;
    }
    
    public String toQueryTriplePattern(){
        String s = source + "\t" + label + "\t" + destination;
        return s;
    }
    
    
}
