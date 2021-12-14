package offLine.nlp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Database;
import offLine.kg_explorer.model.Predicate;

/**
 *
 * @author aorogat
 */
public class Predicate_Representation_Extractor {

    static void fill_S_O() {
        //From Labels: Any label can be a verb can be added.
        //1- Verbs end by a preposition (e.g., developed by, ...)
        ArrayList<Predicate> predicates = Database.getVerbPrepositionLabels();
        for (Predicate predicate : predicates) {
            try {
                Database.storePredicates_VP("VP_S_O", predicate, predicate.getLabel(), 100, 0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //2- Labels of one word and only can be a verb
        predicates = Database.getPredicates();
        for (Predicate predicate : predicates) {
            if (!predicate.getLabel().trim().contains(" ")) //only label of one word
            {
                try {
                    if (wordPOS(predicate.getLabel()).trim().equals("v")) {
                        Database.storePredicates_VP("VP_S_O", predicate, predicate.getLabel(), 99, 0);
                    } else if (wordPOS(predicate.getLabel()).trim().equals("n")) {
                        Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 99, 0);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                //More than one word, if there are no verbs, add to NP_S_O
                try {
                    String label = predicate.getLabel().trim();
                    StringTokenizer tokenizer = new StringTokenizer(label);
                    ArrayList<String> tokens = new ArrayList<>();
                    boolean hasVerb = false;
                    while (tokenizer.hasMoreTokens())
                    {
                        tokens.add(tokenizer.nextToken());
                    }
                    for (int i = 0; i < tokenizer.countTokens(); i++) {
                        if (wordPOS(tokens.get(i)).trim().equals("v")) {
                            hasVerb = true;
                            break;
                        }
                    }
                    if (!hasVerb) {
                        Database.storePredicates_NP("NP_S_O", predicate, label, 98, 0);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    static void fill_O_S() {
        //From Labels: Any label can be a verb can be added.
        //1- Verbs end by a preposition can be changed (e.g., developed by to developed)
        ArrayList<Predicate> predicates = Database.getVerbPrepositionLabels();
        for (Predicate predicate : predicates) {
            try {
                if (predicate.getLabel().toLowerCase().endsWith(" by")) {
                    Database.storePredicates_VP("VP_O_S", predicate, predicate.getLabel().toLowerCase()
                            .replace(" by", ""), 100, 0);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        String dbpediaURL = "https://dbpedia.org/sparql";
        DBpediaExplorer dBpediaExplorer = DBpediaExplorer.getInstance(dbpediaURL);
        fill_S_O();
        fill_O_S();

    }

    private static String wordPOS(String w) throws MalformedURLException, ProtocolException, IOException {
        URL url = new URL("http://127.0.0.1:12311/type?word=" + w);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }
}
