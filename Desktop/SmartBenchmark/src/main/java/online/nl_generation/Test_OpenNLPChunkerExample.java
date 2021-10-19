package online.nl_generation;

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

public class Test_OpenNLPChunkerExample {

    private final ChunkerME chunker;
    private final POSTaggerME tagger;

    public Test_OpenNLPChunkerExample() throws IOException {
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

    public String last_VP_PP(String sentence) {
        String tokens[] = getTokens(sentence);
        String pos_tags[] = getPOS(tokens);
        String chunker_tags[] = getChunker().chunk(tokens, pos_tags);
        double[] probs = getChunker().probs();

        Span[] chunks = groupChunks(tokens, chunker_tags);
        
        Map<String, String> phrases = combineSimplePhrases(tokens, chunker_tags);
        return phrases.get("VP");
    }

    public static void main(String[] args) throws IOException {
        Test_OpenNLPChunkerExample example = new Test_OpenNLPChunkerExample();
//         final String sentence = "The pretty cat chased the ugly rat.";
//        final String sentence = "It is very beautiful.";
        final String sentence = "[sssss eventually flows into the ooooo";
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
        String s = "";
        for (int i = 0; i < chunks.length; i++) {
            s = "";
            if (chunks[i].getType().equals("NP")) {
                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
                    s += tokens[j] + " ";
                }
                phrases.put("NP", s);
            } else if (chunks[i].getType().equals("VP")) {
                //Combine ADVP, VP, PP
                int t = i - 1 >= 0 ? i - 1 : 0; //to avoid -ve index problem
                if (chunks[t].getType().equals("ADVP")) {
                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
                        s += tokens[j] + " ";
                    }
                }
                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
                    s += tokens[j] + " ";
                }
                t = i + 1 >= chunks.length-1 ? chunks.length-1 : i+1; //to avoid out of bound index problem
                if (chunks[t].getType().equals("PP")) {
                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
                        s += tokens[j] + " ";
                    }
                }
                phrases.put("VP", s);
            }

        }
        System.out.println(phrases.toString());
    }
    
    
    private static Map<String, String> combineSimplePhrases(String[] tokens, String[] chunker_tags)
    {
        Span[] chunks = groupChunks(tokens, chunker_tags);
        Map<String, String> phrases = new HashMap<>();
        String s = "";
        for (int i = 0; i < chunks.length; i++) {
            s = "";
            if (chunks[i].getType().equals("NP")) {
                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
                    s += tokens[j] + " ";
                }
                phrases.put("NP", s);
            } else if (chunks[i].getType().equals("VP")) {
                //Combine ADVP, VP, PP
                int t = i - 1 >= 0 ? i - 1 : 0; //to avoid -ve index problem
                if (chunks[t].getType().equals("ADVP")) {
                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
                        s += tokens[j] + " ";
                    }
                }
                for (int j = chunks[i].getStart(); j <= chunks[i].getEnd(); ++j) {
                    s += tokens[j] + " ";
                }
                t = i + 1 >= chunks.length-1 ? chunks.length-1 : i+1; //to avoid out of bound index problem
                if (chunks[t].getType().equals("PP")) {
                    for (int j = chunks[t].getStart(); j <= chunks[t].getEnd(); ++j) {
                        s += tokens[j] + " ";
                    }
                }
                phrases.put("VP", s);
            }

        }
        return phrases;
    }
}
