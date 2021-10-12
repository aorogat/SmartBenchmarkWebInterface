package online.kg_extractor.model;

import java.util.ArrayList;

/**
 *
 * @author aorogat
 */
public class VariableSet {
    ArrayList<Variable> variables = new ArrayList<>();

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }
    
    public String toString()
    {
        String all = "";
        for (Variable variable : variables) {
            all += variable.toString() + "\t";
        }
        return all;
    }
}
