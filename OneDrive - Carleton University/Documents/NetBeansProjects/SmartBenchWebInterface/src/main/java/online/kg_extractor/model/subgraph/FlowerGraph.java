package online.kg_extractor.model.subgraph;

/**
 *
 * @author aorogat
 */
public class FlowerGraph {
    private StarGraph star;
    private CycleGraph cycle;
    private String seedType;

    //Constructors

    public FlowerGraph(StarGraph star, CycleGraph cycle) {
        this.star = star;
        this.cycle = cycle;
    }
    

    public String toString() {
        String s = star.toString() + cycle.toString();
        return s + "\n";
    }

   
    public String getSeedType() {
        return seedType;
    }

    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

    public StarGraph getStar() {
        return star;
    }

    public void setStar(StarGraph star) {
        this.star = star;
    }

    public CycleGraph getCycle() {
        return cycle;
    }

    public void setCycle(CycleGraph cycle) {
        this.cycle = cycle;
    }

    
    
}
