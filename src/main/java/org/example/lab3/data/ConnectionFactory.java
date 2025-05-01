package org.example.lab3.data;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionFactory {

    public static Connection createConnection(String dbUrl, String dbUser, String dbPassword) {
        Properties props = new Properties();
        props.setProperty("user", dbUser);
        props.setProperty("password", dbPassword);

        try {
            return DriverManager.getConnection(dbUrl, props);
        } catch (SQLException e) {
            throw new RuntimeException("Произошла ошибка при попытке создать соединение: %s".formatted(e), e);
        }
    }

}
