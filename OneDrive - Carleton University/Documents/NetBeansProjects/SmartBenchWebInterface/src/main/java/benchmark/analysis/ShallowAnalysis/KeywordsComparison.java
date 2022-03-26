package benchmark.analysis.ShallowAnalysis;

import benchmark.analysis.DataSet.Benchmark;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

/**
 *
 * @author aorogat
 */
@ManagedBean
@RequestScoped
public class KeywordsComparison {

    ArrayList<Benchmark> benchmarks;
    LineChartModel lineChartModel = new LineChartModel();
    BarChartModel barChartModel = new BarChartModel();
    ArrayList<ArrayList<Keyword>> allBenchmarks = new ArrayList<>();

    public KeywordsComparison(ArrayList<Benchmark> benchmarks) {
        this.benchmarks = benchmarks;
        for (Benchmark benchmark : benchmarks) {
            Keywords k = new Keywords();
            ArrayList<Keyword> keywords = k.keywordsAnalysis(benchmark);
            allBenchmarks.add(keywords);
            ChartSeries patternKys = new ChartSeries();
            patternKys.setLabel(benchmark.name);
            for (Keyword keyword : keywords) {
                patternKys.set(keyword.key, keyword.relative);
            }
            lineChartModel.addSeries(patternKys);
            barChartModel.addSeries(patternKys);
        }

        lineChartModel.setTitle("Query Keywords");
        lineChartModel.setLegendPosition("e");
        //model2.setShowPointLabels(true);
        lineChartModel.getAxes().put(AxisType.X, new CategoryAxis("Keyword"));
        Axis xAxis2 = lineChartModel.getAxis(AxisType.X);
        xAxis2.setTickAngle(-30);

        Axis yAxis2 = lineChartModel.getAxis(AxisType.Y);
        yAxis2.setMin(0);
        yAxis2.setMax(100);
        
        barChartModel.setTitle("Query Keywords");
        barChartModel.setLegendPosition("e");
        //model2.setShowPointLabels(true);
        barChartModel.getAxes().put(AxisType.X, new CategoryAxis("Keyword"));
        Axis xAxis3 = barChartModel.getAxis(AxisType.X);
        xAxis3.setTickAngle(-30);

        Axis yAxis3 = barChartModel.getAxis(AxisType.Y);
        yAxis3.setMin(0);
        yAxis3.setMax(100);
    }

    public ArrayList<Benchmark> getBenchmarks() {
        return benchmarks;
    }

    public void setBenchmarks(ArrayList<Benchmark> benchmarks) {
        this.benchmarks = benchmarks;
    }

    public LineChartModel getLineChartModel() {
        return lineChartModel;
    }

    public void setLineChartModel(LineChartModel lineChartModel) {
        this.lineChartModel = lineChartModel;
    }

    public ArrayList<ArrayList<Keyword>> getAllBenchmarks() {
        return allBenchmarks;
    }

    public void setAllBenchmarks(ArrayList<ArrayList<Keyword>> allBenchmarks) {
        this.allBenchmarks = allBenchmarks;
    }

    public BarChartModel getBarChartModel() {
        return barChartModel;
    }

    public void setBarChartModel(BarChartModel barChartModel) {
        this.barChartModel = barChartModel;
    }

    
    
    
}
