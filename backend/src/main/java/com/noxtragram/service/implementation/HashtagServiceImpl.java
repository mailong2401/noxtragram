package com.noxtragram.service.implementation;

import com.noxtragram.model.dto.request.*;
import com.noxtragram.model.dto.response.*;
import com.noxtragram.model.dto.Summary.*;
import com.noxtragram.model.dto.TrendingHashtagDTO;
import com.noxtragram.model.entity.Hashtag;
import com.noxtragram.model.entity.Post;
import com.noxtragram.repository.HashtagRepository;
import com.noxtragram.repository.PostRepository;
import com.noxtragram.service.HashtagService;
import com.noxtragram.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HashtagServiceImpl implements HashtagService {

  private final HashtagRepository hashtagRepository;
  private final PostRepository postRepository;
  private final PostService postService;

  public HashtagServiceImpl(HashtagRepository hashtagRepository,
      PostRepository postRepository,
      PostService postService) {
    this.hashtagRepository = hashtagRepository;
    this.postRepository = postRepository;
    this.postService = postService;
  }

  @Override
  @Transactional(readOnly = true)
  public HashtagResponseDTO getHashtagByName(String name, Long currentUserId) {
    String cleanName = cleanHashtagName(name);
    Hashtag hashtag = hashtagRepository.findByName(cleanName)
        .orElseThrow(() -> new EntityNotFoundException("Hashtag not found: " + cleanName));

    return convertToDTO(hashtag, currentUserId);
  }

  @Override
  public HashtagResponseDTO createHashtag(String name) {
    String cleanName = cleanHashtagName(name);

    if (hashtagRepository.existsByName(cleanName)) {
      throw new IllegalArgumentException("Hashtag already exists: " + cleanName);
    }

    Hashtag hashtag = new Hashtag(cleanName);
    Hashtag savedHashtag = hashtagRepository.save(hashtag);

    return convertToDTO(savedHashtag, null);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<HashtagResponseDTO> searchHashtags(String query, Pageable pageable) {
    String cleanQuery = cleanHashtagName(query);
    Page<Hashtag> hashtags = hashtagRepository.findByNameContainingIgnoreCase(cleanQuery, pageable);
    return hashtags.map(hashtag -> convertToDTO(hashtag, null));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<HashtagResponseDTO> getPopularHashtags(Pageable pageable) {
    Page<Hashtag> hashtags = hashtagRepository.findByOrderByPostCountDesc(pageable);
    return hashtags.map(hashtag -> convertToDTO(hashtag, null));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<HashtagResponseDTO> getRecentHashtags(Pageable pageable) {
    Page<Hashtag> hashtags = hashtagRepository.findByOrderByCreatedAtDesc(pageable);
    return hashtags.map(hashtag -> convertToDTO(hashtag, null));
  }

  @Override
  @Transactional(readOnly = true)
  public List<TrendingHashtagDTO> getTrendingHashtags(int limit) {
    Pageable pageable = Pageable.ofSize(limit);
    Page<Hashtag> trendingHashtags = hashtagRepository.findTrendingHashtags(pageable);

    return trendingHashtags.getContent().stream()
        .map(hashtag -> new TrendingHashtagDTO(
            hashtag.getName(),
            hashtag.getPostCount(),
            calculateTrendScore(hashtag)))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public Page<PostResponseDTO> getPostsByHashtag(String hashtagName, Long currentUserId, Pageable pageable) {
    String cleanName = cleanHashtagName(hashtagName);

    // Kiểm tra hashtag có tồn tại không
    if (!hashtagRepository.existsByName(cleanName)) {
      throw new EntityNotFoundException("Hashtag not found: " + cleanName);
    }

    return postService.getPostsByHashtag(cleanName, currentUserId, pageable);
  }

  @Override
  public void incrementPostCount(String hashtagName) {
    String cleanName = cleanHashtagName(hashtagName);
    Hashtag hashtag = hashtagRepository.findByName(cleanName)
        .orElseGet(() -> {
          Hashtag newHashtag = new Hashtag(cleanName);
          return hashtagRepository.save(newHashtag);
        });

    hashtag.incrementPostCount();
    hashtagRepository.save(hashtag);
  }

  @Override
  public void decrementPostCount(String hashtagName) {
    String cleanName = cleanHashtagName(hashtagName);
    Hashtag hashtag = hashtagRepository.findByName(cleanName)
        .orElseThrow(() -> new EntityNotFoundException("Hashtag not found: " + cleanName));

    hashtag.decrementPostCount();
    hashtagRepository.save(hashtag);
  }

  @Override
  public void followHashtag(String hashtagName, Long userId) {
    // Implementation for follow functionality
    // Would require a new entity HashtagFollow
    throw new UnsupportedOperationException("Follow functionality not implemented yet");
  }

  @Override
  public void unfollowHashtag(String hashtagName, Long userId) {
    // Implementation for unfollow functionality
    throw new UnsupportedOperationException("Follow functionality not implemented yet");
  }

  @Override
  public boolean isHashtagFollowedByUser(String hashtagName, Long userId) {
    // Implementation for check follow status
    return false; // Temporary implementation
  }

  @Override
  @Transactional(readOnly = true)
  public Page<HashtagResponseDTO> getFollowedHashtags(Long userId, Pageable pageable) {
    // Implementation for getting followed hashtags
    throw new UnsupportedOperationException("Follow functionality not implemented yet");
  }

  private String cleanHashtagName(String name) {
    if (name == null) {
      return "";
    }
    // Remove # symbol and trim whitespace
    return name.startsWith("#") ? name.substring(1).trim() : name.trim();
  }

  private int calculateTrendScore(Hashtag hashtag) {
    // Simple trend score calculation based on post count and recency
    int baseScore = hashtag.getPostCount() * 10;

    // Add recency bonus (more recent = higher score)
    if (hashtag.getCreatedAt() != null) {
      long daysSinceCreation = java.time.temporal.ChronoUnit.DAYS.between(
          hashtag.getCreatedAt(), LocalDateTime.now());
      if (daysSinceCreation < 7) {
        baseScore += (7 - (int) daysSinceCreation) * 5;
      }
    }

    return baseScore;
  }

  private HashtagResponseDTO convertToDTO(Hashtag hashtag, Long currentUserId) {
    HashtagResponseDTO dto = new HashtagResponseDTO();
    dto.setId(hashtag.getId());
    dto.setName(hashtag.getName());
    dto.setPostCount(hashtag.getPostCount());
    dto.setCreatedAt(hashtag.getCreatedAt());

    // Set recent posts (limit to 9 posts for grid display)
    List<PostSummaryDTO> recentPosts = hashtag.getPosts().stream()
        .filter(post -> !post.getIsDeleted())
        .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
        .limit(9)
        .map(this::convertToPostSummaryDTO)
        .collect(Collectors.toList());
    dto.setRecentPosts(recentPosts);

    // Set follow status if user is authenticated
    if (currentUserId != null) {
      dto.setIsFollowing(isHashtagFollowedByUser(hashtag.getName(), currentUserId));
    }

    return dto;
  }

  private PostSummaryDTO convertToPostSummaryDTO(Post post) {
    PostSummaryDTO dto = new PostSummaryDTO();
    dto.setId(post.getId());
    dto.setImageUrl(post.getImageUrl());
    dto.setCaption(post.getCaption());
    dto.setLikeCount(post.getLikeCount());
    dto.setCommentCount(post.getCommentCount());
    dto.setCreatedAt(post.getCreatedAt());
    return dto;
  }

  // Helper method để xử lý nhiều hashtags từ một post
  public List<Hashtag> processHashtags(List<String> hashtagNames) {
    return hashtagNames.stream()
        .map(this::cleanHashtagName)
        .distinct()
        .map(name -> hashtagRepository.findByName(name)
            .orElseGet(() -> {
              Hashtag newHashtag = new Hashtag(name);
              return hashtagRepository.save(newHashtag);
            }))
        .collect(Collectors.toList());
  }
}
