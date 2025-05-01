package org.example.lab1;

import java.util.Map;

public class Lab1Main {

    private static final String FILE_PATH = "src/main/resources/text/1.txt";

    public static void main(String[] args) {
        Map<String, Long> countedWords = FileWordCounter.countWordsFromFile(FILE_PATH);

        System.out.println(countedWords);
    }
}
