/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package offLine.kg_explorer.explorer;

import database.Database;
import settings.Settings;

/**
 *
 * @author aorogat
 */
public class MAKGExplorer extends Explorer {
    public MAKGExplorer(String url) {
        super();
        kg = MAKG.getInstance(url);
        endpoint = kg.getEndpoint();
        Database.connect();
    }

    public static MAKGExplorer getInstance(String url) {
        if (instance == null) {
            instance = new DBpediaExplorer(url);
            return (MAKGExplorer) instance;
        } else {
            return (MAKGExplorer) instance;
        }
    }
    
   public static String getPredicateLabel(String predicate) {
        return SPARQL.getNodeFOAFLabel(MAKGExplorer.getInstance(Settings.url), predicate);
    }
    
    @Override
    public String removePrefix(String node) {
        if (node == null) {
            return node;
        }

        if (node.equals("true") || node.equals("false") || node.equals(Settings.Number) || node.equals(Settings.Date) || node.equals(Settings.Literal)) {
            return node;
        }

        String s = "";
        s = SPARQL.getNodeFOAFLabel(this, node);

        if (s == null || s.equals("")) {
            if (node.startsWith("http")) {
                String last = node.substring(node.lastIndexOf("/") + 1);
                if (last.contains("#")) {
                    last = node.substring(node.lastIndexOf("#") + 1);
                }
                String[] r = last.split("(?=\\p{Lu})");
                s = "";
                for (String string : r) {
                    s += string + " ";
                }
                s = s.trim().toLowerCase();
                return s;
            }
        }
        return s;
    }
}
