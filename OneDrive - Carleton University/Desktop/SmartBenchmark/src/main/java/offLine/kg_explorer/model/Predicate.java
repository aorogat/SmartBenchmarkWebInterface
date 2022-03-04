package offLine.kg_explorer.model;

import java.util.ArrayList;
import java.util.List;
import offLine.kg_explorer.explorer.Explorer;
import offLine.kg_explorer.explorer.SPARQL;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class Predicate {

    private String predicateURI;
    private String predicate; //without prefix
    private String label;
    private long weight;
    private List<PredicateTripleExample> tripleExamples = new ArrayList<>();
    private Explorer explorer;
    private String NLPattern; //replicate for each predicate
    //Predicate Context
    private PredicateContext predicateContext;

    public Predicate(Explorer explorer) {
        this.explorer = explorer;
    }

    @Override
    public String toString() {
        return predicateURI + "\t" + label + "\t" + weight + "\t"
                + predicateContext.getSubjectType() + "\t"
                + predicateContext.getObjectType() + "\t"
                + predicateContext.getWeight() + "\t";
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
        return  label;

//        return label;
    }

    public void setLabel(String label) {
        this.label = Settings.explorer.removePrefix(label);
    }

    public long getWeight() {
//        if(weight<=0)
//            weight = SPARQL.getPredicateWeight(predicate, predicateContext.getSubjectType(), predicateContext.getObjectType());
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public List<PredicateTripleExample> getTripleExamples() {
        return tripleExamples;
    }

    public void setTripleExamples(List<PredicateTripleExample> tripleExamples) {
        this.tripleExamples = tripleExamples;
    }

    public PredicateContext getPredicateContext() {
        return predicateContext;
    }

    public void setPredicateContext(PredicateContext predicateContext) {
        this.predicateContext = predicateContext;
    }

    public void print() {
        String format = "%-10s%-25s%-35s%-35s%-35s%-100s%n";
        System.out.format(format, "\t" + getPredicateContext().getWeight(), "\t" + getPredicateURI(),
                "\t" + getLabel(), "\t" + getPredicateContext().getSubjectType(), "\t" + getPredicateContext().getObjectType(),
                "\t" + getTripleExamples().toString());

    }

    public String getNLPattern() {
        return NLPattern;
    }

    public void setNLPattern(String NLPattern) {
        this.NLPattern = NLPattern;
    }

}
