package com.noxtragram.service.implementation;

import com.noxtragram.model.dto.request.PostSaveRequestDTO;
import com.noxtragram.model.dto.response.FolderResponseDTO;
import com.noxtragram.model.dto.response.PostResponseDTO;
import com.noxtragram.model.dto.response.PostSaveResponseDTO;
import com.noxtragram.model.entity.Post;
import com.noxtragram.model.entity.PostSave;
import com.noxtragram.model.entity.User;
import com.noxtragram.repository.PostRepository;
import com.noxtragram.repository.PostSaveRepository;
import com.noxtragram.repository.UserRepository;
import com.noxtragram.service.PostSaveService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostSaveServiceImpl implements PostSaveService {

  private final PostSaveRepository postSaveRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PostServiceImpl postService;

  public PostSaveServiceImpl(PostSaveRepository postSaveRepository,
      PostRepository postRepository,
      UserRepository userRepository,
      PostServiceImpl postService) {
    this.postSaveRepository = postSaveRepository;
    this.postRepository = postRepository;
    this.userRepository = userRepository;
    this.postService = postService;
  }

  @Override
  public void savePost(Long postId, Long userId, PostSaveRequestDTO request) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    if (postSaveRepository.existsByPostAndUser(post, user)) {
      throw new IllegalStateException("Post already saved by user");
    }

    String folderName = (request.getFolderName() != null && !request.getFolderName().trim().isEmpty())
        ? request.getFolderName().trim()
        : "All Posts";

    PostSave postSave = new PostSave(user, post, folderName);
    postSaveRepository.save(postSave);
  }

  @Override
  public void savePost(Long postId, Long userId) {
    PostSaveRequestDTO request = new PostSaveRequestDTO();
    request.setFolderName("All Posts");
    savePost(postId, userId, request);
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
  public void moveToFolder(Long postId, Long userId, String folderName) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    PostSave postSave = postSaveRepository.findByPostAndUser(post, user)
        .orElseThrow(() -> new EntityNotFoundException("Post save not found"));

    String newFolderName = (folderName != null && !folderName.trim().isEmpty())
        ? folderName.trim()
        : "All Posts";

    postSave.setFolderName(newFolderName);
    postSaveRepository.save(postSave);
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

  @Override
  @Transactional(readOnly = true)
  public Page<PostSaveResponseDTO> getSavedPosts(Long userId, Pageable pageable) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Page<PostSave> postSaves = postSaveRepository.findByUserOrderBySavedAtDesc(user, pageable);
    return postSaves.map(this::convertToDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostSaveResponseDTO> getSavedPostsByFolder(Long userId, String folderName, Pageable pageable) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Page<PostSave> postSaves = postSaveRepository.findByUserAndFolderNameOrderBySavedAtDesc(user, folderName, pageable);
    return postSaves.map(this::convertToDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public List<FolderResponseDTO> getUserFolders(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    List<Object[]> folderCounts = postSaveRepository.countPostsByFolder(user);

    return folderCounts.stream()
        .map(result -> new FolderResponseDTO(
            (String) result[0],
            (Long) result[1]))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Long getSavedPostCount(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    return postSaveRepository.countByUser(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Long getSavedPostCountByFolder(Long userId, String folderName) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
    return postSaveRepository.countByUserAndFolderName(user, folderName);
  }

  private PostSaveResponseDTO convertToDTO(PostSave postSave) {
    PostSaveResponseDTO dto = new PostSaveResponseDTO();
    dto.setId(postSave.getId());
    dto.setUserId(postSave.getUser().getId());
    dto.setUsername(postSave.getUser().getUsername());
    dto.setPostId(postSave.getPost().getId());
    dto.setFolderName(postSave.getFolderName());
    dto.setSavedAt(postSave.getSavedAt());

    // Thêm thông tin chi tiết bài viết
    try {
      PostResponseDTO postDetails = postService.getPostById(postSave.getPost().getId(), postSave.getUser().getId());
      dto.setPostDetails(postDetails);
    } catch (Exception e) {
      // Nếu bài viết đã bị xóa, bỏ qua
    }

    return dto;
  }
}
