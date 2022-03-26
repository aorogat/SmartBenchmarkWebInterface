package offLine.kg_explorer.explorer;

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
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import online.kg_extractor.model.Variable;
import online.kg_extractor.model.subgraph.Graph;
import online.kg_extractor.model.VariableSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public abstract class KnowledgeGraph {

    protected String name = "";
    protected static KnowledgeGraph instance = null;
    protected String queryString;
    protected String endpoint;
    protected String[] unwantedProperties;

    public ArrayList<VariableSet> runQuery(String queryString) {
        if (!queryString.toLowerCase().contains("limit ")&&!queryString.toLowerCase().contains("ask where")) {
            queryString = queryString + "\n LIMIT " + (Settings.maxAnswerCardinalityAllowed + 10);
        }
        ArrayList<VariableSet> queryResult = new ArrayList<>();
        try {
            ArrayList<String> answersList = new ArrayList<>();
            String url = endpoint
                    + "?default-graph-uri=" + Settings.default_graph_uri + "&"
                    + "query=" + URLEncoder.encode(queryString, StandardCharsets.UTF_8.toString()) + "&"
                    + "format=application%2Fsparql-results%2Bjson&"
                    + "timeout=30000&"
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
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
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

    protected static JSONObject readJsonFromUrl(String urlString) {
        //InputStream is = new URL(urlString).openStream();

        //dbpedia used https instead of http
        try {
            //https://stackoverflow.com/questions/26716213/unable-to-find-valid-certification-path-to-requested-target-in-glassfish-web-ser
//            System.setProperty("javax.net.ssl.trustStore", "C:/Users/ayaab/GlassFish_Server/glassfish/config/cacerts.jks");
            System.setProperty("javax.net.ssl.trustStore", "C:/Program Files/Java/jdk1.8.0_111/jre/lib/security/cacerts");
            System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//            System.out.println(System.getProperty("javax.net.ssl.trustStore"));

            doTrustToCertificates();
            URL url = new URL(urlString);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
//            HttpsURLConnection c = (HttpsURLConnection) url.openConnection();

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
//                        c = (HttpsURLConnection) url.openConnection();
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
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    
    
    // trusting all certificate 
    public static void doTrustToCertificates() throws Exception {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    return;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    return;
                }

            
            }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '" + session.getPeerHost() + "'.");
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    

    private static Document loadXMLFromString(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(new StringReader(xml));
        return builder.parse(is);
    }

    public abstract String[] getUnwantedProperties();

    public String getName() {
        return name;
    }

    public static KnowledgeGraph getInstance() {
        return instance;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getUnwantedPropertiesString() {
        String unwantedPropertiesString = "";
        for (String unwantedProperty : this.getUnwantedProperties()) {
            unwantedPropertiesString += "" + unwantedProperty + ", ";
        }
        unwantedPropertiesString = unwantedPropertiesString.substring(0, unwantedPropertiesString.length() - 2);
        return unwantedPropertiesString;
    }

}
