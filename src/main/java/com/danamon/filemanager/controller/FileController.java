package com.danamon.filemanager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class FileController {

    @PostMapping("/upload")
    public String upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadPath") String uploadPath)
            throws IOException {

        Path uploadDirectory = Paths.get(uploadPath);

        Files.createDirectories(uploadDirectory);

        Path targetFile = uploadDirectory.resolve(
                file.getOriginalFilename());

        Files.copy(
                file.getInputStream(),
                targetFile,
                StandardCopyOption.REPLACE_EXISTING);
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

    List<FileInfo> files = new ArrayList<>();

    if (path != null && !path.isBlank()) {

        try (Stream<Path> stream = Files.list(Paths.get(path))) {

            files = stream
                    .map(p -> {
                        try {
                            return new FileInfo(
                                    p.getFileName().toString(),
                                    Files.isDirectory(p),
                                    Files.isDirectory(p) ? 0 : Files.size(p),
                                    Files.getLastModifiedTime(p).toString()
                            );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        }
    }

    model.addAttribute("files", files);
    model.addAttribute("currentPath", path);
    model.addAttribute("successFile", successFile);

    return "index";
}


    public class FileInfo {

        private String name;
        private boolean directory;
        private long size;
        private String modifiedDate;

        public FileInfo(
                String name,
                boolean directory,
                long size,
                String modifiedDate) {

            this.name = name;
            this.directory = directory;
            this.size = size;
            this.modifiedDate = modifiedDate;
        }

        public String getName() {
            return name;
        }

        public boolean isDirectory() {
            return directory;
        }

        public long getSize() {
            return size;
        }

        public String getModifiedDate() {
            return modifiedDate;
        }
    }

}
