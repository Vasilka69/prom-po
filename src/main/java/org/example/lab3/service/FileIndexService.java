package org.example.lab3.service;

import org.example.lab3.data.model.FileIndex;

import java.util.List;
import java.util.UUID;

public interface FileIndexService {
    List<FileIndex> getDirectoryFileIndexes(String directory);
    String getFileContentById(UUID id);
    String getFilenameById(UUID id);
}
