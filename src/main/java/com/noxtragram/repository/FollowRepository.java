package com.noxtragram.repository;

import com.noxtragram.model.entity.Follow;
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
public interface FollowRepository extends JpaRepository<Follow, Long> {

  // Kiểm tra user A có đang follow user B không
  boolean existsByFollowerAndFollowing(User follower, User following);

  // Tìm follow relationship cụ thể
  Optional<Follow> findByFollowerAndFollowing(User follower, User following);

  // Lấy danh sách người đang follow một user (followers)
  Page<Follow> findByFollowing(User following, Pageable pageable);

  // Lấy danh sách người mà một user đang follow (following)
  Page<Follow> findByFollower(User follower, Pageable pageable);

  // Đếm số lượng followers của một user
  long countByFollowing(User following);

  // Đếm số lượng người mà một user đang follow
  long countByFollower(User follower);

  // Lấy danh sách ID của những người mà user đang follow
  @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId")
  List<Long> findFollowingUserIds(@Param("userId") Long userId);

  // Lấy danh sách ID của những người đang follow user
  @Query("SELECT f.follower.id FROM Follow f WHERE f.following.id = :userId")
  List<Long> findFollowerUserIds(@Param("userId") Long userId);

  // Tìm các follow relationship mà thông báo được bật
  Page<Follow> findByFollowerAndIsNotificationsEnabledTrue(User follower, Pageable pageable);
}
