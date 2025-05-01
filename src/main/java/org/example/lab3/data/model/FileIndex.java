package org.example.lab3.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FileIndex {
    public static final String SCHEMA = "prom_po";
    public static final String TABLE = "file_index";

    public static final String ID_COLUMN_NAME = "id";
    public static final String FILE_PATH_COLUMN_NAME = "file_path";
    public static final String COUNTED_WORDS_COLUMN_NAME = "counted_words";

    private UUID id;
    private String filePath;
    private Map<String, Long> countedWords;
}
