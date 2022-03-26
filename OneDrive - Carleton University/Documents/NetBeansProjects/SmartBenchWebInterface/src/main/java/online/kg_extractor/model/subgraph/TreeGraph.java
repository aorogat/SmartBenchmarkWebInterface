package online.kg_extractor.model.subgraph;

import java.util.ArrayList;

/**
 *
 * @author aorogat
 */
public class TreeGraph {
    
    private ArrayList<StarGraph> treeGraph;
    private String seedType;
    private String seed_with_Prefix;
    private String seed;

    
    public TreeGraph(ArrayList<StarGraph> treeGraph) {
        this.treeGraph = treeGraph;
        this.seedType = treeGraph.get(0).getSeedType();
        this.seed = treeGraph.get(0).getStar().get(0).getSubject().getValue();
    }
    
    

    public ArrayList<StarGraph> getTreeGraph() {
        return treeGraph;
    }

    public void setTreeGraph(ArrayList<StarGraph> treeGraph) {
        this.treeGraph = treeGraph;
    }

    public String getSeedType() {
        return seedType;
    }

    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getSeed_with_Prefix() {
        return seed_with_Prefix;
    }

    public void setSeed_with_Prefix(String seed_with_Prefix) {
        this.seed_with_Prefix = seed_with_Prefix;
    }
    
    @Override
    public String toString()
    {
        String s = "";
        for (StarGraph starGraph : treeGraph) {
            s += starGraph.toString() + "\n";
        }
        return s;
    }
    
    public int getSize(){
        int s =0 ;
        for (StarGraph starGraph : treeGraph) {
            s += starGraph.getStar().size()+1;
        }
        return s;
    }
    
}
