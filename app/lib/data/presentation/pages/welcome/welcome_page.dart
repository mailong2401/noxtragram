
import 'package:flutter/material.dart';
import 'package:noxtragram/data/presentation/pages/auth/login_page.dart';

class WelcomePage extends StatelessWidget {
  WelcomePage({super.key});

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
                    'Tham gia Ystagram',
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 32,
                        fontWeight: FontWeight.bold),
                  ),
                  const Text(
                    'Chia sẻ điều bạn thích với những người hiểu bạn.',
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 16,
                        fontWeight: FontWeight.normal),
                  ),
                ],
              ),
            ),
            // Nút bắt đầu
            SizedBox(
              width: double.infinity,
              height: 50,
              child: ElevatedButton(
                onPressed: () {
                  // Khi nhấn nút -> chuyển sang LoginPage
                  Navigator.push(
                    context,
                    MaterialPageRoute(builder: (context) => LoginPage()),
                  );
                },
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue,
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(40.0),
                  ),
                ),
                child: const Text(
                  'Bắt đầu',
                  style: TextStyle(fontSize: 18),
                ),
              ),
            ),
            const SizedBox(height: 20,),
            // Nút đã có tài khoản
            SizedBox(
              width: double.infinity,
              height: 50,
              child: OutlinedButton(
                onPressed: (){},
                style: OutlinedButton.styleFrom(
                  side: BorderSide(color: Colors.blue.shade400, width: 2),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(40.0),
                  ),
                  foregroundColor: Colors.blue.shade400,
                  backgroundColor: Colors.transparent,
                ),
                child: const Text(
                  'Tôi đã có tài khoản rồi',
                  style: TextStyle(fontSize: 18),
                ),
              ),
            ),
            const SizedBox(height: 50,), // padding dưới cùng
          ],
        ),
      ),
    );
  }
}
