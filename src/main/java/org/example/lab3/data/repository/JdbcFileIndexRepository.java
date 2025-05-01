package org.example.lab3.data.repository;

import lombok.extern.slf4j.Slf4j;
import org.example.lab3.Lab3Utils;
import org.example.lab3.data.model.FileIndex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class JdbcFileIndexRepository implements FileIndexRepository{

    private static final String SELECT_QUERY_TEMPLATE = "SELECT fi.%s, fi.%s, fi.%s FROM %s.%s fi"
            .formatted(FileIndex.ID_COLUMN_NAME, FileIndex.FILE_PATH_COLUMN_NAME, FileIndex.COUNTED_WORDS_COLUMN_NAME,
                    FileIndex.SCHEMA, FileIndex.TABLE);
    private static final String DELETE_QUERY_TEMPLATE = "DELETE FROM %s.%s fi"
            .formatted(FileIndex.SCHEMA, FileIndex.TABLE);

    private final Connection connection;

    public JdbcFileIndexRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(FileIndex fileIndex) {
        final String insertQuery = """
                INSERT INTO %s.%s (%s, %s, %s)
                VALUES(?, ?, ?::jsonb)
                """.trim().formatted(FileIndex.SCHEMA, FileIndex.TABLE,
                FileIndex.ID_COLUMN_NAME, FileIndex.FILE_PATH_COLUMN_NAME, FileIndex.COUNTED_WORDS_COLUMN_NAME);

        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setObject(1, fileIndex.getId());
            statement.setString(2, fileIndex.getFilePath());
            statement.setString(3, Lab3Utils.objectToJsonString(fileIndex.getCountedWords()));

            int insertedRows = statement.executeUpdate();
            if (insertedRows == 0) {
                log.warn("insert() не добавил ни одной новой записи");
            }
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
    }

    @Override
    public void update(FileIndex fileIndex) {
        final String insertQuery = """
                UPDATE %s.%s fi
                SET %s = ?,
                %s = ?::jsonb
                WHERE fi.%s = ?
                """.trim().formatted(FileIndex.SCHEMA, FileIndex.TABLE, FileIndex.FILE_PATH_COLUMN_NAME, FileIndex.COUNTED_WORDS_COLUMN_NAME, FileIndex.ID_COLUMN_NAME);

        try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
            statement.setString(1, fileIndex.getFilePath());
            statement.setString(2, Lab3Utils.objectToJsonString(fileIndex.getCountedWords()));
            statement.setObject(3, fileIndex.getId());

            int insertedRows = statement.executeUpdate();
            if (insertedRows == 0) {
                log.warn("update() не обновил ни одной записи");
            }
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
    }

    @Override
    public void deleteAll() {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY_TEMPLATE)) {
            int deletedRows = statement.executeUpdate();
            if (deletedRows == 0) {
                log.info("deleteAll() не удалил ни одной записи");
            }
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        final String deleteQuery = """
                %s
                WHERE %s = ?
                """
                .formatted(DELETE_QUERY_TEMPLATE, FileIndex.ID_COLUMN_NAME);

        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            statement.setObject(1, id);
            int deletedRows = statement.executeUpdate();
            if (deletedRows == 0) {
                log.info("deleteById() не удалил ни одной записи");
            }
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
    }

    @Override
    public void deleteByIds(List<UUID> ids) {
        final String deleteQuery = """
                %s
                WHERE %s in (%s)
                """
                .formatted(DELETE_QUERY_TEMPLATE, FileIndex.ID_COLUMN_NAME, String.join(",", Collections.nCopies(ids.size(), "?")));

        try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
            for (int i = 1; i <= ids.size(); i++) {
                statement.setObject(i, ids.get(i - 1));
            }
            int deletedRows = statement.executeUpdate();
            if (deletedRows == 0) {
                log.info("deleteByIds() не удалил ни одной записи");
            }
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
    }

    @Override
    public List<FileIndex> findAll() {
        List<FileIndex> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY_TEMPLATE)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                FileIndex fileIndex = getFileIndexFromRow(resultSet);
                result.add(fileIndex);
            }
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
        return result;
    }

    @Override
    public FileIndex findById(UUID id) {
        final String findByWordQuery = """
                %s
                WHERE %s = ?
                """
                .formatted(SELECT_QUERY_TEMPLATE, FileIndex.ID_COLUMN_NAME);

        try (PreparedStatement statement = connection.prepareStatement(findByWordQuery)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            return getFileIndexFromRow(resultSet);
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
    }

    @Override
    public List<FileIndex> findByPath(String filePath) {
        String escapedFilePath = filePath
                .replace("\\", "\\\\")
                .replace("/", "\\/");
        String filePathParameter = "%" + escapedFilePath + "%";

        final String findByPathQuery = """
                %s
                WHERE %s ilike ?
                """
                .formatted(SELECT_QUERY_TEMPLATE, FileIndex.FILE_PATH_COLUMN_NAME);

        List<FileIndex> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(findByPathQuery)) {
            statement.setObject(1, filePathParameter);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                FileIndex fileIndex = getFileIndexFromRow(resultSet);
                result.add(fileIndex);
            }
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
        return result;
    }

    @Override
    public List<FileIndex> findByWord(String word) {
        String wordParameter = "%\"" + word + "\"%";

        final String findByWordQuery = """
                %s
                WHERE text(%s) ilike ?
                """
                .formatted(SELECT_QUERY_TEMPLATE, FileIndex.COUNTED_WORDS_COLUMN_NAME);

        List<FileIndex> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(findByWordQuery)) {
            statement.setObject(1, wordParameter);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                FileIndex fileIndex = getFileIndexFromRow(resultSet);
                result.add(fileIndex);
            }
        } catch (SQLException e) {
            throw defaultSqlException(e);
        }
        return result;
    }

    private FileIndex getFileIndexFromRow(ResultSet resultSet) {
        try {
            UUID id = UUID.fromString(resultSet.getString(FileIndex.ID_COLUMN_NAME));
            String filePath = resultSet.getString(FileIndex.FILE_PATH_COLUMN_NAME);
            Map<String, Long> countedWords = Lab3Utils.parseMapFromString(resultSet.getString(FileIndex.COUNTED_WORDS_COLUMN_NAME));

            return new FileIndex(id, filePath, countedWords);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при попытке получения результатов запроса: %s".formatted(e));
        }
    }

    private RuntimeException defaultSqlException(Exception e) {
        return new RuntimeException("Произошла ошибка во время выполнения запроса: %s".formatted(e));
    }
}
