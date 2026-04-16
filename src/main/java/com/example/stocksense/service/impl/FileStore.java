package com.example.stocksense.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStore {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String save(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        try {
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String savedName = UUID.randomUUID() + ext;

            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            Path target = dir.resolve(savedName);
            file.transferTo(target.toFile());

            return savedName;
        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage(), e);
        }
    }
}
