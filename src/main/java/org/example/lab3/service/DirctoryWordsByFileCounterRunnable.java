package org.example.lab3.service;

import lombok.extern.slf4j.Slf4j;
import org.example.lab1.FileWordCounter;

import java.util.Map;

@Slf4j
public class DirctoryWordsByFileCounterRunnable implements Runnable {

    private final String filePath;
    private final Map<String, Map<String, Long>> countedWords;

    public DirctoryWordsByFileCounterRunnable(String filePath, Map<String, Map<String, Long>> mapToStoreCount) {
        this.filePath = filePath;
        this.countedWords = mapToStoreCount;
    }

    @Override
    public void run() {
        log.info("Начало работы");

        Map<String, Long> countedWordsFromFile = FileWordCounter.countWordsFromFile(filePath);

        countedWords.put(filePath, countedWordsFromFile);

        log.info("Работа закончена");
    }
}
