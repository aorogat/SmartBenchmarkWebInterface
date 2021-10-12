package offLine.kg_explorer.model;

import java.util.ArrayList;
import java.util.Formatter;

public class ListOfPredicates {
    private ArrayList<Predicate> predicates;

    public ListOfPredicates(ArrayList<Predicate> predicates) {
        this.predicates = predicates;
    }

    
    
    public ArrayList<Predicate> getPredicates() {
        return predicates;
    }

    public void setPredicates(ArrayList<Predicate> predicates) {
        this.predicates = predicates;
    }
    
    public void printHeader()
    {
        String format = "%-10s%-25s%-25s%-100s%n";
        System.out.format(format, "\tWeight", "\tPredicate", "\tLabel", "\tTriple Examples");
        System.out.format(format, "\t======", "\t======", "\t=========", "\t========");
    }
    
    public void print()
    {
        String format = "%-10s%-25s%-25s%-100s%n";
        for (Predicate predicate : predicates) {
            System.out.format(format, "\t"+predicate.getWeight(), "\t"+predicate.getPredicate(), 
                    "\t"+predicate.getLabel(), "\t"+predicate.getTripleExamples().toString());
        }
    }
    
    
    
    
}
