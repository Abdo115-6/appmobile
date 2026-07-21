import 'dart:async';
import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import '../services/api_service.dart';
import '../models/article.dart';
import '../models/bp_customer.dart';
import '../models/devis_request.dart';
import '../services/user_session.dart';
import '../widgets/drawer_menu.dart';

class DevisScreen extends StatefulWidget {
  const DevisScreen({super.key});

  @override
  State<DevisScreen> createState() => _DevisScreenState();
}

class _DevisScreenState extends State<DevisScreen> {
  final _api = ApiService();
  final _siteController = TextEditingController();
  final _clientController = TextEditingController();
  final _articleController = TextEditingController();
  final _quantityController = TextEditingController();
  final _priceController = TextEditingController();

  Article? _selectedArticle;
  BpCustomer? _selectedClient;
  List<BpCustomer> _clientResults = [];
  List<Article> _articleResults = [];
  int _cartons = 0;
  double _adjustedQty = 0;
  String? _error;
  bool _saving = false;
  Timer? _clientDebounce;
  Timer? _articleDebounce;
  bool _showClientSuggestions = false;
  bool _showArticleSuggestions = false;

  final _sites = ['FBC', 'FMD'];

  @override
  void dispose() {
    _siteController.dispose();
    _clientController.dispose();
    _articleController.dispose();
    _quantityController.dispose();
    _priceController.dispose();
    _clientDebounce?.cancel();
    _articleDebounce?.cancel();
    super.dispose();
  }

  void _searchClients(String q) {
    _clientDebounce?.cancel();
    if (q.length < 2) {
      setState(() { _showClientSuggestions = false; _selectedClient = null; });
      return;
    }
    _clientDebounce = Timer(const Duration(milliseconds: 400), () async {
      try {
        final results = await _api.searchClients(q);
        if (mounted) {
          setState(() {
            _clientResults = results;
            _showClientSuggestions = results.isNotEmpty;
          });
        }
      } catch (_) {}
    });
  }

  void _selectClient(BpCustomer client) {
    setState(() {
      _selectedClient = client;
      _clientController.text = '${client.code} - ${client.name}';
      _showClientSuggestions = false;
      _error = null;
    });
  }

  void _searchArticles(String q) {
    _articleDebounce?.cancel();
    if (q.length < 2) {
      setState(() { _showArticleSuggestions = false; _selectedArticle = null; });
      return;
    }
    _articleDebounce = Timer(const Duration(milliseconds: 400), () async {
      try {
        final results = await _api.searchArticles(q);
        if (mounted) {
          setState(() {
            _articleResults = results;
            _showArticleSuggestions = results.isNotEmpty;
          });
        }
      } catch (_) {}
    });
  }

  void _selectArticle(Article article) {
    setState(() {
      _selectedArticle = article;
      final sau = article.sau != null ? ' (${article.sau})' : '';
      _articleController.text = '${article.nom}$sau';
      _showArticleSuggestions = false;
      _error = null;
    });
    _calculateCartons();
  }

  void _fetchArticleByBarcode(String barcode) async {
    try {
      final article = await _api.getArticleByBarcode(barcode);
      setState(() {
        _selectedArticle = article;
        _showArticleSuggestions = false;
        _error = null;
      });
      final sau = article.sau != null ? ' (${article.sau})' : '';
      _articleController.text = '${article.nom}$sau';
      _calculateCartons();
    } catch (e) {
      setState(() => _error = 'Article not found');
    }
  }

  void _calculateCartons() {
    final qty = double.tryParse(_quantityController.text);
    final coeff = _selectedArticle?.coefficient;
    if (coeff == null || coeff <= 0 || qty == null) {
      setState(() { _cartons = 0; _adjustedQty = 0; });
      return;
    }
    final cartons = (qty / coeff).ceil();
    final adjustedQty = cartons * coeff;
    setState(() {
      _cartons = cartons;
      _adjustedQty = adjustedQty;
    });
  }

  Future<void> _generate() async {
    final site = _siteController.text.trim();
    final qty = double.tryParse(_quantityController.text);

    if (site.isEmpty || _selectedClient == null || _selectedArticle == null || qty == null) {
      setState(() => _error = 'Please fill all fields');
      return;
    }

    setState(() { _saving = true; _error = null; });

    try {
      final coeff = _selectedArticle!.coefficient ?? 1;
      final cartons = (qty / coeff).ceil();
      final adjustedQty = cartons * coeff;

      final price = double.tryParse(_priceController.text) ?? 0;

      await _api.submitDevis(DevisRequest(
        site: site,
        clientCode: _selectedClient!.code,
        clientName: _selectedClient!.name,
        articleRef: _selectedArticle!.ref,
        articleName: _selectedArticle!.nom,
        quantity: adjustedQty,
        price: price,
        unit: _selectedArticle!.sau ?? 'UN',
        coefficient: coeff,
        cartons: cartons,
        creusr0: UserSession().email,
      ));
      if (mounted) {
        setState(() { _saving = false; });
        _clearForm();
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('Devi saved successfully!')),
        );
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _saving = false;
          _error = 'Failed to save: $e';
        });
      }
    }
  }

  void _clearForm() {
    _siteController.clear();
    _clientController.clear();
    _articleController.clear();
    _quantityController.clear();
    _priceController.clear();
    setState(() {
      _selectedArticle = null;
      _selectedClient = null;
      _clientResults = [];
      _articleResults = [];
      _cartons = 0;
      _adjustedQty = 0;
      _showClientSuggestions = false;
      _showArticleSuggestions = false;
    });
  }

  void _startScan() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (_) => SizedBox(
        height: 400,
        child: MobileScanner(
          onDetect: (capture) {
            final barcode = capture.barcodes.firstOrNull;
            if (barcode?.rawValue != null) {
              Navigator.pop(context);
              _fetchArticleByBarcode(barcode!.rawValue!);
            }
          },
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Devi')),
      drawer: const DrawerMenu(currentRoute: '/devi'),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Card(
          margin: EdgeInsets.zero,
          child: Padding(
            padding: const EdgeInsets.all(24),
            child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
              const Text('Devi', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 20),

              DropdownButtonFormField<String>(
                value: _siteController.text.isEmpty ? null : _siteController.text,
                decoration: const InputDecoration(labelText: 'Site', border: OutlineInputBorder()),
                items: _sites.map((s) => DropdownMenuItem(value: s, child: Text(s))).toList(),
                onChanged: (v) => _siteController.text = v ?? '',
              ),

              const SizedBox(height: 16),
              TextField(
                controller: _clientController,
                decoration: const InputDecoration(
                  labelText: 'Client',
                  border: OutlineInputBorder(),
                ),
                onChanged: _searchClients,
              ),
              if (_showClientSuggestions)
                Container(
                  constraints: const BoxConstraints(maxHeight: 200),
                  decoration: BoxDecoration(
                    color: Colors.grey[900],
                    border: Border.all(color: Colors.grey[700]!),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: ListView.builder(
                    shrinkWrap: true,
                    itemCount: _clientResults.length,
                    itemBuilder: (_, i) {
                      final c = _clientResults[i];
                      return ListTile(
                        dense: true,
                        title: Text('${c.code} - ${c.name}'),
                        onTap: () => _selectClient(c),
                      );
                    },
                  ),
                ),

              const SizedBox(height: 16),
              Row(children: [
                Expanded(
                  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    TextField(
                      controller: _articleController,
                      decoration: const InputDecoration(labelText: 'Article', border: OutlineInputBorder()),
                      onChanged: _searchArticles,
                    ),
                    if (_showArticleSuggestions)
                      Container(
                        constraints: const BoxConstraints(maxHeight: 200),
                        decoration: BoxDecoration(
                          color: Colors.grey[900],
                          border: Border.all(color: Colors.grey[700]!),
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: ListView.builder(
                          shrinkWrap: true,
                          itemCount: _articleResults.length,
                          itemBuilder: (_, i) {
                            final a = _articleResults[i];
                            final sau = a.sau != null ? ' (${a.sau})' : '';
                            return ListTile(
                              dense: true,
                              title: Text('${a.nom}$sau'),
                              subtitle: Text(a.ref ?? '', style: TextStyle(color: Colors.grey[500], fontSize: 12)),
                              onTap: () => _selectArticle(a),
                            );
                          },
                        ),
                      ),
                  ]),
                ),
                const SizedBox(width: 8),
                IconButton(
                  icon: const Icon(Icons.qr_code_scanner),
                  onPressed: _startScan,
                ),
              ]),

              const SizedBox(height: 16),
              TextField(
                controller: _quantityController,
                decoration: const InputDecoration(labelText: 'Quantity', border: OutlineInputBorder()),
                keyboardType: TextInputType.number,
                onChanged: (_) => _calculateCartons(),
              ),

              const SizedBox(height: 16),
              TextField(
                controller: _priceController,
                decoration: const InputDecoration(labelText: 'Price (MAD)', border: OutlineInputBorder()),
                keyboardType: const TextInputType.numberWithOptions(decimal: true),
              ),

              const SizedBox(height: 20),
              Row(children: [
                Expanded(
                  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text('Coefficient', style: TextStyle(color: Colors.grey[600], fontSize: 11)),
                    const SizedBox(height: 4),
                    Text(
                      _selectedArticle?.coefficient?.toStringAsFixed(2) ?? '-',
                      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                    ),
                  ]),
                ),
                Expanded(
                  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text('Cartons', style: TextStyle(color: Colors.grey[600], fontSize: 11)),
                    const SizedBox(height: 4),
                    Text(
                      '$_cartons',
                      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                    ),
                  ]),
                ),
                Expanded(
                  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text('Adjusted Qty', style: TextStyle(color: Colors.grey[600], fontSize: 11)),
                    const SizedBox(height: 4),
                    Text(
                      _adjustedQty == 0 && _selectedArticle == null ? '-' : _adjustedQty.toStringAsFixed(2),
                      style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                    ),
                  ]),
                ),
              ]),

              const SizedBox(height: 28),
              SizedBox(
                width: double.infinity,
                height: 56,
                child: ElevatedButton(
                  onPressed: _saving ? null : _generate,
                  child: _saving
                      ? const SizedBox(width: 24, height: 24, child: CircularProgressIndicator(strokeWidth: 2))
                      : const Text('Generate', style: TextStyle(fontSize: 16)),
                ),
              ),

              if (_error != null)
                Padding(
                  padding: const EdgeInsets.only(top: 12),
                  child: Text(_error!, style: const TextStyle(color: Colors.red)),
                ),
            ]),
          ),
        ),
      ),
    );
  }
}
