package org.example.lab2;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Lab2MainThreads {

    private static final String DIRECTORY_TO_SCAN = "src/main/resources/text/";

    private static final Map<String, Long> countedWords = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Многопоточный режим:");

        List<String> filePaths = Lab2Utils.getFilePathsInDirectory(DIRECTORY_TO_SCAN);

        List<Thread> threads = new ArrayList<>();
        Instant startTime = Instant.now();
        for (String filePath : filePaths) {
            Thread thread = new Thread(new FileWordCounterRunnable(filePath, countedWords));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
        Instant finishTime = Instant.now();

        System.out.println(countedWords);
        System.out.printf("Затраченное время : %s мс.%n", Duration.between(startTime, finishTime).toMillis());
    }
}
