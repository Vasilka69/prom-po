package org.example.lab2;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainThreadPool {

    private static final String DIRECTORY_TO_SCAN = "src/main/resources/text/";

    private static final Map<String, Long> countedWords = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Многопоточный режим (ThreadPool):");

        List<String> filePaths = getFilesInDirectory(DIRECTORY_TO_SCAN);

        ExecutorService executorService = Executors.newFixedThreadPool(filePaths.size());

        Instant startTime = Instant.now();
        for (String filePath : filePaths) {
            executorService.submit(new FileWordCounterRunnable(filePath, countedWords));
        }
        executorService.shutdown();

        boolean isTasksDone = executorService.awaitTermination(5, TimeUnit.MINUTES);
        Instant endTime = Instant.now();

        if (isTasksDone) {
            System.out.println("Все задачи выполнены");
        } else {
            System.out.println("Истёк таймаут на выполнение задач");
        }

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
