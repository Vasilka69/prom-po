package org.example.lab4;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.example.lab3.Lab3Main;
import org.example.lab3.data.repository.JdbcFileIndexRepository;
import org.example.lab3.service.FileIndexService;
import org.example.lab3.service.FileIndexServiceImpl;
import org.example.lab4.controller.FileIndexController;

import jakarta.servlet.http.HttpServlet;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class Lab4Main {

    private static final String WEBAPP = "src/main/webapp";
    private static final String CONTEXT_PATH = "";

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();

        tomcat.setBaseDir(WEBAPP);
        tomcat.setPort(8080);

        String appBase = new File(WEBAPP).getAbsolutePath();
        Context context = tomcat.addContext(CONTEXT_PATH, appBase);

        tomcat.addServlet(CONTEXT_PATH, "default", new DefaultServlet());
        context.addServletMappingDecoded("/", "default");
        context.addWelcomeFile("index.html");

        try (Connection connection = Lab3Main.createConnectionWithDefaultCredentials()) {
            FileIndexController fileIndexController = buildFileIndexController(connection);
            HttpServlet servlet = new PseudoDispatcherServlet(fileIndexController);
            String servletName = servlet.getClass().getSimpleName();
            String urlPattern = "/api/*";

            tomcat.addServlet(CONTEXT_PATH, servletName, servlet);
            context.addServletMappingDecoded(urlPattern, servletName);

            tomcat.getConnector();
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private static FileIndexController buildFileIndexController(Connection connection) {
        JdbcFileIndexRepository fileIndexRepository = new JdbcFileIndexRepository(connection);
        FileIndexService fileService = new FileIndexServiceImpl(fileIndexRepository);
        return new FileIndexController(fileService, fileIndexRepository);
    }
}
