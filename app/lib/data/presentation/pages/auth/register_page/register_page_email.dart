
import 'package:flutter/material.dart';

class RegisterPageEmail extends StatelessWidget {
  RegisterPageEmail({super.key});

  final TextEditingController accountController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  void _login() {
    final account = accountController.text;
    final password = passwordController.text;
    print('Account: $account');
    print('Password: $password');
    // TODO: Xử lý logic đăng nhập
  }

  void _register() {
    print('Register pressed');
    // TODO: Điều hướng sang màn hình đăng ký
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.indigo.shade900,
        iconTheme: IconThemeData(color: Colors.white), // đổi màu tất cả icon
        leading: IconButton(
          icon: Icon(Icons.arrow_back_ios),
          onPressed: () {
            // Xử lý khi bấm nút
          },
        ),
      ),

      body: Container(
        width: double.infinity,
        height: double.infinity,
        color: Colors.indigo.shade900,
        padding: const EdgeInsets.symmetric(horizontal: 24.0),
        child: Column(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'Email của bạn là gì?',
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 32,
                        fontWeight: FontWeight.bold),
                  ),
                  const Text(
                    'Nhập email có thể dùng để liên hệ với bạn. Sẽ không ai nhìn '
                        'thấy thông tin này trên trang cá nhân của bạn.',
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 16,
                        fontWeight: FontWeight.normal),
                  ),

                  const SizedBox(height: 40),

                  // Email
                  TextField(
                    controller: accountController,
                    style: const TextStyle(color: Colors.white),
                    decoration: InputDecoration(
                      labelText: 'Email',
                      labelStyle: const TextStyle(color: Colors.white70),
                      enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(15.0),
                        borderSide: const BorderSide(color: Colors.white70),
                      ),
                    ),
                  ),

                  const SizedBox(height: 16),

                  // Nút tiếp
                  SizedBox(
                    width: double.infinity,
                    height: 50,
                    child: ElevatedButton(
                      onPressed: (){},
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.blue,
                        foregroundColor: Colors.white,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(40.0),
                        ),
                      ),
                      child: const Text(
                        'Tiếp',
                        style: TextStyle(fontSize: 18),
                      ),
                    ),
                  ),

                  const SizedBox(height: 16),

                  // Nút tạo tài khoản
                  SizedBox(
                    width: double.infinity,
                    height: 50,
                    child: OutlinedButton(
                      onPressed: _register,
                      style: OutlinedButton.styleFrom(
                        side: BorderSide(color: Colors.blue.shade400, width: 2),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(40.0),
                        ),
                        foregroundColor: Colors.blue.shade400,
                        backgroundColor: Colors.transparent,
                      ),
                      child: const Text(
                        'Đăng kí bằng số di động',
                        style: TextStyle(fontSize: 18),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            // Quên mật khẩu
            Align(
              alignment: Alignment.center,
              child: TextButton(
                onPressed: (){},
                style: TextButton.styleFrom(
                  foregroundColor: Colors.blue,
                  backgroundColor: Colors.transparent,
                ),
                child: const Text('Tôi đã có tài khoản rồi', style: TextStyle(fontSize: 18),),
              ),
            ),
            const SizedBox(height: 20),
          ],
        ),
      ),
    );
  }
}
