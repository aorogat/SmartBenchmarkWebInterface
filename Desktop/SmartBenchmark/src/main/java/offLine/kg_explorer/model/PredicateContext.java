
package offLine.kg_explorer.model;

public class PredicateContext {
        //Predicate Context
    private String subjectType;
    private String objectType;
    private long weight;

    public PredicateContext() {
    }

    public PredicateContext(String subjectType, String objectType, long weight) {
        this.subjectType = subjectType;
        this.objectType = objectType;
        this.weight = weight;
    }
    
    

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
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
