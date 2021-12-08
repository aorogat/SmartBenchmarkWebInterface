package offLine.kg_explorer.explorer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import offLine.kg_explorer.model.ListOfPredicates;
import offLine.kg_explorer.model.Predicate;
import offLine.kg_explorer.model.PredicateContext;
import offLine.kg_explorer.model.PredicateTripleExample;
import offLine.kg_explorer.model.Predicate_NLP_Representation;

/**
 *
 * @author aorogat
 */
public class Database {

    static Statement st = null;
    static boolean connected = false;

    public static Statement connect() {
        if (connected) {
            return st;
        }
        connected = true;
        String url = "jdbc:postgresql://localhost:5432/dbpedia";
        String user = "postgres";
        String password = "admin";

        try {
            Connection con = DriverManager.getConnection(url, user, password);

            st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT VERSION()");
            if (rs.next()) {
//                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return st;
    }

    public static boolean storePredicates(Predicate predicate) {
        connect();
        try {
            String sql = "";
            sql += "INSERT INTO \"Predicates\" (\"URI\", \"Label\", \"Context_Subject\", \"Context_Object\", \"ContextWeight\")\n"
                    + "VALUES( '" + predicate.getPredicateURI() + "',"
                    + "'" + predicate.getLabel() + "',"
                    + "'" + predicate.getPredicateContext().getSubjectType() + "',"
                    + "'" + predicate.getPredicateContext().getObjectType() + "',"
                    + "" + predicate.getPredicateContext().getWeight() + ""
                    + " )"
                    + "ON CONFLICT (\"URI\", \"Context_Subject\", \"Context_Object\") DO NOTHING;; \n";

            st.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static ArrayList<Predicate> getPredicates() {
        connect();
        ArrayList<Predicate> predicates = new ArrayList<>();
        try {
            String sql = "SELECT *\n"
                    + "FROM \"Predicates\"\n"
                    + "WHERE \"processed\" is NULL "
                    + "ORDER BY \"ContextWeight\" DESC;";

            ResultSet result = st.executeQuery(sql);
            Predicate p;
            while (result.next()) {
                p = new Predicate(Explorer.instance);
                p.setPredicateURI(result.getString("URI"));
                p.setLabel(result.getString("Label"));
                p.setPredicateContext(new PredicateContext(
                        result.getString("Context_Subject"),
                        result.getString("Context_Object"),
                        (int) result.getDouble("ContextWeight")));
                predicates.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return predicates;
    }

    public static boolean storePredicates_NLP_Representation(Predicate predicate, ArrayList<PredicateTripleExample> examples) {
        connect();
        try {
            String sql = "";
            System.out.println(predicate.toString());
            for (PredicateTripleExample example : examples) {
                
                for (Predicate_NLP_Representation nlp : example.getNlsSuggestionsObjects()) {
                    System.out.println("'" + predicate.getPredicateURI() + "',"
                            + "'" + predicate.getPredicateContext().getSubjectType() + "',"
                            + "'" + predicate.getPredicateContext().getObjectType() + "',"
                            + "'" + example.getSubjectURI() + "',"
                            + "'" + example.getObjectURI() + "',"
                            + "'" + nlp.getSentence() + "',"
                            + "'" + nlp.getPattern() + "',"
                            + "'" + nlp.getReducedPattern() + "'");
                    
                    sql += "INSERT INTO \"NLP_Representation\" (\"URI\", \"ContextSubject\", \"ContextObject\", "
                            + "\"TripleExampleSubject\", \"TripleExampleObject\", \"Sentence\", "
                            + "\"Pattern\", \"ReducedPattern\")\n"
                            + "VALUES( '" + predicate.getPredicateURI() + "',"
                            + "'" + predicate.getPredicateContext().getSubjectType() + "',"
                            + "'" + predicate.getPredicateContext().getObjectType() + "',"
                            + "'" + example.getSubjectURI() + "',"
                            + "'" + example.getObjectURI() + "',"
                            + "'" + nlp.getSentence() + "',"
                            + "'" + nlp.getPattern() + "',"
                            + "'" + nlp.getReducedPattern() + "'"
                            + " )"
                            + "; \n";
                }
            }
            
            sql += "UPDATE \"Predicates\" SET"
                    + "\"processed\" = 'YES'\n"
                    + "WHERE "
                    + "\"URI\" = '"+predicate.getPredicateURI()+"' AND"
                    + "\"Context_Subject\" = '"+predicate.getPredicateContext().getSubjectType()+"' AND"
                    + "\"Context_Object\" = '"+predicate.getPredicateContext().getObjectType()+"';";
            
            st.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void main(String[] args) {
        connect();
    }
}
