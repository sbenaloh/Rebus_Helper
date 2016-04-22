//Rebus is a word puzzle game, often consisting of a picture and one or two letters
//that combine to the answer (a single word).  There are 3 levels of hints: giving
//the length of the answer, the length and one letter, and revealing all the letters
//of the answer.

//The optimization that I want to implement on top of just finding words with certain
//letters is finding the words that also have other words within them.  Often the picture
//is a word, then tacking on the letters in the picture (usually either front or back, 
//only sometimes in the middle) makes a new word.

//Based on the level of hint bought (with game coins, not real $$), search parameters
//should adjust:
// HINT LEVEL:      NONE        LENGTH      +1 LETTER       ALL LETTERS
//                  pattern     pattern     pattern         Just anagram*
//                  WiW         WiW         WiW
//                              length      length
//                                          known Letter
//
// pattern  =   Letters seen in picture
// WiW      =   Word-in-Word search, optional parameter
// length   =   length of answer
// anagram  =   Not handled here, anagram engines already exist

import java.io.*;
import java.util.stream.*;
import java.util.*;
import java.nio.file.*;

public class Rebus_Helper {
    
    private static List<String> totalDict;
    private static List<String> smallDict;
    
    private static String pattern;
    private static int length;
    private static int knownIndex;
    private static char knownLetter;
    private static int count;
    private static boolean wordInWord;
    
    private static Stream<String> mainStream;
    
    public static void main(String[] args) throws IOException {
        totalDict = new LinkedList<String>();
        smallDict = new LinkedList<String>();
        wordInWord = false;
        
        // Load Dictionary
        Files.lines(Paths.get("dictionary.txt"))
            .forEach(totalDict::add);
            
        // Initialize stream, parallel because order doesn't matter
        mainStream = totalDict.stream().parallel();
        
        // First command-line argument is the pattern
        if (args.length > 0) {
            pattern = args[0];           
            mainStream = mainStream.filter(s -> s.contains(pattern));
        }
        
        // Second command-line argument is whether or not to do WiW filtering (performed
        // later)
        if (args.length > 1) {
            wordInWord = args[1].toLowerCase().equals("y");
        }
        
        // Third command-line argument is the length
        if (args.length > 2) {           
            length = Integer.parseInt(args[2]);
            // Filter the stream by the length
            mainStream = mainStream.filter(s -> s.length() == length);
        }
        
        // Fourth command-line argument is a specific character at a specific index
        if (args.length > 3) {
            knownLetter = args[3].charAt(0);
            knownIndex = Integer.parseInt(args[4]);
            // Filter the stream for words with the right character at the right index
            mainStream = mainStream.filter(s -> s.charAt(knownIndex) == knownLetter);
        }
        
        // Performing the WiW filter
        if (wordInWord) {
            
            // Add Strings to the smallDict (filtered)
            for (String s : totalDict) {
                if (pattern.length() > 0) {
                    // If there is a specified pattern, smallDict takes words
                    // of size 'length of answer' - 'length of pattern', so that
                    // the smallDict word and the pattern combine to the answer
                    if (s.length() == length - pattern.length()) {
                        smallDict.add(s);
                    }
                } else if (s.length() < length && s.length() > 2) {
                    // If there is no specified pattern, add words that are smaller
                    // than the length of the answer, and larger than 2 (otherwise 
                    // there would be far too many possibilites)
                    smallDict.add(s);
                }
            }
            
            // Filter by WiW            
            mainStream = mainStream.filter(s -> {
                // If a pattern was specified
                if (pattern.length() > 0) {
                    String t = "";
                    // The pattern will likely either be at the start or end of the answer;
                    // take the answer and remove the pattern, check if it's in the filtered
                    // dictionary
                    if (s.startsWith(pattern)) {
                        t = s.substring(pattern.length());
                    } else if (s.endsWith(pattern)) {
                        t = s.substring(0, s.length() - pattern.length());
                    }
                    // If the length of the answer is unknown, the dictionary cannot be filtered
                    // (use totalDict)
                    if (length == 0) {
                        return totalDict.contains(t);
                    } else {
                        return smallDict.contains(t);
                    }
                } else {
                    // If no pattern was specified, find words with other words within them
                    for (String t : smallDict) {
                        if (s.contains(t)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        
        // Prints out possibilities in alphabetical order
        mainStream.peek(s -> {count++;}).forEachOrdered(System.out::println);
        System.out.println(count + " possibilities");
    }   
}