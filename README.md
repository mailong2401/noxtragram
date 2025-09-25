# 📸 Noxtragram

Một ứng dụng mạng xã hội được xây dựng với Flutter cho frontend và Spring Boot cho backend, mô phỏng các tính năng cốt lõi của Instagram.

![Flutter](https://img.shields.io/badge/Flutter-3.19.0-blue?logo=flutter)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green?logo=spring)
![Dart](https://img.shields.io/badge/Dart-3.3.0-blue?logo=dart)
![Java](https://img.shields.io/badge/Java-21-red?logo=java)

## 🌟 Tính năng

### ✅ Đã hoàn thành
- **🔐 Authentication**
  - Đăng nhập/Đăng ký tài khoản
  - Xác thực JWT
  - Quên mật khẩu
  - Đăng xuất

- **📱 Feed & Posts**
  - Tạo bài viết với hình ảnh
  - Hiển thị feed bài viết
  - Like/Unlike bài viết
  - Bình luận trên bài viết
  - Xóa bài viết

- **👥 Social Features**
  - Follow/Unfollow người dùng
  - Tìm kiếm người dùng
  - Trang cá nhân
  - Chỉnh sửa thông tin cá nhân

- **💬 Real-time Chat**
  - Nhắn tin real-time
  - Danh sách cuộc trò chuyện
  - Xem trạng thái online
  - Thông báo tin nhắn mới

### 🚧 Đang phát triển
- [ ] Stories (24h)
- [ ] Push Notifications
- [ ] Video Posts
- [ ] Direct Message với hình ảnh
- [ ] Hashtag & Explore
- [ ] Analytics

## 🏗️ Kiến trúc hệ thống


lib/
├── core/ # Core functionality
├── data/ # Data layer (models, repositories)
├── domain/ # Business logic
└── presentation/ # UI layer (pages, widgets)

## 🛠️ Công nghệ sử dụng

### Frontend
- **Framework**: Flutter 3.19.0
- **Language**: Dart 3.3.0
- **State Management**: Riverpod
- **HTTP Client**: Dio
- **Local Storage**: Hive
- **WebSocket**: web_socket_channel
- **Image Picker**: image_picker
- **Caching**: cached_network_image
