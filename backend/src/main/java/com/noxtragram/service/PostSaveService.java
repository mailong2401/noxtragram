package com.noxtragram.service;

import com.noxtragram.model.dto.request.PostSaveRequestDTO;
import com.noxtragram.model.dto.response.FolderResponseDTO;
import com.noxtragram.model.dto.response.PostSaveResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostSaveService {

  void savePost(Long postId, Long userId, PostSaveRequestDTO request);

  void savePost(Long postId, Long userId);

  void unsavePost(Long postId, Long userId);

  void moveToFolder(Long postId, Long userId, String folderName);

  boolean isPostSavedByUser(Long postId, Long userId);

  Page<PostSaveResponseDTO> getSavedPosts(Long userId, Pageable pageable);

  Page<PostSaveResponseDTO> getSavedPostsByFolder(Long userId, String folderName, Pageable pageable);

  List<FolderResponseDTO> getUserFolders(Long userId);

  Long getSavedPostCount(Long userId);

  Long getSavedPostCountByFolder(Long userId, String folderName);
}
