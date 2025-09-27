package com.noxtragram.service.implementation;

import com.noxtragram.model.dto.request.PostRequestDTO;
import com.noxtragram.model.dto.response.PostResponseDTO;
import com.noxtragram.model.dto.Summary.UserSummaryDTO;
import com.noxtragram.model.entity.*;
import com.noxtragram.repository.*;
import com.noxtragram.service.FileStorageService;
import com.noxtragram.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final HashtagRepository hashtagRepository;
  private final LikeRepository likeRepository;
  private final PostSaveRepository postSaveRepository;
  private final FileStorageService fileStorageService;

  public PostServiceImpl(PostRepository postRepository, UserRepository userRepository,
      HashtagRepository hashtagRepository, LikeRepository likeRepository,
      PostSaveRepository postSaveRepository, FileStorageService fileStorageService) {
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.hashtagRepository = hashtagRepository;
    this.likeRepository = likeRepository;
    this.postSaveRepository = postSaveRepository;
    this.fileStorageService = fileStorageService;
  }

  @Override
  public PostResponseDTO createPost(PostRequestDTO postRequest, List<MultipartFile> files, Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Post post = new Post();
    post.setCaption(postRequest.getCaption());
    post.setUser(user);
    post.setLocation(postRequest.getLocation());

    // Xử lý upload files
    if (files != null && !files.isEmpty()) {
      List<String> imageUrls = new ArrayList<>();

      for (MultipartFile file : files) {
        try {
          String fileName = fileStorageService.storeFile(file, "posts");
          String fileUrl = fileStorageService.getFileUrl(fileName, "posts");
          imageUrls.add(fileUrl);
        } catch (Exception e) {
          throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
      }

      // Sửa lỗi: Luôn set imageUrls, chỉ set imageUrl nếu có 1 ảnh
      post.setImageUrls(imageUrls);
      if (imageUrls.size() == 1) {
        post.setImageUrl(imageUrls.get(0));
      } else {
        post.setImageUrl(null); // Clear imageUrl nếu có nhiều ảnh
      }
    }

    // Xử lý hashtags
    if (postRequest.getHashtags() != null && !postRequest.getHashtags().isEmpty()) {
      List<Hashtag> hashtags = processHashtags(postRequest.getHashtags());
      post.setHashtags(hashtags);
    }

    Post savedPost = postRepository.save(post);
    return convertToDTO(savedPost, userId);
  }

  @Override
  @Transactional(readOnly = true)
  public PostResponseDTO getPostById(Long postId, Long currentUserId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    return convertToDTO(post, currentUserId);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostResponseDTO> getPostsByUserId(Long userId, Long currentUserId, Pageable pageable) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Page<Post> posts = postRepository.findByUserAndIsDeletedFalseOrderByCreatedAtDesc(user, pageable);
    return posts.map(post -> convertToDTO(post, currentUserId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostResponseDTO> getFeedPosts(Long currentUserId, Pageable pageable) {
    // Simple feed implementation - show posts from all users
    // Can be enhanced to show posts from followed users only
    Page<Post> posts = postRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);
    return posts.map(post -> convertToDTO(post, currentUserId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostResponseDTO> getPostsByHashtag(String hashtag, Long currentUserId, Pageable pageable) {
    String cleanedHashtag = hashtag.startsWith("#") ? hashtag.substring(1) : hashtag;
    Page<Post> posts = postRepository.findByHashtagName(cleanedHashtag, pageable);
    return posts.map(post -> convertToDTO(post, currentUserId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostResponseDTO> getSavedPosts(Long userId, Long currentUserId, Pageable pageable) {
    Page<Post> posts = postRepository.findSavedPostsByUserId(userId, pageable);
    return posts.map(post -> convertToDTO(post, currentUserId));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostResponseDTO> getPopularPosts(Long currentUserId, Pageable pageable) {
    Page<Post> posts = postRepository.findPopularPosts(pageable);
    return posts.map(post -> convertToDTO(post, currentUserId));
  }

  @Override
  public PostResponseDTO updatePost(Long postId, PostRequestDTO postRequest, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    if (!post.getUser().getId().equals(userId)) {
      throw new SecurityException("You are not authorized to update this post");
    }

    post.setCaption(postRequest.getCaption());
    post.setLocation(postRequest.getLocation());

    // Cập nhật hashtags
    if (postRequest.getHashtags() != null) {
      List<Hashtag> hashtags = processHashtags(postRequest.getHashtags());
      post.setHashtags(hashtags);
    }

    Post updatedPost = postRepository.save(post);
    return convertToDTO(updatedPost, userId);
  }

  @Override
  public void deletePost(Long postId, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));

    if (!post.getUser().getId().equals(userId)) {
      throw new SecurityException("You are not authorized to delete this post");
    }

    post.setIsDeleted(true);
    postRepository.save(post);
  }

  @Override
  public void likePost(Long postId, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    if (likeRepository.existsByPostAndUser(post, user)) {
      throw new IllegalStateException("Post already liked by user");
    }

    Like like = new Like();
    like.setPost(post);
    like.setUser(user);
    likeRepository.save(like);

    post.setLikeCount(post.getLikeCount() + 1);
    postRepository.save(post);
  }

  @Override
  public void unlikePost(Long postId, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Like like = likeRepository.findByPostAndUser(post, user)
        .orElseThrow(() -> new EntityNotFoundException("Like not found"));

    likeRepository.delete(like);

    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
    postRepository.save(post);
  }

  @Override
  public void savePost(Long postId, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    if (postSaveRepository.existsByPostAndUser(post, user)) {
      throw new IllegalStateException("Post already saved by user");
    }

    PostSave postSave = new PostSave();
    postSave.setPost(post);
    postSave.setUser(user);
    postSaveRepository.save(postSave);
  }

  @Override
  public void unsavePost(Long postId, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    PostSave postSave = postSaveRepository.findByPostAndUser(post, user)
        .orElseThrow(() -> new EntityNotFoundException("Post save not found"));

    postSaveRepository.delete(postSave);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isPostLikedByUser(Long postId, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return likeRepository.existsByPostAndUser(post, user);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isPostSavedByUser(Long postId, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return postSaveRepository.existsByPostAndUser(post, user);
  }

  private List<Hashtag> processHashtags(List<String> hashtagNames) {
    return hashtagNames.stream()
        .map(name -> {
          String cleanedName = name.startsWith("#") ? name.substring(1) : name;
          return hashtagRepository.findByName(cleanedName)
              .orElseGet(() -> {
                Hashtag newHashtag = new Hashtag();
                newHashtag.setName(cleanedName);
                return hashtagRepository.save(newHashtag);
              });
        })
        .collect(Collectors.toList());
  }

  private PostResponseDTO convertToDTO(Post post, Long currentUserId) {
    UserSummaryDTO userSummary = new UserSummaryDTO(
        post.getUser().getId(),
        post.getUser().getUsername(),
        post.getUser().getProfilePicture(),
        post.getUser().getFullName());

    List<String> hashtagNames = post.getHashtags().stream()
        .map(Hashtag::getName)
        .collect(Collectors.toList());

    PostResponseDTO dto = new PostResponseDTO();
    dto.setId(post.getId());
    dto.setCaption(post.getCaption());
    dto.setImageUrl(post.getImageUrl());
    dto.setImageUrls(post.getImageUrls() != null ? post.getImageUrls() : new ArrayList<>());
    dto.setVideoUrl(post.getVideoUrl());
    dto.setLocation(post.getLocation());
    dto.setLikeCount(post.getLikeCount());
    dto.setCommentCount(post.getCommentCount());
    dto.setShareCount(post.getShareCount());
    dto.setCreatedAt(post.getCreatedAt());
    dto.setUpdatedAt(post.getUpdatedAt());
    dto.setUser(userSummary);
    dto.setHashtags(hashtagNames);

    // Set interaction flags
    if (currentUserId != null) {
      try {
        dto.setIsLikedByCurrentUser(isPostLikedByUser(post.getId(), currentUserId));
        dto.setIsSavedByCurrentUser(isPostSavedByUser(post.getId(), currentUserId));
      } catch (Exception e) {
        dto.setIsLikedByCurrentUser(false);
        dto.setIsSavedByCurrentUser(false);
      }
    } else {
      dto.setIsLikedByCurrentUser(false);
      dto.setIsSavedByCurrentUser(false);
    }

    return dto;
  }
}
