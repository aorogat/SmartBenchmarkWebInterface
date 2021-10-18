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
        String lastVP = "";
        String lastPP = "";
        for (Span chunk : chunks) {
            if (chunk.getType().equals("VP")) {
                lastVP = "";
                for (int j = chunk.getStart(); j <= chunk.getEnd(); ++j) {
                    lastVP += tokens[j] + " ";
                }
                lastPP = "";
            }
            if (chunk.getType().equals("PP")) {
                for (int j = chunk.getStart(); j <= chunk.getEnd(); ++j) {
                    lastPP += tokens[j] + " ";
                }
            }
            
        }
        return (lastVP.trim() + " " + lastPP.trim()).trim();
    }

    public static void main(String[] args) throws IOException {
        Test_OpenNLPChunkerExample example = new Test_OpenNLPChunkerExample();
//         final String sentence = "The pretty cat chased the ugly rat.";
//        final String sentence = "It is very beautiful.";
        final String sentence = "sssss died of cancer in a hospital in ooooooo";
        String tokens[] = example.getTokens(sentence);
        String pos_tags[] = example.getPOS(tokens);
        String chunker_tags[] = example.getChunker().chunk(tokens, pos_tags);
        double[] probs = example.getChunker().probs();

        for (int i = 0; i < chunker_tags.length; ++i) {
            System.out.println(chunker_tags[i] + " -> " + tokens[i] + " (" + probs[i] + ")");
        }

        System.out.println("================= Chunks ===================");
        Span[] chunks = groupChunks(tokens, chunker_tags);
        for (Span chunk : chunks) {
            System.out.print(chunk.getType() + " => ");
            for (int j = chunk.getStart(); j <= chunk.getEnd(); ++j) {
                System.out.print(tokens[j] + " ");
            }
            System.out.println();
        }
    }
}
