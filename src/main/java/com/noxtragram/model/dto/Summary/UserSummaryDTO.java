package com.noxtragram.model.dto.Summary;

public class UserSummaryDTO {
  private Long id;
  private String username;
  private String profilePicture;
  private String displayName;

  // Constructors, Getters and Setters
  public UserSummaryDTO() {
  }

  public UserSummaryDTO(Long id, String username, String profilePicture, String displayName) {
    this.id = id;
    this.username = username;
    this.profilePicture = profilePicture;
    this.displayName = displayName;
  }

  // Getters and Setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(String profilePicture) {
    this.profilePicture = profilePicture;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }
}
