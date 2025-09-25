package com.noxtragram.service;

import com.noxtragram.model.dto.response.*;
import com.noxtragram.model.dto.TrendingHashtagDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HashtagService {

  HashtagResponseDTO getHashtagByName(String name, Long currentUserId);

  HashtagResponseDTO createHashtag(String name);

  Page<HashtagResponseDTO> searchHashtags(String query, Pageable pageable);

  Page<HashtagResponseDTO> getPopularHashtags(Pageable pageable);

  Page<HashtagResponseDTO> getRecentHashtags(Pageable pageable);

  List<TrendingHashtagDTO> getTrendingHashtags(int limit);

  Page<PostResponseDTO> getPostsByHashtag(String hashtagName, Long currentUserId, Pageable pageable);

  void incrementPostCount(String hashtagName);

  void decrementPostCount(String hashtagName);

  void followHashtag(String hashtagName, Long userId);

  void unfollowHashtag(String hashtagName, Long userId);

  boolean isHashtagFollowedByUser(String hashtagName, Long userId);

  Page<HashtagResponseDTO> getFollowedHashtags(Long userId, Pageable pageable);
}
