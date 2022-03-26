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
public class NobelPrizeExplorer extends Explorer {

    public NobelPrizeExplorer(String url) {
        super();
        kg = NobelPrizeKG.getInstance(url);
        endpoint = kg.getEndpoint();
        Database.connect();
    }

    public static NobelPrizeExplorer getInstance(String url) {
        if (instance == null) {
            instance = new NobelPrizeExplorer(url);
            return (NobelPrizeExplorer) instance;
        } else {
            return (NobelPrizeExplorer) instance;
        }
    }

    public static String getPredicateLabel(String predicate) {
        return SPARQL.getNodeLabel(NobelPrizeExplorer.getInstance(Settings.url), predicate);
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
        if(node.startsWith("http") || node.startsWith("<http"))
            s = SPARQL.getNodeLabel(this, node);

        if (s == null || s.equals("")) {
            if (node.startsWith("http")) {
                String last = node.substring(node.lastIndexOf("/") + 1);
                last = last.replace("#", " ").replace("_", " ");
                //for predicates with the form XXX%3YYYY get only XXX
                int start;
                int end;
                if(last.contains("%")){
                    end = last.lastIndexOf("%");
                    last = last.substring(0, end);
                }
                String[] r = last.split("(?=\\p{Lu})");
                s = "";
                for (String string : r) {
                    s += string.trim() + " ";
                }
                s = s.trim().toLowerCase();
                return s;
            }
            else{
                String last = node.replace("#", " ").replace("_", " ");
                //for predicates with the form XXX%3YYYY get only XXX
                int start;
                int end;
                if(last.contains("%")){
                    end = last.lastIndexOf("%");
                    last = last.substring(0, end);
                }
                String[] r = last.split("(?=\\p{Lu})");
                s = "";
                for (String string : r) {
                    s += string.trim() + " ";
                }
                s = s.trim().toLowerCase();
                return s;
            }
        }
        return s;
    }
}
