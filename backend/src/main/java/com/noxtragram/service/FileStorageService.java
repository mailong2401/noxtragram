package com.noxtragram.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.noxtragram.exception.FileNotFoundException;
import com.noxtragram.exception.FileStorageException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

  private final Path fileStorageLocation;
  private final List<String> allowedImageExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
  private final List<String> allowedVideoExtensions = Arrays.asList("mp4", "mov", "avi", "mkv", "webm");
  private final long maxFileSize = 10 * 1024 * 1024; // 10MB

  public FileStorageService(@Value("${app.upload.path:./uploads}") String uploadPath) {
    this.fileStorageLocation = Paths.get(uploadPath).toAbsolutePath().normalize();

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  /**
   * Lưu file với category cụ thể (profiles, posts, etc.)
   */
  public String storeFile(MultipartFile file, String category) {
    return storeFile(file, category, null);
  }

  /**
   * Lưu file với custom filename
   */
  public String storeFile(MultipartFile file, String category, String customFileName) {
    // Validate file
    validateFile(file);

    // Tạo tên file duy nhất
    String fileName = generateFileName(file, customFileName);

    // Tạo đường dẫn đầy đủ
    Path targetLocation = createCategoryPath(category).resolve(fileName);

    try {
      // Copy file đến target location
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return fileName;
    } catch (IOException ex) {
      throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
    }
  }

  /**
   * Lưu nhiều files cùng lúc
   */
  public List<String> storeMultipleFiles(MultipartFile[] files, String category) {
    return Arrays.stream(files)
        .map(file -> storeFile(file, category))
        .toList();
  }

  /**
   * Load file dưới dạng Resource
   */
  public Resource loadFileAsResource(String fileName, String category) {
    try {
      Path filePath = createCategoryPath(category).resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());

      if (resource.exists()) {
        return resource;
      } else {
        throw new FileNotFoundException("File not found: " + fileName);
      }
    } catch (MalformedURLException ex) {
      throw new FileNotFoundException("File not found: " + fileName, ex);
    }
  }

  /**
   * Xóa file
   */
  public void deleteFile(String fileName, String category) {
    try {
      Path filePath = createCategoryPath(category).resolve(fileName).normalize();
      Files.deleteIfExists(filePath);
    } catch (IOException ex) {
      throw new FileStorageException("Could not delete file: " + fileName, ex);
    }
  }

  /**
   * Xóa nhiều files
   */
  public void deleteMultipleFiles(List<String> fileNames, String category) {
    fileNames.forEach(fileName -> deleteFile(fileName, category));
  }

  /**
   * Kiểm tra file có tồn tại không
   */
  public boolean fileExists(String fileName, String category) {
    Path filePath = createCategoryPath(category).resolve(fileName).normalize();
    return Files.exists(filePath);
  }

  /**
   * Lấy kích thước file
   */
  public long getFileSize(String fileName, String category) {
    try {
      Path filePath = createCategoryPath(category).resolve(fileName).normalize();
      return Files.size(filePath);
    } catch (IOException ex) {
      throw new FileStorageException("Could not get file size: " + fileName, ex);
    }
  }

  /**
   * Lấy MIME type của file
   */
  public String getFileContentType(String fileName, String category) {
    try {
      Path filePath = createCategoryPath(category).resolve(fileName).normalize();
      return Files.probeContentType(filePath);
    } catch (IOException ex) {
      return "application/octet-stream";
    }
  }

  /**
   * Resize ảnh (placeholder - có thể tích hợp với Thumbnailator)
   */
  public String resizeAndStoreImage(MultipartFile file, String category, int width, int height) {
    validateImageFile(file);

    // TODO: Implement image resizing logic with Thumbnailator
    // For now, just store the original file
    return storeFile(file, category);
  }

  /**
   * Tạo thumbnail từ video (placeholder)
   */
  public String generateVideoThumbnail(String videoFileName, String category) {
    // TODO: Implement video thumbnail generation with FFmpeg
    // For now, return a placeholder image path
    return "default-thumbnail.jpg";
  }

  // 🔧 PRIVATE HELPER METHODS

  /**
   * Validate file trước khi lưu
   */
  private void validateFile(MultipartFile file) {
    if (file.isEmpty()) {
      throw new FileStorageException("File is empty");
    }

    if (file.getSize() > maxFileSize) {
      throw new FileStorageException("File size exceeds the maximum limit of 10MB");
    }

    String fileExtension = getFileExtension(file.getOriginalFilename());
    if (!isAllowedExtension(fileExtension)) {
      throw new FileStorageException("File type not allowed: " + fileExtension);
    }
  }

  /**
   * Validate image file cụ thể
   */
  private void validateImageFile(MultipartFile file) {
    validateFile(file);

    String fileExtension = getFileExtension(file.getOriginalFilename());
    if (!allowedImageExtensions.contains(fileExtension.toLowerCase())) {
      throw new FileStorageException("File is not a valid image: " + fileExtension);
    }
  }

  /**
   * Validate video file cụ thể
   */
  private void validateVideoFile(MultipartFile file) {
    validateFile(file);

    String fileExtension = getFileExtension(file.getOriginalFilename());
    if (!allowedVideoExtensions.contains(fileExtension.toLowerCase())) {
      throw new FileStorageException("File is not a valid video: " + fileExtension);
    }
  }

  /**
   * Tạo tên file duy nhất
   */
  private String generateFileName(MultipartFile file, String customFileName) {
    String originalFileName = file.getOriginalFilename();
    String fileExtension = getFileExtension(originalFileName);

    if (customFileName != null && !customFileName.isEmpty()) {
      return customFileName + "." + fileExtension;
    }

    // Tạo UUID-based filename để tránh trùng lặp
    return UUID.randomUUID().toString() + "." + fileExtension;
  }

  /**
   * Lấy extension từ filename
   */
  private String getFileExtension(String fileName) {
    if (fileName == null || fileName.lastIndexOf(".") == -1) {
      throw new FileStorageException("File has no extension: " + fileName);
    }
    return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
  }

  /**
   * Kiểm tra extension có được cho phép không
   */
  private boolean isAllowedExtension(String extension) {
    return allowedImageExtensions.contains(extension) || allowedVideoExtensions.contains(extension);
  }

  /**
   * Tạo đường dẫn cho category cụ thể
   */
  private Path createCategoryPath(String category) {
    Path categoryPath = this.fileStorageLocation.resolve(category);
    try {
      Files.createDirectories(categoryPath);
      return categoryPath;
    } catch (IOException ex) {
      throw new FileStorageException("Could not create category directory: " + category, ex);
    }
  }

  /**
   * Lấy đường dẫn đầy đủ của file
   */
  public String getFullFilePath(String fileName, String category) {
    return createCategoryPath(category).resolve(fileName).toString();
  }

  /**
   * Lấy URL để truy cập file
   */
  public String getFileUrl(String fileName, String category) {
    return "http://localhost:8080/api/uploads/" + category + "/" + fileName;
  }

  /**
   * Clean up old files (cho cron job)
   */
  public void cleanupOldFiles(String category, long olderThanDays) {
    try {
      Path categoryPath = createCategoryPath(category);
      Files.walk(categoryPath)
          .filter(Files::isRegularFile)
          .filter(path -> {
            try {
              return Files.getLastModifiedTime(path).toMillis() < System.currentTimeMillis()
                  - (olderThanDays * 24 * 60 * 60 * 1000);
            } catch (IOException e) {
              return false;
            }
          })
          .forEach(path -> {
            try {
              Files.delete(path);
            } catch (IOException e) {
              // Log warning but continue
              System.err.println("Could not delete old file: " + path);
            }
          });
    } catch (IOException ex) {
      throw new FileStorageException("Could not cleanup old files", ex);
    }
  }
}
