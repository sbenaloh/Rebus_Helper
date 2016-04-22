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
    
    private static Stream<String> mainStream;
    
    public static void main(String[] args) throws IOException {
        totalDict = new LinkedList<String>();
        smallDict = new LinkedList<String>();
        Files.lines(Paths.get("dictionary.txt"))
            .forEach(totalDict::add);
        mainStream = totalDict.stream().parallel();

        if (args.length > 0) {
            pattern = args[0];           
            mainStream = mainStream.filter(s -> s.contains(pattern));
            if (args.length < 2) {
                smallDict = totalDict;
            }
        }
        if (args.length > 2) {           
            length = Integer.parseInt(args[2]);

            mainStream = mainStream.filter(s -> s.length() == length);
            for (String t : totalDict) {
                if (pattern.length() > 0) {
                    if (t.length() == length - pattern.length()) {
                        smallDict.add(t);
                    }
                } else if (t.length() < length && t.length() > 2) {
                    smallDict.add(t);
                }
            }
        }
        if (args.length > 3) {
            knownLetter = args[3].charAt(0);
            knownIndex = Integer.parseInt(args[4]);
            
            mainStream = mainStream.filter(s -> s.charAt(knownIndex) == knownLetter);
        }
        if (args.length > 1 && args[1].toLowerCase().equals("y")) {
            mainStream = mainStream.filter(s -> {
                if (pattern.length() > 0) {
                    String t = "";
                    if (s.startsWith(pattern)) {
                        t = s.substring(pattern.length());
                    } else if (s.endsWith(pattern)) {
                        t = s.substring(0, s.length() - pattern.length());
                    }
                    if (length == 0) {
                        return totalDict.contains(t);
                    } else {
                        return smallDict.contains(t);
                    }
                } else {
                    for (String t : smallDict) {
                        if (s.contains(t)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }
        count = 0;
        mainStream.peek(s -> {count++;}).forEach(System.out::println);
        System.out.println(count + " possibilities");
    }   
}