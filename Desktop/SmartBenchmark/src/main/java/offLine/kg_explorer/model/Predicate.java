package offLine.kg_explorer.model;

import java.util.ArrayList;
import java.util.List;
import offLine.kg_explorer.explorer.Explorer;

/**
 *
 * @author aorogat
 */
public class Predicate {
    private String predicateURI;
    private String predicate; //without prefix
    private String label;
    private long weight;
    private List<PredicateTriple> tripleExamples = new ArrayList<>();
    private Explorer explorer;

    public Predicate(Explorer explorer) {
        this.explorer = explorer;
    }
    
    @Override
    public String toString()
    {
        return predicateURI + "\t" + label + "\t" + weight + "\t" +  
                tripleExamples.toString();
    }

    public String getPredicateURI() {
        return predicateURI;
    }

    public void setPredicateURI(String predicateURI) {
        this.predicateURI = predicateURI;
    }

    public String getPredicate() {
        return explorer.removePrefix(predicateURI);
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public List<PredicateTriple> getTripleExamples() {
        return tripleExamples;
    }

    public void setTripleExamples(List<PredicateTriple> tripleExamples) {
        this.tripleExamples = tripleExamples;
    }
    
    
    
}
