package com.noxtragram.repository;

import com.noxtragram.model.entity.Like;
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
public interface LikeRepository extends JpaRepository<Like, Long> {

  // Tìm like theo post và user
  Optional<Like> findByPostAndUser(Post post, User user);

  // Kiểm tra tồn tại like
  boolean existsByPostAndUser(Post post, User user);

  // Đếm số like của một post
  long countByPost(Post post);

  // Lấy danh sách like của một post (có phân trang)
  Page<Like> findByPostOrderByCreatedAtDesc(Post post, Pageable pageable);

  // Lấy danh sách like của một user
  Page<Like> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

  // Lấy danh sách user đã like một post
  @Query("SELECT l.user.id FROM Like l WHERE l.post.id = :postId")
  List<Long> findUserIdsByPostId(@Param("postId") Long postId);

  // Xóa like theo post và user
  void deleteByPostAndUser(Post post, User user);

  // Xóa tất cả like của một post
  void deleteByPost(Post post);

  // Xóa tất cả like của một user
  void deleteByUser(User user);

  // Kiểm tra user đã like post nào chưa
  boolean existsByUserAndPostId(User user, Long postId);
}
