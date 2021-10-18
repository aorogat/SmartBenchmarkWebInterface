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
        String format = "%-10s%-25s%-35s%-35s%-35s%-100s%n";
        System.out.format(format, "\tWeight", "\tPredicate", "\tLabel", "\tSubject", "\tObject", "\tTriple Examples");
        System.out.format(format, "\t======", "\t======", "\t=========", "\t========", "\t=========", "\t========");
    }
    
    public void print()
    {
        String format = "%-10s%-25s%-35s%-35s%-35s%-100s%n";
        for (Predicate predicate : predicates) {
            System.out.format(format, "\t"+predicate.getPredicateContext().getWeight(), "\t"+predicate.getPredicate(), 
                    "\t"+predicate.getLabel(), "\t"+predicate.getPredicateContext().getSubjectType(),"\t"+predicate.getPredicateContext().getObjectType(), "\t"+predicate.getTripleExamples().toString());
        }
    }
    
    public void printLast10()
    {
        int start=0;
        if(predicates.size()-10>=0)
            start = predicates.size()-10;
        String format = "%-10s%-25s%-35s%-35s%-35s%-100s%n";
        for (int i=start; i<predicates.size(); i++) {
        Predicate predicate = predicates.get(i);
            System.out.format(format, "\t"+predicate.getPredicateContext().getWeight(), "\t"+predicate.getPredicate(), 
                    "\t"+predicate.getLabel(), "\t"+predicate.getPredicateContext().getSubjectType(),"\t"+predicate.getPredicateContext().getObjectType(), "\t"+predicate.getTripleExamples().toString());
        }
    }
    
    
    
    
}
