package benchmark.analysis.model;

import benchmark.analysis.DataSet.Benchmark;
import benchmark.analysis.NLQAnalysis.NLQCategoraizer;
import benchmark.analysis.NLQAnalysis.NlqComparison;
import benchmark.analysis.ShallowAnalysis.Keywords;
import benchmark.analysis.ShallowAnalysis.KeywordsComparison;
import benchmark.analysis.ShallowAnalysis.NoOfTriples;
import benchmark.analysis.ShallowAnalysis.NoOfTriplesComparison;
import benchmark.analysis.ShallowAnalysis.OperatorDistribution;
import benchmark.analysis.ShallowAnalysis.OperatorDistributionComparison;
import benchmark.analysis.ShapeAnalysis.CategorizedQuestions;
import benchmark.analysis.ShapeAnalysis.ShapesComparison;
import java.io.IOException;
import java.util.ArrayList;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.LineChartModel;


public class MainBeanCompare {

    Keywords ks = new Keywords();
    NoOfTriples nu = new NoOfTriples();
    OperatorDistribution ope = new OperatorDistribution();
    CategorizedQuestions categorizedQuestions;
    NLQCategoraizer nlqCategoraizer;
    
    KeywordsComparison keywordsComparison;
    NoOfTriplesComparison noOfTriplesComparison;
    OperatorDistributionComparison operatorDistributionComparison;
    ShapesComparison shapesComparison;
    NlqComparison nlqComparison;

    public static String knowledgebase = "-- Knowledgebase --";
    public static String benchmarkAnalysis = "-- Benchmark --";
    public static String benchmark = "-- Benchmark --";
    public static String analysisType = "-- Analysis Type --";
    public static boolean update = false;

    public ArrayList knowledgebases = new ArrayList();
    public ArrayList benchmarks = new ArrayList();
    public ArrayList analysisTypes = new ArrayList();

    public static boolean propertiesDefined = false;
    public boolean[] propertiesNLQTypes = new boolean[10];
    public boolean[] propertiesQueryShapes = new boolean[10];

    public ArrayList propertiesNLQTypesValues = new ArrayList();
    public ArrayList propertiesQueryShapesValues = new ArrayList();

    public static String eval_knowledgebase = "-- Knowledgebase --";
    public static String eval_benchmarkAnalysis = "-- Benchmark --";
    public static String eval_benchmark = "-- Benchmark --";
    public static boolean eval_Properties_defined = false;
    public static boolean eval_update_answers = false;
    public static String eval_SPARQL_URL;
    public static int eval_thresould = 0;

    public static Benchmark benchmarkData;
    public ArrayList<String> selectedBenchmarks = new ArrayList();
    
    boolean compareToQasparql;
    boolean compareToWdaqua;
    
    String qaSystemName;
    
    LineChartModel differentThetalineChartModel = new LineChartModel();

    public MainBeanCompare() throws IOException {
        
        for (int i = 0; i < propertiesNLQTypes.length; i++) {
            propertiesNLQTypes[i] = false;
        }
        for (int i = 0; i < propertiesQueryShapes.length; i++) {
            propertiesQueryShapes[i] = false;
        }
    }


    public String analysis() throws IOException {
        getBenchmarkData(benchmark);
        ks.keywordsAnalysis(benchmarkData);
        nu.triplesAnalysis(benchmarkData);
        ope.analysis(benchmarkData);
        categorizedQuestions = new CategorizedQuestions(benchmarkData);
        nlqCategoraizer = new NLQCategoraizer(benchmarkData);
        return "analysis.xhtml?faces-redirect=true";
    }

    public String compare() throws IOException {
        ArrayList<Benchmark> bs = new ArrayList<>();
        for (String s : selectedBenchmarks) {
            bs.add(getBenchmarkData(s));
        }
        keywordsComparison = new KeywordsComparison(bs);
        noOfTriplesComparison = new NoOfTriplesComparison(bs);
        operatorDistributionComparison = new OperatorDistributionComparison(bs);
        shapesComparison = new ShapesComparison(bs);
        nlqComparison = new NlqComparison(bs);
        return "compare.xhtml?faces-redirect=true";
    }
    
    
    ////////////////////////////////////////////////////////////////////////////

    public String getKnowledgebase() {
        return knowledgebase;
    }

    public void setKnowledgebase(String knowledgebase) {
        this.knowledgebase = knowledgebase;
    }

    public String getBenchmark() {
        return benchmark;
    }

    public void setBenchmark(String benchmark) {
        this.benchmark = benchmark;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public ArrayList<String> getKnowledgebases() {
        knowledgebases.clear();
        knowledgebases.add("-- Select KG --");
        knowledgebases.add("DBpedia");
        knowledgebases.add("Wikidata");
        knowledgebases.add("Freebase");
        knowledgebases.add("Another KG");
        return knowledgebases;
    }

    public void setKnowledgebases(ArrayList<String> knowledgebases) {
        this.knowledgebases = knowledgebases;
    }

    public ArrayList<String> getBenchmarks() {
        benchmarks.clear();
        benchmarks.add("-- Select Benchmark --");
        benchmarks.add("QALD-1");
        benchmarks.add("QALD-2");
        benchmarks.add("QALD-3");
        benchmarks.add("QALD-4");
        benchmarks.add("QALD-5");
        benchmarks.add("QALD-6");
        benchmarks.add("QALD-7");
        benchmarks.add("QALD-8");
        benchmarks.add("QALD-9");
        benchmarks.add("QALD-ALL");
        benchmarks.add("LC-QUAD");
        benchmarks.add("WebQuestions");
        benchmarks.add("GraphQuestions");
        benchmarks.add("SimpleQuestions");
        benchmarks.add("SimpleDBpediaQA");
        benchmarks.add("TempQuestions");
        benchmarks.add("ComplexQuestions");
        benchmarks.add("ComQA");
        //benchmarks.add("UserDefined");
        return benchmarks;
    }

    public void setBenchmarks(ArrayList<String> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public String getBenchmarkAnalysis() {
        return benchmarkAnalysis;
    }

    public void setBenchmarkAnalysis(String benchmarkAnalysis) {
        MainBeanCompare.benchmarkAnalysis = benchmarkAnalysis;
    }

    public ArrayList<String> getAnalysisTypes() {
        analysisTypes.clear();
        analysisTypes.add("-- Analysis Type --");
        analysisTypes.add("Compact");
        analysisTypes.add("Deep");
        return analysisTypes;
    }

    public void setAnalysisTypes(ArrayList analysisTypes) {
        this.analysisTypes = analysisTypes;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        MainBeanCompare.update = update;
    }

    public boolean[] getPropertiesNLQTypes() {
        return propertiesNLQTypes;
    }

    public void setPropertiesNLQTypes(boolean[] propertiesNLQTypes) {
        this.propertiesNLQTypes = propertiesNLQTypes;
    }

    public boolean[] getPropertiesQueryShapes() {
        return propertiesQueryShapes;
    }

    public void setPropertiesQueryShapes(boolean[] propertiesQueryShapes) {
        this.propertiesQueryShapes = propertiesQueryShapes;
    }

    public ArrayList getPropertiesNLQTypesValues() {
        propertiesNLQTypesValues.clear();
        propertiesNLQTypesValues.add("What");
        propertiesNLQTypesValues.add("When");
        propertiesNLQTypesValues.add("Where");
        propertiesNLQTypesValues.add("Who");
        propertiesNLQTypesValues.add("Whom");
        propertiesNLQTypesValues.add("Whose");
        propertiesNLQTypesValues.add("Which");
        propertiesNLQTypesValues.add("How-Adj");
        propertiesNLQTypesValues.add("Yes-No");
        propertiesNLQTypesValues.add("Request");
        propertiesNLQTypesValues.add("Topical");

        return propertiesNLQTypesValues;
    }

    public void setPropertiesNLQTypesValues(ArrayList propertiesNLQTypesValues) {
        this.propertiesNLQTypesValues = propertiesNLQTypesValues;
    }

    public ArrayList getPropertiesQueryShapesValues() {
        propertiesQueryShapesValues.clear();
        propertiesQueryShapesValues.add("Single-Edge");
        propertiesQueryShapesValues.add("Chain");
        propertiesQueryShapesValues.add("Chain-Set");
        propertiesQueryShapesValues.add("Tree");
        propertiesQueryShapesValues.add("Star");
        propertiesQueryShapesValues.add("Forest");
        propertiesQueryShapesValues.add("Flower");
        propertiesQueryShapesValues.add("Flower-Set");
        propertiesQueryShapesValues.add("Cycle");

        return propertiesQueryShapesValues;
    }

    public void setPropertiesQueryShapesValues(ArrayList propertiesQueryShapesValues) {
        this.propertiesQueryShapesValues = propertiesQueryShapesValues;
    }

    public boolean isPropertiesDefined() {
        return propertiesDefined;
    }

    public void setPropertiesDefined(boolean propertiesDefined) {
        MainBeanCompare.propertiesDefined = propertiesDefined;
    }

    public String getEval_knowledgebase() {
        return eval_knowledgebase;
    }

    public void setEval_knowledgebase(String eval_knowledgebase) {
        MainBeanCompare.eval_knowledgebase = eval_knowledgebase;
    }

    public String getEval_benchmarkAnalysis() {
        return eval_benchmarkAnalysis;
    }

    public void setEval_benchmarkAnalysis(String eval_benchmarkAnalysis) {
        MainBeanCompare.eval_benchmarkAnalysis = eval_benchmarkAnalysis;
    }

    public String getEval_benchmark() {
        return eval_benchmark;
    }

    public void setEval_benchmark(String eval_benchmark) {
        MainBeanCompare.eval_benchmark = eval_benchmark;
    }

    public boolean isEval_Properties_defined() {
        return eval_Properties_defined;
    }

    public void setEval_Properties_defined(boolean eval_Properties_defined) {
        MainBeanCompare.eval_Properties_defined = eval_Properties_defined;
    }

    public boolean isEval_update_answers() {
        return eval_update_answers;
    }

    public void setEval_update_answers(boolean eval_update_answers) {
        MainBeanCompare.eval_update_answers = eval_update_answers;
    }

    public String getEval_SPARQL_URL() {
        return eval_SPARQL_URL;
    }

    public void setEval_SPARQL_URL(String eval_SPARQL_URL) {
        MainBeanCompare.eval_SPARQL_URL = eval_SPARQL_URL;
    }

    public int getEval_thresould() {
        return eval_thresould;
    }

    public void setEval_thresould(int eval_thresould) {
        MainBeanCompare.eval_thresould = eval_thresould;
    }

    int number8;
    int number9;

    public int getNumber8() {
        return number8;
    }

    public void setNumber8(int number8) {
        this.number8 = number8;
    }

    public int getNumber9() {
        return number9;
    }

    public void setNumber9(int number9) {
        this.number9 = number9;
    }

    public ArrayList getSelectedBenchmarks() {
        return selectedBenchmarks;
    }

    public void setSelectedBenchmarks(ArrayList selectedBenchmarks) {
        this.selectedBenchmarks = selectedBenchmarks;
    }

    public Keywords getKs() {
        return ks;
    }

    public void setKs(Keywords ks) {
        this.ks = ks;
    }

    public NoOfTriples getNu() {
        return nu;
    }

    public void setNu(NoOfTriples nu) {
        this.nu = nu;
    }

    public OperatorDistribution getOpe() {
        return ope;
    }

    public void setOpe(OperatorDistribution ope) {
        this.ope = ope;
    }

    public CategorizedQuestions getCategorizedQuestions() {
        return categorizedQuestions;
    }

    public void setCategorizedQuestions(CategorizedQuestions categorizedQuestions) {
        this.categorizedQuestions = categorizedQuestions;
    }

    public NLQCategoraizer getNlqCategoraizer() {
        return nlqCategoraizer;
    }

    public void setNlqCategoraizer(NLQCategoraizer nlqCategoraizer) {
        this.nlqCategoraizer = nlqCategoraizer;
    }

    public static Benchmark getBenchmarkData(String benchmarkName) {
        //Avoid multiple call time
//        if(benchmarkData != null)
//            if(benchmarkData.questions.size()>0) return benchmarkData;
        
        
        benchmarkData = new Benchmark();
        try {
            if (benchmarkName.equals("QALD-1")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_1);
            } else if (benchmarkName.equals("QALD-2")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_2);
            } else if (benchmarkName.equals("QALD-3")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_3);
            } else if (benchmarkName.equals("QALD-4")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_4);
            } else if (benchmarkName.equals("QALD-5")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_5);
            } else if (benchmarkName.equals("QALD-6")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_6);
            } else if (benchmarkName.equals("QALD-7")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_7);
            } else if (benchmarkName.equals("QALD-8")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_8);
            } else if (benchmarkName.equals("QALD-9")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_9);
            } else if (benchmarkName.equals("QALD-ALL")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.QALD_ALL);
            } else if (benchmarkName.equals("LC-QUAD")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.LC_QUAD);
            } else if (benchmarkName.equals("WebQuestions")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.WebQuestions);
            } else if (benchmarkName.equals("GraphQuestions")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.GraphQuestions);
            } else if (benchmarkName.equals("SimpleDBpediaQA")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.SimpleDBpediaQA);
            } else if (benchmarkName.equals("SimpleQuestions")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.SimpleQuestions);
            } else if (benchmarkName.equals("ComplexQuestions")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.ComplexQuestions);
            } else if (benchmarkName.equals("ComQA")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.ComQA);
            } else if (benchmarkName.equals("TempQuestions")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.TempQuestions);
            } else if (benchmarkName.equals("UserDefined")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.UserDefined);
            } else if (benchmarkName.equals("PropertiesDefined")) {
                benchmarkData.parseBenchmarkFiles(Benchmark.PropertiesDefined);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return benchmarkData;
    }

    public static void setBenchmarkData(Benchmark benchmarkData) {
        MainBeanCompare.benchmarkData = benchmarkData;
    }

    public KeywordsComparison getKeywordsComparison() {
        return keywordsComparison;
    }

    public void setKeywordsComparison(KeywordsComparison keywordsComparison) {
        this.keywordsComparison = keywordsComparison;
    }

    public NoOfTriplesComparison getNoOfTriplesComparison() {
        return noOfTriplesComparison;
    }

    public void setNoOfTriplesComparison(NoOfTriplesComparison noOfTriplesComparison) {
        this.noOfTriplesComparison = noOfTriplesComparison;
    }

    public OperatorDistributionComparison getOperatorDistributionComparison() {
        return operatorDistributionComparison;
    }

    public void setOperatorDistributionComparison(OperatorDistributionComparison operatorDistributionComparison) {
        this.operatorDistributionComparison = operatorDistributionComparison;
    }

    public ShapesComparison getShapesComparison() {
        return shapesComparison;
    }

    public void setShapesComparison(ShapesComparison shapesComparison) {
        this.shapesComparison = shapesComparison;
    }

    public NlqComparison getNlqComparison() {
        return nlqComparison;
    }

    public void setNlqComparison(NlqComparison nlqComparison) {
        this.nlqComparison = nlqComparison;
    }



    public boolean isCompareToQasparql() {
        return compareToQasparql;
    }

    public void setCompareToQasparql(boolean compareToQasparql) {
        this.compareToQasparql = compareToQasparql;
    }

    public boolean isCompareToWdaqua() {
        return compareToWdaqua;
    }

    public void setCompareToWdaqua(boolean compareToWdaqua) {
        this.compareToWdaqua = compareToWdaqua;
    }

    public String getQaSystemName() {
        return qaSystemName;
    }

    public void setQaSystemName(String qaSystemName) {
        this.qaSystemName = qaSystemName;
    }
    
    
    public LineChartModel getDifferentThetalineChartModel() {
        differentThetalineChartModel = new LineChartModel();

        

        
        differentThetalineChartModel.setTitle("Different Thresholds");
        differentThetalineChartModel.setLegendPosition("e");
        //model2.setShowPointLabels(true);
        differentThetalineChartModel.getAxes().put(AxisType.X, new CategoryAxis("Threshold"));
        Axis xAxis2 = differentThetalineChartModel.getAxis(AxisType.X);
        xAxis2.setTickAngle(-30);

        Axis yAxis2 = differentThetalineChartModel.getAxis(AxisType.Y);
        yAxis2.setMin(0);
        yAxis2.setMax(1);
        yAxis2.setTickCount(11);
        yAxis2.setLabel("F1-Global");
        
        return differentThetalineChartModel;
    }

    public void setDifferentThetalineChartModel(LineChartModel differentThetalineChartModel) {
        this.differentThetalineChartModel = differentThetalineChartModel;
    }

    
    
    
    
    
    
}
