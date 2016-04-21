import java.io.*;
import java.util.stream.*;
import java.util.*;
import java.nio.file.*;

public class Rebus_Helper {
    
    private static List<String> totalDict;
    private static List<String> smallDict;
    private static Scanner console;
    
    private static String pattern;
    private static int length;
    private static int index;
    
    private static Stream<String> mainStream;
    
    public static void main(String[] args) throws IOException {
        totalDict = new LinkedList<String>();
        smallDict = new LinkedList<String>();
        console = new Scanner(System.in);
        Files.lines(Paths.get("dictionary.txt"))
            .forEach(totalDict::add);
        System.out.println("Done populating dict");
        mainStream = totalDict.stream();
        
        if (args.length > 0) {
            pattern = args[0];
            
            mainStream = mainStream.filter(s -> s.contains(pattern));
            
            // totalDict.stream()
            //     .filter(s -> s.contains(pattern))
            //     .forEach(System.out::println);
        }
        if (args.length > 1) {           
            length = Integer.parseInt(args[1]);
            
            mainStream = mainStream.filter(s -> s.length() == length);
            
            // totalDict.stream()
            //     .filter(s -> s.length() == length)
            //     .filter(s -> s.contains(pattern))
            //     .forEach(System.out::println);
        }
        //System.out.println(args.length);
        mainStream.forEach(System.out::println);
    }   
}