package offLine.kg_explorer.model;

/**
 *
 * @author aorogat
 */
public class NlsSuggestion {
    private String sentence;
    private String pattern;
    private String reducedPattern;
    private String label;
    private String sType;
    private String oType;

    public NlsSuggestion(String sentence, String pattern, String reducedPattern, String label, String sType, String oType) {
        this.sentence = sentence;
        this.pattern = pattern;
        this.reducedPattern = reducedPattern;
        this.label = label;
        this.sType = sType;
        this.oType = oType;
    }

    

    
    
    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getReducedPattern() {
        return reducedPattern;
    }

    public void setReducedPattern(String reducedPattern) {
        this.reducedPattern = reducedPattern;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getsType() {
        return sType;
    }

    public void setsType(String sType) {
        this.sType = sType;
    }

    public String getoType() {
        return oType;
    }

    public void setoType(String oType) {
        this.oType = oType;
    }
    
    
    
}
