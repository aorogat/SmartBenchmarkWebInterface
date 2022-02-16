package online.nl_generation.chunking;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

public class Chuncker {

    private final ChunkerME chunker;
    private final POSTaggerME tagger;
    static int VP_counter = 0;
    static int NP_counter = 0;

    public Chuncker() throws IOException {
        //Download from http://opennlp.sourceforge.net/models-1.5/
        String currentDirectory = System.getProperty("user.dir");
        chunker = new ChunkerME(new ChunkerModel(new FileInputStream(currentDirectory + "\\OpennlpPretrainedModel\\en-chunker.bin")));
        tagger = new POSTaggerME(new POSModel(new FileInputStream(currentDirectory + "\\OpennlpPretrainedModel\\en-pos-maxent.bin")));
    }

    // Given a single sentence, returns its tokens
    public static String[] getTokens(String sentence) {
        return SimpleTokenizer.INSTANCE.tokenize(sentence);
    }

    public String[] getPOS(String[] tokens) {
        return tagger.tag(tokens);
    }

    public ChunkerME getChunker() {
        return chunker;
    }

    public static Span[] groupChunks(String[] tokens, String[] chunked_tags) {
        int start = 0;
        boolean in_chunk = false;
        String POS_category = "";
        ArrayList<Span> spans = new ArrayList<>();
        for (int i = 0; i < chunked_tags.length; ++i) {
            String cur_tag = chunked_tags[i];
            if (cur_tag.startsWith("B")) {
                if (in_chunk) {
                    spans.add(new Span(start, i - 1, POS_category));
                }
                start = i;
                POS_category = cur_tag.substring(cur_tag.indexOf('-') + 1);
                in_chunk = true;
            } else if (cur_tag.startsWith("O")) {
                if (in_chunk) {
                    spans.add(new Span(start, i - 1, POS_category));
                }
                in_chunk = false;
            }
        }
        return spans.stream().toArray(Span[]::new);
    }

    public String get_only_VP(String sentence) { //this method return a VP if and only if it is the only pharase between the subject and object
        String replace;
        replace = sentence.replace("sssss", "");
        replace = sentence.replace("ooooo", "");
        sentence += " exxxx";

        String tokens[] = getTokens(sentence);
        String pos_tags[] = getPOS(tokens);
        String chunker_tags[] = getChunker().chunk(tokens, pos_tags);
        double[] probs = getChunker().probs();

        Span[] chunks = groupChunks(tokens, chunker_tags);

        Map<String, String> phrases = combineSimplePhrases(tokens, chunker_tags);

        int VP_counter = 0;
        int NP_counter = 0;
        String VP = "";

        for (String phraseKey : phrases.keySet()) {
            if (phraseKey.contains("VP")) {
                VP_counter++;
                VP = phrases.get(phraseKey);
            } else if (phraseKey.contains("NP")) {
                if ((!phrases.get(phraseKey).equals("exxxx"))
                        && (!phrases.get(phraseKey).equals("sssss"))
                        && (!phrases.get(phraseKey).equals("ooooo"))) {
                    NP_counter++;
                }
            }
//            System.out.println(phraseKey + ": " + phrases.get(phraseKey));
        }
        if (VP_counter == 1 && NP_counter == 0 && !VP.contains(" and ") && !VP.contains(" or ")) {
            return VP;
        } else {
            return "";
        }
    }

    public String get_only_NP(String sentence) { //this method return a NP if and only if it is in the form of "is the ... of"
        String replace;
        replace = sentence.replace("sssss", "");
        replace = replace.replace("ooooo", "");
        String np;

        String pattern = "(?i)is\\s+the\\s+(\\w+)\\s+of";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(replace);
        if (m.find()) {
            return m.group(1);
        }
        return "";
//        while (m.find()) {
//            System.out.println("Found value: " + m.group(0));
//        }
//
//        if (replace.trim().matches(".*(is|are) the (\\w)+ of.*")) {
//            return replace;
//        } else {
//            return "";
//        }
    }

    public String last_VP_PP(String sentence) {
        String tokens[] = getTokens(sentence);
        String pos_tags[] = getPOS(tokens);
        String chunker_tags[] = getChunker().chunk(tokens, pos_tags);
        double[] probs = getChunker().probs();

        Span[] chunks = groupChunks(tokens, chunker_tags);

        Map<String, String> phrases = combineSimplePhrases(tokens, chunker_tags);
        return phrases.get("VP");
    }

    public String isItVP(String phrase) {
        String tokens[] = getTokens(phrase);
        String pos_tags[] = getPOS(tokens);
        String chunker_tags[] = getChunker().chunk(tokens, pos_tags);
        double[] probs = getChunker().probs();
        Span[] chunks = groupChunks(tokens, chunker_tags);
        String s = "";
        Map<String, String> phrases = combineSimplePhrases(tokens, chunker_tags);
        if (phrases.isEmpty()) {
            return "";
        }
        if (VP_counter > 0) {
            return phrases.get("VP1").replace("X_NOUN", "").replace("Y_NOUN", "").trim();
        } else {
            return phrases.get("NP1").replace("X_NOUN", "").replace("Y_NOUN", "").trim();
        }
    }

    public ArrayList<Phrase> firstANDlast_VP_PP(String sentence, String label, boolean s_o_direction) {
        //To hold the output
        ArrayList<Phrase> verb_phrasess = new ArrayList<>();

        String tokens[] = getTokens(sentence);
        String pos_tags[] = getPOS(tokens);
        String chunker_tags[] = getChunker().chunk(tokens, pos_tags);
        double[] probs = getChunker().probs();
        Span[] chunks = groupChunks(tokens, chunker_tags);
        String s = "";
        Map<String, String> phrases = combineSimplePhrases(tokens, chunker_tags);

        String currentVP = null;
        Phrase verbPhraseFirst = new Phrase();
        int last = VP_counter;
        for (int i = 1; i <= last; i++) {
            try {
                verbPhraseFirst = new Phrase();
                currentVP = phrases.get("VP" + i).trim();
                verbPhraseFirst.phrase = currentVP;
                verbPhraseFirst.type = Phrase.VP;
                if (currentVP == null
                        || currentVP.toLowerCase().trim().length() < 4
                        || currentVP.toLowerCase().trim().equals("is_verb")
                        || currentVP.toLowerCase().trim().equals("are_verb")
                        || currentVP.toLowerCase().trim().equals("was_verb")
                        || currentVP.toLowerCase().trim().equals("were_verb")
                        || currentVP.toLowerCase().trim().equals("s")
                        || currentVP.toLowerCase().trim().contains("ooooo")
                        || currentVP.toLowerCase().trim().contains("sssss")
                        || currentVP.toLowerCase().trim().contains("{")
                        || currentVP.toLowerCase().trim().contains("}")
                        || currentVP.toLowerCase().trim().contains("[")
                        || currentVP.toLowerCase().trim().contains("]")
                        || currentVP.toLowerCase().trim().contains(":")) {
                    currentVP = "";
                    verbPhraseFirst.phrase = currentVP;
                } else {
                    //Get similarity
//                    double sim = BasicNLP_FromPython.similarity(label, currentVP);
//                    currentVP = currentVP + "(" + sim + ")";
//                    verbPhraseFirst.labelSimilarity = sim;
                    
                    //Get verb base form
                    String lastVerb = "";
                    String sc = "";
                    StringTokenizer tokenizer = new StringTokenizer(currentVP);
                    while (tokenizer.hasMoreTokens()) {   
                        sc = tokenizer.nextToken();
                        if(sc.toLowerCase().contains("_verb"))
                            lastVerb = sc;
                    }
                    verbPhraseFirst.baseVerbForm = BasicNLP_FromPython.vaseVerb(lastVerb.toLowerCase().replace("_verb", ""));

                    if (s_o_direction) {
                        currentVP = "vp_s_o:" + currentVP;
                        verbPhraseFirst.direction = Phrase.S_O;
                    } else {
                        currentVP = "vp_o_s:" + currentVP;
                        verbPhraseFirst.direction = Phrase.O_S;
                    }
                }
            } catch (ProtocolException ex) {
                Logger.getLogger(Chuncker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Chuncker.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (!"".equals(currentVP)) {
                verb_phrasess.add(verbPhraseFirst);
            }
        }

        return verb_phrasess;
    }

    public static void main(String[] args) throws IOException {
        Chuncker example = new Chuncker();
//         final String sentence = "The pretty cat chased the ugly rat.";
//        final String sentence = "It is very beautiful.";
        final String sentence = "sssss is the brother of ooooo"; //add extra word as the chunker always remove the last word?!
        String tokens[] = example.getTokens(sentence);
        String pos_tags[] = example.getPOS(tokens);
        String chunker_tags[] = example.getChunker().chunk(tokens, pos_tags);
        double[] probs = example.getChunker().probs();

        for (int i = 0; i < chunker_tags.length; ++i) {
            System.out.println(chunker_tags[i] + " -> " + tokens[i] + " (" + probs[i] + ")");
        }

        System.out.println("================= Chunks ===================");
        Span[] chunks = groupChunks(tokens, chunker_tags);
        Map<String, String> phrases = new HashMap<>();

        System.out.println(example.get_only_VP(sentence));
        System.out.println(example.get_only_NP(sentence));

//        String s = "";
//        for (int i = 0; i < chunks.length; i++) {
//            s = "";
//            if (chunks[i].getType().equals("NP")) {
//                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
//                    s += tokens[j] + " ";
//                }
//                System.out.println("NP: " + s);
//                phrases.put("NP", s);
//            } else if (chunks[i].getType().equals("VP")) {
//                //Combine ADVP, VP, PP
//                int t = i - 1 >= 0 ? i - 1 : 0; //to avoid -ve index problem
//                if (chunks[t].getType().equals("ADVP")) {
//                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
//                        s += tokens[j] + " ";
//                    }
//                }
//                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
//                    s += tokens[j] + " ";
//                }
//                t = i + 1 >= chunks.length-1 ? chunks.length-1 : i+1; //to avoid out of bound index problem
//                if (chunks[t].getType().equals("PP")) {
//                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
//                        s += tokens[j] + " ";
//                    }
//                }
//                System.out.println("VP: " + s);
//                phrases.put("VP", s);
//            }
//
//        }
//        System.out.println(phrases.toString());
    }

    private static Map<String, String> combineSimplePhrases(String[] tokens, String[] chunker_tags) {
        Span[] chunks = groupChunks(tokens, chunker_tags);
        Map<String, String> phrases = new HashMap<>();
        String s = "";
        VP_counter = 0;
        NP_counter = 0;
        for (int i = 0; i < chunks.length; i++) {
            s = "";
            if (chunks[i].getType().equals("NP")) {
                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
                    s += tokens[j] + "_NOUN ";
                }
                phrases.put("NP" + (++NP_counter), s);
            } else if (chunks[i].getType().equals("VP")) {
                //Combine ADVP, VP, PP
                int t = i - 1 >= 0 ? i - 1 : 0; //to avoid -ve index problem
                if (chunks[t].getType().equals("ADVP")) {
                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
                        s += tokens[j] + "_ADVP ";
                    }
                }
                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
                    s += tokens[j] + "_VERB ";
                }
                t = i + 1 >= chunks.length - 1 ? chunks.length - 1 : i + 1; //to avoid out of bound index problem
                if (chunks[t].getType().equals("PP")) {
                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
                        s += tokens[j] + " ";
                    }
                }
                phrases.put("VP" + (++VP_counter), s);
            } else {
                if (!(chunks[i].getType().equals("ADVP"))
                        && !(chunks[i].getType().equals("PP"))) {
                    for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
                        s += tokens[j] + " ";
                    }
                }
            }

        }
        return phrases;
    }
}
