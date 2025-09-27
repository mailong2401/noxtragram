package com.noxtragram.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class HashtagRequestDTO {

  @NotBlank(message = "Hashtag name is required")
  @Size(max = 50, message = "Hashtag name must not exceed 50 characters")
  private String name;

  // Constructors
  public HashtagRequestDTO() {
  }

  public HashtagRequestDTO(String name) {
    this.name = name;
  }

  // Getters and Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
