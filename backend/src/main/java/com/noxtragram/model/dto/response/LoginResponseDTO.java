package com.noxtragram.model.dto.response;

public class LoginResponseDTO {

  private String token;
  private String tokenType = "Bearer";
  private UserResponseDTO user;

  // Constructors
  public LoginResponseDTO() {
  }

  public LoginResponseDTO(String token, UserResponseDTO user) {
    this.token = token;
    this.user = user;
  }

  // Getters and Setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getTokenType() {
    return tokenType;
  }

  public void setTokenType(String tokenType) {
    this.tokenType = tokenType;
  }

  public UserResponseDTO getUser() {
    return user;
  }

  public void setUser(UserResponseDTO user) {
    this.user = user;
  }
}
