package com.noxtragram.repository;

import com.noxtragram.model.entity.Post;
import com.noxtragram.model.entity.PostSave;
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
public interface PostSaveRepository extends JpaRepository<PostSave, Long> {

  // Tìm PostSave theo post và user
  Optional<PostSave> findByPostAndUser(Post post, User user);

  // Kiểm tra tồn tại
  boolean existsByPostAndUser(Post post, User user);

  // Lấy danh sách PostSave của user (có phân trang)
  Page<PostSave> findByUserOrderBySavedAtDesc(User user, Pageable pageable);

  // Lấy danh sách PostSave của user theo folder
  Page<PostSave> findByUserAndFolderNameOrderBySavedAtDesc(User user, String folderName, Pageable pageable);

  // Đếm số bài viết đã lưu của user
  long countByUser(User user);

  // Đếm số bài viết đã lưu theo folder
  long countByUserAndFolderName(User user, String folderName);

  // Lấy danh sách folder của user
  @Query("SELECT DISTINCT ps.folderName FROM PostSave ps WHERE ps.user = :user")
  List<String> findFoldersByUser(@Param("user") User user);

  // Lấy số bài viết trong mỗi folder
  @Query("SELECT ps.folderName, COUNT(ps) FROM PostSave ps WHERE ps.user = :user GROUP BY ps.folderName")
  List<Object[]> countPostsByFolder(@Param("user") User user);

  // Xóa PostSave theo post và user
  void deleteByPostAndUser(Post post, User user);

  // Di chuyển bài viết sang folder khác
  @Query("UPDATE PostSave ps SET ps.folderName = :newFolder WHERE ps.user = :user AND ps.post = :post")
  void moveToFolder(@Param("user") User user, @Param("post") Post post, @Param("newFolder") String newFolder);

  // Kiểm tra user đã lưu post chưa
  boolean existsByUserAndPostId(User user, Long postId);
}
