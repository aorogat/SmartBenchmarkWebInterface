/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offLine.kg_explorer.explorer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.VariableSet;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import settings.Settings;

/**
 *
 * @author ayaab
 */
public class NobelPrizeKG extends KnowledgeGraph {

    public NobelPrizeKG(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
        if (instance == null) {
            instance = new NobelPrizeKG(endpoint);
            return (NobelPrizeKG) instance;
        } else {
            return (NobelPrizeKG) instance;
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
            "rdf:type",
            "rdfs:subClassOf",
            "rdfs:range",
            "rdfs:domain",
            "owl:sameAs",
            "owl:equivalentProperty",
            "owl:differentFrom",
            "owl:versionInfo",
            "owl:disjointWith",
            "owl:equivalentClass",
            "foaf:name",
            "foaf:primaryTopic",
            "<http://purl.org/linguistics/gold/hypernym>",
            "<http://xmlns.com/foaf/0.1/name>",
            "<http://purl.org/dc/terms/title>"

        };
        return unwantedProperties;
    }

    @Override
    public ArrayList<VariableSet> runQuery(String queryString) {

        queryString = "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + "PREFIX schema: <http://schema.org/>\n"
                + queryString;

        if (!queryString.toLowerCase().contains("limit ")) {
            queryString = queryString + "\n LIMIT " + (Settings.maxAnswerCardinalityAllowed + 10);
        }
        ArrayList<VariableSet> queryResult = new ArrayList<>();

        ArrayList<String> answersList = new ArrayList<>();

        try {
            JSONObject json = postFromCURL(Settings.url, queryString);
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

                        } catch (Exception ex) {
                            ex.printStackTrace();
                            System.out.println(queryString);
                        }
                    }
                    queryResult.add(variableSet);
                }

            } catch (Exception e) {
                //e.printStackTrace();
                try {
                    VariableSet variableSet = new VariableSet();
                    //Boolean
                    if (json.getBoolean("boolean")) {
                        variableSet.getVariables().add(new Variable("v", "true", "boolean"));
                    } else {
                        variableSet.getVariables().add(new Variable("v", "false", "boolean"));
                    }
                    queryResult.add(variableSet);
                } catch (Exception et) {
                    et.printStackTrace();
                    System.out.println(queryString);
                }
            }
        } catch (Exception ee) {
            //ee.printStackTrace();
        }
        try {
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return queryResult;
    }

    private JSONObject postRequest(String url, String queryString) {
        String responseJSON = "";
        JSONObject jobj = null;
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);

        queryString = "Select * {?s ?p ?o} limit 10";

        String json = "{\"query\": \"" + queryString + "\", "
                + "\"format\": \"application/sparql-results+json\", "
                + "\"timeout\": \"30000\""
                + "}";

        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        // set your POST request headers to accept json contents
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        try {
            // your closeablehttp response
            CloseableHttpResponse response = client.execute(httpPost);

            // print your status code from the response
            System.out.println(response.getStatusLine().getStatusCode());

            // take the response body as a json formatted string 
            responseJSON = EntityUtils.toString(response.getEntity());

            // convert/parse the json formatted string to a json object
            jobj = new JSONObject(responseJSON);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return jobj;
    }

    static JSONObject postFromCURL(String url, String queryString) throws IOException, JSONException {

        JSONObject json = null;

        //////////////////
//        String command = "curl \""+url+"\" "
//                + "  -H \"authority: data.nobelprize.org\" "
//                + "  -H \"sec-ch-ua: \\\" Not A;Brand\\\";v=\\\"99\\\", \\\"Chromium\\\";v=\\\"98\\\", \\\"Google Chrome\\\";v=\\\"98\\\"\" "
//                + "  -H \"accept: application/sparql-results+json,*/*;q=0.9\" "
//                + "  -H \"content-type: application/x-www-form-urlencoded\" "
//                + "  -H \"sec-ch-ua-mobile: ?0\" "
//                + "  -H \"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\" "
//                + "  -H \"sec-ch-ua-platform: \\\"Windows\\\"\" "
//                + "  -H \"origin: https://data.nobelprize.org\" "
//                + "  -H \"sec-fetch-site: same-origin\" "
//                + "  -H \"sec-fetch-mode: cors\" "
//                + "  -H \"sec-fetch-dest: empty\" "
//                + "  -H \"referer: https://data.nobelprize.org/sparql\" "
//                + "  -H \"accept-language: en-CA,en;q=0.9,ar-EG;q=0.8,ar;q=0.7,en-GB;q=0.6,en-US;q=0.5\" "
//                + "  -H \"cookie: _ga=GA1.2.1783696672.1646674344; _gid=GA1.2.239545374.1646674344; _hjSessionUser_1004842=eyJpZCI6IjkxZmViOTMyLTNmNzQtNTc0Mi05MTg0LWMzMTQ1MGRhYjdlZiIsImNyZWF0ZWQiOjE2NDY2NzQzNDQxNTQsImV4aXN0aW5nIjp0cnVlfQ==\" "
//                + "  --data-raw \"query="+URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString())
//                + "\"  ";
//                
//        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
//        Process process = processBuilder.start();
//        try {
//            process.waitFor(5, TimeUnit.SECONDS);  // let the process run for 5 seconds
//            process.destroy();                     // tell the process to stop
//            process.waitFor(10, TimeUnit.SECONDS); // give it a chance to stop
//            process.destroyForcibly();             // tell the OS to kill the process
//            process.waitFor();                     // the process is now dead
//        } catch (InterruptedException ex) {
//            ex.printStackTrace();
//        }
//
//        InputStream inputStream = process.getInputStream();
//        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
//        /////////////////
//
//        System.out.println(result);
        URL url2 = new URL("https://data.nobelprize.org/store/sparql");
        HttpURLConnection http = (HttpURLConnection) url2.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("authority", "data.nobelprize.org");
        http.setRequestProperty("accept", "application/sparql-results+json,*/*;q=0.9");
        http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
        http.setRequestProperty("sec-ch-ua-mobile", "?0");
        http.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36");
        http.setRequestProperty("origin", "https://data.nobelprize.org");
        http.setRequestProperty("sec-fetch-site", "same-origin");
        http.setRequestProperty("sec-fetch-mode", "cors");
        http.setRequestProperty("sec-fetch-dest", "empty");
        http.setRequestProperty("referer", "https://data.nobelprize.org/sparql");
        http.setRequestProperty("accept-language", "en-CA,en;q=0.9,ar-EG;q=0.8,ar;q=0.7,en-GB;q=0.6,en-US;q=0.5");
        http.setRequestProperty("cookie", "_ga=GA1.2.1783696672.1646674344; _gid=GA1.2.239545374.1646674344; _hjSessionUser_1004842=eyJpZCI6IjkxZmViOTMyLTNmNzQtNTc0Mi05MTg0LWMzMTQ1MGRhYjdlZiIsImNyZWF0ZWQiOjE2NDY2NzQzNDQxNTQsImV4aXN0aW5nIjp0cnVlfQ==");

        String data = "query=" + URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString());

        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        OutputStream stream = http.getOutputStream();
        stream.write(out);

        BufferedReader br = null;
        if (100 <= http.getResponseCode() && http.getResponseCode() <= 399) {
            br = new BufferedReader(new InputStreamReader(http.getInputStream()));
        } else {
            br = new BufferedReader(new InputStreamReader(http.getErrorStream()));
        }

        String result = br.lines().collect(Collectors.joining());

//        System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
//        System.out.println(result);
        http.disconnect();

        try {
            json = new JSONObject(result);

            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

}
