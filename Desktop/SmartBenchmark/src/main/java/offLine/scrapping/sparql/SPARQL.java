
package offLine.scrapping.sparql;

public class SPARQL {
    //Good Queries
    /*
    //Direct subclasses of a specific class
    SELECT ?directSub ?super 
        WHERE { ?directSub rdfs:subClassOf ?super .
        FILTER NOT EXISTS { ?otherSub rdfs:subClassOf ?super. 
                            ?directSub rdfs:subClassOf ?otherSub .
                            FILTER (?otherSub != ?directSub)
         }
 }
    */
    
    //Returen direct superclass of an instance
    //https://stackoverflow.com/questions/29154819/jena-sparql-only-return-direct-superclass-of-instance
    /*
    PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
  PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> 
  PREFIX owl: <http://www.w3.org/2002/07/owl#> 
  SELECT * { 
    <ind> rdf:type ?directType .
    FILTER NOT EXISTS {
      <ind> rdf:type ?type .
      ?type rdfs:subClassOf ?directType .
      FILTER NOT EXISTS {
         ?type owl:equivalentClass ?directType .
      }
    }
  }
    */
}
