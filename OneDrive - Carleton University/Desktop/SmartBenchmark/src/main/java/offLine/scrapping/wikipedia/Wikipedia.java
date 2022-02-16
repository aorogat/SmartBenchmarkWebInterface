package offLine.scrapping.wikipedia;

import com.ibm.icu.text.BreakIterator;
import edu.stanford.nlp.coref.data.DocumentPreprocessor;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.WordToSentenceProcessor;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author aorogat
 */
public class Wikipedia {

    public static ArrayList<String> getNLSentences(String startString, String endString, boolean fromSubjectPage) {
        HashSet<String> sentences = new HashSet<>();
        

        Document doc = null;
        try {
            if (fromSubjectPage) {
                doc = Wikipedia.getWikiDocumentByKeyword(startString);//(capitalize(startString));
            } else {
                doc = Wikipedia.getWikiDocumentByKeyword(endString);//(capitalize(endString));
            }
            //Get Sentences start by startString and ends by endString from paragraphs
            // Use https://github.com/unicode-org/icu/releases/tag/release-69-1 for better sentece split
            //https://stackoverflow.com/questions/2687012/split-string-into-sentences
            //https://unicode-org.github.io/icu-docs/#/icu4j/com/ibm/icu/text/BreakIterator.html

            startString = startString.toLowerCase().replaceAll("\\(.*\\)", "");
            endString = endString.toLowerCase().replaceAll("\\(.*\\)", "");

            String docString = doc.getElementsByTag("p").text();
            //docString += doc.getElementsByClass("infobox").text();
            //String docString = doc.text(); //Out of memory exception and not working well
            String nls = ".";

            List<CoreLabel> tokens = new ArrayList<CoreLabel>();
            PTBTokenizer<CoreLabel> tokenizer = new PTBTokenizer<CoreLabel>(new StringReader(docString), new CoreLabelTokenFactory(), "");
            while (tokenizer.hasNext()) {
                tokens.add(tokenizer.next());
            }
            //// Split sentences from tokens
            List<List<CoreLabel>> sentencesList = new WordToSentenceProcessor<CoreLabel>().process(tokens);
            //// Join back together
            int endL;
            int startL = 0;
            List<String> sentenceList = new ArrayList<String>();
            for (List<CoreLabel> sentence : sentencesList) {
                endL = sentence.get(sentence.size() - 1).endPosition();
                sentenceList.add(docString.substring(startL, endL).trim());
                startL = endL;
            }
            for (String s : sentenceList) {
                s = s.toLowerCase();
                if (s.contains(startString) && s.contains(endString)) {
                    sentences.add(s);
                }
            }
//            System.out.println(StringUtils.join(sentenceList, " _ "));

            //Get sentences contains both start and end
//            BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
//            iterator.setText(docString.toLowerCase());
//            int start = iterator.first();
//            for (int end = iterator.next();
//                    end != BreakIterator.DONE;
//                    start = end, end = iterator.next()) {
//                String s = docString.toLowerCase().substring(start, end);
//
//                if (s.contains(startString) && s.contains(endString)) {
//                    sentences.add(s);
//                }

//                if(startString.contains(endString)){
//                    if(s.contains(startString) && s.replace(startString, "").contains(endString))
//                        sentences.add(s);
//                }
//                else if(endString.contains(startString))
//                {
//                    if(s.contains(endString) && s.replace(endString, "").contains(startString))
//                        sentences.add(s);    
//                }
//                else if(s.contains(startString) && s.contains(endString))
//                    sentences.add(s);
//            }

            //Get senteces start by subject and ends by object
//            String inbetweenString = ".";
//            while (inbetweenString != null) {
//                inbetweenString = StringUtils.substringBetween(docString, startString, endString);
//                if (inbetweenString != null) {
//                    nls = startString
//                            + inbetweenString
//                            + endString;
//                    sentences.add(nls);
//                    docString = docString.replace(nls.toLowerCase(), "");
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(sentences);

    }

    private static Document getWikiDocumentByKeyword(String keyword) throws Exception {
        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/" + keyword).get();
        return doc;
    }

    public static String toTitleCase(String word) {
        if (word.equals("in")
                || word.equals("on")
                || word.equals("at")
                || word.equals("the")
                || word.equals("of")) {
            return word;
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    public static String capitalize(String phrase) {
        String[] splitPhrase = phrase.split(" ");
        String result = "";

        for (String word : splitPhrase) {
            result += toTitleCase(word) + " ";
        }
        return result.trim();
    }

}
