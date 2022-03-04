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

    public DBpedia(String endpoint) {
        this.endpoint = endpoint;
    }

    public static KnowledgeGraph getInstance(String endpoint) {
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
            "dbo:deathPlace",
            "dbr:deathPlace",
            "dbp:deathPlace",
            "dbo:birthPlace",
            "dbr:birthPlace",
            "dbp:birthPlace",
            
            "rdfs:label",
            "rdfs:subPropertyOf",
            "rdfs:comment",
            "rdfs:label",
            "rdfs:seeAlso",
            "rdf:type",
            "rdfs:subClassOf",
            "rdfs:range",
            "rdfs:domain",
            
            "dbo:abstract",
            "dbo:wikiPageWikiLink",
            "dbo:wikiPageExternalLink",
            "dbo:wikiPageID",
            "dbo:wikiPageLength",
            "dbo:wikiPageRevisionID",
            "dbo:wikiPageRedirects",
            "dbo:wikiPageDisambiguates",
            "dbo:thumbnail",
            
            "dbo:population_as_of",
            
            "<http://dbpedia.org/ontology/wikiPageRevisionID>",
            
            "dbo:wikiPageWikiLink",
            "<http://dbpedia.org/ontology/wikiPageWikiLink>",
            
            
            
            "dbp:image",
            "dbp:wikiPageUsesTemplate",
            "dbp:image",
            "dbp:name",
            
            "owl:sameAs",
            "owl:equivalentProperty",
            "owl:differentFrom",
            "owl:versionInfo",
            "owl:disjointWith",
            "owl:equivalentClass",
            
            "foaf:name",
            "foaf:primaryTopic",
        
            "<http://purl.org/linguistics/gold/hypernym>",
//            "<http://www.w3.org/ns/prov#wasDerivedFrom>"
        
        
        
        };
        return unwantedProperties;
    }

    
}
