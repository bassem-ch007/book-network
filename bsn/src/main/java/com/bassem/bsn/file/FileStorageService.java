package com.bassem.bsn.file;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final UserDetailsService userDetailsService;
    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath;
    public String saveFile(@Nonnull MultipartFile sourceFile, @Nonnull Integer userid){
        final String fileUploadSubPath = "users"+ separator + userid;
        return uploadFile(sourceFile,fileUploadSubPath);
    }

    private String uploadFile(@Nonnull MultipartFile sourceFile,@Nonnull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if (!targetFolder.exists()) {
            boolean createdFolder = targetFolder.mkdirs();
            if (!createdFolder) {
                log.warn("Failed to create directory: ");
                return null;
            }
        }
        final String fileExtension=getFileExtension(sourceFile.getOriginalFilename());
        // ./uploads/users/1//1235565596223123.jpg
        final String targetFilePath = finalUploadPath + separator +System.currentTimeMillis()+ "." +fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath,sourceFile.getBytes());
            log.info("File uploaded successfully");
            return targetFilePath;
        }catch (IOException e){
            log.error("Failed to save file {}",sourceFile.getOriginalFilename(),e);
        }
        return null;
    }

    private String getFileExtension(String filename) {
        if (filename==null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

}
