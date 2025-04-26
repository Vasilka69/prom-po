package org.example.lab2;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MainSingleThread {

    private static final String DIRECTORY_TO_SCAN = "src/main/resources/text/";

    private static final Map<String, Long> countedWords = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Однопоточный режим:");

        List<String> filePaths = getFilesInDirectory(DIRECTORY_TO_SCAN);

        Instant startTime = Instant.now();
        for (String filePath : filePaths) {
            new FileWordCounterRunnable(filePath, countedWords).run();
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
