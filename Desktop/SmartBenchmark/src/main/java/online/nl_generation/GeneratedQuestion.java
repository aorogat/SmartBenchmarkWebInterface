package online.nl_generation;

import java.util.ArrayList;

public class GeneratedQuestion 
{
    private ArrayList<String> questionString;
    private String query;
    
    public GeneratedQuestion() {
    }

    public GeneratedQuestion(ArrayList<String> questionString, String query) {
        this.questionString = questionString;
        this.query = query;
    }

    
    
    
    
    
    public ArrayList<String> getQuestionString() {
        return questionString;
    }

    public void setQuestionString(ArrayList<String> questionString) {
        this.questionString = questionString;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
    
    
    public void print()
    {
        System.out.println(query);
        for (String q : questionString) {
            System.out.print("\033[1;35m");//MAGENTA Color
            System.out.println(q);
        }
        System.out.println("");
    }
    
}
