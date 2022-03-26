/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.carleton.smartbenchweb;

import benchmark.analysis.ShallowAnalysis.Keyword;
import database.Database;
import database.DatabaseIntializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import benchmark.analysis.model.MainBeanCompare;
import offLine.scrapping.model.PredicatesLexicon;
import online.nl_generation.Benchmark;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LegendPlacement;
import org.primefaces.model.chart.LinearAxis;
import system.components.RandomSeedGenerator;
import system.components.ShapesGenerator;
import system.components.SmartBench;

/**
 *
 * @author aorogat
 */
@ManagedBean
@SessionScoped
public class MainBean {

    BarChartModel barChartMode_keywords = new BarChartModel();
    BarChartModel barChartMode_NLQ = new BarChartModel();
    BarChartModel barChartMode_Shape = new BarChartModel();

    MainBeanCompare beanCompare;

    String kgName;
    String kgURL;
    String textCorpus;
    public ArrayList textCorpusList = new ArrayList();

    String maxAnswerCardinalityAllowed;
    String benchmarkLength;
    String step;

    String person;
    String place;
    static int updateCounter = 0;
    public static String output = "";

    String kg;
    PredicatesLexicon lexicon;
    Benchmark benchmark;

    RandomSeedGenerator randomSeedGenerator = new RandomSeedGenerator();

    /**
     * Creates a new instance of MainBean
     */
    public MainBean() throws IOException {
        beanCompare = new MainBeanCompare();

        keywordsBar();
        nlqBar();
        shapesBar();

//        run();
        ShapesGenerator generator = new ShapesGenerator();
        lexicon = ShapesGenerator.lexicon;
        benchmark = ShapesGenerator.benchmark;
        benchmark.generatedBenchmark = new ArrayList<>(ShapesGenerator.generatedQuestions);
    }

    public void update() {
        benchmark.generatedBenchmark = new ArrayList<>(ShapesGenerator.generatedQuestions);
        ShapesGenerator.periodicCall(updateCounter);
        updateCounter++;
    }

    public void run() {
        try {
            DatabaseIntializer.intialize();

            //OFFLINE
            //Step 1
//            Predicate_Extractor extractor = new Predicate_Extractor();
//            extractor.exploreAllPredicates();
            //Step 2
//            NLP_Pattern_Extractor.extractNLPPatterns();
            //Step 3
//            Predicate_Representation_Extractor.fill_from_Labels_VP_and_NP_S_O();
//            Predicate_Representation_Extractor.fill_from_Labels_VP_O_S();
//            Predicate_Representation_Extractor.fill_from_text_corpus_VP();
//            Predicate_Representation_Extractor.fill_from_text_corpus_NP();
            Database.populateLexicon();

            //Online
            ShapesGenerator.generateShapes(); //generate and save them and prune and save again

        } catch (IOException ex) {
            Logger.getLogger(SmartBench.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getKgName() {
        return kgName;
    }

    public void setKgName(String kgName) {
        this.kgName = kgName;
    }

    public String getKgURL() {
        return kgURL;
    }

    public void setKgURL(String kgURL) {
        this.kgURL = kgURL;
    }

    public String getTextCorpus() {
        return textCorpus;
    }

    public void setTextCorpus(String textCorpus) {
        this.textCorpus = textCorpus;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public ArrayList getTextCorpusList() {
        textCorpusList.clear();
        textCorpusList.add("Wikipedia");
        textCorpusList.add("Google");
        return textCorpusList;
    }

    public void setTextCorpusList(ArrayList textCorpusList) {
        this.textCorpusList = textCorpusList;
    }

    public String getMaxAnswerCardinalityAllowed() {
        return maxAnswerCardinalityAllowed;
    }

    public void setMaxAnswerCardinalityAllowed(String maxAnswerCardinalityAllowed) {
        this.maxAnswerCardinalityAllowed = maxAnswerCardinalityAllowed;
    }

    public String getBenchmarkLength() {
        return benchmarkLength;
    }

    public void setBenchmarkLength(String benchmarkLength) {
        this.benchmarkLength = benchmarkLength;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getOutput() {
        if (output.length() > 1000) {
            output = output.substring(999);
        }
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getKg() {
        return kg;
    }

    public void setKg(String kg) {
        this.kg = kg;
    }

    public PredicatesLexicon getLexicon() {
        return lexicon;
    }

    public void setLexicon(PredicatesLexicon lexicon) {
        this.lexicon = lexicon;
    }

    public Benchmark getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(Benchmark benchmark) {
        this.benchmark = benchmark;
    }

    public RandomSeedGenerator getRandomSeedGenerator() {
        return randomSeedGenerator;
    }

    public void setRandomSeedGenerator(RandomSeedGenerator randomSeedGenerator) {
        this.randomSeedGenerator = randomSeedGenerator;
    }

    public MainBeanCompare getBeanCompare() {
        return beanCompare;
    }

    public void setBeanCompare(MainBeanCompare beanCompare) {
        this.beanCompare = beanCompare;
    }

    public BarChartModel getBarChartMode_keywords() {
        return barChartMode_keywords;
    }

    public void setBarChartMode_keywords(BarChartModel barChartMode_keywords) {
        this.barChartMode_keywords = barChartMode_keywords;
    }

    public BarChartModel getBarChartMode_NLQ() {
        return barChartMode_NLQ;
    }

    public void setBarChartMode_NLQ(BarChartModel barChartMode_NLQ) {
        this.barChartMode_NLQ = barChartMode_NLQ;
    }

    public BarChartModel getBarChartMode_Shape() {
        return barChartMode_Shape;
    }

    public void setBarChartMode_Shape(BarChartModel barChartMode_Shape) {
        this.barChartMode_Shape = barChartMode_Shape;
    }
    
    
    
    
    private void keywordsBar(){
        ChartSeries patternKys = new ChartSeries();
        
        
        patternKys = new ChartSeries();
        patternKys.setLabel("SMART-BENCH");
        patternKys.set("SELECT", 90);
        patternKys.set("ASK", 10);
        patternKys.set("DISTINCT", 70);
        patternKys.set("LIMIT", 3);
        patternKys.set("OFFSET", 3);
        patternKys.set("ORDER_BY", 3);
        patternKys.set("FILTER", 8);
        patternKys.set("AND", 60);
        patternKys.set("UNION", 5);
        patternKys.set("NOT_EXISTS", 2);
        patternKys.set("MINUS", 2);
        patternKys.set("AGGREGATORS", 20);
        patternKys.set("GROUP_BY", 5);
        patternKys.set("HAVING", 5);
        barChartMode_keywords.addSeries(patternKys);
        
        patternKys = new ChartSeries();
        patternKys.setLabel("QALD-9");
        patternKys.set("SELECT", 91);
        patternKys.set("ASK", 9);
        patternKys.set("DISTINCT", 76);
        patternKys.set("LIMIT", 10);
        patternKys.set("OFFSET", 6);
        patternKys.set("ORDER_BY", 9);
        patternKys.set("FILTER", 5);
        patternKys.set("AND", 43);
        patternKys.set("UNION", 7);
        patternKys.set("NOT_EXISTS", 0);
        patternKys.set("MINUS", 0);
        patternKys.set("AGGREGATORS", 5);
        patternKys.set("GROUP_BY", 5);
        patternKys.set("HAVING", 1);
        barChartMode_keywords.addSeries(patternKys);

        patternKys = new ChartSeries();
        patternKys.setLabel("LC-QuAD-1");
        patternKys.set("SELECT", 91);
        patternKys.set("ASK", 9);
        patternKys.set("DISTINCT", 91);
        patternKys.set("LIMIT", 0);
        patternKys.set("OFFSET", 0);
        patternKys.set("ORDER_BY", 0);
        patternKys.set("FILTER", 0);
        patternKys.set("AND", 71);
        patternKys.set("UNION", 7);
        patternKys.set("NOT_EXISTS", 0);
        patternKys.set("MINUS", 0);
        patternKys.set("AGGREGATORS", 0);
        patternKys.set("GROUP_BY", 0);
        patternKys.set("HAVING", 0);
        barChartMode_keywords.addSeries(patternKys);
        
        
        
        
        //barChartMode_keywords.setTitle("Query Keywords");
        barChartMode_keywords.setLegendPosition("e");
        barChartMode_keywords.setLegendPlacement(LegendPlacement.INSIDE);
        barChartMode_keywords.getAxes().put(AxisType.X, new CategoryAxis("Keyword"));
        Axis xAxis3 = barChartMode_keywords.getAxis(AxisType.X);
        xAxis3.setTickAngle(-30);

        Axis yAxis3 = barChartMode_keywords.getAxis(AxisType.Y);
        yAxis3.setMin(0);
        yAxis3.setMax(200);
//        yAxis3.setLabel("Percentage");
        barChartMode_keywords.getAxes().put(AxisType.Y, new LinearAxis("Percentage"));
        yAxis3.setTickAngle(90);
    }
    
    
    
    
    private void nlqBar(){
        ChartSeries patternKys = new ChartSeries();
        
        patternKys = new ChartSeries();
        patternKys.setLabel("SMART-BENCH");
        patternKys.set("What", 12);
        patternKys.set("When", 5);
        patternKys.set("Where", 6);
        patternKys.set("Which", 12);
        patternKys.set("Who", 4);
        patternKys.set("Whom", 3);
        patternKys.set("Whose", 3);
        patternKys.set("How many", 10);
        patternKys.set("Yes-No", 10);
        patternKys.set("Request", 12);
        patternKys.set("Topicall", 10);
        barChartMode_NLQ.addSeries(patternKys);
        
        patternKys = new ChartSeries();
        patternKys.setLabel("QALD-9");
        patternKys.set("What", 13.5);
        patternKys.set("When", 3.4);
        patternKys.set("Where", 3);
        patternKys.set("Which", 26);
        patternKys.set("Who", 17);
        patternKys.set("Whom", 0);
        patternKys.set("Whose", 0);
        patternKys.set("How many", 11);
        patternKys.set("Yes-No", 9);
        patternKys.set("Request", 18);
        patternKys.set("Topicall", 0);
        barChartMode_NLQ.addSeries(patternKys);

        patternKys = new ChartSeries();
        patternKys.setLabel("LC-QuAD-1");
        patternKys.set("What", 53);
        patternKys.set("When", 0);
        patternKys.set("Where", 10);
        patternKys.set("Which", 13);
        patternKys.set("Who", 12);
        patternKys.set("Whom", 0.1);
        patternKys.set("Whose", 0.2);
        patternKys.set("How many", 1.3);
        patternKys.set("Yes-No", 2.1);
        patternKys.set("Request", 5.6);
        patternKys.set("Topicall", 2);
        barChartMode_NLQ.addSeries(patternKys);
        
        
        
        
//        barChartMode_NLQ.setTitle("Question Type");
        barChartMode_NLQ.setLegendPosition("e");
        barChartMode_NLQ.setLegendPlacement(LegendPlacement.INSIDE);
        barChartMode_NLQ.getAxes().put(AxisType.X, new CategoryAxis("Question Type"));
        Axis xAxis3 = barChartMode_NLQ.getAxis(AxisType.X);
        xAxis3.setTickAngle(-30);

        Axis yAxis3 = barChartMode_NLQ.getAxis(AxisType.Y);
        yAxis3.setMin(0);
        yAxis3.setMax(60);
//        yAxis3.setLabel("Percentage");
        barChartMode_NLQ.getAxes().put(AxisType.Y, new LinearAxis("Percentage"));
        yAxis3.setTickAngle(90);
    }
    
    
    
    private void shapesBar(){
        ChartSeries patternKys = new ChartSeries();
        
         patternKys = new ChartSeries();
        patternKys.setLabel("SMART-BENCH");
        patternKys.set("Single-Edge", 25);
        patternKys.set("Chain", 65);
        patternKys.set("Star", 20);
        patternKys.set("Tree", 90);
        patternKys.set("Cycle", 3);
        patternKys.set("Flower", 95);
        patternKys.set("Set", 100);
        barChartMode_Shape.addSeries(patternKys);
        
        patternKys = new ChartSeries();
        patternKys.setLabel("QALD-9");
        patternKys.set("Single-Edge", 57);
        patternKys.set("Chain", 90);
        patternKys.set("Star", 9);
        patternKys.set("Tree", 98);
        patternKys.set("Cycle", 0.3);
        patternKys.set("Flower", 99);
        patternKys.set("Set", 100);
        barChartMode_Shape.addSeries(patternKys);

        patternKys = new ChartSeries();
        patternKys.setLabel("LC-QuAD-1");
        patternKys.set("Single-Edge", 29);
        patternKys.set("Chain", 73);
        patternKys.set("Star", 27);
        patternKys.set("Tree", 100);
        patternKys.set("Cycle", 0);
        patternKys.set("Flower", 100);
        patternKys.set("Set", 100);
        barChartMode_Shape.addSeries(patternKys);
        
        
       
        
//        barChartMode_Shape.setTitle("Query Shapes");
        barChartMode_Shape.setLegendPosition("e");
        barChartMode_Shape.setLegendPlacement(LegendPlacement.INSIDE);
        barChartMode_Shape.getAxes().put(AxisType.X, new CategoryAxis("Shape"));
        Axis xAxis3 = barChartMode_Shape.getAxis(AxisType.X);
        xAxis3.setTickAngle(-30);

        Axis yAxis3 = barChartMode_Shape.getAxis(AxisType.Y);
        yAxis3.setMin(0);
        yAxis3.setMax(100);
//        yAxis3.setLabel("Percentage");
        barChartMode_Shape.getAxes().put(AxisType.Y, new LinearAxis("Percentage"));
        yAxis3.setTickAngle(90);
    }
    

}
