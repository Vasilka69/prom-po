package org.example.lab3.data.repository;

import org.example.lab3.data.model.FileIndex;

import java.util.List;
import java.util.UUID;

public interface FileIndexRepository {
    void insert(FileIndex fileIndex);
    void update(FileIndex fileIndex);
    void deleteAll();
    void deleteById(UUID id);
    void deleteByIds(List<UUID> ids);

    List<FileIndex> findAll();
    FileIndex findById(UUID id);
    List<FileIndex> findByPath(String filePath);
    List<FileIndex> findByWord(String word);
}
