package com.noxtragram.mapper;

import com.noxtragram.model.dto.request.UserRequestDTO;
import com.noxtragram.model.dto.request.UserUpdateRequestDTO;
import com.noxtragram.model.dto.response.UserResponseDTO;
import com.noxtragram.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toEntity(UserRequestDTO userRequestDTO) {
    if (userRequestDTO == null) {
      return null;
    }

    User user = new User();
    user.setUsername(userRequestDTO.getUsername());
    user.setEmail(userRequestDTO.getEmail());
    user.setPassword(userRequestDTO.getPassword()); // Will be encoded in service
    user.setFullName(userRequestDTO.getFullName());
    user.setBio(userRequestDTO.getBio());
    user.setWebsite(userRequestDTO.getWebsite());
    user.setPhoneNumber(userRequestDTO.getPhoneNumber());
    user.setIsPrivate(userRequestDTO.getIsPrivate());

    return user;
  }

  public UserResponseDTO toResponseDTO(User user) {
    if (user == null) {
      return null;
    }

    UserResponseDTO userResponseDTO = new UserResponseDTO();
    userResponseDTO.setId(user.getId());
    userResponseDTO.setUsername(user.getUsername());
    userResponseDTO.setEmail(user.getEmail());
    userResponseDTO.setFullName(user.getFullName());
    userResponseDTO.setBio(user.getBio());
    userResponseDTO.setProfilePicture(user.getProfilePicture());
    userResponseDTO.setWebsite(user.getWebsite());
    userResponseDTO.setPhoneNumber(user.getPhoneNumber());
    userResponseDTO.setIsPrivate(user.getIsPrivate());
    userResponseDTO.setIsVerified(user.getIsVerified());
    userResponseDTO.setIsActive(user.getIsActive());
    userResponseDTO.setPostCount(user.getPostCount());
    userResponseDTO.setFollowerCount(user.getFollowerCount());
    userResponseDTO.setFollowingCount(user.getFollowingCount());
    userResponseDTO.setCreatedAt(user.getCreatedAt());
    userResponseDTO.setUpdatedAt(user.getUpdatedAt());

    return userResponseDTO;
  }

  public void updateEntityFromDTO(UserUpdateRequestDTO userUpdateDTO, User user) {
    if (userUpdateDTO == null || user == null) {
      return;
    }

    if (userUpdateDTO.getUsername() != null) {
      user.setUsername(userUpdateDTO.getUsername());
    }
    if (userUpdateDTO.getFullName() != null) {
      user.setFullName(userUpdateDTO.getFullName());
    }
    if (userUpdateDTO.getBio() != null) {
      user.setBio(userUpdateDTO.getBio());
    }
    if (userUpdateDTO.getWebsite() != null) {
      user.setWebsite(userUpdateDTO.getWebsite());
    }
    if (userUpdateDTO.getPhoneNumber() != null) {
      user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
    }
    if (userUpdateDTO.getIsPrivate() != null) {
      user.setIsPrivate(userUpdateDTO.getIsPrivate());
    }
  }
}
