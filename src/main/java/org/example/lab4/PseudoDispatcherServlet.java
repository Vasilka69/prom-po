package org.example.lab4;


import org.example.lab3.Lab3Utils;
import org.example.lab4.controller.Controller;
import org.example.lab4.controller.FileIndexController;
import org.example.lab4.controller.Mapping;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PseudoDispatcherServlet extends HttpServlet {

    private final transient List<Controller> controllers;

    public PseudoDispatcherServlet(FileIndexController fileIndexController) {
        this.controllers = List.of(fileIndexController);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map.Entry<Method, Controller> handler = findHandlerByRequest(request);

        if (handler == null || handler.getKey() == null || handler.getValue() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Method method = handler.getKey();
        Controller controllerInstance = handler.getValue();
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            Object result = method.invoke(controllerInstance, request, response);

            if (result != null) {
                if (response.getContentType() == null) {
                    response.setContentType("application/json");
                    response.getWriter().write(Lab3Utils.objectToJsonString(result));
                } else {
                    response.getWriter().write(result.toString());
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            handleControllerException(e, response);
        }
    }

    private Map.Entry<Method, Controller> findHandlerByRequest(HttpServletRequest request) {
        for (Controller controller : controllers) {
            Class<? extends Controller> controllerClass = controller.getClass();
            for (Method method : controllerClass.getMethods()) {
                Mapping mappingAnnotation = method.getAnnotation(Mapping.class);
                if (isMethodSuitable(request, mappingAnnotation)) {
                    return new AbstractMap.SimpleImmutableEntry<>(method, controller);
                }
            }
        }
        return null;
    }

    private boolean isMethodSuitable(HttpServletRequest request, Mapping mappingAnnotation) {
        String requestPathInfo = request.getPathInfo() != null ? request.getPathInfo() : "/";
        Set<String> requestParameterNames = new HashSet<>(Collections.list(request.getParameterNames()));

        return mappingAnnotation != null
                && mappingAnnotation.httpMethod().getStringValue().equals(request.getMethod())
                && requestPathInfo.equals(mappingAnnotation.urlPattern())
                && requestParameterNames.equals(Set.of(mappingAnnotation.requiredParameters()));
    }

    private void handleControllerException(Exception exception, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain");
        try {
            Throwable exceptionToLog = exception.getCause() != null ? exception.getCause() : exception;
            response.getWriter().write("Внутренняя ошибка сервера:%n%s".formatted(exceptionToLog.getMessage()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
