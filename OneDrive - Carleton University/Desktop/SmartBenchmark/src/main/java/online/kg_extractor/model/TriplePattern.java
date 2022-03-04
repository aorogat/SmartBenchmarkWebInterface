package online.kg_extractor.model;

import java.util.ArrayList;
import offLine.kg_explorer.explorer.SPARQL;
import offLine.kg_explorer.model.PredicateContext;
import offLine.scrapping.model.PredicateNLRepresentation;
import offLine.scrapping.model.PredicatesLexicon;
import settings.Settings;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author aorogat
 */
public class TriplePattern {

    private Variable subject;
    private Variable object;
    private Variable predicate;

    String s_type;
    String o_type;

    String s_type_without_prefix;
    String o_type_without_prefix;

    public TriplePattern(Variable source, Variable destination, Variable label) {
        this.subject = source;
        this.object = destination;
        this.predicate = label;
        setContext();

    }

    private void setContext() {
        ArrayList<PredicateContext> allPossibleContexts = SPARQL.getPredicateContextFromTripleExample(subject.getValueWithPrefix(),
                predicate.getValueWithPrefix(),
                object.getValueWithPrefix());
        //select one exist in Lexicon
        for (PredicateContext possibleContext : allPossibleContexts) {
            ArrayList<PredicateNLRepresentation> predicatesNL = PredicatesLexicon.predicatesNL;
            for (PredicateNLRepresentation predicateNLRepresentation : predicatesNL) {
                if (possibleContext.getSubjectType().equals(predicateNLRepresentation.getSubject_type())
                        && possibleContext.getObjectType().equals(predicateNLRepresentation.getObject_type())) {
                    if (this.s_type == null) {
                        this.s_type = possibleContext.getSubjectType();
                    }
                    if (this.o_type == null) {
                        this.o_type = possibleContext.getObjectType();
                    }
                    break;
                }
            }

        }

        if (s_type == null || o_type == null) {
            return;
        }

        if (o_type.equals(Settings.Number)) {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.Number;
        } else if (o_type.equals(Settings.Date)) {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.Date;
        } else {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.explorer.removePrefix(o_type);
        }
    }

    public TriplePattern(Variable subject, Variable object, Variable predicate, String s_type, String o_type) {
        this.subject = subject;
        this.object = object;
        this.predicate = predicate;
        this.s_type = s_type;
        this.o_type = o_type;
        if (o_type.equals(Settings.Number)) {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.Number;
        } else if (o_type.equals(Settings.Date)) {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.Date;
        } else if (o_type.equals(Settings.Literal)) {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.Literal;
        } else {
            s_type_without_prefix = Settings.explorer.removePrefix(s_type);
            o_type_without_prefix = Settings.explorer.removePrefix(o_type);
        }
    }

    public Variable getSubject() {
        return subject;
    }

    public void setSubject(Variable source) {
        this.subject = source;
    }

    public Variable getObject() {
        return object;
    }

    public void setObject(Variable destination) {
        this.object = destination;
    }

    public Variable getPredicate() {
        return predicate;
    }

    public void setPredicate(Variable label) {
        this.predicate = label;
    }

    public String getS_type() {
        return s_type;
    }

    public void setS_type(String s_type) {
        this.s_type = s_type;
    }

    public String getO_type() {
        return o_type;
    }

    public void setO_type(String o_type) {
        this.o_type = o_type;
    }

    public String toString() {
        String s = "";
        if (s_type == null || o_type == null) {
            setContext();
        }
        if (s_type == null || o_type == null || o_type_without_prefix == null || s_type_without_prefix == null) {
            return null;
        }
        if (o_type_without_prefix.equals(Settings.Number) || o_type_without_prefix.equals(Settings.Date) || o_type_without_prefix.equals(Settings.Literal)) {
            s = subject.getValue() + "[" + s_type_without_prefix + "]" + " ____" + predicate.getValue() + "____ " + object.getValueWithPrefix() + "[" + o_type_without_prefix + "]";
        } else {
            s = subject.getValue() + "[" + s_type_without_prefix + "]" + " ____" + predicate.getValue() + "____ " + object.getValue() + "[" + o_type_without_prefix + "]";
        }
        return s;
    }

    public String toStringNotSubject() {
        if (s_type == null || o_type == null || o_type_without_prefix == null) {
            setContext();
        }
        if (s_type == null || o_type == null || o_type_without_prefix == null) {
            return null;
        }
        String s = " ____" + predicate.getValue() + "____ " + object.getValue() + "[" + o_type_without_prefix + "]";
        return s;
    }

    public String toQueryTriplePattern() {
        if (s_type == null || o_type == null) {
            setContext();
        }
        String o = "";
        if (object.getValueWithPrefix().startsWith("http")) {
            o = "<" + object.getValueWithPrefix() + ">";
        } else {
            o = object + "";
        }
        String s = "<" + subject + ">\t<" + predicate + ">\t" + o + " ";
        if (o_type.equals(Settings.Literal)) {
            s = "<" + subject + ">\t<" + predicate + ">\t\"" + o + "\" ";
        }
        if (o_type.equals(Settings.Date) || isValidDate(o)) {
            s = "<" + subject + ">\t<" + predicate + ">\t\"" + o + "\"^^xsd:dateTime ";
        }
        
        if (o_type.equals(Settings.Number) || isNumeric(o)) {
            s = "<" + subject + ">\t<" + predicate + ">\t" + o;
        }
        return s;
    }

    public static boolean isValidDate(String inDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(inDate.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getS_type_without_prefix() {
        return s_type_without_prefix;
    }

    public void setS_type_without_prefix(String s_type_without_prefix) {
        this.s_type_without_prefix = s_type_without_prefix;
    }

    public String getO_type_without_prefix() {
        return o_type_without_prefix;
    }

    public void setO_type_without_prefix(String o_type_without_prefix) {
        this.o_type_without_prefix = o_type_without_prefix;
    }

}
