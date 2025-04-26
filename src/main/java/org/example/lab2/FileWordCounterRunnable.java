package org.example.lab2;

import org.example.lab1.FileWordCounter;

import java.util.Map;

public class FileWordCounterRunnable implements Runnable {

    private final String filePath;
    private final Map<String, Long> countedWords;

    public FileWordCounterRunnable(String filePath, Map<String, Long> mapToStoreCount) {
        this.filePath = filePath;
        this.countedWords = mapToStoreCount;
    }

    @Override
    public void run() {
        System.out.println("Начал работу %s".formatted(Thread.currentThread().getName()));

        Map<String, Long> countedWordsFromFile = FileWordCounter.countWordsFromFile(filePath);

        countedWordsFromFile.forEach((word, count) -> countedWords.computeIfAbsent(word, key -> 0L));
        countedWordsFromFile.forEach((word, count) -> countedWords.computeIfPresent(word, (key, oldValue) -> oldValue + count));

        System.out.println("Закончил работу %s".formatted(Thread.currentThread().getName()));
    }
}
