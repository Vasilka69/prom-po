package org.example.lab3;

import org.example.lab3.data.ConnectionFactory;
import org.example.lab3.data.model.FileIndex;
import org.example.lab3.data.repository.FileIndexRepository;
import org.example.lab3.data.repository.JdbcFileIndexRepository;
import org.example.lab3.service.FileIndexService;
import org.example.lab3.service.FileIndexServiceImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class Lab3Main {

    private static final String DIRECTORY_TO_SCAN = "src/main/resources/text/";

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/prom_po";
    private static final String DB_USER = "<USER>";
    private static final String DB_PASSWORD = "<PASSWORD>";


    public static void main(String[] args) throws SQLException {
        try (Connection connection = createConnectionWithDefaultCredentials()) {
            FileIndexRepository fileIndexRepository = new JdbcFileIndexRepository(connection);
            FileIndexService fileIndexService = new FileIndexServiceImpl(fileIndexRepository);

            List<FileIndex> fileIndexes = fileIndexService.getDirectoryFileIndexes(DIRECTORY_TO_SCAN);
            for (FileIndex fileIndex : fileIndexes) {
                fileIndexRepository.insert(fileIndex);
            }

            List<FileIndex> findAll = fileIndexRepository.findAll();
            System.out.println("findAll[%d]: %s".formatted(findAll.size(), findAll));

            List<FileIndex> findByPath = fileIndexRepository.findByPath("text\\1");
            System.out.println("findByPath[%d]: %s".formatted(findByPath.size(), findByPath));

            List<FileIndex> findByWord = fileIndexRepository.findByWord("лесу");
            System.out.println("findByWord[%d]: %s".formatted(findByWord.size(), findByWord));

            fileIndexRepository.deleteByIds(fileIndexes.stream()
                    .map(FileIndex::getId)
                    .limit(fileIndexes.size() / 2)
                    .toList());
            List<FileIndex> allAfterDeletionByIds = fileIndexRepository.findAll();
            System.out.println("allAfterDeletionByIds[%d]: %s".formatted(allAfterDeletionByIds.size(), allAfterDeletionByIds));
        }
    }

    public static Connection createConnectionWithDefaultCredentials() {
        return ConnectionFactory.createConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
