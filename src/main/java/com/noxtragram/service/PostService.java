package com.noxtragram.service;

import com.noxtragram.model.dto.request.PostRequestDTO;
import com.noxtragram.model.dto.response.PostResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

  PostResponseDTO createPost(PostRequestDTO postRequest, List<MultipartFile> files, Long userId);

  PostResponseDTO getPostById(Long postId, Long currentUserId);

  Page<PostResponseDTO> getPostsByUserId(Long userId, Long currentUserId, Pageable pageable);

  Page<PostResponseDTO> getFeedPosts(Long currentUserId, Pageable pageable);

  Page<PostResponseDTO> getPostsByHashtag(String hashtag, Long currentUserId, Pageable pageable);

  Page<PostResponseDTO> getSavedPosts(Long userId, Long currentUserId, Pageable pageable);

  Page<PostResponseDTO> getPopularPosts(Long currentUserId, Pageable pageable);

  PostResponseDTO updatePost(Long postId, PostRequestDTO postRequest, Long userId);

  void deletePost(Long postId, Long userId);

  void likePost(Long postId, Long userId);

  void unlikePost(Long postId, Long userId);

  void savePost(Long postId, Long userId);

  void unsavePost(Long postId, Long userId);

  boolean isPostLikedByUser(Long postId, Long userId);

  boolean isPostSavedByUser(Long postId, Long userId);
}
