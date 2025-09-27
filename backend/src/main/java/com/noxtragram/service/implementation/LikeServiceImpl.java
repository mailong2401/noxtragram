package com.noxtragram.service.implementation;

import com.noxtragram.model.dto.response.LikeResponseDTO;
import com.noxtragram.model.entity.Like;
import com.noxtragram.model.entity.Post;
import com.noxtragram.model.entity.User;
import com.noxtragram.repository.LikeRepository;
import com.noxtragram.repository.PostRepository;
import com.noxtragram.repository.UserRepository;
import com.noxtragram.service.LikeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LikeServiceImpl implements LikeService {

  private final LikeRepository likeRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  public LikeServiceImpl(LikeRepository likeRepository,
      PostRepository postRepository,
      UserRepository userRepository) {
    this.likeRepository = likeRepository;
    this.postRepository = postRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void likePost(Long postId, Long userId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    // Kiểm tra đã like chưa
    if (likeRepository.existsByPostAndUser(post, user)) {
      throw new IllegalStateException("Post already liked by user");
    }

    Like like = new Like(user, post);
    likeRepository.save(like);

    // Cập nhật like count trong post
    post.incrementLikeCount();
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

    // Cập nhật like count trong post
    post.decrementLikeCount();
    postRepository.save(post);
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
  public long getLikeCount(Long postId) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    return likeRepository.countByPost(post);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LikeResponseDTO> getLikesByPost(Long postId, Pageable pageable) {
    Post post = postRepository.findByIdAndIsDeletedFalse(postId)
        .orElseThrow(() -> new EntityNotFoundException("Post not found"));

    Page<Like> likes = likeRepository.findByPostOrderByCreatedAtDesc(post, pageable);
    return likes.map(this::convertToDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LikeResponseDTO> getLikesByUser(Long userId, Pageable pageable) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    Page<Like> likes = likeRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    return likes.map(this::convertToDTO);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Long> getUserIdsWhoLikedPost(Long postId) {
    return likeRepository.findUserIdsByPostId(postId);
  }

  private LikeResponseDTO convertToDTO(Like like) {
    LikeResponseDTO dto = new LikeResponseDTO();
    dto.setId(like.getId());
    dto.setPostId(like.getPost().getId());
    dto.setUserId(like.getUser().getId());
    dto.setUsername(like.getUser().getUsername());
    dto.setProfilePicture(like.getUser().getProfilePicture());
    dto.setCreatedAt(like.getCreatedAt());
    return dto;
  }
}
