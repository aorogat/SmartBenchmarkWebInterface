/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offLine.kg_explorer.explorer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class WikidataKG extends KnowledgeGraph {

    public WikidataKG(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
        if (instance == null) {
            instance = new WikidataKG(endpoint);
            return (WikidataKG) instance;
        } else {
            return (WikidataKG) instance;
        }
    }

    @Override
    public String[] getUnwantedProperties() {
        String[] s = {""};
        return s;
    }

    public ArrayList<VariableSet> runQuery(String queryString) {
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
        String command = "curl \"" + url + "\" \n"
                + "  -H \"authority: data.nobelprize.org\" \n"
                + "  -H \"sec-ch-ua: \\\" Not A;Brand\\\";v=\\\"99\\\", \\\"Chromium\\\";v=\\\"98\\\", \\\"Google Chrome\\\";v=\\\"98\\\"\" \n"
                + "  -H \"accept: application/sparql-results+json,*/*;q=0.9\" \n"
                + "  -H \"content-type: application/x-www-form-urlencoded\" \n"
                + "  -H \"sec-ch-ua-mobile: ?0\" \n"
                + "  -H \"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\" \n"
                + "  -H \"sec-ch-ua-platform: \\\"Windows\\\"\" \n"
                + "  -H \"origin: https://data.nobelprize.org\" \n"
                + "  -H \"sec-fetch-site: same-origin\" \n"
                + "  -H \"sec-fetch-mode: cors\" \n"
                + "  -H \"sec-fetch-dest: empty\" \n"
                + "  -H \"referer: https://data.nobelprize.org/sparql\" \n"
                + "  -H \"accept-language: en-CA,en;q=0.9,ar-EG;q=0.8,ar;q=0.7,en-GB;q=0.6,en-US;q=0.5\" \n"
                + "  -H \"cookie: _ga=GA1.2.1783696672.1646674344; _gid=GA1.2.239545374.1646674344; _hjSessionUser_1004842=eyJpZCI6IjkxZmViOTMyLTNmNzQtNTc0Mi05MTg0LWMzMTQ1MGRhYjdlZiIsImNyZWF0ZWQiOjE2NDY2NzQzNDQxNTQsImV4aXN0aW5nIjp0cnVlfQ==\" \n"
                + "  --data-raw \"query=" + URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString()) + "&default-graph-uri=&format=application%2Fsparql-results%2Bjson&timeout=30000&debug=on\" ";
//        "curl \"" + url + "\" \n"
//                + "  -H \"authority: query.wikidata.org\" \n"
//                + "  -H \"sec-ch-ua: \\\" Not A;Brand\\\";v=\\\"99\\\", \\\"Chromium\\\";v=\\\"98\\\", \\\"Google Chrome\\\";v=\\\"98\\\"\" \n"
//                + "  -H \"accept: application/sparql-results+json,*/*;q=0.9\" \n"
//                + "  -H \"content-type: application/x-www-form-urlencoded\" \n"
//                + "  -H \"sec-ch-ua-mobile: ?0\" \n"
//                + "  -H \"user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36\" \n"
//                + "  -H \"sec-ch-ua-platform: \\\"Windows\\\"\" \n"
//                + "  -H \"sec-fetch-site: same-origin\" \n"
//                + "  -H \"sec-fetch-mode: cors\" \n"
//                + "  -H \"sec-fetch-dest: empty\" \n"
//                + "  -H \"accept-language: en-CA,en;q=0.9,ar-EG;q=0.8,ar;q=0.7,en-GB;q=0.6,en-US;q=0.5\" \n"
//                + "  -H \"cookie: _ga=GA1.2.1783696672.1646674344; _gid=GA1.2.239545374.1646674344; _hjSessionUser_1004842=eyJpZCI6IjkxZmViOTMyLTNmNzQtNTc0Mi05MTg0LWMzMTQ1MGRhYjdlZiIsImNyZWF0ZWQiOjE2NDY2NzQzNDQxNTQsImV4aXN0aW5nIjp0cnVlfQ==\" \n"
//                + "  --data-raw \"query=" + URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString()) + "&default-graph-uri=&format=application%2Fsparql-results%2Bjson&timeout=30000&debug=on\" \n"
//                + "  ";

        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        Process process = processBuilder.start();
        try {
            process.waitFor(5, TimeUnit.SECONDS);  // let the process run for 5 seconds
            process.destroy();                     // tell the process to stop
            process.waitFor(50, TimeUnit.SECONDS); // give it a chance to stop
            process.destroyForcibly();             // tell the OS to kill the process
            process.waitFor();                     // the process is now dead
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        InputStream inputStream = process.getInputStream();
        String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        /////////////////

        System.out.println(result);

        try {
            json = new JSONObject(result);

            return json;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }

}
