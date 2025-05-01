package org.example.lab3.service;

import lombok.extern.slf4j.Slf4j;
import org.example.lab1.FileWordCounter;
import org.example.lab2.Lab2Utils;
import org.example.lab3.data.model.FileIndex;
import org.example.lab3.data.repository.FileIndexRepository;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FileIndexServiceImpl implements FileIndexService {

    private final FileIndexRepository fileIndexRepository;

    public FileIndexServiceImpl(FileIndexRepository fileIndexRepository) {
        this.fileIndexRepository = fileIndexRepository;
    }

    @Override
    public List<FileIndex> getDirectoryFileIndexes(String directory) {
        return countWordsInDirectory(directory).entrySet().stream()
                .map(entry -> new FileIndex(UUID.randomUUID(), entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public String getFileContentById(UUID id) {
        FileIndex fileIndex = getFileIndexById(id);
        return FileWordCounter.getFileContent(fileIndex.getFilePath());
    }

    @Override
    public String getFilenameById(UUID id) {
        FileIndex fileIndex = getFileIndexById(id);
        return Paths.get(fileIndex.getFilePath()).getFileName().toString();
    }

    private Map<String, Map<String, Long>> countWordsInDirectory(String directory) {
        List<String> filePaths = Lab2Utils.getFilePathsInDirectory(directory);

        ExecutorService executorService = Executors.newFixedThreadPool(filePaths.size());

        Map<String, Map<String, Long>> countedWords = new ConcurrentHashMap<>();
        for (String filePath : filePaths) {
            executorService.submit(new DirctoryWordsByFileCounterRunnable(filePath, countedWords));
        }
        executorService.shutdown();

        boolean isTasksDone;
        try {
            isTasksDone = executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        if (isTasksDone) {
            log.info("Все задачи выполнены");
        } else {
            log.error("Истёк таймаут на выполнение задач");
        }

        return countedWords;
    }

    private FileIndex getFileIndexById(UUID id) {
        FileIndex fileIndex = fileIndexRepository.findById(id);
        if (fileIndex == null) {
            throw new RuntimeException("Не найден файл по указанному id (%s)".formatted(id));
        }
        return fileIndex;
    }
}
