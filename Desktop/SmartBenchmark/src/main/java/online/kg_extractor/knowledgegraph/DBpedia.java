package online.kg_extractor.knowledgegraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.VariableSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author aorogat
 */
//Singleton Class
public class DBpedia extends KnowledgeGraph {

    private DBpedia(String endpoint) {
        this.endpoint = endpoint;
    }

    public static DBpedia getInstance(String endpoint) {
        if (instance == null) {
            instance = new DBpedia(endpoint);
            return (DBpedia) instance;
        } else {
            return (DBpedia) instance;
        }
    }

    @Override
    public String[] getUnwantedProperties() {
        unwantedProperties = new String[]{
            "rdfs:label",
            "rdfs:subPropertyOf",
            "rdfs:comment",
            "rdfs:label",
            "rdfs:seeAlso",
            
            "dbo:abstract",
            "dbo:wikiPageWikiLink",
            "dbo:wikiPageExternalLink",
            "dbo:wikiPageID",
            "dbo:wikiPageLength",
            "dbo:wikiPageRevisionID",
            "dbo:wikiPageRedirects",
            "dbo:wikiPageDisambiguates",
            
            "dbp:image",
            "dbp:wikiPageUsesTemplate",
            "dbp:image",
            "dbp:name",
            
            "owl:sameAs",
            "owl:equivalentProperty",
            "owl:differentFrom",
            
            "foaf:name",
            "foaf:primaryTopic"};
        return unwantedProperties;
    }

    @Override
    public ArrayList<VariableSet> runQuery(String queryString) {
        ArrayList<VariableSet> queryResult = new ArrayList<>();
        try {
            ArrayList<String> answersList = new ArrayList<>();
            String url = endpoint
                    + "?default-graph-uri=http%3A%2F%2Fdbpedia.org&"
                    + "query=" + URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString()) + "&"
                    + "format=application%2Fsparql-results%2Bjson&"
                    + "timeout=0&"
                    + "debug=on";
            try {
                JSONObject json = readJsonFromUrl(url);
                try {

                    JSONArray vars = json.getJSONObject("head").getJSONArray("vars");
                    JSONArray bindings = json.getJSONObject("results").getJSONArray("bindings");

                    for (Object binding : bindings) {
                        VariableSet variableSet = new VariableSet();
                        for (Object var : vars) {
                            try {

                                String v = (String) var;
                                JSONObject b = (JSONObject) binding;

                                variableSet.getVariables().add(
                                        new Variable(v, b.getJSONObject(v).getString("value"),
                                                b.getJSONObject(v).getString("type")));

//                                answersList.add(b.getJSONObject(v).getString("value")
//                                        .replace("http://dbpedia.org/resource/", "")
//                                        .trim().replace('_', ' '));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        queryResult.add(variableSet);
                    }

                } catch (Exception e) {
                    //e.printStackTrace();
                }
            } catch (Exception ee) {
                //ee.printStackTrace();
            }
            try {
            } catch (Exception e) {
                //e.printStackTrace();
            }
        } catch (UnsupportedEncodingException ex) {
            //ex.printStackTrace();
        }
        return queryResult;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String urlString) throws IOException, JSONException {
        //InputStream is = new URL(urlString).openStream();

        //dbpedia used https instead of http
        URL url = new URL(urlString);
        HttpURLConnection c = (HttpURLConnection) url.openConnection();
        Set<String> visitedUrls = new HashSet<>();
        boolean doneRedirecting = false;
        while (!doneRedirecting) {
            switch (c.getResponseCode()) {
                case HttpURLConnection.HTTP_MOVED_PERM:
                case HttpURLConnection.HTTP_MOVED_TEMP:
                    // Follow redirect if not already visisted
                    String newLocation = c.getHeaderField("Location");
                    if (visitedUrls.contains(newLocation)) {
                        throw new RuntimeException(MessageFormat.format(
                                "Infinite redirect loop detected for URL", ""));
                    }
                    visitedUrls.add(newLocation);

                    url = new URL(newLocation);
                    c = (HttpURLConnection) url.openConnection();
                    break;
                default:
                    doneRedirecting = true;
                    break;
            }
        }

        InputStream is = c.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            //System.out.println(jsonText);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

}
