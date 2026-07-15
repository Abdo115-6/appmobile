import 'package:flutter/material.dart';
import 'screens/login_screen.dart';

void main() => runApp(const MallzellijApp());

class MallzellijApp extends StatelessWidget {
  const MallzellijApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Mallzellij',
      debugShowCheckedModeBanner: false,
      theme: ThemeData.dark().copyWith(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo, brightness: Brightness.dark),
      ),
      home: const LoginScreen(),
    );
  }
}
