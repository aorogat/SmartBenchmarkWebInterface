package system.components;

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
import database.Database;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.model.Predicate;
import offLine.scrapping.wikipedia.NLP;
import online.nl_generation.chunking.BasicNLP_FromPython;
import online.nl_generation.chunking.Phrase;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class Predicate_Representation_Extractor {

    public static void fill_from_Labels_VP_and_NP_S_O() {
        //From Labels: Any label can be a verb can be added.
        //1- Verbs end by a preposition (e.g., developed by, ...) if "for used, it is NP
        ArrayList<Predicate> predicates = Database.getVerbPrepositionLabels();
        for (Predicate predicate : predicates) {
            try {
                if (predicate.getLabel().endsWith(" of")) {
                    if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                        Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                    } else {
                        Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 99, 1, 1, 1);
                    }
                } else {
                    Database.storePredicates_VP("VP_S_O", predicate, predicate.getLabel(), 100, 1, 1, 1);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        //2- Labels of one word and only can be a verb
        predicates = Database.getPredicates();
        ArrayList<String> tokens = new ArrayList<>();;
        for (Predicate predicate : predicates) {
            String la = predicate.getLabel();
            if(la==null)
                continue;
            if (!la.trim().contains(" ")) //only label of one word
            {
                try {
                    if (wordPOS(predicate.getLabel()).trim().contains("v") && !wordPOS(predicate.getLabel()).trim().contains("n")) {
                        Database.storePredicates_VP("VP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                    } else if (wordPOS(predicate.getLabel()).trim().contains("n")) {
                        if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                            Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                        } else {
                            Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 99, 1, 1, 1);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                //More than one word, if there are no verbs, add to NP_S_O
                try {

                    String in = null;
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
//                            break;
                        }
                        if (label.contains(" above ")) {
                            in = "above";
                        }
                        if (label.contains(" across ")) {
                            in = "across";
                        }
                        if (label.contains(" across ")) {
                            in = "across";
                        }
                        if (label.contains(" along ")) {
                            in = "along";
                        }
                        if (label.contains(" among ")) {
                            in = "among";
                        }
                        if (label.contains(" around ")) {
                            in = "around";
                        }
                        if (label.contains(" at ")) {
                            in = "at";
                        }
                        if (label.contains(" before ")) {
                            in = "before";
                        }
                        if (label.contains(" behind ")) {
                            in = "behind";
                        }
                        if (label.contains(" below ")) {
                            in = "below";
                        }
                        if (label.contains(" beneath ")) {
                            in = "beneath";
                        }
                        if (label.contains(" beside ")) {
                            in = "beside";
                        }
                        if (label.contains(" between ")) {
                            in = "between";
                        }
                        if (label.contains(" by ")) {
                            in = "by";
                        }
                        if (label.contains(" from ")) {
                            in = "from";
                        }
                        if (label.contains(" in ")) {
                            in = "in";
                        }
                        if (label.contains(" into ")) {
                            in = "into";
                        }
                        if (label.contains(" near ")) {
                            in = "near";
                        }
                        if (label.contains(" on ")) {
                            in = "on";
                        }
                        if (label.contains(" to ")) {
                            in = "to";
                        }
                        if (label.contains(" toward ")) {
                            in = "toward";
                        }
                        if (label.contains(" under ")) {
                            in = "under";
                        }
                        if (label.contains(" upon ")) {
                            in = "upon";
                        }
                        if (label.contains(" with ")) {
                            in = "with";
                        }
                        if (label.contains(" within ")) {
                            in = "within";
                        }
                        hasVerb = true;
//                        break;
                    }

                    if (in != null) {
                        String l = predicate.getLabel();
                        int i = l.indexOf(in) + in.length();
                        String p = l.substring(0, i);
                        Database.storePredicates_VP("VP_S_O", predicate, p, 98, 1, 1, 1);
                    } else {
//                        if (!hasVerb) {
                        if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                            Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 98, 1, 1, 1);
                        } else {
                            Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 98, 1, 1, 1);
                        }
//                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void fill_from_Labels_VP_and_NP_S_O(Predicate predicate) {
        //From Labels: Any label can be a verb can be added.
        //1- Verbs end by a preposition (e.g., developed by, ...) if "for used, it is NP

        try {
            if (predicate.getLabel().endsWith(" of")) {
                if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                    Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                } else {
                    Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 99, 1, 1, 1);
                }
            } else {
                Database.storePredicates_VP("VP_S_O", predicate, predicate.getLabel(), 100, 1, 1, 1);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //2- Labels of one word and only can be a verb
        ArrayList<String> tokens = new ArrayList<>();
        if (!predicate.getLabel().trim().contains(" ")) //only label of one word
        {
            try {
                if (wordPOS(predicate.getLabel()).trim().contains("v") && !wordPOS(predicate.getLabel()).trim().contains("n")) {
                    Database.storePredicates_VP("VP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                } else if (wordPOS(predicate.getLabel()).trim().contains("n")) {
                    if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                        Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 99, 1, 1, 1);
                    } else {
                        Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 99, 1, 1, 1);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //More than one word, if there are no verbs, add to NP_S_O
            try {

                String in = null;
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
//                            break;
                    }
                    if (label.contains(" above ")) {
                        in = "above";
                    }
                    if (label.contains(" across ")) {
                        in = "across";
                    }
                    if (label.contains(" across ")) {
                        in = "across";
                    }
                    if (label.contains(" along ")) {
                        in = "along";
                    }
                    if (label.contains(" among ")) {
                        in = "among";
                    }
                    if (label.contains(" around ")) {
                        in = "around";
                    }
                    if (label.contains(" at ")) {
                        in = "at";
                    }
                    if (label.contains(" before ")) {
                        in = "before";
                    }
                    if (label.contains(" behind ")) {
                        in = "behind";
                    }
                    if (label.contains(" below ")) {
                        in = "below";
                    }
                    if (label.contains(" beneath ")) {
                        in = "beneath";
                    }
                    if (label.contains(" beside ")) {
                        in = "beside";
                    }
                    if (label.contains(" between ")) {
                        in = "between";
                    }
                    if (label.contains(" by ")) {
                        in = "by";
                    }
                    if (label.contains(" from ")) {
                        in = "from";
                    }
                    if (label.contains(" in ")) {
                        in = "in";
                    }
                    if (label.contains(" into ")) {
                        in = "into";
                    }
                    if (label.contains(" near ")) {
                        in = "near";
                    }
                    if (label.contains(" on ")) {
                        in = "on";
                    }
                    if (label.contains(" to ")) {
                        in = "to";
                    }
                    if (label.contains(" toward ")) {
                        in = "toward";
                    }
                    if (label.contains(" under ")) {
                        in = "under";
                    }
                    if (label.contains(" upon ")) {
                        in = "upon";
                    }
                    if (label.contains(" with ")) {
                        in = "with";
                    }
                    if (label.contains(" within ")) {
                        in = "within";
                    }
                    hasVerb = true;
//                        break;
                }

                if (in != null) {
                    String l = predicate.getLabel();
                    int i = l.indexOf(in) + in.length();
                    String p = l.substring(0, i);
                    Database.storePredicates_VP("VP_S_O", predicate, p, 98, 1, 1, 1);
                } else {
//                        if (!hasVerb) {
                    if (Settings.Triple_NP_Direction == Settings.LABEL_NP_SO) {
                        Database.storePredicates_NP("NP_S_O", predicate, predicate.getLabel(), 98, 1, 1, 1);
                    } else {
                        Database.storePredicates_NP("NP_O_S", predicate, predicate.getLabel(), 98, 1, 1, 1);
                    }
//                        }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void fill_from_Labels_VP_O_S() {
        //From Labels: Any label can be a verb can be added.
        //1- Verbs end by a preposition can be changed (e.g., developed by to developed)
        ArrayList<Predicate> predicates = Database.getVerbPrepositionLabels();
        for (Predicate predicate : predicates) {
            try {
                if (predicate.getLabel().toLowerCase().endsWith(" by")) {
                    Database.storePredicates_VP("VP_O_S", predicate, predicate.getLabel().toLowerCase()
                            .replace(" by", ""), 100, 1, 1, 1);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    static void fill_from_text_corpus_VP() {
        ArrayList<Predicate> predicates = Database.getNLPatterns();
        ArrayList<Phrase> phrases = new ArrayList<>();
        for (Predicate predicate : predicates) {
            System.out.print(predicate.getPredicateURI() + "\t");
            System.out.print(predicate.getPredicateContext().getSubjectType() + "\t");
            System.out.print(predicate.getPredicateContext().getObjectType() + "\t");
            System.out.print(predicate.getLabel() + "\t");
            System.out.print(predicate.getNLPattern() + "\n");
            try {
                phrases = NLP.getCandidatePhrases(predicate.getNLPattern(),
                        predicate.getLabel(),
                        getFirstRegularExMatch(predicate.getNLPattern(), "(\\[s\\{(.*?)\\}\\]?)"),
                        getFirstRegularExMatch(predicate.getNLPattern(), "(\\[o\\{(.*?)\\}\\]?)"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            for (Phrase phrase : phrases) {
                phrase.setLabelSimilarity(BasicNLP_FromPython.phraseSimilarity(predicate.getLabel(), phrase.getPhrase()));
                phrase.setSubjectSimilarity(BasicNLP_FromPython.phraseSimilarity(SPARQL.getNodeLabel(Settings.explorer, predicate.getPredicateContext().getSubjectType()),
                        phrase.getPhrase())
                );
                phrase.setObjectSimilarity(BasicNLP_FromPython.phraseSimilarity(SPARQL.getNodeLabel(Settings.explorer, predicate.getPredicateContext().getObjectType()),
                        phrase.getPhrase())
                );
                phrase.setObjectSimilarity(BasicNLP_FromPython.phraseSimilarity(predicate.getLabel(), phrase.getPhrase()));
                try {
                    System.out.println(phrase.getSentence());
                    System.out.println(phrase.getPhrase() + "(" + phrase.getLabelSimilarity() + ")"
                            + "(" + phrase.getSubjectSimilarity() + ")"
                            + "(" + phrase.getObjectSimilarity() + ")"
                            + "(" + phrase.getBaseVerbForm() + ")");
                    Database.storeNL_VP(phrase, predicate);

                } catch (IOException ex) {
                    Logger.getLogger(Predicate_Representation_Extractor.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    static void fill_from_text_corpus_NP() {
        ArrayList<Predicate> predicates = Database.getNLPatterns();
        for (Predicate predicate : predicates) {
            System.out.print(predicate.getPredicateURI() + "\t");
            System.out.print(predicate.getPredicateContext().getSubjectType() + "\t");
            System.out.print(predicate.getPredicateContext().getObjectType() + "\t");
            System.out.print(predicate.getLabel() + "\t");
            System.out.print(predicate.getNLPattern() + "\n");

            String np = getFirstRegularExMatch(predicate.getNLPattern(), "((is|are|was|were)\\ (a|the|an)\\ (([a-z0-9]*(\\ )*){1,3})\\ (" + getVerbPrepositionsConcatenated("|") + "))");
            if ("".equals(np) || np == null) {
                continue;
            }

            boolean s_o_direction = predicate.getNLPattern().indexOf("[s{") < predicate.getNLPattern().indexOf("[o{");
            String subjectType = SPARQL.getNodeLabel(Settings.explorer, predicate.getPredicateContext().getSubjectType());
            String objectType = SPARQL.getNodeLabel(Settings.explorer, predicate.getPredicateContext().getObjectType());
            double labelSimilarity = BasicNLP_FromPython.phraseSimilarity(predicate.getLabel(), np);
            double subjectSimilarity = BasicNLP_FromPython.phraseSimilarity(subjectType, np);
            double objectSimilarity = BasicNLP_FromPython.phraseSimilarity(objectType, np);

            System.out.println();
            System.out.println("");

            Phrase phrase = new Phrase();
            phrase.setSentence(predicate.getNLPattern());
            phrase.setPhrase(np);
            phrase.setLabelSimilarity(labelSimilarity);
            phrase.setSubjectSimilarity(subjectSimilarity);
            phrase.setObjectSimilarity(objectSimilarity);

            if (s_o_direction) {
                phrase.setDirection(Phrase.S_O);
            } else {
                phrase.setDirection(Phrase.O_S);
            }
            System.out.println(np + "\t" + predicate.getLabel() + "\t" + phrase.getLabelSimilarity());
            System.out.println(np + "\t" + subjectType + "\t" + subjectSimilarity);
            System.out.println(np + "\t" + objectType + "\t" + objectSimilarity);
            try {
                if (!"".equals(phrase.getPhrase())) {
                    Database.storeNL_NP(phrase, predicate);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private static String getFirstRegularExMatch(String sentence, String reg) {
        String p = "";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(sentence);
        if (matcher.find()) {
            p = matcher.group(1);
        }
        return p;
    }

    public static void main(String[] args) throws SQLException, IOException {
        fill_from_Labels_VP_and_NP_S_O();
        fill_from_Labels_VP_O_S();
//        fill_from_text_corpus_VP();
//        fill_from_text_corpus_NP();
        Database.populateLexicon();
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

    public static String getVerbPrepositionsConcatenated(String separator) {
        return "above" + separator
                + "across" + separator
                + "about" + separator
                + "of" + separator
                + "for" + separator
                + "against" + separator
                + "along" + separator
                + "among" + separator
                + "around" + separator
                + "at" + separator
                + "before" + separator
                + "behind" + separator
                + "below" + separator
                + "beneath" + separator
                + "beside" + separator
                + "between" + separator
                + //                "by" + separator +
                "in" + separator
                + "into" + separator
                + "near" + separator
                + "on" + separator
                + "to" + separator
                + "toward" + separator
                + "under" + separator
                + "upon" + separator
                + "with" + separator
                + "within";
    }
}
