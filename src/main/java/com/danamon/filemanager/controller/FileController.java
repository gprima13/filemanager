package com.danamon.filemanager.controller;

import com.danamon.filemanager.model.FileInfo;
import com.danamon.filemanager.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadPath") String uploadPath,
            Model model)
            throws IOException {
        String errorMessage = validate(uploadPath);
        if(errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
                model.addAttribute("currentPath", null);
                model.addAttribute("files", null);
                model.addAttribute( "successFile", null);
                return "index";
            }
            fileService.upload(uploadPath, file);
        return "redirect:/files?path=" +
                URLEncoder.encode(uploadPath, StandardCharsets.UTF_8) +
                "&successFile=" +
                URLEncoder.encode(
                        file.getOriginalFilename(),
                        StandardCharsets.UTF_8);
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/files";
    }

    @GetMapping("/files")
    public String files(
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String successFile,
            Model model)
            throws IOException {
            String errorMessage = validate(path);
        if(errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("currentPath", path);
            model.addAttribute("files", null);
            model.addAttribute( "successFile", successFile);
            return "index";
        }
        List<FileInfo> files = fileService.getListFile(path);
        model.addAttribute("files", files);
        model.addAttribute("currentPath", path);
        model.addAttribute("successFile", successFile);

        return "index";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> download(
            @RequestParam String path) throws IOException {

        Path file = Paths.get(path).toAbsolutePath().normalize();

        if (!Files.exists(file) || !Files.isRegularFile(file)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "File not found"
            );
        }

        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\""
                )
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    private String validate(String path) {
        return fileService.isDirectory(path);
    }

}
