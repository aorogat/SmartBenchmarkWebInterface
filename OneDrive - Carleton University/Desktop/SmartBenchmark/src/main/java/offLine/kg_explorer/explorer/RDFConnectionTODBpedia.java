package offLine.kg_explorer.explorer;

import org.apache.jena.query.DatasetFactory;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionRemote;
import org.apache.jena.system.Txn;
import org.apache.jena.vocabulary.ReasonerVocabulary;

/*
 * Example of a building a remote connection.
 * The connection is to a Fuskei server and may use special features such as more efficient data encoding.
 */
public class RDFConnectionTODBpedia {

    public static void main(String... args) {

        RDFConnection conn = RDFConnectionRemote.create()
                .destination("https://dbpedia.org/sparql")
                .queryEndpoint("sparql")
                // Set a specific accept header; here, sparql-results+json (preferred) and text/tab-separated-values
                // The default is "application/sparql-results+json, application/sparql-results+xml;q=0.9, text/tab-separated-values;q=0.7, text/csv;q=0.5, application/json;q=0.2, application/xml;q=0.2, */*;q=0.1"
                .acceptHeaderSelectQuery("application/sparql-results+json, application/sparql-results+xml;q=0.9")
                .build();

//        Query query = QueryFactory.create("SELECT DISTINCT ?s ?p ?o WHERE { "
//                + "?s ?p ?o. ?o ?t ?l. }");
        Query query = QueryFactory.create(
                "PREFIX rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "SELECT *  {  "
                + "SERVICE <http://dbpedia.org/sparql>\n"
                + "  {?s ?p ?o. ?o rdf:type ?type}"
                + "FILTER EXISTS {\n"
                + "      ?o <" + ReasonerVocabulary.directRDFType + "> ?type .\n"
                + "    }.\n"
                + "} "
                + " ");
//        Query query = QueryFactory.create(
//                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
//                + "PREFIX fn: <java:jena.example.similar.propfunction.>\n"
//                + "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>\n"
//                + "SELECT  ?company ?label ?funcRes\n"
//                + "WHERE \n"
//                + "{\n"
//                + "  SERVICE <http://dbpedia.org/sparql>\n"
//                + "  {\n"
//                + "    ?company a dbpedia-owl:Company . \n"
//                + "    ?company rdfs:label ?label .\n"
//                + "    FILTER (lang(?x) = \"en\")\n"
//                + "  }\n"
//                + "  BIND (fn:DiceCoeff(?x, \"exp\") as ?funcRes)\n"
//                + "}\n"
//                + "ORDER BY DESC(?funcRes)\n"
//                + "LIMIT 10");
        System.out.println(ReasonerVocabulary.directRDFType);
        QueryExecution qexec = QueryExecutionFactory.create(query, DatasetFactory.createMem());
        ResultSet r = qexec.execSelect();
        ResultSetFormatter.out(System.out, r, query);
//        conn.querySelect("SELECT DISTINCT ?p ?type {  SERVICE <http://dbpedia.org/sparql>\n"
//                + "  {?s ?p ?o}. "
//                + "?o <" + ReasonerVocabulary.directRDFType + "> ?type }", (qs) -> {
//                    Resource subject = qs.getResource("p");
//                    System.out.println("Subject: " + subject);
//                });
        conn.commit();

    }
}
