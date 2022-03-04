package online.kg_extractor.model;

import settings.Settings;

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
        if(value.equals(Settings.Number)||value.equals(Settings.Date)||value.equals(Settings.Literal))
            return value;
        return Settings.explorer.removePrefix(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return value.replace("\n", " - ");
    }
}
