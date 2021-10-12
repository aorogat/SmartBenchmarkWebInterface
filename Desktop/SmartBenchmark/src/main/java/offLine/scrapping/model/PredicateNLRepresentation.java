package offLine.scrapping.model;

public class PredicateNLRepresentation {
    private String predicate;
    //Context
    private String subject_type;
    private String object_type;
     
    private String predicate_s_O_NP; // representing predicate as NP between S and O. ex (S is a citizen of O) 
    private String predicate_o_s_VP; // representing predicate as VP between O and S. ex (O protect S) 
    private String predicate_s_O_VP;
    private String predicate_o_s_NP; 

    public PredicateNLRepresentation(String predicate, String subject_type, String object_type, 
            String predicate_s_O_NP, String predicate_o_s_VP, 
            String predicate_o_s_NP, String predicate_s_O_VP) {
        this.predicate = predicate;
        this.subject_type = subject_type;
        this.object_type = object_type;
        // S is the ... of O
        this.predicate_s_O_NP = predicate_s_O_NP;
        // O .... S
        this.predicate_o_s_VP = predicate_o_s_VP;
        // S .... O
        this.predicate_s_O_VP = predicate_s_O_VP;
        // O is the .... of S
        this.predicate_o_s_NP = predicate_o_s_NP;
    }

    
    
    
    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getSubject_type() {
        return subject_type;
    }

    public void setSubject_type(String subject_type) {
        this.subject_type = subject_type;
    }

    public String getObject_type() {
        return object_type;
    }

    public void setObject_type(String object_type) {
        this.object_type = object_type;
    }


    public String getPredicate_s_O_NP() {
        return predicate_s_O_NP;
    }

    public void setPredicate_s_O_NP(String predicate_s_O_NP) {
        this.predicate_s_O_NP = predicate_s_O_NP;
    }

    public String getPredicate_o_s_VP() {
        return predicate_o_s_VP;
    }

    public void setPredicate_o_s_VP(String predicate_o_s_VP) {
        this.predicate_o_s_VP = predicate_o_s_VP;
    }

    public String getPredicate_s_O_VP() {
        return predicate_s_O_VP;
    }

    public void setPredicate_s_O_VP(String predicate_s_O_VP) {
        this.predicate_s_O_VP = predicate_s_O_VP;
    }

    public String getPredicate_o_s_NP() {
        return predicate_o_s_NP;
    }

    public void setPredicate_o_s_NP(String predicate_o_s_NP) {
        this.predicate_o_s_NP = predicate_o_s_NP;
    }

    
    
}
