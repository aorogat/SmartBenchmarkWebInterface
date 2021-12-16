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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import offLine.kg_explorer.explorer.DBpediaExplorer;
import offLine.kg_explorer.explorer.Database;
import offLine.kg_explorer.model.Predicate;
import offLine.scrapping.wikipedia.NLP;
import online.nl_generation.chunking.Phrase;

/**
 *
 * @author aorogat
 */
public class Predicate_Representation_Extractor {

    static void fill_S_O() {
        //From Labels: Any label can be a verb can be added.
        //1- Verbs end by a preposition (e.g., developed by, ...) if "for used, it is NP
        ArrayList<Predicate> predicates = Database.getVerbPrepositionLabels();
        for (Predicate predicate : predicates) {
            try {
                if (predicate.getLabel().endsWith(" of")) {
                    Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 99, 0);
                } else {
                    Database.storePredicates_VP("VP_S_O", predicate, predicate.getLabel(), 100, 0);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //2- Labels of one word and only can be a verb
        predicates = Database.getPredicates();
        ArrayList<String> tokens = new ArrayList<>();;
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
                    tokens.clear();
                    boolean hasVerb = false;
                    while (tokenizer.hasMoreTokens()) {
                        tokens.add(tokenizer.nextToken());
                    }
                    for (int i = 0; i < tokens.size(); i++) {
                        if (wordPOS(tokens.get(i)).trim().contains("v")
                                || label.contains(" above")
                                || label.contains(" across")
                                || label.contains(" against")
                                || label.contains(" along")
                                || label.contains(" among")
                                || label.contains(" around")
                                || label.contains(" at")
                                || label.contains(" before")
                                || label.contains(" behind")
                                || label.contains(" below")
                                || label.contains(" beneath")
                                || label.contains(" beside")
                                || label.contains(" between")
                                || label.contains(" by")
                                || label.contains(" from")
                                || label.contains(" in")
                                || label.contains(" into")
                                || label.contains(" near")
                                || label.contains(" on")
                                || label.contains(" to")
                                || label.contains(" toward")
                                || label.contains(" under")
                                || label.contains(" upon")
                                || label.contains(" with")
                                || label.contains(" within")) {
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

    static void fill_from_text_corpus() {
        ArrayList<Predicate> predicates = Database.getNLPatterns();
        ArrayList<Phrase> phrases = new ArrayList<>();
        for (Predicate predicate : predicates) {
            System.out.print(predicate.getPredicateURI() + "\t");
            System.out.print(predicate.getPredicateContext().getSubjectType() + "\t");
            System.out.print(predicate.getPredicateContext().getObjectType() + "\t");
            System.out.print(predicate.getLabel() + "\t");
            System.out.print(predicate.getNLPattern() + "\n");
            try {
                phrases = NLP.summarySentence(predicate.getNLPattern(),
                        predicate.getLabel(),
                        getType(predicate.getNLPattern(), "(\\[s\\{(.*?)\\}\\]?)"),
                        getType(predicate.getNLPattern(), "(\\[o\\{.*\\}\\]?)"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            for (Phrase phrase : phrases) {
                try {
                    System.out.println(phrase.getSentence());
                    System.out.println(phrase.getVerbPhrase() + "(" + phrase.getLabelSimilarity() + ")"
                            + "(" + phrase.getBaseVerbForm() + ")");
                    Database.storeNL_VP(phrase, predicate);
                } catch (IOException ex) {
                    Logger.getLogger(Predicate_Representation_Extractor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    private static String getType(String mydata, String reg) {
        String p = "";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(mydata);
        if (matcher.find()) {
            p = matcher.group(1);
        }
        return p;
    }

    public static void main(String[] args) throws SQLException {
        fill_S_O();
        fill_O_S();
        fill_from_text_corpus();
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
