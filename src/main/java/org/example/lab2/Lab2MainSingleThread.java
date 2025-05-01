package org.example.lab2;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lab2MainSingleThread {

    private static final String DIRECTORY_TO_SCAN = "src/main/resources/text/";

    private static final Map<String, Long> countedWords = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Однопоточный режим:");

        List<String> filePaths = Lab2Utils.getFilePathsInDirectory(DIRECTORY_TO_SCAN);

        Instant startTime = Instant.now();
        for (String filePath : filePaths) {
            new FileWordCounterRunnable(filePath, countedWords).run();
        }
        Instant finishTime = Instant.now();

        System.out.println(countedWords);
        System.out.printf("Затраченное время : %s мс.%n", Duration.between(startTime, finishTime).toMillis());
    }
}
