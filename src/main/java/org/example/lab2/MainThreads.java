package org.example.lab2;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainThreads {

    private static final String DIRECTORY_TO_SCAN = "src/main/resources/text/";

    private static final Map<String, Long> countedWords = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Многопоточный режим:");

        List<String> filePaths = getFilesInDirectory(DIRECTORY_TO_SCAN);

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
        Instant endTime = Instant.now();

        System.out.println(countedWords);
        System.out.printf("Затраченное время : %s мс.%n", Duration.between(startTime, endTime).toMillis());
    }

    private static List<String> getFilesInDirectory(String directoryPath) {
        File[] files = Paths.get(directoryPath).toFile().listFiles();

        if (files == null) {
            throw new RuntimeException("Ошибка при попытке чтения файлов из директории %s: listFiles() вернул null".formatted(directoryPath));
        }

        return Arrays.stream(files)
                .map(File::getPath)
                .toList();
    }
}
