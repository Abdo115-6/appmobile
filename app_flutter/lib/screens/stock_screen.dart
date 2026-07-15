import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../models/article_stock.dart';

class StockScreen extends StatefulWidget {
  final int articleId;
  const StockScreen({super.key, required this.articleId});

  @override
  State<StockScreen> createState() => _StockScreenState();
}

class _StockScreenState extends State<StockScreen> {
  final _api = ApiService();
  List<ArticleStock> _stocks = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    try {
      _stocks = await _api.getArticleStocks(widget.articleId);
    } catch (_) {}
    setState(() => _loading = false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Stock Details')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _load,
              child: ListView.builder(
                itemCount: _stocks.length,
                itemBuilder: (_, i) {
                  final s = _stocks[i];
                  return Card(
                    margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                        Text(s.siteName ?? '', style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                        const SizedBox(height: 8),
                        Text('Quantity: ${s.quantity ?? 0}'),
                        Text('À louer: ${s.quantiteALouer ?? 0}'),
                        if (s.prix != null)
                          Text('Price: ${s.prix!.toStringAsFixed(2)} MAD'),
                      ]),
                    ),
                  );
                },
              ),
            ),
    );
  }
}
