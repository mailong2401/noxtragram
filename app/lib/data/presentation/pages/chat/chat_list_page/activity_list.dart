import 'package:flutter/material.dart';

class HorizontalUserList extends StatelessWidget {
  final List<User> users;
  final Function(User)? onUserTap;

  const HorizontalUserList({
    Key? key,
    required this.users,
    this.onUserTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 100, // Giảm chiều cao tổng
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        padding: EdgeInsets.symmetric(horizontal: 16),
        itemCount: users.length,
        itemBuilder: (context, index) {
          final user = users[index];
          return GestureDetector(
            onTap: () => onUserTap?.call(user),
            child: Container(
              width: 80, // Giảm chiều rộng
              margin: EdgeInsets.only(right: 8),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Stack(
                    children: [
                      CircleAvatar(
                        radius: 22,
                        backgroundImage: NetworkImage(user.avatarUrl),
                      ),
                      if (user.isOnline)
                        Positioned(
                          right: 0,
                          bottom: 0,
                          child: Container(
                            width: 12,
                            height: 12,
                            decoration: BoxDecoration(
                              color: Colors.green,
                              shape: BoxShape.circle,
                              border: Border.all(color: Colors.white, width: 2),
                            ),
                          ),
                        ),
                    ],
                  ),
                  SizedBox(height: 4),
                  Text(
                    user.name.split(' ').first, // Chỉ hiển thị tên đầu
                    style: TextStyle(fontSize: 15, color: Colors.white),
                    overflow: TextOverflow.ellipsis,
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildUserItem(User user, int index) {
    return GestureDetector(
      onTap: () => onUserTap?.call(user),
      child: Container(
        width: 80, // Chiều rộng mỗi item
        margin: EdgeInsets.only(right: 12),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Avatar với indicator online
            Stack(
              children: [
                CircleAvatar(
                  radius: 28,
                  backgroundImage: NetworkImage(user.avatarUrl),
                  backgroundColor: Colors.grey[300],
                ),
                if (user.isOnline)
                  Positioned(
                    right: 0,
                    bottom: 0,
                    child: Container(
                      width: 16,
                      height: 16,
                      decoration: BoxDecoration(
                        color: Colors.green,
                        shape: BoxShape.circle,
                        border: Border.all(color: Colors.white, width: 2),
                      ),
                    ),
                  ),
              ],
            ),
            SizedBox(height: 8),
            Text(
              user.name,
              style: TextStyle(fontSize: 12, fontWeight: FontWeight.bold),
              overflow: TextOverflow.ellipsis,
              maxLines: 1,
            ),
            Text(
              user.status,
              style: TextStyle(fontSize: 10, color: Colors.grey),
              overflow: TextOverflow.ellipsis,
              maxLines: 1,
            ),
          ],
        ),
      ),
    );
  }
}

// Model User
class User {
  final String id;
  final String name;
  final String avatarUrl;
  final bool isOnline;
  final String status;

  User({
    required this.id,
    required this.name,
    required this.avatarUrl,
    this.isOnline = false,
    this.status = 'Hoạt động',
  });
}

// Dữ liệu mẫu
class UserData {
  static List<User> get activeUsers => [
    User(
      id: '1',
      name: 'Minh Tris',
      avatarUrl: 'https://picsum.photos/100?random=1',
      isOnline: true,
      status: 'Đang hoạt động',
    ),
    User(
      id: '2',
      name: 'Tris Đẹp Trai',
      avatarUrl: 'https://picsum.photos/100?random=2',
      isOnline: true,
      status: 'Trực tuyến',
    ),
    User(
      id: '3',
      name: 'Tris Pro',
      avatarUrl: 'https://picsum.photos/100?random=3',
      isOnline: false,
      status: 'Offline',
    ),
    User(
      id: '4',
      name: 'Super Tris',
      avatarUrl: 'https://picsum.photos/100?random=4',
      isOnline: true,
      status: 'Nhắn tin...',
    ),
    User(
      id: '5',
      name: 'Tris VIP',
      avatarUrl: 'https://picsum.photos/100?random=5',
      isOnline: true,
      status: 'Đang bận',
    ),
    User(
      id: '6',
      name: 'Tris Legend',
      avatarUrl: 'https://picsum.photos/100?random=6',
      isOnline: false,
      status: '2 giờ trước',
    ),
  ];
}