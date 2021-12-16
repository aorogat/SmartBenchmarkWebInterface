package online.nl_generation.chunking;

/**
 *
 * @author aorogat
 */
public class Phrase {
    public final static byte S_O = 1;
    public final static byte O_S = 2;
    
    public final static byte VP = 1;
    public final static byte NP = 2;
    
    String sentence;
    String verbPhrase; //e.g., flows into
    String baseVerbForm; //e.g., flow
    double labelSimilarity; //e.g., "flows into" similarity with "river mouth" is 3.9
    byte direction; //S_O or O_S for subject to object or object to subject
    byte type; //Verb or Noun phrase

    public String getVerbPhrase() {
        return verbPhrase;
    }

    public void setVerbPhrase(String verbPhrase) {
        this.verbPhrase = verbPhrase;
    }

    public String getBaseVerbForm() {
        return baseVerbForm;
    }

    public void setBaseVerbForm(String baseVerbForm) {
        this.baseVerbForm = baseVerbForm;
    }

    public double getLabelSimilarity() {
        return labelSimilarity;
    }

    public void setLabelSimilarity(double labelSimilarity) {
        this.labelSimilarity = labelSimilarity;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
    
    
    
    
    
}
