# 🚀 Noxtragram

Noxtragram là một mạng xã hội mini lấy cảm hứng từ Instagram, được xây dựng bằng **Spring Boot (backend)** và **React/Flutter (frontend)**.  
Dự án được phát triển nhằm mục đích học tập, rèn luyện kỹ năng fullstack và có thể mở rộng thành sản phẩm thực tế.

---

## ✨ Tính năng chính
- 📸 Đăng bài viết (ảnh / video)
- ❤️ Like & 💬 Comment
- 👤 Trang cá nhân
- 🔔 Thông báo
- 💌 Tin nhắn (realtime)
- 🔑 Đăng ký / Đăng nhập / Bảo mật JWT
- 📂 Upload & quản lý file

---

## 📂 Cấu trúc dự án
- noxtragram/
- │── backend/ # Spring Boot (REST API + Security + JPA)
- │── web/ # React (UI web)
- │── app/ # Flutter (ứng dụng mobile)
- │── README.md
- │── .gitignore


---

## 🛠️ Công nghệ sử dụng
- **Backend:** Spring Boot, Spring Security, JPA/Hibernate, MySQL/PostgreSQL  
- **Frontend Web:** ReactJS, Axios, TailwindCSS  
- **Mobile App:** Flutter  
- **Khác:** Docker, GitHub Actions (CI/CD), JWT Auth  

---

## ⚡ Yêu cầu cài đặt
- [Java 21+](https://adoptopenjdk.net/)  
- [Maven 3.9+](https://maven.apache.org/)  
- [Node.js 18+](https://nodejs.org/) / [Yarn](https://yarnpkg.com/)  
- [Flutter](https://flutter.dev/) (nếu build mobile)  
- MySQL  

---

## 🚀 Cách chạy dự án

```bash
cd backend
./mvnw spring-boot:run
````

API sẽ chạy tại: `http://localhost:8080`

### 2. Web (React)

```bash
cd web
npm install
npm start
```

App sẽ chạy tại: `http://localhost:3000`

### 3. Mobile (Flutter)

```bash
cd app
flutter pub get
flutter run
```

---

## 📖 API Documentation

* Swagger UI: `http://localhost:8080/swagger-ui.html`
* Postman Collection: [`/docs/Noxtragram.postman_collection.json`](docs/Noxtragram.postman_collection.json)

---

## 📸 Demo

| Web UI                               | Mobile UI                               |
| ------------------------------------ | --------------------------------------- |
| ![Web Screenshot](docs/web-demo.png) | ![Mobile Screenshot](docs/app-demo.png) |

---

## 🤝 Đóng góp

Mọi đóng góp đều được chào đón!

1. Fork repo
2. Tạo branch mới (`feature/my-feature`)
3. Commit và push
4. Tạo Pull Request 🎉

---

## 📜 Giấy phép

MIT License © 2025 [Mai Dương Long](https://github.com/mailong2401)
