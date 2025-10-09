package com.noxtragram.model.entity;

public enum MessageType {
  TEXT("text", "Tin nhắn văn bản"),
  IMAGE("image", "Tin nhắn hình ảnh"),
  VIDEO("video", "Tin nhắn video"),
  VOICE("voice", "Tin nhắn thoại"),
  FILE("file", "Tin nhắn file"),
  LOCATION("location", "Tin nhắn vị trí"),
  STICKER("sticker", "Nhãn dán"),
  SYSTEM("system", "Tin nhắn hệ thống");

  private final String code;
  private final String description;

  MessageType(String code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  public static MessageType fromCode(String code) {
    for (MessageType type : MessageType.values()) {
      if (type.getCode().equalsIgnoreCase(code)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown message type code: " + code);
  }

  public boolean isMedia() {
    return this == IMAGE || this == VIDEO || this == VOICE;
  }

  public boolean isFile() {
    return this == FILE;
  }

  public boolean isSystem() {
    return this == SYSTEM;
  }

  public String[] getSupportedMimeTypes() {
    switch (this) {
      case IMAGE:
        return new String[] { "image/jpeg", "image/png", "image/gif", "image/webp" };
      case VIDEO:
        return new String[] { "video/mp4", "video/avi", "video/mov", "video/webm" };
      case VOICE:
        return new String[] { "audio/mpeg", "audio/wav", "audio/ogg", "audio/aac" };
      case FILE:
        return new String[] {
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain",
            "application/zip",
            "application/x-rar-compressed"
        };
      default:
        return new String[0];
    }
  }

  public long getMaxFileSize() {
    switch (this) {
      case IMAGE:
        return 10 * 1024 * 1024;
      case VIDEO:
        return 50 * 1024 * 1024;
      case VOICE:
        return 5 * 1024 * 1024;
      case FILE:
        return 25 * 1024 * 1024;
      default:
        return 0;
    }
  }

  public String getIcon() {
    switch (this) {
      case TEXT:
        return "📝";
      case IMAGE:
        return "🖼️";
      case VIDEO:
        return "🎥";
      case VOICE:
        return "🎤";
      case FILE:
        return "📎";
      case LOCATION:
        return "📍";
      case STICKER:
        return "😊";
      case SYSTEM:
        return "⚙️";
      default:
        return "💬";
    }
  }

  public String getCssClass() {
    return "message-type-" + this.code.toLowerCase();
  }

  @Override
  public String toString() {
    return this.code;
  }
}
