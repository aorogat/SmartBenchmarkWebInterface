
package offLine.kg_explorer.model;

public class PredicateContext {
        //Predicate Context
    private String subjectType;
    private String objectType;

    public PredicateContext() {
    }
    
    public PredicateContext(String subjectType, String objectType) {
        this.subjectType = subjectType;
        this.objectType = objectType;
    }
    
    

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    
    
}
