package org.example.lab4.controller;

import org.example.lab3.data.repository.FileIndexRepository;
import org.example.lab3.service.FileIndexService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FileIndexController implements Controller {

    private static final String ID_REQUEST_PARAMETER = "id";
    private static final String WORD_REQUEST_PARAMETER = "word";

    private final FileIndexService fileIndexService;
    private final FileIndexRepository fileIndexRepository;

    public FileIndexController(FileIndexService fileIndexService, FileIndexRepository fileIndexRepository) {
        this.fileIndexService = fileIndexService;
        this.fileIndexRepository = fileIndexRepository;
    }

    @Mapping(httpMethod = HttpMethod.GET, urlPattern = "/file-index")
    public Object findAll(HttpServletRequest request, HttpServletResponse response) {
        return fileIndexRepository.findAll();
    }

    @Mapping(httpMethod = HttpMethod.GET, urlPattern = "/file-index", requiredParameters = {ID_REQUEST_PARAMETER})
    public Object findById(HttpServletRequest request, HttpServletResponse response) {
        UUID id = UUID.fromString(request.getParameter(ID_REQUEST_PARAMETER));
        return fileIndexRepository.findById(id);
    }

    @Mapping(httpMethod = HttpMethod.GET, urlPattern = "/file-index/content", requiredParameters = {ID_REQUEST_PARAMETER})
    public Object getFileContentById(HttpServletRequest request, HttpServletResponse response) {
        UUID id = UUID.fromString(request.getParameter(ID_REQUEST_PARAMETER));
        return fileIndexService.getFileContentById(id);
    }

    @Mapping(httpMethod = HttpMethod.GET, urlPattern = "/file-index", requiredParameters = {WORD_REQUEST_PARAMETER})
    public Object findByWord(HttpServletRequest request, HttpServletResponse response) {
        String word = request.getParameter(WORD_REQUEST_PARAMETER);
        return fileIndexRepository.findByWord(word);
    }

    @Mapping(httpMethod = HttpMethod.GET, urlPattern = "/file-index/download", requiredParameters = {ID_REQUEST_PARAMETER})
    public Object downloadFileById(HttpServletRequest request, HttpServletResponse response) {
        UUID id = UUID.fromString(request.getParameter(ID_REQUEST_PARAMETER));

        String urlCompatibleFilename = URLEncoder
                .encode(fileIndexService.getFilenameById(id), StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setHeader("Content-Disposition", "attachment; filename=%s".formatted(urlCompatibleFilename));
        response.setContentType("application/octet-stream");

        return fileIndexService.getFileContentById(id);
    }
}
