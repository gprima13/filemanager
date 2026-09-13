package com.danamon.filemanager.service;

import com.danamon.filemanager.model.FileInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
public class FileService {
    public List<FileInfo> getListFile(String path) throws IOException {
        List<FileInfo> files = new ArrayList<>();
        log.info("Getting files from path: {}" , path);
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
                                log.error(e);
                                throw new RuntimeException(e);
                            }
                        })
                        .collect(Collectors.toList());
            }
        }
        return files;
    }

    public void upload (String uploadPath, MultipartFile file) throws IOException {
        Path uploadDirectory = Paths.get(uploadPath);
        Files.createDirectories(uploadDirectory);
        log.info("Uploading {} to path: {}" , file.getOriginalFilename(), uploadDirectory.toString());
        Path targetFile = uploadDirectory.resolve(
                file.getOriginalFilename());
        Files.copy(
                file.getInputStream(),
                targetFile,
                StandardCopyOption.REPLACE_EXISTING);
    }

    public String isDirectory(String path) {
        if (path != null && !path.isBlank()) {
            Path directory = Paths.get(path);
            if(!Files.exists(directory)) {
                return "The specified path does not exist";
            }
            if(!Files.isDirectory(directory)){
                return "The specified path is not directory";
            }
        }
        return null;
    }

}
