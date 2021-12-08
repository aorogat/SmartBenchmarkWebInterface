package online.nl_generation.chunking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class PhraseSimilarity {

    public static void main(String[] args) throws IOException {
//            similarity("river mouth", "flows into");
//            similarity("flows to", "flows into");
              similarity("nationality", "was born on");
    }
    
    public static double similarity(String label, String predicateRepresentation) throws MalformedURLException, ProtocolException, IOException {
        predicateRepresentation = predicateRepresentation.replace("s_o:", "");
        predicateRepresentation = predicateRepresentation.replace("o_s:", "");
        double similar = 0;
        Chuncker chunkerExample = new Chuncker();
        String label_modified = chunkerExample.isItVP("X " + label + " Y");
        if(label_modified == null)
            label_modified = chunkerExample.isItVP(label);
        if(label_modified == null)
            label_modified = label.split(" ")[label.split(" ").length-1]+"_VERB";
        
        ArrayList<String> label_AllNouns = new ArrayList<>();
        
        String label_mainVerb = "";
        String predicateRepresentation_mainVerb = "";
        
        //Get label verb or nouns
        String[] label_words = label_modified.split(" ");
        for (int i = 0; i < label_words.length; i++) {
            if (label_words[i].contains("_VERB")) {
                label_mainVerb = label_words[i];
            } else if (label_words[i].contains("_NOUN")) {
                label_AllNouns.add(label_words[i]);
            }
        }
        
        //Get predicate verb or nouns
        String predicate_modified = chunkerExample.isItVP("X " + predicateRepresentation.replace("_VERB", "")
                .replace("_NOUN", "").trim()+ " Y");
        String[] predicate_words = predicate_modified.split(" ");
        for (int i = 0; i < predicate_words.length; i++) {
            if (predicate_words[i].contains("_VERB")) {
                predicateRepresentation_mainVerb = predicate_words[i];
            } else if (predicate_words[i].contains("_NOUN")) {
                predicateRepresentation_mainVerb = predicate_words[i];
            }
        }
        if(predicateRepresentation_mainVerb==null)
            predicateRepresentation_mainVerb = predicate_words[predicate_words.length-1]+"_VERB";
        
        if (!"".equals(label_mainVerb)) {
            return similaritySimple(label_mainVerb, predicateRepresentation_mainVerb);
        } else {
            double sum = 0;
            double avgSim = 0;
            for (String l : label_AllNouns) {
                sum += similaritySimple(l, predicateRepresentation_mainVerb);
            }
            avgSim = sum/label_AllNouns.size();
            similar = avgSim;
//            System.out.println("avgSim: " + avgSim);
        }
        return similar;
    }

    private static double similaritySimple(String w1, String w2) throws MalformedURLException, ProtocolException, IOException {
        URL url = new URL("http://127.0.0.1:12311/similarity?word1="+w1+"&word2="+w2);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return Double.parseDouble(content.toString());
    }
}