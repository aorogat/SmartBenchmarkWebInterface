package online.nl_generation;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.StringTokenizer;
import online.kg_extractor.model.VariableSet;
import settings.KG_Settings;

public class GeneratedQuestion {

    public static final String QT_WHAT = "What";
    public static final String QT_WHO = "Who";
    public static final String QT_WHOSE = "Whose";
    public static final String QT_WHOM = "Whom";
    public static final String QT_WHERE = "Where";
    public static final String QT_WHEN = "When";
    public static final String QT_WHICH = "Which";
    public static final String QT_HOW_MANY = "How-many";
    public static final String QT_HOW_ADJ = "How-adj";
    public static final String QT_YES_NO_IS = "Yes-No-Is";
    public static final String QT_YES_NO_DO = "Yes-No-Do";
    public static final String QT_REQUEST = "Requests";
    public static final String QT_TOPICAL_EMPH = "Topical-Emphasize";
    public static final String QT_TOPICAL_PRUNE = "Topical-Prune";

    public static final String SH_SINGLE_EDGE = "SINGLE_EDGE";
    public static final String SH_CHAIN = "CHAIN";
    public static final String SH_STAR = "STAR";
    public static final String SH_TREE = "TREE";
    public static final String SH_CYCLE = "CYCLE";
    public static final String SH_PETAL = "PETAL";
    public static final String SH_FLOWER = "FLOWER";
    public static final String SH_SET = "SET";
    public static final String SH_STAR_MODIFIED = "STAR_MODIFIED";
    public static final String SH_SET_MODIFIED = "SET_MODIFIED";
    public static final String SH_CYCLE_GENERAL = "CYCLE_GENERAL";

    private String seed_withPrefix;
    private String seedType_withPrefix;

    private String questionString;
    private String query;
    private String graphString;

    private int noOfTriples;
    private String QuestionType;
    private String ShapeType;
    private ArrayList<String> answers;
    private int answerCardinality;
    private int noOfTokens;
    private int keywords;
    private double questionComplexity;

    public GeneratedQuestion() {
    }

//    public GeneratedQuestion(String questionString, String query, String graphString) {
//        this.questionString = questionString;
//        this.query = query;
//        this.graphString = graphString;
//    }
    public GeneratedQuestion(String seed_withPrefix, String seedType_withPrefix, String questionString, String query, String graphString, int noOfTriples, String QuestionType, String ShapeType) {
        this.seed_withPrefix = seed_withPrefix;
        this.seedType_withPrefix = seedType_withPrefix;
        this.questionString = questionString.replace("(", "").replace(")", "").replace("  ", " ").replace(" , ", ", ");
        this.query = query;
        this.graphString = graphString;
        this.noOfTriples = noOfTriples;
        this.QuestionType = QuestionType;
        this.ShapeType = ShapeType;
        
        try {
            ArrayList<VariableSet> answersVar = KG_Settings.knowledgeGraph.runQuery(query);
            if (answersVar.size() > KG_Settings.maxAnswerCardinalityAllowed) {
                System.out.println("Very long cardinality");
                return;
            }
            answers = new ArrayList<>();
            for (VariableSet variableSet : answersVar) {
                String a = variableSet.getVariables().get(0).getValueWithPrefix();
                if (a == null) {
                    continue;
                }
                answers.add(a);
            }
            answerCardinality = answers.size();

            StringTokenizer st = new StringTokenizer(questionString, " ");
            noOfTokens = st.countTokens();

            keywords = get_K_length();
            questionComplexity = round((noOfTokens * noOfTriples * keywords) / (double) (20 * 5 * 3), 3);
            
            
            
            
            
            if (questionComplexity > 1) {
                questionComplexity = 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        print();
    }

    public String getQuestionString() {
        return questionString;
    }

    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void print() {
        
        if(answerCardinality<=0) return;
//        System.out.println("=================================== Question Start ==========================================");
        System.out.println(graphString);
        System.out.println(query);
        System.out.print("\033[1;35m");//MAGENTA Color
        System.out.println(questionString);
        System.out.println("Answer Cardinality: " + answerCardinality);
        System.out.println("Answer: " + answers.toString());
        System.out.println("#Tokens: " + noOfTokens);
        System.out.println("#Triple Patterns: " + noOfTriples);
        System.out.println("#Query Keywords: " + keywords);
        System.out.println("#Question Complexity: " + questionComplexity);
        System.out.println("QT: " + QuestionType);
        System.out.println("Shape: " + ShapeType);
//        System.out.println("=================================== Question End ============================================");
//        System.out.println("");
        System.out.println("");
    }

    public String getGraphString() {
        return graphString;
    }

    public void setGraphString(String graphString) {
        this.graphString = graphString;
    }

    public int getNoOfTriples() {
        return noOfTriples;
    }

    public void setNoOfTriples(int noOfTriples) {
        this.noOfTriples = noOfTriples;
    }

    public String getSeed_withPrefix() {
        return seed_withPrefix;
    }

    public void setSeed_withPrefix(String seed_withPrefix) {
        this.seed_withPrefix = seed_withPrefix;
    }

    public String getSeedType_withPrefix() {
        return seedType_withPrefix;
    }

    public void setSeedType_withPrefix(String seedType_withPrefix) {
        this.seedType_withPrefix = seedType_withPrefix;
    }

    public String getQuestionType() {
        return QuestionType;
    }

    public void setQuestionType(String QuestionType) {
        this.QuestionType = QuestionType;
    }

    public String getShapeType() {
        return ShapeType;
    }

    public void setShapeType(String ShapeType) {
        this.ShapeType = ShapeType;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public int getAnswerCardinality() {
        return answerCardinality;
    }

    public void setAnswerCardinality(int answerCardinality) {
        this.answerCardinality = answerCardinality;
    }

    public int getNoOfTokens() {
        return noOfTokens;
    }

    public void setNoOfTokens(int noOfTokens) {
        this.noOfTokens = noOfTokens;
    }

    public double getQustionComplexity() {
        return questionComplexity;
    }

    public void setQustionComplexity(double qustionComplexity) {
        this.questionComplexity = qustionComplexity;
    }

    private int get_K_length() {
        int k = 0;
        String q = query.toLowerCase().replace("\n", "").replace("\r", "").replaceAll(" ", "");

        if (q.contains("select")) {
            k++;
        }
        if (q.contains("ask")) {
            k++;
        }
        if (q.contains("where")) {
            k++;
        }
        if (q.contains("distinct")) {
            k++;
        }
        if (q.contains("limit")) {
            k++;
        }
        if (q.contains("offset")) {
            k++;
        }
        if (q.contains("orderby")) {
            k++;
        }
        if (q.contains("asc(")) {
            k++;
        }
        if (q.contains("desc(")) {
            k++;
        }
        if (q.contains("filter")) {
            k++;
        }
        if (q.contains("union{")) {
            k++;
        }
        if (q.contains("notexists{")) {
            k++;
        } else if (q.contains("exists{")) {
            k++;
        }
        if (q.contains("minus{")) {
            k++;
        }
        if (q.contains("groupby")) {
            k++;
        }
        if (q.contains("having")) {
            k++;
        }
        if (q.contains("count(")) {
            k++;
        }
        if (q.contains("min(")) {
            k++;
        }
        if (q.contains("max(")) {
            k++;
        }
        if (q.contains("sum(")) {
            k++;
        }
        if (q.contains("avg(")) {
            k++;
        }
        return k;
    }

    
    public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    long factor = (long) Math.pow(10, places);
    value = value * factor;
    long tmp = Math.round(value);
    return (double) tmp / factor;
}
    
    
    
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GeneratedQuestion)
        {
            GeneratedQuestion temp = (GeneratedQuestion) obj;
            if(this.questionString.equals(temp.questionString))
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.questionString.hashCode());        
    }
}
