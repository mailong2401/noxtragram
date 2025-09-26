package com.noxtragram.repository;

import com.noxtragram.model.entity.Post;
import com.noxtragram.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  // Tìm tất cả post của một user (chưa bị xóa)
  Page<Post> findByUserAndIsDeletedFalseOrderByCreatedAtDesc(User user, Pageable pageable);

  // Tìm tất cả post chưa bị xóa (cho feed)
  Page<Post> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

  // Tìm post theo ID và chưa bị xóa
  Optional<Post> findByIdAndIsDeletedFalse(Long id);

  @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h.name = :hashtag AND p.isDeleted = false ORDER BY p.createdAt DESC")
  Page<Post> findByHashtagName(@Param("hashtag") String hashtag, Pageable pageable);

  // Tìm post theo hashtag
  @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE h.name = :hashtag AND p.isDeleted = false ORDER BY p.createdAt DESC")
  Page<Post> findByHashtag(@Param("hashtag") String hashtag, Pageable pageable);

  // Tìm post đã lưu bởi user - ĐÃ SỬA: thay ps.createdAt bằng ps.savedAt
  @Query("SELECT p FROM Post p JOIN p.savedByUsers ps WHERE ps.user.id = :userId AND p.isDeleted = false ORDER BY ps.savedAt DESC")
  Page<Post> findSavedPostsByUserId(@Param("userId") Long userId, Pageable pageable);

  // Tìm post phổ biến (nhiều like nhất)
  @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.likeCount DESC, p.createdAt DESC")
  Page<Post> findPopularPosts(Pageable pageable);

  // Tìm post theo location
  Page<Post> findByLocationContainingIgnoreCaseAndIsDeletedFalse(String location, Pageable pageable);

  // Đếm số post của user
  Long countByUserAndIsDeletedFalse(User user);

  // Kiểm tra user có phải là chủ sở hữu post không
  @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Post p WHERE p.id = :postId AND p.user.id = :userId")
  boolean isPostOwnedByUser(@Param("postId") Long postId, @Param("userId") Long userId);

  // Tìm post gần đây trong khoảng thời gian
  @Query("SELECT p FROM Post p WHERE p.isDeleted = false AND p.createdAt >= :startDate ORDER BY p.createdAt DESC")
  Page<Post> findRecentPostsSince(@Param("startDate") java.time.LocalDateTime startDate, Pageable pageable);

  // Tìm post có nhiều comment nhất
  @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.commentCount DESC, p.createdAt DESC")
  Page<Post> findMostCommentedPosts(Pageable pageable);

  // Tìm post của những user đang follow
  @Query("SELECT p FROM Post p WHERE p.user.id IN (SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId) AND p.isDeleted = false ORDER BY p.createdAt DESC")
  Page<Post> findPostsFromFollowedUsers(@Param("userId") Long userId, Pageable pageable);

  // Tìm post đã like bởi user
  @Query("SELECT p FROM Post p JOIN p.likes l WHERE l.user.id = :userId AND p.isDeleted = false ORDER BY l.createdAt DESC")
  Page<Post> findLikedPostsByUserId(@Param("userId") Long userId, Pageable pageable);
}
