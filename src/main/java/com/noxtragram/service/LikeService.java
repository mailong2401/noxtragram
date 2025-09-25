package com.noxtragram.service;

import com.noxtragram.model.dto.response.LikeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LikeService {

  void likePost(Long postId, Long userId);

  void unlikePost(Long postId, Long userId);

  boolean isPostLikedByUser(Long postId, Long userId);

  long getLikeCount(Long postId);

  Page<LikeResponseDTO> getLikesByPost(Long postId, Pageable pageable);

  Page<LikeResponseDTO> getLikesByUser(Long userId, Pageable pageable);

  List<Long> getUserIdsWhoLikedPost(Long postId);
}
