package online.nl_generation;

import java.util.ArrayList;
import online.kg_extractor.model.subgraph.FlowerGraph;
import online.kg_extractor.model.subgraph.TreeGraph;

/**
 *
 * @author aorogat
 */
public class FlowerQuestion {
    FlowerGraph flowerGraph;
    StarQuestion starQuestion;
    CycleQuestion cycleQuestion;
    
    ArrayList<GeneratedQuestion> allPossibleQuestions = new ArrayList<>();

    public FlowerQuestion(FlowerGraph flowerGraph) {
        this.flowerGraph = flowerGraph;
        starQuestion = new StarQuestion(flowerGraph.getStar());
        cycleQuestion = new CycleQuestion(flowerGraph.getCycle());
        
        String question = "";
        String starQuestionString = starQuestion.selectWhichQuestions(CoordinatingConjunction.AND);
        starQuestionString = starQuestionString.substring(0, starQuestionString.length()-1) + " ";
        
        if(starQuestionString==null | starQuestionString.contains("null"))
            return;
        
        question = starQuestionString;
        cycleQuestion.direction = CycleQuestion.FORWARD;
        String cycleQuestionString = cycleQuestion.selectWh_Questions(CoordinatingConjunction.AND,  "NP");
        if(cycleQuestionString==null | cycleQuestionString.contains("null"))
            return;
        question += cycleQuestionString.replaceAll("\\b(What|Where)\\b", ", as well it").replaceAll("\\b(Who|Whose|Whom)\\b", ", as well he/she");
        ArrayList<String> questions = new ArrayList<>();
        questions.add(question);
        
        String queryString = starQuestion.selectQuery(flowerGraph.getStar(), CoordinatingConjunction.AND);
        queryString = queryString.substring(0,queryString.length()-1);
        
        queryString += cycleQuestion.selectQuery(flowerGraph.getCycle(), CoordinatingConjunction.AND).replace("SELECT DISTINCT ?Seed WHERE{", "");
        
        String graphString = flowerGraph.toString();
        
        
        GeneratedQuestion generatedQuestion = new GeneratedQuestion(questions, queryString, graphString);
        allPossibleQuestions.add(generatedQuestion);
    }

    public FlowerGraph getFlowerGraph() {
        return flowerGraph;
    }

    public void setFlowerGraph(FlowerGraph flowerGraph) {
        this.flowerGraph = flowerGraph;
    }

    public StarQuestion getStarQuestion() {
        return starQuestion;
    }

    public void setStarQuestion(StarQuestion starQuestion) {
        this.starQuestion = starQuestion;
    }

    public CycleQuestion getCycleQuestion() {
        return cycleQuestion;
    }

    public void setCycleQuestion(CycleQuestion cycleQuestion) {
        this.cycleQuestion = cycleQuestion;
    }

    public ArrayList<GeneratedQuestion> getAllPossibleQuestions() {
        return allPossibleQuestions;
    }

    public void setAllPossibleQuestions(ArrayList<GeneratedQuestion> allPossibleQuestions) {
        this.allPossibleQuestions = allPossibleQuestions;
    }
    
    
    
    
}
