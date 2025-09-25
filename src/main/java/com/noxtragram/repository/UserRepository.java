package com.noxtragram.repository;

import com.noxtragram.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);

  Boolean existsByEmail(String email);

  Boolean existsByUsername(String username);

  List<User> findByUsernameContainingIgnoreCase(String username);

  List<User> findByFullNameContainingIgnoreCase(String fullName);

  @Query("SELECT u FROM User u WHERE u.isActive = true")
  List<User> findAllActiveUsers();

  @Query("SELECT u.followers FROM User u WHERE u.id = :userId")
  List<User> findFollowersByUserId(@Param("userId") Long userId);

  @Query("SELECT u.following FROM User u WHERE u.id = :userId")
  List<User> findFollowingByUserId(@Param("userId") Long userId);
}
