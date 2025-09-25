package com.noxtragram.model.dto.response;

public class FolderResponseDTO {
  private String folderName;
  private Long postCount;

  // Constructors
  public FolderResponseDTO() {
  }

  public FolderResponseDTO(String folderName, Long postCount) {
    this.folderName = folderName;
    this.postCount = postCount;
  }

  // Getters and Setters
  public String getFolderName() {
    return folderName;
  }

  public void setFolderName(String folderName) {
    this.folderName = folderName;
  }

  public Long getPostCount() {
    return postCount;
  }

  public void setPostCount(Long postCount) {
    this.postCount = postCount;
  }
}
