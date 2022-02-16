package online.kg_extractor.model;

import settings.KG_Settings;

/**
 *
 * @author aorogat
 */
public class Variable {

    private String name;
    private String value;
    private String type;

    public Variable(String name, String value, String type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getValueWithPrefix() {
        return value;
    }

    public String getValue() {
        if(value.equals(KG_Settings.Number)||value.equals(KG_Settings.Date))
            return value;
        return KG_Settings.explorer.removePrefix(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value.replace("\n", " - ");
    }
}
