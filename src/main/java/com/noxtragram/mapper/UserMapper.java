package com.noxtragram.mapper;

import com.noxtragram.model.dto.user.*;
import com.noxtragram.model.entity.User;

public class UserMapper {

  public static UserDTO toDTO(User user) {
    if (user == null)
      return null;

    return UserDTO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .profilePicture(user.getProfilePicture())
        .isActive(user.getIsActive())
        .isVerified(user.getIsVerified())
        .createdAt(user.getCreatedAt())
        .build();
  }

  public static User toEntity(UserDTO userDTO) {
    if (userDTO == null)
      return null;

    User user = new User();
    user.setId(userDTO.getId());
    user.setUsername(userDTO.getUsername());
    user.setEmail(userDTO.getEmail());
    user.setFullName(userDTO.getFullName());
    user.setProfilePicture(userDTO.getProfilePicture());
    user.setIsActive(userDTO.getIsActive());
    user.setIsVerified(user.getIsVerified());
    return user;
  }

  // Phương thức cập nhật từ DTO (dùng cho update)
  public static void updateEntityFromDTO(UserUpdateDTO dto, User user) {
    if (dto.getFullName() != null)
      user.setFullName(dto.getFullName());
    if (dto.getBio() != null)
      user.setBio(dto.getBio());
    if (dto.getWebsite() != null)
      user.setWebsite(dto.getWebsite());
    if (dto.getPhoneNumber() != null)
      user.setPhoneNumber(dto.getPhoneNumber());
    if (dto.getIsPrivate() != null)
      user.setIsPrivate(dto.getIsPrivate());
  }
}
