import java.io.*;
import java.util.*;
import java.nio.file.*;

public class Rebus_Helper {
    
    private static List<String> totalDict;
    private static List<String> smallDict;
    private static Scanner console;
    
    public static void main(String[] args) throws IOException {
        totalDict = new LinkedList<String>();
        smallDict = new LinkedList<String>();
        console = new Scanner(System.in);
        Files.lines(Paths.get("dictionary.txt"))
            .forEach(totalDict::add);
        System.out.println("Done populating dict");
        if (args.length == 2) {
            int length = Integer.parseInt(args[0]);
            String pattern = args[1];
            
            totalDict.stream()
                .filter(s -> s.length() == length)
                .filter(s -> s.contains(pattern))
                .forEach(smallDict::add);
            smallDict.stream()
                .forEach(System.out::println);
        }
        System.out.println(args.length);
    }   
}