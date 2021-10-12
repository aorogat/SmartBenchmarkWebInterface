
package online.nl_generation;

import java.util.Random;

public class Request {
    private static String[] prefixes = {
    "Give me the ",
    "Give me all",
    "Tell me the",
    "Tell me all",
    "Mention the",
    "Mention all"
    };
    private static Random rand = new Random();
    
    public static String getRequestPrefix()
    {
        return prefixes[rand.nextInt(prefixes.length)];
    }
}
