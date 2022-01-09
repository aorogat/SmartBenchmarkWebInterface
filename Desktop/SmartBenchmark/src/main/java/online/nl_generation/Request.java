
package online.nl_generation;

import java.util.Random;

public class Request {
    private static String[] prefixes = {
    "Give me",
    //"Give me all",
    "Tell me",
    //"Tell me all",
    "Mention",
    "Find",
    "Return",
    //"Mention all"
    };
    private static Random rand = new Random();
    
    public static String getRequestPrefix()
    {
        return prefixes[rand.nextInt(prefixes.length)];
    }
}
