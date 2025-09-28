
import 'package:flutter/material.dart';

class LoginPage extends StatelessWidget {
  LoginPage({super.key});

  final TextEditingController accountController = TextEditingController();
  final TextEditingController passwordController = TextEditingController();

  void _login() {
    final account = accountController.text;
    final password = passwordController.text;
    print('Account: $account');
    print('Password: $password');
    // TODO: Xử lý logic đăng nhập
  }

  void _forgotPassword() {
    print('Forgot password pressed');
    // TODO: Xử lý quên mật khẩu
  }

  void _register() {
    print('Register pressed');
    // TODO: Điều hướng sang màn hình đăng ký
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        width: double.infinity,
        height: double.infinity,
        color: Colors.indigo.shade900,
        padding: const EdgeInsets.symmetric(horizontal: 24.0),
        child: Column(
          children: [
            Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Logo/Title (tuỳ chọn)
                  const Text(
                    'Ynstagram',
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 32,
                        fontWeight: FontWeight.bold),
                  ),

                  const SizedBox(height: 40),

                  // Email
                  TextField(
                    controller: accountController,
                    style: const TextStyle(color: Colors.white),
                    decoration: InputDecoration(
                      labelText: 'Tên người dùng,email/số điện thoại',
                      labelStyle: const TextStyle(color: Colors.white70),
                      enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(15.0),
                        borderSide: const BorderSide(color: Colors.white70),
                      ),
                    ),
                  ),

                  const SizedBox(height: 16),

                  // Password
                  TextField(
                    controller: passwordController,
                    obscureText: true,
                    style: const TextStyle(color: Colors.white),
                    decoration: InputDecoration(
                      labelText: 'Mật khẩu',
                      labelStyle: const TextStyle(color: Colors.white70),
                      enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(15.0),
                        borderSide: const BorderSide(color: Colors.white70),
                      ),
                    ),
                  ),

                  const SizedBox(height: 16),

                  // Nút Login
                  SizedBox(
                    width: double.infinity,
                    height: 50,
                    child: ElevatedButton(
                      onPressed: _login,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.blue,
                        foregroundColor: Colors.white,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(40.0),
                        ),
                      ),
                      child: const Text(
                        'Đăng nhập',
                        style: TextStyle(fontSize: 18),
                      ),
                    ),
                  ),

                  const SizedBox(height: 16),

                  // Quên mật khẩu
                  Align(
                    alignment: Alignment.center,
                    child: TextButton(
                      onPressed: _forgotPassword,
                      style: TextButton.styleFrom(
                        foregroundColor: Colors.white,
                        backgroundColor: Colors.transparent,
                      ),
                      child: const Text('Quên mật khẩu?', style: TextStyle(fontSize: 18),),
                    ),
                  ),
                ],
              ),
            ),
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
                  'Tạo tài khoản mới',
                  style: TextStyle(fontSize: 18),
                ),
              ),
            ),
            const SizedBox(height: 40,), // padding dưới cùng
          ],
        ),
      ),
    );
  }
}
