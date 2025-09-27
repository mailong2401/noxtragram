package com.noxtragram.repository;

import com.noxtragram.model.entity.Hashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

  /**
   * Tìm hashtag theo tên (chính xác, không phân biệt hoa thường)
   */
  Optional<Hashtag> findByName(String name);

  /**
   * Tìm hashtag theo tên (chính xác, không phân biệt hoa thường)
   * Sử dụng IgnoreCase để đảm bảo tìm kiếm không phân biệt hoa thường
   */
  Optional<Hashtag> findByNameIgnoreCase(String name);

  /**
   * Kiểm tra xem hashtag có tồn tại theo tên không
   */
  boolean existsByName(String name);

  /**
   * Kiểm tra xem hashtag có tồn tại theo tên không (không phân biệt hoa thường)
   */
  boolean existsByNameIgnoreCase(String name);

  /**
   * Tìm kiếm hashtag theo từ khóa (chứa từ khóa, không phân biệt hoa thường)
   */
  Page<Hashtag> findByNameContainingIgnoreCase(String name, Pageable pageable);

  /**
   * Lấy danh sách hashtag sắp xếp theo số lượng post giảm dần (phổ biến nhất)
   */
  Page<Hashtag> findByOrderByPostCountDesc(Pageable pageable);

  /**
   * Lấy danh sách hashtag sắp xếp theo thời gian tạo mới nhất
   */
  Page<Hashtag> findByOrderByCreatedAtDesc(Pageable pageable);

  /**
   * Lấy danh sách hashtag trending dựa trên số lượng post và độ mới
   * Công thức trending: kết hợp post_count và recency (độ mới)
   */
  @Query("SELECT h FROM Hashtag h ORDER BY h.postCount DESC, h.createdAt DESC")
  Page<Hashtag> findTrendingHashtags(Pageable pageable);

  /**
   * Lấy danh sách hashtag trending với công thức phức tạp hơn
   * Ưu tiên hashtag có nhiều post trong thời gian gần đây
   * Sử dụng FUNCTION() để xử lý date arithmetic
   */
  @Query("SELECT h FROM Hashtag h WHERE h.createdAt >= FUNCTION('DATE_SUB', CURRENT_DATE, 7, 'DAY') ORDER BY h.postCount DESC, h.createdAt DESC")
  Page<Hashtag> findWeeklyTrendingHashtags(Pageable pageable);

  /**
   * Tìm hashtag theo danh sách tên
   */
  @Query("SELECT h FROM Hashtag h WHERE h.name IN :names")
  java.util.List<Hashtag> findByNames(@Param("names") java.util.List<String> names);

  /**
   * Đếm số lượng hashtag
   */
  long count();

  /**
   * Lấy hashtag có nhiều post nhất
   * Sử dụng Pageable thay vì LIMIT để đảm bảo compatibility
   */
  @Query("SELECT h FROM Hashtag h ORDER BY h.postCount DESC")
  Page<Hashtag> findMostPopularHashtag(Pageable pageable);

  /**
   * Lấy tổng số post của tất cả hashtag
   */
  @Query("SELECT SUM(h.postCount) FROM Hashtag h")
  Long getTotalPostCount();

  /**
   * Lấy hashtag được sử dụng nhiều nhất trong khoảng thời gian
   */
  @Query("SELECT h FROM Hashtag h WHERE h.createdAt BETWEEN :startDate AND :endDate ORDER BY h.postCount DESC")
  Page<Hashtag> findPopularHashtagsInPeriod(@Param("startDate") java.time.LocalDateTime startDate,
      @Param("endDate") java.time.LocalDateTime endDate,
      Pageable pageable);
}
