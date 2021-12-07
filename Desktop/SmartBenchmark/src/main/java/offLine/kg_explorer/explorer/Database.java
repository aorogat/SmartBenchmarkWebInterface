package offLine.kg_explorer.explorer;

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

/**
 *
 * @author aorogat
 */
public class Database {

    private static Statement connect() {
        String url = "jdbc:postgresql://localhost:5432/dbpedia";
        String user = "postgres";
        String password = "admin";
        Statement st = null;

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

    public static boolean storePredicates(ArrayList<Predicate> predicates) {
        try {
            String sql = "";
            Statement st = connect();
            for (Predicate predicate : predicates) {
                sql += "INSERT INTO predicates (URI, Label, Weight, Context_Subject, Context_Object, ContextWeight)\n"
                        + "VALUES( '"+predicate.getPredicateURI()+"',"
                        + "'"+predicate.getLabel()+"',"
                        + "'"+predicate.getPredicateContext().getSubjectType()+"',"
                        + "'"+predicate.getPredicateContext().getObjectType()+"',"
                        + ""+predicate.getPredicateContext().getWeight()+""
                        + " ); \n";
            }
            
            ResultSet rs = st.executeQuery(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void main(String[] args) {
        connect();
    }
}
