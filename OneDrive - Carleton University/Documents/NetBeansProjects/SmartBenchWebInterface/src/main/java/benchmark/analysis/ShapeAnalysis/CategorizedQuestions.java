/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package benchmark.analysis.ShapeAnalysis;

import benchmark.analysis.DataSet.Benchmark;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import benchmark.analysis.qa.dataStructures.Question;
import java.io.StringWriter;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
 
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;
import org.primefaces.model.*;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.PieChartModel;
 

/**
 *
 * @author aorogat
 */

public class CategorizedQuestions {

    ArrayList<Question> singleShape_Qs = new ArrayList<>();
    ArrayList<Question> chain_Qs = new ArrayList<>();
    ArrayList<Question> chainSet_Qs = new ArrayList<>();
    ArrayList<Question> star_Qs = new ArrayList<>();
    ArrayList<Question> tree_Qs = new ArrayList<>();
    ArrayList<Question> forest_Qs = new ArrayList<>();
    ArrayList<Question> cycle_Qs = new ArrayList<>();
    ArrayList<Question> flower_Qs = new ArrayList<>();
    ArrayList<Question> flowerSet_Qs = new ArrayList<>();

    ShapesSummary shapesSummary = new ShapesSummary();
    
    PieChartModel pieModelSelect= new PieChartModel();
    BarChartModel barModel = new BarChartModel();
    
    private List<SortMeta> sortBy;
    int allSize = 0;

    
    public CategorizedQuestions(Benchmark benchmark) throws IOException {
      
    singleShape_Qs = new ArrayList<>();
    chain_Qs = new ArrayList<>();
    chainSet_Qs = new ArrayList<>();
    star_Qs = new ArrayList<>();
    tree_Qs = new ArrayList<>();
    forest_Qs = new ArrayList<>();
    cycle_Qs = new ArrayList<>();
    flower_Qs = new ArrayList<>();
    flowerSet_Qs = new ArrayList<>();
    allSize = benchmark.questionsWithoutDuplicates.size();
        
        //DataSetPreprocessing.getQueriesWithoutDuplicates(9, false, false, false);
        for (Question q : benchmark.questionsWithoutDuplicates) {
            String queryString = q.getQuestionQuery();
            try {
                Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
                q.setQuestionQuery(query.toString(Syntax.syntaxSPARQL));
                String current = query.toString();
                if (QueryShapeType.isSingleEdge(current)) {
                    singleShape_Qs.add(q);
                }
                if (QueryShapeType.isChain(current)
                    &&!QueryShapeType.isSingleEdge(current)) {
                    chain_Qs.add(q);
                }
                if (QueryShapeType.isChainSet(current)
                        &&!QueryShapeType.isChain(current)) {
                    chainSet_Qs.add(q);
                }
                if (QueryShapeType.isCycle(current)) {
                    cycle_Qs.add(q);
                }
                if (QueryShapeType.isFlower(current)
                    &&!QueryShapeType.isTree(current)) {
                    flower_Qs.add(q);
                }
                if (QueryShapeType.isFlowerSet(current)
                   &&!QueryShapeType.isFlower(current)) {
                    flowerSet_Qs.add(q);
                }
                if (QueryShapeType.isForest(current)
                        &&!QueryShapeType.isTree(current)) {
                    forest_Qs.add(q);
                }
                if (QueryShapeType.isStar(current)) {
                    star_Qs.add(q);
                }
                if (QueryShapeType.isTree(current)
                    &&!QueryShapeType.isChain(current)
                    &&!QueryShapeType.isStar(current)) {
                    tree_Qs.add(q);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                //System.out.println(queryString);
            }
        }
        
        pieModelSelect.clear();
        pieModelSelect.set("Single-Edge("+singleShape_Qs.size()+")" , singleShape_Qs.size());
        pieModelSelect.set("Chain("+chain_Qs.size()+")", chain_Qs.size());
        pieModelSelect.set("Chain-Set("+chainSet_Qs.size()+")", chainSet_Qs.size());
        pieModelSelect.set("Tree("+tree_Qs.size()+")", tree_Qs.size());
        pieModelSelect.set("Star("+star_Qs.size()+")", star_Qs.size());
        pieModelSelect.set("Forest("+forest_Qs.size()+")", forest_Qs.size());
        pieModelSelect.set("Flower("+flower_Qs.size()+")", flower_Qs.size());
        pieModelSelect.set("Flower-Set("+flowerSet_Qs.size()+")", flowerSet_Qs.size());
        

        pieModelSelect.setTitle("Query Shape");
        pieModelSelect.setLegendPosition("e");
        pieModelSelect.setShowDataLabels(true);
        pieModelSelect.setDiameter(150);
        pieModelSelect.setShadow(false);
        
        createCharts();
    }

    String printQuestionsAsXML(ArrayList<Question> qs) {
        String file = ("<questions>"  + "\n");
        int id = 0;
        for (Question q : qs) {
            id++;
            q.setId(id);
            file += jaxbObjectToXML(q);
        }
        file += ("</questions>"  + "\n");
        return file;
    }

    
    private static String jaxbObjectToXML(Question question) 
    {
        try
        {
            //Create JAXB Context
            JAXBContext jaxbContext = JAXBContext.newInstance(Question.class);
             
            //Create Marshaller
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
 
            //Required formatting??
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            jaxbMarshaller.setProperty("jaxb.fragment", Boolean.TRUE);
 
            //Print XML String to Console
            StringWriter sw = new StringWriter();
             
            //Write XML to StringWriter
            jaxbMarshaller.marshal(question, sw);
             
            //Verify XML Content
            String xmlContent = sw.toString();
            return xmlContent;
 
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return "";
    }

    
    private void createCharts() {

        barModel.clear();

        //////////////////////////////////////////
        ChartSeries shapes = new ChartSeries();
        shapes.setLabel("Query Shape");
        shapes.set("Single-Edge", singleShape_Qs.size());
        shapes.set("Chain", chain_Qs.size()+singleShape_Qs.size());
        shapes.set("Chain-Set", chainSet_Qs.size()+chain_Qs.size()+singleShape_Qs.size());
        shapes.set("Star", star_Qs.size());
        shapes.set("Tree", tree_Qs.size()+chain_Qs.size()+singleShape_Qs.size());
        shapes.set("Forest", forest_Qs.size()+tree_Qs.size()+chain_Qs.size()+singleShape_Qs.size()+chainSet_Qs.size());
        shapes.set("Flower", flower_Qs.size()+tree_Qs.size()+chain_Qs.size()+singleShape_Qs.size());
        shapes.set("Flower-Set", flowerSet_Qs.size()+flower_Qs.size()+tree_Qs.size()+chain_Qs.size()+singleShape_Qs.size()+
                chainSet_Qs.size()+forest_Qs.size());
        shapes.set("Cycle", cycle_Qs.size());

        barModel.addSeries(shapes);
        barModel.setTitle("Cumulative Query Shapes");
        barModel.setLegendPosition("e");
        barModel.setShowPointLabels(true);
        barModel.getAxes().put(AxisType.X, new CategoryAxis("Shape"));
        Axis xAxis = barModel.getAxis(AxisType.X);
        xAxis.setTickAngle(-30);
        Axis yAxis = barModel.getAxis(AxisType.Y);
        yAxis.setMin(0);
    }
    
    public ArrayList<Question> getSingleShape_Qs() {
        return singleShape_Qs;
    }

    public void setSingleShape_Qs(ArrayList<Question> singleShape_Qs) {
        this.singleShape_Qs = singleShape_Qs;
    }

    public ArrayList<Question> getChain_Qs() {
        return chain_Qs;
    }

    public void setChain_Qs(ArrayList<Question> chain_Qs) {
        this.chain_Qs = chain_Qs;
    }

    public ArrayList<Question> getChainSet_Qs() {
        return chainSet_Qs;
    }

    public void setChainSet_Qs(ArrayList<Question> chainSet_Qs) {
        this.chainSet_Qs = chainSet_Qs;
    }

    public ArrayList<Question> getStar_Qs() {
        return star_Qs;
    }

    public void setStar_Qs(ArrayList<Question> star_Qs) {
        this.star_Qs = star_Qs;
    }

    public ArrayList<Question> getTree_Qs() {
        return tree_Qs;
    }

    public void setTree_Qs(ArrayList<Question> tree_Qs) {
        this.tree_Qs = tree_Qs;
    }

    public ArrayList<Question> getForest_Qs() {
        return forest_Qs;
    }

    public void setForest_Qs(ArrayList<Question> forest_Qs) {
        this.forest_Qs = forest_Qs;
    }

    public ArrayList<Question> getCycle_Qs() {
        return cycle_Qs;
    }

    public void setCycle_Qs(ArrayList<Question> cycle_Qs) {
        this.cycle_Qs = cycle_Qs;
    }

    public ArrayList<Question> getFlower_Qs() {
        return flower_Qs;
    }

    public void setFlower_Qs(ArrayList<Question> flower_Qs) {
        this.flower_Qs = flower_Qs;
    }

    public ArrayList<Question> getFlowerSet_Qs() {
        return flowerSet_Qs;
    }

    public void setFlowerSet_Qs(ArrayList<Question> flowerSet_Qs) {
        this.flowerSet_Qs = flowerSet_Qs;
    }

    public PieChartModel getPieModelSelect() {
        return pieModelSelect;
    }

    public void setPieModelSelect(PieChartModel pieModelSelect) {
        this.pieModelSelect = pieModelSelect;
    }

    public BarChartModel getBarModel() {
        return barModel;
    }

    public void setBarModel(BarChartModel barModel) {
        this.barModel = barModel;
    }

    public ShapesSummary getShapesSummary() {
        shapesSummary.summarys.add(new OneShapeSummary("Single-Edge", singleShape_Qs.size(), allSize));
        shapesSummary.summarys.add(new OneShapeSummary("Chain", chain_Qs.size(), allSize));
        shapesSummary.summarys.add(new OneShapeSummary("Chain Set", chainSet_Qs.size(), allSize));
        shapesSummary.summarys.add(new OneShapeSummary("Star", star_Qs.size(), allSize));
        shapesSummary.summarys.add(new OneShapeSummary("Cycle", cycle_Qs.size(), allSize));
        shapesSummary.summarys.add(new OneShapeSummary("Tree", tree_Qs.size(), allSize));
        shapesSummary.summarys.add(new OneShapeSummary("Forest", forest_Qs.size(), allSize));
        shapesSummary.summarys.add(new OneShapeSummary("Flower", flower_Qs.size(), allSize));
        shapesSummary.summarys.add(new OneShapeSummary("Flower Set", flowerSet_Qs.size(), allSize));
                
        return shapesSummary;
    }

    public void setShapesSummary(ShapesSummary shapesSummary) {
        this.shapesSummary = shapesSummary;
    }
    
    
    
}
