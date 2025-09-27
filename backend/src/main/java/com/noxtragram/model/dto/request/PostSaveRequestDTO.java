package com.noxtragram.model.dto.request;

public class PostSaveRequestDTO {
  private String folderName;

  // Constructors
  public PostSaveRequestDTO() {
  }

  public PostSaveRequestDTO(String folderName) {
    this.folderName = folderName;
  }

  // Getters and Setters
  public String getFolderName() {
    return folderName;
  }

  public void setFolderName(String folderName) {
    this.folderName = folderName;
  }
}
