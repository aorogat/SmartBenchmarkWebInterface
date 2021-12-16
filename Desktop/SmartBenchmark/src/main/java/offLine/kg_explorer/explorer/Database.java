package offLine.kg_explorer.explorer;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
import online.nl_generation.chunking.Phrase;

/**
 *
 * @author aorogat
 */
public class Database {

    static Statement st = null;
    static Connection con;
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
            con = DriverManager.getConnection(url, user, password);

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
                    + "ORDER BY \"URI\", \"Context_Subject\", \"Context_Object\";";

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
        System.out.println("Predicate List Size = " + predicates.size());
        System.out.println("==============================================");
        return predicates;
    }

    public static boolean storePredicates_NLP_Representation(Predicate predicate, ArrayList<PredicateTripleExample> examples) throws IOException {
        connect();
        String sql = "";

        System.out.println(predicate.toString());
        for (PredicateTripleExample example : examples) {

            for (Predicate_NLP_Representation nlp : example.getNlsSuggestionsObjects()) {
                try {
                    System.out.println("'" + predicate.getPredicateURI() + "',"
                            + "'" + predicate.getPredicateContext().getSubjectType() + "',"
                            + "'" + predicate.getPredicateContext().getObjectType() + "',"
                            + "'" + example.getSubjectURI() + "',"
                            + "'" + example.getObjectURI() + "',"
                            + "'" + nlp.getSentence() + "',"
                            + "'" + nlp.getPattern() + "',"
                            + "'" + nlp.getReducedPattern() + "'");

                    sql = "INSERT INTO \"NLP_Representation\" (\"URI\", \"ContextSubject\", \"ContextObject\", "
                            + "\"TripleExampleSubject\", \"TripleExampleObject\", \"Sentence\", "
                            + "\"Pattern\", \"ReducedPattern\")\n"
                            + "VALUES(?,?,?,?,?,?,?,?);";
                    PreparedStatement preparedStatement = con.prepareStatement(sql);

                    preparedStatement.setString(1, predicate.getPredicateURI());
                    preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
                    preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
                    preparedStatement.setString(4, example.getSubjectURI());
                    preparedStatement.setString(5, example.getObjectURI());
                    preparedStatement.setString(6, nlp.getSentence().replace("'", "''"));
                    preparedStatement.setString(7, nlp.getPattern().replace("'", "''"));
                    preparedStatement.setString(8, nlp.getReducedPattern().replace("'", "''"));

                    preparedStatement.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println(sql);
                    Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        sql = "UPDATE \"Predicates\" SET "
                + "\"processed\" = 'YES'\n "
                + "WHERE "
                + "\"URI\" = ? AND "
                + "\"Context_Subject\" = ? AND "
                + "\"Context_Object\" = ?;";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public static ArrayList<Predicate> getVerbPrepositionLabels() {
        connect();
        ArrayList<Predicate> predicates = new ArrayList<>();
        try {
            String sql = "SELECT *\n"
                    + "FROM public.\"Predicates\" \n"
                    + "WHERE (\"Label\" ~*  '.*\\s(above|across|against|along|among|around|at\n"
                    + "	   |before|behind|below|beneath|beside|between|by|from|in\n"
                    + "	   |into|near|on|to|toward|under|upon|with|within|of)$')\n"
                    + "ORDER BY \"URI\", \"Context_Subject\", \"Context_Object\";";

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
        System.out.println("Predicate List Size = " + predicates.size());
        System.out.println("==============================================");
        return predicates;
    }

    public static ArrayList<Predicate> getNLPatterns() {
        connect();
        ArrayList<Predicate> predicates = new ArrayList<>();
        try {
            String sql = "SELECT \"NLP_Representation\".\"URI\", \"NLP_Representation\".\"ContextSubject\", \"NLP_Representation\".\"ContextObject\", \"NLP_Representation\".\"Pattern\", \"Predicates\".\"Label\"\n"
                    + "FROM public.\"NLP_Representation\" INNER JOIN \"Predicates\" ON(\"NLP_Representation\".\"URI\"=\"Predicates\".\"URI\" AND \n"
                    + "														   \"NLP_Representation\".\"ContextSubject\"=\"Predicates\".\"Context_Subject\" AND\n"
                    + "														   \"NLP_Representation\".\"ContextObject\"=\"Predicates\".\"Context_Object\")\n"
                    + "ORDER BY \"URI\", \"ContextSubject\", \"ContextObject\";";

            ResultSet result = st.executeQuery(sql);
            Predicate p;
            while (result.next()) {
                p = new Predicate(Explorer.instance);
                p.setPredicateURI(result.getString("URI"));
                p.setLabel(result.getString("Label"));
                p.setPredicateContext(new PredicateContext(
                        result.getString("ContextSubject"),
                        result.getString("ContextObject"), 0));
                p.setNLPattern(result.getString("Pattern"));
                predicates.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Predicate List Size = " + predicates.size());
        System.out.println("==============================================");
        return predicates;
    }

    public static boolean storePredicates_VP(String table, Predicate predicate, String vp, int confidence, double labelSimilarity) throws IOException {
        System.out.println("storePredicates_VP: " + predicate.getLabel());
        connect();
        String sql = "";

        System.out.println(predicate.toString());
        try {
            sql = "INSERT INTO \"" + table + "\" (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", "
                    + "\"VP\", \"labelSimilarity\", \"confidence\")\n"
                    + "VALUES(?,?,?,?,?,?)"
                    + " ON CONFLICT (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", \"VP\") DO NOTHING;";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, vp);
            preparedStatement.setDouble(5, labelSimilarity);
            preparedStatement.setInt(6, confidence);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(sql);
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public static boolean storePredicates_NP(String table, Predicate predicate, String np, int confidence, double labelSimilarity) throws IOException {
        System.out.println("storePredicates_NP: " + predicate.getLabel());
        connect();
        String sql = "";

        System.out.println(predicate.toString());
        try {
            sql = "INSERT INTO \"" + table + "\" (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", "
                    + "\"NP\", \"labelSimilarity\", \"confidence\")\n"
                    + "VALUES(?,?,?,?,?,?)"
                    + " ON CONFLICT (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", \"NP\") DO NOTHING;";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, np);
            preparedStatement.setDouble(5, labelSimilarity);
            preparedStatement.setInt(6, confidence);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(sql);
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }
    
    
    public static boolean storeNL_VP(Phrase phrase, Predicate predicate) throws IOException {
        System.out.println("storeNL_VP: " + phrase.getVerbPhrase());
        connect();
        String sql = "";
        String table = ""; 
        
        if(phrase.getDirection()==Phrase.S_O)
            table = "VP_S_O";
        else
            table = "VP_O_S";

        try {
            sql = "INSERT INTO \"" + table + "\" (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", "
                    + "\"VP\", \"labelSimilarity\", \"baseVerb\", \"sentence\")\n"
                    + "VALUES(?,?,?,?,?,?,?)"
                    + " ON CONFLICT (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", \"VP\") DO NOTHING;";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, phrase.getVerbPhrase());
            preparedStatement.setDouble(5, phrase.getLabelSimilarity());
            preparedStatement.setString(6, phrase.getBaseVerbForm());
            preparedStatement.setString(7, phrase.getSentence());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(sql);
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public static void main(String[] args) {
        connect();
    }
}
