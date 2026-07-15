import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/article.dart';
import 'stock_screen.dart';
import '../widgets/drawer_menu.dart';

class ArticlesScreen extends StatefulWidget {
  const ArticlesScreen({super.key});

  @override
  State<ArticlesScreen> createState() => _ArticlesScreenState();
}

class _ArticlesScreenState extends State<ArticlesScreen> {
  final _api = ApiService();
  List<Article> _articles = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    try {
      _articles = await _api.getArticles();
    } catch (_) {}
    setState(() => _loading = false);
  }

  Future<void> _search(String q) async {
    setState(() => _loading = true);
    try {
      _articles = q.isEmpty ? await _api.getArticles() : await _api.searchArticles(q);
    } catch (_) {}
    setState(() => _loading = false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Articles')),
      drawer: const DrawerMenu(currentRoute: '/articles'),
      body: Column(children: [
        Padding(
          padding: const EdgeInsets.all(8),
          child: TextField(
            onChanged: (v) => _search(v),
            decoration: const InputDecoration(
              hintText: 'Search...',
              prefixIcon: Icon(Icons.search),
              border: OutlineInputBorder(),
            ),
          ),
        ),
        Expanded(
          child: _loading
              ? const Center(child: CircularProgressIndicator())
              : RefreshIndicator(
                  onRefresh: _load,
                  child: ListView.builder(
                    itemCount: _articles.length,
                    itemBuilder: (_, i) {
                      final a = _articles[i];
                      return ListTile(
                        title: Text(a.nom),
                        subtitle: Text('À louer: ${a.quantiteALouer}'),
                        trailing: const Icon(Icons.chevron_right),
                        onTap: () => Navigator.push(context, MaterialPageRoute(builder: (_) => StockScreen(articleId: a.id))),
                      );
                    },
                  ),
                ),
        ),
      ]),
    );
  }
}
