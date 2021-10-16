package offLine.scrapping.wikipedia;

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
        startString = startString.toLowerCase();
        endString = endString.toLowerCase();
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
            //docString += doc.getElementsByClass("infobox").text();
            //String docString = doc.text(); //Out of memory exception and not working well
            String nls = ".";
            
            //Get sentences contains both start and end
            BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
            iterator.setText(docString);
            int start = iterator.first();
            for (int end = iterator.next();
                end != BreakIterator.DONE;
                start = end, end = iterator.next()) {
                String s = docString.toLowerCase().substring(start,end);
                
                if(s.contains(startString) && s.contains(endString))
                    sentences.add(s);
                    
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
            }
            
            //Get senteces start by subject and ends by object
            String inbetweenString = ".";
            while (inbetweenString!=null) {
                inbetweenString = StringUtils.substringBetween(docString, startString, endString);
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
