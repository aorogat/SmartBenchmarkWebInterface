package offLine.scrapping.wikipedia;

import java.io.IOException;
import online.nl_generation.chunking.Chuncker;

public class NLP {

    static Chuncker chunkerExample;

    public static String summarySentence(String sentence, String sType, String oType) throws IOException {
        if (chunkerExample == null) {
            chunkerExample = new Chuncker();
        }
        sentence = removeHyphenedPhrases(sentence);
        sentence = removeParenthesesPhrases(sentence);
        sentence = removeRefrence(sentence);
        sentence = removeBeforeSubjectAfterObject(sentence);

        sentence = sentence.replace(sType, "sssss");
        sentence = sentence.replace(oType, "ooooo");
        sentence = sentence.replace("sssss", "");
        sentence = sentence.replace("ooooo", "");

        String sentenceVP = chunkerExample.get_only_VP(sentence);

        if (sentenceVP == null || sentenceVP.length() < 4
                || sentenceVP.trim().toLowerCase().equals("is")
                || sentenceVP.trim().toLowerCase().equals("are")
                || sentenceVP.trim().toLowerCase().equals("was")
                || sentenceVP.trim().toLowerCase().equals("were")
                || !sentenceVP.trim().equals(sentence.trim())) {
            sentenceVP = "";
        }

        sentenceVP = sentenceVP.trim();

        //try NP
        String sentenceNP = "";
        if (sentenceVP.equals("")) {
            sentenceNP = chunkerExample.get_only_NP(sentence);
            return sentenceNP;
        }
        return sentenceVP;
    }

    public static String removeHyphenedPhrases(String sentence) {
        return sentence.replaceAll(" - .* - ", "");
    }

    public static String removeParenthesesPhrases(String sentence) {
        return sentence.replaceAll("\\(.*\\)", "");
    }

    public static String removeRefrence(String sentence) {
        return sentence.replaceAll("\\[[0-9]+\\]", "").replaceAll("[0-9]+\\]", "").replaceAll("\\[[0-9]*$", "")
                .replaceAll("\\[citation needed\\]", "");
    }

    public static String removeBetweenCommas(String sentence) {
        String localSentence = sentence.toLowerCase();
        String[] phrases = localSentence.split(",");

        for (String phrase : phrases) {
            if (!(phrase.contains("[s{") || phrase.contains("[o{"))) {
                localSentence = localSentence.replace("," + phrase, "").replace(phrase + ",", ""); //replace ignore regular expressions
            }
        }
        return localSentence;
    }

    public static String removeBeforeSubjectAfterObject(String sentence) {
        String[] phrases = sentence.split(" ");
        String resultString = "";
        boolean start = false;
        boolean end = false;
        for (String phrase : phrases) {
            if (phrase.startsWith("[s")) {
                start = true;
            } else if (phrase.startsWith("[o")) {
                end = true;
            }

            if (start || end) {
                resultString += " " + phrase;
            }
            if (start && end) {
                break;
            }
        }
        return resultString.trim();
    }

}
