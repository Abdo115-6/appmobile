import 'package:flutter/material.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import '../services/api_service.dart';
import '../models/article.dart';
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

  Article? _selectedArticle;
  int _cartons = 0;
  String? _error;
  bool _scanning = false;

  final _sites = ['FBC', 'FMD'];

  @override
  void dispose() {
    _siteController.dispose();
    _clientController.dispose();
    _articleController.dispose();
    _quantityController.dispose();
    super.dispose();
  }

  void _lookupArticle(String input) async {
    if (input.length < 3) return;
    try {
      final article = await _api.getArticleByBarcode(input.trim().split(' ')[0]);
      setState(() {
        _selectedArticle = article;
        _error = null;
      });
      _articleController.text = article.nom;
      _calculateCartons();
    } catch (e) {
      setState(() => _error = 'Article not found');
    }
  }

  void _calculateCartons() {
    final qty = int.tryParse(_quantityController.text);
    if (_selectedArticle == null || _selectedArticle!.coefficient == null || _selectedArticle!.coefficient == 0 || qty == null) {
      setState(() => _cartons = 0);
      return;
    }
    setState(() {
      _cartons = (qty / _selectedArticle!.coefficient).ceil();
    });
  }

  void _generate() {
    final site = _siteController.text.trim();
    final client = _clientController.text.trim();
    final qty = int.tryParse(_quantityController.text);

    if (site.isEmpty || client.isEmpty || _selectedArticle == null || qty == null) {
      setState(() => _error = 'Please fill all fields');
      return;
    }

    final coeff = _selectedArticle!.coefficient ?? 1;
    final summary = 'Site: $site\n'
        'Client: $client\n'
        'Article: ${_selectedArticle!.nom} (${_selectedArticle!.ref})\n'
        'Quantity: $qty\n'
        'Coefficient: $coeff\n'
        'Cartons: $_cartons';

    showDialog(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Devi Generated'),
        content: Text(summary),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('OK')),
        ],
      ),
    );
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
              _articleController.text = barcode!.rawValue!;
              _lookupArticle(barcode.rawValue!);
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
                decoration: const InputDecoration(labelText: 'Client', border: OutlineInputBorder()),
              ),

              const SizedBox(height: 16),
              Row(children: [
                Expanded(
                  child: TextField(
                    controller: _articleController,
                    decoration: const InputDecoration(labelText: 'Article', border: OutlineInputBorder()),
                    onChanged: _lookupArticle,
                  ),
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

              const SizedBox(height: 20),
              Row(children: [
                Expanded(
                  child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
                    Text('Coefficient', style: TextStyle(color: Colors.grey[600], fontSize: 11)),
                    const SizedBox(height: 4),
                    Text(
                      _selectedArticle?.coefficient?.toString() ?? '-',
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
              ]),

              const SizedBox(height: 28),
              SizedBox(
                width: double.infinity,
                height: 56,
                child: ElevatedButton(
                  onPressed: _generate,
                  child: const Text('Generate', style: TextStyle(fontSize: 16)),
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
