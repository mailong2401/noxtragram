# Noxtragram - Backend API

A Spring Boot backend for Instagram-like social media application with real-time features.

## 🚀 Tech Stack

- **Framework**: Spring Boot 3.3.1
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Token)
- **Real-time**: WebSocket (STOMP)
- **File Storage**: Local file system / AWS S3
- **Caching**: Redis (optional)
- **Build Tool**: Maven
- **Java Version**: 21+

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- MySQL 8+
- Redis (optional, for caching)

## 🔧 Installation & Setup

### 1. Clone the repository
```bash
git clone https://github.com/your-username/instagram-backend.git
cd instagram-backend
```

### 2. Configure database
Create a PostgreSQL database:
```sql
CREATE DATABASE instagram_db;
```

### 3. Configure application properties
Create `src/main/resources/application-dev.yml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/instagram_db
    username: your-username
    password: your-password
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

  redis:
    host: localhost
    port: 6379

jwt:
  secret: your-jwt-secret-key-minimum-512-bits
  expiration: 86400000 # 24 hours

file:
  upload-dir: ./uploads

logging:
  level:
    com.instagram: DEBUG
    org.springframework.security: DEBUG
```

### 4. Build and run the application
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or run directly
java -jar target/instagram-backend-1.0.0.jar
```

The application will be available at `http://localhost:8080/api`

## 📚 API Documentation


## 📌 User Endpoints

| **Method** | **Endpoint**                 | **Description**                                      |
| ---------- | ---------------------------- | ---------------------------------------------------- |
| **POST**   | `/users/register`            | Đăng ký tài khoản mới                                |
| **POST**   | `/users/login`               | Đăng nhập, trả về token                              |
| **GET**    | `/users/me`                  | Lấy thông tin user hiện tại (dựa vào Authentication) |
| **GET**    | `/users/{id}`                | Lấy user theo ID                                     |
| **GET**    | `/users/email/{email}`       | Lấy user theo email                                  |
| **GET**    | `/users/username/{username}` | Lấy user theo username                               |
| **PUT**    | `/users/{userId}`            | Cập nhật thông tin user                              |
| **DELETE** | `/users/{userId}`            | Xóa user                                             |

---

## 🔐 Password Management

| **Method** | **Endpoint**               | **Description**                       |
| ---------- | -------------------------- | ------------------------------------- |
| **PUT**    | `/users/{userId}/password` | Đổi mật khẩu                          |
| **POST**   | `/users/reset-password`    | Reset mật khẩu (theo email/OTP, v.v.) |

---

## 📸 Profile Picture

| **Method** | **Endpoint**                      | **Description**     |
| ---------- | --------------------------------- | ------------------- |
| **POST**   | `/users/{userId}/profile-picture` | Upload ảnh đại diện |
| **DELETE** | `/users/{userId}/profile-picture` | Xóa ảnh đại diện    |

---

## 👥 Follow System

| **Method** | **Endpoint**                                  | **Description**                         |
| ---------- | --------------------------------------------- | --------------------------------------- |
| **POST**   | `/users/{followerId}/follow/{followingId}`    | Follow user khác                        |
| **POST**   | `/users/{followerId}/unfollow/{followingId}`  | Unfollow user khác                      |
| **GET**    | `/users/{userId}/is-following/{targetUserId}` | Kiểm tra user có follow user khác không |
| **GET**    | `/users/{userId}/followers`                   | Lấy danh sách followers                 |
| **GET**    | `/users/{userId}/following`                   | Lấy danh sách đang follow               |
| **GET**    | `/users/{userId}/follower-count`              | Lấy số lượng followers                  |
| **GET**    | `/users/{userId}/following-count`             | Lấy số lượng following                  |

---

## 🔍 Search

| **Method** | **Endpoint**                          | **Description**              |
| ---------- | ------------------------------------- | ---------------------------- |
| **GET**    | `/users/search?keyword=...`           | Tìm kiếm user theo keyword   |
| **GET**    | `/users/search/username?username=...` | Tìm kiếm user theo username  |
| **GET**    | `/users/search/fullname?fullName=...` | Tìm kiếm user theo full name |

---

## 💡 Suggestions

| **Method** | **Endpoint**                           | **Description**       |
| ---------- | -------------------------------------- | --------------------- |
| **GET**    | `/users/{userId}/suggestions?limit=10` | Gợi ý bạn bè cho user |

---

## 👑 Admin Endpoints

| **Method** | **Endpoint**                 | **Description**               |
| ---------- | ---------------------------- | ----------------------------- |
| **POST**   | `/users/{userId}/verify`     | Xác thực (tick xanh) cho user |
| **POST**   | `/users/{userId}/unverify`   | Hủy xác thực user             |
| **POST**   | `/users/{userId}/deactivate` | Vô hiệu hóa tài khoản         |
| **POST**   | `/users/{userId}/reactivate` | Kích hoạt lại tài khoản       |

---

## 🔧 Utility

| **Method** | **Endpoint**                         | **Description**                          |
| ---------- | ------------------------------------ | ---------------------------------------- |
| **GET**    | `/users/check-email?email=...`       | Kiểm tra email đã tồn tại chưa           |
| **GET**    | `/users/check-username?username=...` | Kiểm tra username đã tồn tại chưa        |
| **GET**    | `/users/stats/active-count`          | Lấy tổng số user đang hoạt động          |
| **GET**    | `/users/active`                      | Lấy danh sách tất cả user đang hoạt động |



### Post Endpoints

| **Method** | **Endpoint**                       | **Description**                                          |
| ---------- | ---------------------------------- | -------------------------------------------------------- |
| **POST**   | `/api/posts`                       | Tạo post mới (ảnh/video + caption + hashtag + location). |
| **GET**    | `/api/posts/{postId}`              | Lấy chi tiết 1 post theo ID.                             |
| **GET**    | `/api/posts/user/{userId}`         | Lấy danh sách post của 1 user (có phân trang).           |
| **GET**    | `/api/posts/feed`                  | Lấy feed từ những user đã follow (có phân trang).        |
| **GET**    | `/api/posts/hashtag/{hashtag}`     | Lấy danh sách post theo hashtag (có phân trang).         |
| **GET**    | `/api/posts/saved`                 | Lấy danh sách post đã lưu của user (có phân trang).      |
| **GET**    | `/api/posts/popular`               | Lấy danh sách post phổ biến (dựa trên số like).          |
| **PUT**    | `/api/posts/{postId}`              | Cập nhật nội dung post.                                  |
| **DELETE** | `/api/posts/{postId}`              | Xóa post.                                                |
| **POST**   | `/api/posts/{postId}/like`         | Like 1 post.                                             |
| **DELETE** | `/api/posts/{postId}/like`         | Unlike 1 post.                                           |
| **POST**   | `/api/posts/{postId}/save`         | Save 1 post.                                             |
| **DELETE** | `/api/posts/{postId}/save`         | Unsave 1 post.                                           |
| **GET**    | `/api/posts/{postId}/interactions` | Lấy trạng thái tương tác với post (liked/saved).         |


### Chat Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/chat/rooms` | Get user's chat rooms |
| POST | `/api/chat/rooms` | Create chat room |
| GET | `/api/chat/rooms/{roomId}/messages` | Get chat messages |
| POST | `/api/chat/messages` | Send message |

## 🔌 WebSocket Configuration

### Real-time Features
- **Chat Messages**: Real-time messaging
- **Notifications**: Live notifications for likes, comments, follows
- **Online Status**: User presence indicators

### WebSocket Endpoints
- **Connect**: `/ws`
- **Subscribe to chat**: `/user/{userId}/queue/messages`
- **Subscribe to notifications**: `/user/{userId}/queue/notifications`

### Message Types
```json
{
  "type": "CHAT_MESSAGE",
  "senderId": 123,
  "content": "Hello!",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

## 🗃️ Database Schema

### Main Entities
- **Users**: User profiles and authentication
- **Posts**: User posts with media
- **Comments**: Post comments
- **Likes**: Post likes
- **Follows**: User relationships
- **ChatRooms**: Conversation rooms
- **Messages**: Chat messages
- **Notifications**: System notifications

## 🔒 Security Features

- JWT-based authentication
- Password encryption with BCrypt
- Role-based authorization
- CORS configuration
- CSRF protection
- File upload security
- SQL injection prevention

## 📁 File Upload

### Supported Files
- Images (JPEG, PNG, GIF)
- Videos (MP4, MOV)
- Maximum file size: 10MB

### Storage Locations
- Profile pictures: `/uploads/profiles/`
- Post media: `/uploads/posts/`
- Chat files: `/uploads/chat/`

## 🧪 Testing

### Run Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# With coverage
mvn jacoco:report
```

### Test Profiles
- `test`: H2 in-memory database
- `dev`: Development database
- `prod`: Production database

## 🚀 Deployment

### Production Setup
1. Set environment variables:
```bash
export SPRING_PROFILES_ACTIVE=prod
export DATABASE_URL=your-production-db-url
export JWT_SECRET=your-secure-jwt-secret
```

2. Build for production:
```bash
mvn clean package -Pprod
```

3. Deploy JAR file:
```bash
java -jar -Dspring.profiles.active=prod instagram-backend-1.0.0.jar
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/instagram-backend-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 📊 Monitoring & Logging

### Health Check
```bash
curl http://localhost:8080/api/actuator/health
```

### Log Files
- Location: `logs/application.log`
- Rotation: Daily
- Levels: DEBUG, INFO, WARN, ERROR

## 🔄 API Versioning

API endpoints are versioned:
```
/api/v1/auth/login
/api/v1/users/profile
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open Pull Request

## 📝 Code Style

- Follow Java naming conventions
- Use meaningful variable names
- Add Javadoc for public methods
- Write unit tests for new features
- Use Lombok for boilerplate code reduction

## 🐛 Troubleshooting

### Common Issues

1. **Port already in use**: Change `server.port` in application properties
2. **Database connection failed**: Check credentials and connection URL
3. **JWT errors**: Verify secret key configuration
4. **File upload fails**: Check directory permissions and file size limits

### Logs Location
- Application logs: `logs/application.log`
- Spring Boot logs: Console output

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Email: support@instagram-clone.com
- Documentation: [API Docs](https://docs.instagram-clone.com)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 🙏 Acknowledgments

- Spring Boot team
- PostgreSQL community
- WebSocket STOMP implementation

---

**Note**: This is a development version. For production deployment, ensure proper security measures and environment configuration.
