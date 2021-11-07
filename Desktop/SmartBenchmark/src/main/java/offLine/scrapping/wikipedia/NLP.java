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
        boolean s_o_direction = sentence.indexOf("sssss") < sentence.indexOf("ooooo");

        sentence = sentence.replace("sssss", "");
        sentence = sentence.replace("ooooo", "");

//        String sentenceVP = chunkerExample.get_only_VP(sentence);
        String sentenceVP = chunkerExample.firstANDlast_VP_PP(sentence, s_o_direction);

        if (sentenceVP == null
                || sentenceVP.toLowerCase().replace("--", "").trim().length() < 4
                || sentenceVP.toLowerCase().replace("--", "").trim().equals("is")
                || sentenceVP.toLowerCase().replace("--", "").trim().equals("are")
                || sentenceVP.toLowerCase().replace("--", "").trim().equals("was")
                || sentenceVP.toLowerCase().replace("--", "").trim().equals("were") //                || !sentenceVP.trim().equals(sentence.trim())
                || sentenceVP.toLowerCase().replace("--", "").trim().equals("s_o:")
                || sentenceVP.toLowerCase().replace("--", "").trim().equals("o_s:")) {
            sentenceVP = "";
        }

        sentenceVP = sentenceVP.trim();

        //try NP
        String sentenceNP = "";
        if (sentenceVP.equals("")) {
            sentenceNP = chunkerExample.get_only_NP(sentence);
            if (s_o_direction) {
                sentenceNP = "np_s_o:" + sentenceNP;
            } else {
                sentenceNP = "np_o_s:" + sentenceNP;
            }
            if (sentenceNP == null
                    || sentenceNP.toLowerCase().replace("--", "").trim().length() < 4
                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("is")
                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("are")
                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("was")
                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("were") //                || !sentenceVP.trim().equals(sentence.trim())
                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("np_s_o:")
                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("np_o_s:")) {
                sentenceNP = "";
                return sentenceNP;
            }
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
