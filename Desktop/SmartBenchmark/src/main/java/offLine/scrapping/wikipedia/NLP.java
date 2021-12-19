package offLine.scrapping.wikipedia;

import java.io.IOException;
import java.util.ArrayList;
import online.nl_generation.chunking.Chuncker;
import online.nl_generation.chunking.Phrase;

public class NLP {

    static Chuncker chunkerExample;

    public static ArrayList<Phrase> getCandidatePhrases(String sentence, String label, String sType, String oType) throws IOException {
        String sourceSentence = sentence;
        if (chunkerExample == null) {
            chunkerExample = new Chuncker();
        }
        sentence = removeHyphenedPhrases(sentence);
        sentence = removeParenthesesPhrases(sentence);
        sentence = removeRefrence(sentence);
//        sentence = removeBeforeSubjectAfterObject(sentence);

        sentence = sentence.replace(sType, "sssss");
        sentence = sentence.replace(oType, "ooooo");
        boolean s_o_direction = sentence.indexOf("sssss") < sentence.indexOf("ooooo");

//        sentence = sentence.replace("sssss", "");
//        sentence = sentence.replace("ooooo", "");
//        String sentenceVP = chunkerExample.get_only_VP(sentence);
        ArrayList<Phrase> vps = chunkerExample.firstANDlast_VP_PP(sentence, label, s_o_direction);
        String sentenceVP = "";
        for (Phrase vp : vps) {
            vp.setSentence(sourceSentence);
            if (vp == null) {
                continue;
            }
            sentenceVP += "vp_";
            if (vp.getDirection() == Phrase.S_O) {
                sentenceVP += "s_o:";
            } else {
                sentenceVP += "o_s:";
            }
            sentenceVP += vp.getPhrase() + "(" + vp.getLabelSimilarity() + ")";
            sentenceVP += "[" + vp.getBaseVerbForm() + "]\t\t";
        }
//        String sentenceVP = chunkerExample.firstANDlast_VP_PP(sentence, label, s_o_direction).toString();

//        if (sentenceVP == null
//                || sentenceVP.toLowerCase().replace("--", "").trim().length() < 4
//                || sentenceVP.toLowerCase().replace("--", "").trim().equals("is")
//                || sentenceVP.toLowerCase().replace("--", "").trim().equals("are")
//                || sentenceVP.toLowerCase().replace("--", "").trim().equals("was")
//                || sentenceVP.toLowerCase().replace("--", "").trim().equals("were") //                || !sentenceVP.trim().equals(sentence.trim())
//                || sentenceVP.toLowerCase().replace("--", "").trim().equals("vp_s_o:")
//                || sentenceVP.toLowerCase().replace("--", "").trim().equals("vp_o_s:")) {
//            sentenceVP = "";
//        }
//
//        sentenceVP = sentenceVP.trim();
        //try NP
//        String sentenceNP = "";
//        if (sentenceVP.equals("")) {
//            sentenceNP = chunkerExample.get_only_NP(sentence);
//            if (s_o_direction) {
//                sentenceNP = "np_s_o:" + sentenceNP.replaceAll("\\_VERB", "").replaceAll("\\_NOUN", "");
//            } else {
//                sentenceNP = "np_o_s:" + sentenceNP.replaceAll("\\_VERB", "").replaceAll("\\_NOUN", "");
//            }
//            if (sentenceNP == null
//                    || sentenceNP.toLowerCase().replace("--", "").trim().length() < 4
//                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("is")
//                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("are")
//                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("was")
//                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("were") //                || !sentenceVP.trim().equals(sentence.trim())
//                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("np_s_o:")
//                    || sentenceNP.toLowerCase().replace("--", "").trim().equals("np_o_s:")) {
//                sentenceNP = "";
//                return sentenceNP;
//            }
        ArrayList<Phrase> modifiedPhrases = new ArrayList<>();
        for (Phrase vp : vps) {
            vp.setPhrase(vp.getPhrase().replaceAll("\\_VERB", "").replaceAll("\\_NOUN", "").replaceAll("\\_ADVP", ""));
            if (vp.getPhrase() == null
                    || vp.getPhrase().toLowerCase().replace("--", "").trim().length() < 4
                    || vp.getPhrase().toLowerCase().replace("--", "").trim().equals("is")
                    || vp.getPhrase().toLowerCase().replace("--", "").trim().equals("are")
                    || vp.getPhrase().toLowerCase().replace("--", "").trim().equals("was")
                    || vp.getPhrase().toLowerCase().replace("--", "").trim().equals("were") //                || !sentenceVP.trim().equals(sentence.trim())
                    || vp.getPhrase().toLowerCase().replace("--", "").trim().equals("np_s_o:")
                    || vp.getPhrase().toLowerCase().replace("--", "").trim().equals("np_o_s:")) {

            }
            else
                modifiedPhrases.add(vp);
        }
        return modifiedPhrases;
//        return sentenceVP.replaceAll("\\_VERB", "").replaceAll("\\_NOUN", "").replaceAll("\\_ADVP", "");
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
