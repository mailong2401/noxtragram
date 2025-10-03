import 'package:flutter/material.dart';
import 'package:noxtragram/data/presentation/pages/home/home_page.dart';

import 'data/presentation/pages/auth/login_page.dart';
import 'data/presentation/pages/auth/register_page/register_page_email.dart';
import 'data/presentation/pages/auth/register_page/register_page_phone.dart';
import 'data/presentation/pages/chat/chat_list_page/chat_list_page.dart';
import 'data/presentation/pages/welcome/welcome_page.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext context) {
    return MaterialApp(home: LoginPage());
  }
}
