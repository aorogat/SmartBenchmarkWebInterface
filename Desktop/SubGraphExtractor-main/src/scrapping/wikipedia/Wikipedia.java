package scrapping.wikipedia;

import com.ibm.icu.text.BreakIterator;
import java.util.ArrayList;
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
        ArrayList<String> sentences = new ArrayList<>();
        Document doc = null;
        try {
            if(fromSubjectPage)
                doc = Wikipedia.getWikiDocumentByKeyword(startString);
            else
                doc = Wikipedia.getWikiDocumentByKeyword(endString);
            //Get Sentences start by startString and ends by endString from paragraphs
            // Use https://github.com/unicode-org/icu/releases/tag/release-69-1 for better sentece split
            //https://stackoverflow.com/questions/2687012/split-string-into-sentences
            //https://unicode-org.github.io/icu-docs/#/icu4j/com/ibm/icu/text/BreakIterator.html
            String docString = doc.getElementsByTag("p").text();
            String nls = ".";
            
            //Get sentences contains both start and end
            BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
            iterator.setText(docString);
            int start = iterator.first();
            for (int end = iterator.next();
                end != BreakIterator.DONE;
                start = end, end = iterator.next()) {
                String s = docString.toLowerCase().substring(start,end);
                if(s.toLowerCase().contains(startString.toLowerCase()) &&
                   s.toLowerCase().contains(endString.toLowerCase()))
                sentences.add(s);
            }
            
            //Get senteces start by subject and ends by object
            String inbetweenString = ".";
            while (inbetweenString!=null) {
                inbetweenString = StringUtils.substringBetween(docString, startString.toLowerCase(), endString.toLowerCase());
                if(inbetweenString == null)
                    break;
                nls = startString
                        + inbetweenString
                        + endString;
                sentences.add(nls);
                docString = docString.replace(nls.toLowerCase(), "");
            }
                  
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return sentences;

    }
    
    private static Document getWikiDocumentByKeyword(String keyword) throws Exception {
        Document doc = Jsoup.connect("https://en.wikipedia.org/wiki/" + keyword).get();
        return doc;
    }
}
