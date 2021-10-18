package offLine.scrapping.wikipedia;

import java.io.IOException;
import online.nl_generation.Test_OpenNLPChunkerExample;

public class NLP {

    static Test_OpenNLPChunkerExample chunkerExample;
   
    
    
    public static String summarySentence(String sentence) throws IOException {
        if(chunkerExample==null)
            chunkerExample = new Test_OpenNLPChunkerExample();
        return chunkerExample.last_VP_PP(removeBeforeSubjectAfterObject(
//                    removeBetweenCommas(
                        removeRefrence(
                            removeParenthesesPhrases(
                                removeHyphenedPhrases(sentence)))));
    }

    public static String removeHyphenedPhrases(String sentence) {
        return sentence.replaceAll(" - .* - ", "");
    }

    public static String removeParenthesesPhrases(String sentence) {
        return sentence.replaceAll("\\(.*\\)", "");
    }

    public static String removeRefrence(String sentence) {
        return sentence.replaceAll("\\[[0-9]+\\]", "").replaceAll("[0-9]+\\]", "").replaceAll("\\[[0-9]*$", "");
    }

    public static String removeBetweenCommas(String sentence) {
        String localSentence = sentence.toLowerCase();
        String[] phrases = localSentence.split(",");
        
        
        for (String phrase : phrases) {
            if(!(phrase.contains("[s{")||phrase.contains("[o{")))
                localSentence = localSentence.replace(","+phrase, "").replace(phrase+",", ""); //replace ignore regular expressions
        }
        return localSentence;
    }
    
    public static String removeBeforeSubjectAfterObject(String sentence) {
        String[] phrases = sentence.split(" ");
        String resultString = "";
        boolean start = false;
        boolean end = false;
        for (String phrase : phrases) {
            if(phrase.startsWith("[s"))
            {
                start = true;
            }
            else if(phrase.startsWith("[o"))
            {
                end = true;
            }
            
            if(start||end)
            {
                resultString += " " + phrase;
            }
            if(start&&end)
            {
                break;
            }
        }
        return resultString.trim();
    }

    
}
