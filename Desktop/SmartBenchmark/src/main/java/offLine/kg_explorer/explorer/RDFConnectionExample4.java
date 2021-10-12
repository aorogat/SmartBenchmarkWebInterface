package offLine.kg_explorer.explorer;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;

/*
 * Example of a building a remote connection.
 * The connection is to a Fuskei server and may use special features such as more efficient data encoding.
 */
public class RDFConnectionExample4 {
    public static void main(String ...args) {

        RDFConnection conn0 = RDFConnectionRemote.create()
            .destination("https://dbpedia.org/sparql")
            .queryEndpoint("sparql")
            // Set a specific accept header; here, sparql-results+json (preferred) and text/tab-separated-values
            // The default is "application/sparql-results+json, application/sparql-results+xml;q=0.9, text/tab-separated-values;q=0.7, text/csv;q=0.5, application/json;q=0.2, application/xml;q=0.2, */*;q=0.1"
            .acceptHeaderSelectQuery("application/sparql-results+json, application/sparql-results+xml;q=0.9")
            .build();

        Query query = QueryFactory.create("SELECT DISTINCT ?s ?p ?o WHERE { "
                + "?s ?p ?o. ?o ?t ?l. }");

        // Whether the connection can be reused depends on the details of the implementation.
        // See example 5.
        try ( RDFConnection conn = conn0 ) {
            conn.queryResultSet(query, ResultSetFormatter::out);
        }
    }
}