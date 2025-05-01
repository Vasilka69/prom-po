package org.example.lab1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FileWordCounter {

    private FileWordCounter() { }

    public static Map<String, Long> countWordsFromFile(String filePath) {
        String content = getFileContent(filePath);

        return countWords(content);
    }

    public static String getFileContent(String filePath) {
        try {
            return String.join(" ", Files.readAllLines(Path.of(filePath)));
        } catch (IOException e) {
            throw new RuntimeException("Не удалось прочитать содержимое файла: %s".formatted(e));
        }
    }

    private static Map<String, Long> countWords(String content) {
        String preformattedContent = content
                .toLowerCase()
                .replaceAll("[.,!?:;\"<>()—]", "")
                .replace("  ", " ");

        return Arrays.stream(preformattedContent.split(" "))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

}
