package database;

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
import offLine.scrapping.model.PredicateNLRepresentation;
import online.nl_generation.chunking.Phrase;
import settings.Settings;

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
        String url = Settings.databaseURL;
        String dbName = Settings.databaseName;
        String user = Settings.databaseUser;
        String password = Settings.databasePassword;

        try {
            con = DriverManager.getConnection(url + dbName, user, password);

            st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT VERSION()");
            if (rs.next()) {
                System.out.println(rs.getString(1));
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

    public static ArrayList<PredicateNLRepresentation> getPredicatesNLRepresentationLexicon() {
        connect();
        ArrayList<PredicateNLRepresentation> predicatesNLRepresentations = new ArrayList<>();
        try {
            String sql = "SELECT *, (\n"
                    + "        select count(*)\n"
                    + "        from (values (\"VP_S_O\"), (\"NP_S_O\"), (\"VP_O_S\"), (\"NP_O_S\")) as v(col)\n"
                    + "        where v.col is not null\n"
                    + "    ) as counter\n"
                    + "FROM \"Lexicon\"\n"
                    + "ORDER BY \"counter\" DESC;";

            ResultSet result = st.executeQuery(sql);
            PredicateNLRepresentation p;
            while (result.next()) {
                p = new PredicateNLRepresentation(
                        result.getString("PredicateURI"),
                        result.getString("Context_Subject"),
                        result.getString("Context_Object"),
                        result.getString("NP_S_O"),
                        result.getString("VP_O_S"),
                        result.getString("NP_O_S"),
                        result.getString("VP_S_O")
                );
                predicatesNLRepresentations.add(p);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return predicatesNLRepresentations;
    }

    public static ArrayList<Predicate> getAvailablePredicates() {
        connect();
        ArrayList<Predicate> predicates = new ArrayList<>();
        try {
            String sql
                    = "SELECT DISTINCT l.\"PredicateURI\",l.\"Context_Subject\",l.\"Context_Object\", p.\"ContextWeight\", (\n"
                    + "	select count(*)\n"
                    + "    from (values (\"VP_S_O\"), (\"NP_S_O\"), (\"VP_O_S\"), (\"NP_O_S\")) as v(col)\n"
                    + "    where v.col is not null ) as counter\n"
                    + "    FROM \"Lexicon\" l, \"Predicates\" p\n"
                    + "	where l.\"PredicateURI\" = p.\"URI\" and l.\"Context_Subject\"=p.\"Context_Subject\" and l.\"Context_Object\"=p.\"Context_Object\"\n"
                    + "    ORDER BY p.\"ContextWeight\" DESC;";

            ResultSet result = st.executeQuery(sql);
            while (result.next()) {
                Predicate predicate = new Predicate(Settings.explorer);
                predicate.setPredicateURI(result.getString("PredicateURI"));
                predicate.setPredicateContext(new PredicateContext(result.getString("Context_Subject"), result.getString("Context_Object"), 0));
                predicate.setWeight(result.getLong("ContextWeight"));
                if(!predicate.getPredicateURI().contains("#"))
                    predicates.add(predicate);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return predicates;
    }

    public static ArrayList<Predicate> getPredicates() {
        connect();
        ArrayList<Predicate> predicates = new ArrayList<>();
        try {
            String sql = "SELECT *\n"
                    + "FROM \"Predicates\"\n"
                    //                    + "WHERE \"processed\" is NULL "
                    + "ORDER BY \"URI\", \"Context_Subject\", \"Context_Object\";";

            ResultSet result = st.executeQuery(sql);
            Predicate p;
            while (result.next()) {
                p = new Predicate(Settings.explorer);
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
                p = new Predicate(Settings.explorer);
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
            String sql = "SELECT \"NLP_Representation\".\"URI\", "
                    + "\"NLP_Representation\".\"ContextSubject\", "
                    + "\"NLP_Representation\".\"ContextObject\", "
                    + "\"NLP_Representation\".\"Pattern\", "
                    + "\"Predicates\".\"Label\"\n"
                    + "FROM public.\"NLP_Representation\" INNER JOIN \"Predicates\" ON(\"NLP_Representation\".\"URI\"=\"Predicates\".\"URI\" AND \n"
                    + "														   \"NLP_Representation\".\"ContextSubject\"=\"Predicates\".\"Context_Subject\" AND\n"
                    + "														   \"NLP_Representation\".\"ContextObject\"=\"Predicates\".\"Context_Object\")\n"
                    //                    + " WHERE \"NLP_Representation\".\"processed\" is null "
                    + "ORDER BY \"URI\", \"ContextSubject\", \"ContextObject\";";

            ResultSet result = st.executeQuery(sql);
            Predicate p;
            while (result.next()) {
                p = new Predicate(Settings.explorer);
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

    public static boolean populateLexicon() throws IOException {
        try {
            String sql = "REFRESH MATERIALIZED VIEW \"Lexicon\";";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public static boolean storePredicates_VP(String table, Predicate predicate, String vp, int confidence, double labelSimilarity, double subjectSimilarity, double objectSimilarity) throws IOException {
        System.out.println("storePredicates_VP: " + predicate.getLabel());
        connect();
        String sql = "";

        System.out.println(predicate.toString());
        try {
            sql = "INSERT INTO \"" + table + "\" (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", "
                    + "\"VP\", \"labelSimilarity\", \"confidence\", \"sentence\", \"subjectSimilarity\", \"objectSimilarity\")\n"
                    + "VALUES(?,?,?,?,?,?,?,?,?)"
                    + " ON CONFLICT (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", \"VP\", \"sentence\")  DO NOTHING;";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, vp);
            preparedStatement.setDouble(5, labelSimilarity);
            preparedStatement.setInt(6, confidence);
            preparedStatement.setString(7, predicate.getLabel());
            preparedStatement.setDouble(8, subjectSimilarity);
            preparedStatement.setDouble(9, objectSimilarity);

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(sql);
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public static boolean storePredicates_NP(String table, Predicate predicate, String np, int confidence, double subjectSimilarity, double objectSimilarity, double labelSimilarity) throws IOException {
        System.out.println("storePredicates_NP: " + predicate.getLabel());
        connect();
        String sql = "";

        System.out.println(predicate.toString());
        try {
            sql = "INSERT INTO \"" + table + "\" (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", "
                    + "\"NP\", \"labelSimilarity\", \"confidence\", \"subjectSimilarity\", \"objectSimilarity\", \"sentence\")\n"
                    + "VALUES(?,?,?,?,?,?,?,?,?)"
                    + " ON CONFLICT (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", \"NP\", \"sentence\")  DO NOTHING;";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, np);
            preparedStatement.setDouble(5, labelSimilarity);
            preparedStatement.setInt(6, confidence);
            preparedStatement.setDouble(7, subjectSimilarity);
            preparedStatement.setDouble(8, objectSimilarity);
            preparedStatement.setString(9, predicate.getLabel());

            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(sql);
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public static boolean storeNL_VP(Phrase phrase, Predicate predicate) throws IOException {
        System.out.println("storeNL_VP: " + phrase.getPhrase());
        connect();
        String sql = "";
        String table = "";

        if (phrase.getDirection() == Phrase.S_O) {
            table = "VP_S_O";
        } else {
            table = "VP_O_S";
        }

        try {
            sql = "INSERT INTO \"" + table + "\" (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", "
                    + "\"VP\", \"labelSimilarity\", \"baseVerb\", \"sentence\", \"subjectSimilarity\", \"objectSimilarity\")\n"
                    + "VALUES(?,?,?,?,?,?,?,?,?)"
                    + " ON CONFLICT (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", \"VP\", \"sentence\") DO UPDATE\n"
                    + "SET \"labelSimilarity\" = excluded.\"labelSimilarity\", "
                    + "\"subjectSimilarity\" = excluded.\"subjectSimilarity\","
                    + "\"objectSimilarity\" = excluded.\"objectSimilarity\";";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, phrase.getPhrase());
            preparedStatement.setDouble(5, phrase.getLabelSimilarity());
            preparedStatement.setString(6, phrase.getBaseVerbForm());
            preparedStatement.setString(7, phrase.getSentence());
            preparedStatement.setDouble(8, phrase.getSubjectSimilarity());
            preparedStatement.setDouble(9, phrase.getObjectSimilarity());

            preparedStatement.executeUpdate();

            sql = "UPDATE \"NLP_Representation\" SET \"processed\" = 'YES'\n"
                    + "WHERE "
                    + "\"URI\" = ? AND "
                    + "\"ContextSubject\" = ? AND "
                    + "\"ContextObject\" = ? AND "
                    + "\"Pattern\" = ?;";
            preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, phrase.getSentence());
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            System.out.println(sql);
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    public static boolean storeNL_NP(Phrase phrase, Predicate predicate) throws IOException {
        System.out.println("storeNL_NP: " + phrase.getPhrase());
        connect();
        String sql = "";
        String table = "";

        if (phrase.getDirection() == Phrase.S_O) {
            table = "NP_S_O";
        } else {
            table = "NP_O_S";
        }

        try {
            sql = "INSERT INTO \"" + table + "\" (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", "
                    + "\"NP\", \"labelSimilarity\", \"sentence\", \"subjectSimilarity\", \"objectSimilarity\")\n"
                    + "VALUES(?,?,?,?,?,?,?,?)"
                    + " ON CONFLICT (\"PredicateURI\", \"Context_Subject\", \"Context_Object\", \"NP\", \"sentence\") DO NOTHING;";
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, phrase.getPhrase());
            preparedStatement.setDouble(5, phrase.getLabelSimilarity());
            preparedStatement.setString(6, phrase.getSentence());
            preparedStatement.setDouble(7, phrase.getSubjectSimilarity());
            preparedStatement.setDouble(8, phrase.getObjectSimilarity());

            preparedStatement.executeUpdate();

            sql = "UPDATE \"NLP_Representation\" SET \"processed\" = 'YES_ALL'\n"
                    + "WHERE "
                    + "\"URI\" = ? AND "
                    + "\"ContextSubject\" = ? AND "
                    + "\"ContextObject\" = ? AND "
                    + "\"Pattern\" = ?;";
            preparedStatement = con.prepareStatement(sql);

            preparedStatement.setString(1, predicate.getPredicateURI());
            preparedStatement.setString(2, predicate.getPredicateContext().getSubjectType());
            preparedStatement.setString(3, predicate.getPredicateContext().getObjectType());
            preparedStatement.setString(4, phrase.getSentence());
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
