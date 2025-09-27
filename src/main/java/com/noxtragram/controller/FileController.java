package com.noxtragram.controller;

import com.noxtragram.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/uploads")
public class FileController {

  @Autowired
  private FileStorageService fileStorageService;

  @GetMapping("/{category}/{fileName:.+}")
  public ResponseEntity<Resource> serveFile(
      @PathVariable String category,
      @PathVariable String fileName,
      HttpServletRequest request) {

    try {
      // Load file as resource
      Resource resource = fileStorageService.loadFileAsResource(fileName, category);

      // Determine content type
      String contentType = getContentType(request, resource);

      return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(contentType))
          .header(HttpHeaders.CONTENT_DISPOSITION,
              "inline; filename=\"" + resource.getFilename() + "\"")
          .body(resource);

    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/posts/{fileName:.+}")
  public ResponseEntity<Resource> servePostFile(
      @PathVariable String fileName,
      HttpServletRequest request) {
    return serveFile("posts", fileName, request);
  }

  @GetMapping("/profiles/{fileName:.+}")
  public ResponseEntity<Resource> serveProfileFile(
      @PathVariable String fileName,
      HttpServletRequest request) {
    return serveFile("profiles", fileName, request);
  }

  @GetMapping("/{fileName:.+}")
  public ResponseEntity<Resource> serveGenericFile(
      @PathVariable String fileName,
      HttpServletRequest request) {
    return serveFile("general", fileName, request);
  }

  private String getContentType(HttpServletRequest request, Resource resource) {
    try {
      String contentType = request.getServletContext()
          .getMimeType(resource.getFile().getAbsolutePath());

      if (contentType == null) {
        // Fallback to common types based on file extension
        String filename = resource.getFilename();
        if (filename != null) {
          if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            contentType = "image/jpeg";
          } else if (filename.endsWith(".png")) {
            contentType = "image/png";
          } else if (filename.endsWith(".gif")) {
            contentType = "image/gif";
          } else if (filename.endsWith(".webp")) {
            contentType = "image/webp";
          } else if (filename.endsWith(".mp4")) {
            contentType = "video/mp4";
          } else if (filename.endsWith(".mov")) {
            contentType = "video/quicktime";
          } else if (filename.endsWith(".avi")) {
            contentType = "video/x-msvideo";
          } else {
            contentType = "application/octet-stream";
          }
        } else {
          contentType = "application/octet-stream";
        }
      }

      return contentType;
    } catch (IOException ex) {
      return "application/octet-stream";
    }
  }
}
