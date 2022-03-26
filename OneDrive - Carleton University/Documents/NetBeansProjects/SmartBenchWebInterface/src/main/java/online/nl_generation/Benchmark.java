package online.nl_generation;

import java.util.ArrayList;

public class Benchmark {
    public ArrayList<GeneratedQuestion> generatedBenchmark = new ArrayList<>();

    public Benchmark() {
    }
    
    

    public Benchmark(ArrayList<GeneratedQuestion> generatedBenchmark) {
        this.generatedBenchmark = generatedBenchmark;
    }

    public ArrayList<GeneratedQuestion> getGeneratedBenchmark() {
        return generatedBenchmark;
    }

    public void setGeneratedBenchmark(ArrayList<GeneratedQuestion> generatedBenchmark) {
        this.generatedBenchmark = generatedBenchmark;
    }
    
    
    
    
    
}
