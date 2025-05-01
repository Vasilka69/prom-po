package org.example.lab2;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Lab2Utils {

    private Lab2Utils() { }

    public static List<String> getFilePathsInDirectory(String directoryPath) {
        File[] files = Paths.get(directoryPath).toFile().listFiles();

        if (files == null) {
            throw new RuntimeException("Ошибка при попытке чтения файлов из директории %s: listFiles() вернул null".formatted(directoryPath));
        }

        return Arrays.stream(files)
                .map(File::getPath)
                .toList();
    }
}
