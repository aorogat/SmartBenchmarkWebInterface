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
        this.predicate_s_O_NP = predicate_s_O_NP.replaceAll("\\(.*\\)", "");
        // O .... S
        this.predicate_o_s_VP = predicate_o_s_VP.replaceAll("\\(.*\\)", "");
        // S .... O
        this.predicate_s_O_VP = predicate_s_O_VP.replaceAll("\\(.*\\)", "");
        // O is the .... of S
        this.predicate_o_s_NP = predicate_o_s_NP.replaceAll("\\(.*\\)", "");
        
        
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
        if (predicate_s_O_NP == null) {
            return predicate_s_O_NP;
        }
        if (predicate_s_O_NP.toLowerCase().startsWith("is ")
                || predicate_s_O_NP.toLowerCase().startsWith("is/")
                || predicate_s_O_NP.toLowerCase().startsWith("are ")
                || predicate_s_O_NP.toLowerCase().startsWith("was ")
                || predicate_s_O_NP.toLowerCase().startsWith("was/")
                || predicate_s_O_NP.toLowerCase().startsWith("were ")) {
            predicate_s_O_NP = predicate_s_O_NP;
        } else {
            predicate_s_O_NP = "is the " + predicate_s_O_NP;
        }

        if (predicate_s_O_NP.toLowerCase().endsWith(" above")
                || predicate_s_O_NP.toLowerCase().endsWith(" across")
                || predicate_s_O_NP.toLowerCase().endsWith(" about")
                || predicate_s_O_NP.toLowerCase().endsWith(" of")
                || predicate_s_O_NP.toLowerCase().endsWith(" for")
                || predicate_s_O_NP.toLowerCase().endsWith(" against")
                || predicate_s_O_NP.toLowerCase().endsWith(" along")
                || predicate_s_O_NP.toLowerCase().endsWith(" among")
                || predicate_s_O_NP.toLowerCase().endsWith(" around")
                || predicate_s_O_NP.toLowerCase().endsWith(" at")
                || predicate_s_O_NP.toLowerCase().endsWith(" before")
                || predicate_s_O_NP.toLowerCase().endsWith(" behind")
                || predicate_s_O_NP.toLowerCase().endsWith(" below")
                || predicate_s_O_NP.toLowerCase().endsWith(" beneath")
                || predicate_s_O_NP.toLowerCase().endsWith(" beside")
                || predicate_s_O_NP.toLowerCase().endsWith(" between")
                || predicate_s_O_NP.toLowerCase().endsWith(" in")
                || predicate_s_O_NP.toLowerCase().endsWith(" into")
                || predicate_s_O_NP.toLowerCase().endsWith(" near")
                || predicate_s_O_NP.toLowerCase().endsWith(" on")
                || predicate_s_O_NP.toLowerCase().endsWith(" to")
                || predicate_s_O_NP.toLowerCase().endsWith(" toward")
                || predicate_s_O_NP.toLowerCase().endsWith(" under")
                || predicate_s_O_NP.toLowerCase().endsWith(" upon")
                || predicate_s_O_NP.toLowerCase().endsWith(" with")
                || predicate_s_O_NP.toLowerCase().endsWith(" within")) {
            return predicate_s_O_NP;
        } else {
            return predicate_s_O_NP + " of";
        }

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
        if (predicate_o_s_NP == null) {
            return predicate_o_s_NP;
        }
        if (predicate_o_s_NP.toLowerCase().startsWith("is ")
                || predicate_o_s_NP.toLowerCase().startsWith("is/")
                || predicate_o_s_NP.toLowerCase().startsWith("are ")
                || predicate_o_s_NP.toLowerCase().startsWith("was ")
                || predicate_o_s_NP.toLowerCase().startsWith("was/")
                || predicate_o_s_NP.toLowerCase().startsWith("were ")) {
            predicate_o_s_NP = predicate_o_s_NP;
        } else {
            predicate_o_s_NP = "is the " + predicate_o_s_NP;
        }

        if (predicate_o_s_NP.toLowerCase().endsWith(" above")
                || predicate_o_s_NP.toLowerCase().endsWith(" across")
                || predicate_o_s_NP.toLowerCase().endsWith(" about")
                || predicate_o_s_NP.toLowerCase().endsWith(" of")
                || predicate_o_s_NP.toLowerCase().endsWith(" for")
                || predicate_o_s_NP.toLowerCase().endsWith(" against")
                || predicate_o_s_NP.toLowerCase().endsWith(" along")
                || predicate_o_s_NP.toLowerCase().endsWith(" among")
                || predicate_o_s_NP.toLowerCase().endsWith(" around")
                || predicate_o_s_NP.toLowerCase().endsWith(" at")
                || predicate_o_s_NP.toLowerCase().endsWith(" before")
                || predicate_o_s_NP.toLowerCase().endsWith(" behind")
                || predicate_o_s_NP.toLowerCase().endsWith(" below")
                || predicate_o_s_NP.toLowerCase().endsWith(" beneath")
                || predicate_o_s_NP.toLowerCase().endsWith(" beside")
                || predicate_o_s_NP.toLowerCase().endsWith(" between")
                || predicate_o_s_NP.toLowerCase().endsWith(" in")
                || predicate_o_s_NP.toLowerCase().endsWith(" into")
                || predicate_o_s_NP.toLowerCase().endsWith(" near")
                || predicate_o_s_NP.toLowerCase().endsWith(" on")
                || predicate_o_s_NP.toLowerCase().endsWith(" to")
                || predicate_o_s_NP.toLowerCase().endsWith(" toward")
                || predicate_o_s_NP.toLowerCase().endsWith(" under")
                || predicate_o_s_NP.toLowerCase().endsWith(" upon")
                || predicate_o_s_NP.toLowerCase().endsWith(" with")
                || predicate_o_s_NP.toLowerCase().endsWith(" within")) {
            return predicate_o_s_NP;
        } else {
            return predicate_o_s_NP + " of";
        }

    }

    public void setPredicate_o_s_NP(String predicate_o_s_NP) {
        this.predicate_o_s_NP = predicate_o_s_NP;
    }

}
