import 'package:flutter/material.dart';
import '../services/user_session.dart';
import '../screens/login_screen.dart';
import '../screens/articles_screen.dart';
import '../screens/inventory_screen.dart';

class DrawerMenu extends StatelessWidget {
  final String currentRoute;
  const DrawerMenu({super.key, required this.currentRoute});

  @override
  Widget build(BuildContext context) {
    final session = UserSession();
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          DrawerHeader(
            decoration: const BoxDecoration(color: Colors.indigo),
            child: Text('Mallzellij', style: TextStyle(color: Colors.white, fontSize: 24)),
          ),
          ListTile(
            leading: const Icon(Icons.inventory),
            title: const Text('Articles'),
            selected: currentRoute == '/articles',
            onTap: () {
              Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const ArticlesScreen()));
            },
          ),
          if (session.isAdmin)
            ListTile(
              leading: const Icon(Icons.edit_note),
              title: const Text('Inventory'),
              selected: currentRoute == '/inventory',
              onTap: () {
                Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const InventoryScreen()));
              },
            ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout),
            title: const Text('Logout'),
            onTap: () {
              session.logout();
              Navigator.pushAndRemoveUntil(
                context,
                MaterialPageRoute(builder: (_) => const LoginScreen()),
                (route) => false,
              );
            },
          ),
        ],
      ),
    );
  }
}
