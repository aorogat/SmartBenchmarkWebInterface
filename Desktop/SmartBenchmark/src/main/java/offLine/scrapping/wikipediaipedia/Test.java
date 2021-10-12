package offLine.scrapping.wikipediaipedia;
import java.util.ArrayList;
import offLine.scrapping.wikipedia.NLP;
import offLine.scrapping.wikipedia.Wikipedia;

/**
 *
 * @author aorogat
 */
public class Test {
    public static void main(String[] args) throws Exception {
        //Send Subject and Object
        ArrayList<String> sentences = Wikipedia.getNLSentences("Balornock", "GLASGOW",true);
        for (String sentence : sentences) {
            System.out.println(sentence + "\t\t" + NLP.summarySentence(sentence));
        }
    }
}
