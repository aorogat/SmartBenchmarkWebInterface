package online.nl_generation.chunking;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        if (replace.trim().matches("is the ([a-zA-Z])+ of")) {
            return replace;
        } else {
            return "";
        }
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

    public String isItVP(String phrase)
    {
        String tokens[] = getTokens(phrase);
        String pos_tags[] = getPOS(tokens);
        String chunker_tags[] = getChunker().chunk(tokens, pos_tags);
        double[] probs = getChunker().probs();
        Span[] chunks = groupChunks(tokens, chunker_tags);
        String s = "";
        Map<String, String> phrases = combineSimplePhrases(tokens, chunker_tags);
        if(phrases.isEmpty())
            return null;
        if(VP_counter>0)
            return phrases.get("VP1").replace("X_NOUN", "").replace("Y_NOUN", "").trim();
        else
            return phrases.get("NP1").replace("X_NOUN", "").replace("Y_NOUN", "").trim();
    }
    
    public String firstANDlast_VP_PP(String sentence, boolean s_o_direction) {
        String tokens[] = getTokens(sentence);
        String pos_tags[] = getPOS(tokens);
        String chunker_tags[] = getChunker().chunk(tokens, pos_tags);
        double[] probs = getChunker().probs();
        Span[] chunks = groupChunks(tokens, chunker_tags);
        String s = "";
        Map<String, String> phrases = combineSimplePhrases(tokens, chunker_tags);
        String firstVP = null;
        if(VP_counter>0)
            firstVP = phrases.get("VP1").trim();
        String lastVP = null;
        if(VP_counter>1)
            lastVP = phrases.get("VP" + VP_counter).trim();
        if (firstVP == null
                || firstVP.toLowerCase().trim().length() < 4
                || firstVP.toLowerCase().trim().equals("is")
                || firstVP.toLowerCase().trim().equals("are")
                || firstVP.toLowerCase().trim().equals("was")
                || firstVP.toLowerCase().trim().equals("were")) {
            firstVP = "";
        } else {
            if (s_o_direction) {
                firstVP = "vp_s_o:" + firstVP;
            } else {
                firstVP = "vp_o_s:" + firstVP;
            }
        }
        if (lastVP == null
                || lastVP.toLowerCase().trim().length() < 4
                || lastVP.toLowerCase().trim().equals("is")
                || lastVP.toLowerCase().trim().equals("are")
                || lastVP.toLowerCase().trim().equals("was")
                || lastVP.toLowerCase().trim().equals("were")) {
            lastVP = "";
        } else {
            if (s_o_direction) {
                lastVP = "vp_s_o:" + lastVP;
            } else {
                lastVP = "vp_o_s:" + lastVP;
            }
        }
        return firstVP + "--" + lastVP + "--";
//        for (int i = 1; i <= VP_counter; i++) {
//            String curr = phrases.get("VP" + i).trim();
//            if (curr == null
//                    || curr.toLowerCase().trim().length() < 4
//                    || curr.toLowerCase().trim().equals("is")
//                    || curr.toLowerCase().trim().equals("are")
//                    || curr.toLowerCase().trim().equals("was")
//                    || curr.toLowerCase().trim().equals("were")) {
//                continue;
//            }
//            if (s_o_direction) {
//                s += "vp_s_o:" + phrases.get("VP" + i).trim() + "--";
//            } else {
//                s += "vp_o_s:" + phrases.get("VP" + i).trim() + "--";
//            }
//        }
//        return s;
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
                    s += tokens[j]+"_NOUN ";
                }
                phrases.put("NP" + (++NP_counter), s);
            } else if (chunks[i].getType().equals("VP")) {
                //Combine ADVP, VP, PP
                int t = i - 1 >= 0 ? i - 1 : 0; //to avoid -ve index problem
                if (chunks[t].getType().equals("ADVP")) {
                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
                        s += tokens[j]+"_ADVP ";
                    }
                }
                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
                    s += tokens[j]+"_VERB ";
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
