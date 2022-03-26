package system.components;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import online.nl_generation.Benchmark;

/**
 *
 * @author aorogat
 */
public class BenchmarkJsonWritter {

    public static void save(Benchmark benchmark, String benchmarkName) {
//        JSONArray jsonArray = new JSONArray();
//        int j = 0;
//        SortedMap<String, Object> json = new TreeMap<>();
//        ArrayList<GeneratedQuestion> generatedQuestions = new ArrayList<>();

//        for (int i = 0; i < benchmark.generatedBenchmark.size(); i++) {
//            if(benchmark.generatedBenchmark.get(i).getAnswers()!=null)
//                json.put("question" + ++j, benchmark.generatedBenchmark.get(i));
//        }
        ObjectMapper mapper = new ObjectMapper();
        try {

            // Writing to a file   
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
//            Scanner in = new Scanner(System.in);
//            System.out.print("Enter benchmark name: ");
//            String benchmarkName = in.nextLine();
                    
            writer.writeValue(new File(benchmarkName + ".json"), benchmark.generatedBenchmark);
            
            System.out.println("Saved sucessfuly");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
