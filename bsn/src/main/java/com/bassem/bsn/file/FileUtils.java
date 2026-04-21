package com.bassem.bsn.file;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtils {
    public static  byte[] getFileFromLocation(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            log.warn("File path is null or empty");
            return null;
        }
        Path file = Paths.get(filePath);
        if (!Files.exists(file)) {
            log.error("File not found:{} ", filePath);
            return null;
        }

        if (!Files.isReadable(file)) {
            log.error("Cannot read file:{} ", filePath);
            return null;
        }

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + filePath, e);
        }

    }
}
