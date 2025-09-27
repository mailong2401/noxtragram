# Noxtragram - Mạng xã hội chia sẻ hình ảnh

![Noxtragram](https://img.shields.io/badge/Noxtragram-Social%20Network-purple?style=for-the-badge)
![React](https://img.shields.io/badge/React-19.1.1-blue?style=for-the-badge)
![Version](https://img.shields.io/badge/Version-0.1.0-green?style=for-the-badge)

Noxtragram là một mạng xã hội chia sẻ hình ảnh hiện đại, lấy cảm hứng từ Instagram, được xây dựng với ReactJS và Spring Boot.

## ✨ Tính năng chính

### 📱 Người dùng
- ✅ Đăng ký và đăng nhập tài khoản
- ✅ Chia sẻ hình ảnh và video
- ✅ Like, comment và lưu bài viết
- ✅ Theo dõi người dùng khác
- ✅ Tìm kiếm và khám phá nội dung

### 🏷️ Hashtag & Khám phá
- ✅ Tìm kiếm bài viết theo hashtag
- ✅ Hashtag trending và phổ biến
- ✅ Khám phá nội dung mới

### 💬 Tương tác xã hội
- ✅ Tin nhắn trực tiếp (DM)
- ✅ Thông báo thời gian thực
- ✅ Tương tác với bài viết

## 🚀 Bắt đầu

### Yêu cầu hệ thống
- Node.js 16.0.0 hoặc cao hơn
- npm hoặc yarn
- Backend Noxtragram (Spring Boot)

### Cài đặt

1. **Clone repository**
```bash
git clone <repository-url>
cd noxtragram-web
```

2. **Cài đặt dependencies**
```bash
npm install
```

3. **Cấu hình environment**
Tạo file `.env` trong thư mục gốc:
```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
REACT_APP_APP_NAME=Noxtragram
REACT_APP_VERSION=0.1.0
```

4. **Chạy ứng dụng**
```bash
# Development mode
npm start

# Production build
npm run build

# Run tests
npm test
```

## 📁 Cấu trúc dự án

```
src/
├── components/          # Components tái sử dụng
│   ├── common/         # Components chung
│   ├── layout/         # Layout components
│   └── ui/             # UI components
├── pages/              # Các trang chính
│   ├── Auth/           # Đăng nhập/đăng ký
│   ├── Home/           # Trang chủ (Feed)
│   ├── Profile/        # Trang cá nhân
│   ├── Explore/        # Khám phá
│   ├── Messages/       # Tin nhắn
│   └── Notifications/  # Thông báo
├── services/           # API services
├── hooks/              # Custom hooks
├── contexts/           # React contexts
├── utils/              # Utilities
└── assets/             # Tài nguyên tĩnh
```

## 🛠️ Công nghệ sử dụng

### Frontend
- **React 19.1.1** - Thư viện UI chính
- **React Router** - Điều hướng trang
- **Axios** - HTTP client
- **Heroicons** - Icon library

### Development Tools
- **Create React App** - Boilerplate
- **Jest & Testing Library** - Testing
- **ESLint** - Code linting

## 🌐 API Endpoints

Ứng dụng kết nối với backend thông qua các endpoints:

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/posts` | Lấy danh sách bài viết |
| POST | `/api/posts` | Tạo bài viết mới |
| GET | `/api/users/me` | Thông tin user hiện tại |
| GET | `/api/hashtags/{name}` | Thông tin hashtag |

## 🎨 UI/UX Features

- **Responsive Design** - Tương thích mọi thiết bị
- **Modern Interface** - Giao diện hiện đại, clean
- **Dark/Light Mode** - Hỗ trợ chế độ sáng/tối
- **Smooth Animations** - Hiệu ứng mượt mà

## 📱 Tính năng đang phát triển

- [ ] Stories (24h)
- [ ] Reels (video ngắn)
- [ ] Live Streaming
- [ ] Shopping Integration
- [ ] Advanced Analytics

## 🤝 Đóng góp

Chúng tôi hoan nghênh mọi đóng góp! Hãy:

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

Dự án được phân phối under the MIT License. Xem file `LICENSE` để biết thêm chi tiết.

## 👥 Đội ngũ phát triển

- **Long Nguyen** - Full-stack Developer
- *Thêm thành viên...*

## 📞 Liên hệ

- Email: contact@noxtragram.com
- Website: https://noxtragram.com
- Documentation: [API Docs](./docs/api.md)

## 🙏 Acknowledgements

- Instagram - Nguồn cảm hứng thiết kế
- React Team - Framework tuyệt vời
- Spring Boot Team - Backend robust

---

**Made with ❤️ for the Noxtragram community**

<div align="center">

### ⭐ Đừng quên star repository nếu bạn thấy dự án hữu ích!

</div>
