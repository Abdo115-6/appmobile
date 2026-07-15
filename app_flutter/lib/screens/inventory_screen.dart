import 'dart:io';
import 'package:flutter/material.dart';
import 'package:open_file/open_file.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import '../services/api_service.dart';
import '../services/user_session.dart';
import '../models/inventory_request.dart';
import '../widgets/drawer_menu.dart';

class InventoryScreen extends StatefulWidget {
  const InventoryScreen({super.key});

  @override
  State<InventoryScreen> createState() => _InventoryScreenState();
}

class _InventoryScreenState extends State<InventoryScreen> {
  final _api = ApiService();
  final _articleCtrl = TextEditingController();
  final _palletCtrl = TextEditingController();
  final _cartonCtrl = TextEditingController();
  final _metreCtrl = TextEditingController();

  String _equipe = '';
  String _depot = '';
  String _zone = '';

  final _equipes = ['Équipe 1', 'Équipe 2', 'Équipe 3'];
  final _depots = ['Dépôt 1', 'Dépôt 2', 'Dépôt 3'];
  final _zones = ['Zone A', 'Zone B', 'Zone C'];

  bool _loading = false;

  void _submit() {
    if (_articleCtrl.text.isEmpty || _palletCtrl.text.isEmpty || _cartonCtrl.text.isEmpty || _metreCtrl.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Fill all fields')));
      return;
    }

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => AlertDialog(
        title: const Text('Confirm Inventory'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          _row('Équipe', _equipe),
          _row('Dépôt', _depot),
          _row('Zone', _zone),
          _row('Article', _articleCtrl.text),
          _row('Pallet', _palletCtrl.text),
          _row('Carton', _cartonCtrl.text),
          _row('M²', _metreCtrl.text),
          _row('Opérateur', UserSession().email ?? ''),
        ]),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Edit')),
          ElevatedButton(onPressed: () {
            Navigator.pop(context);
            _send();
          }, child: const Text('Confirm')),
        ],
      ),
    );
  }

  Widget _row(String label, String value) => Padding(
    padding: const EdgeInsets.symmetric(vertical: 4),
    child: Row(children: [SizedBox(width: 100, child: Text('$label:')), Text(value)]),
  );

  Future<void> _send() async {
    setState(() => _loading = true);
    try {
      await _api.submitInventory(InventoryRequest(
        ynum0: '',
        ydepot0: _depot,
        yequipe0: _equipe,
        yzone0: _zone,
        yitmref0: _articleCtrl.text,
        yqtyplt0: double.tryParse(_palletCtrl.text) ?? 0,
        yqtycrt0: double.tryParse(_cartonCtrl.text) ?? 0,
        yqtymtr0: double.tryParse(_metreCtrl.text) ?? 0,
        creusr0: UserSession().email ?? '',
      ));
      _articleCtrl.clear();
      _palletCtrl.clear();
      _cartonCtrl.clear();
      _metreCtrl.clear();
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Inventory submitted')));
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Submit failed')));
    }
    setState(() => _loading = false);
  }

  Future<void> _download() async {
    final format = await showDialog<String>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('Choose format'),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          ListTile(title: const Text('CSV'), onTap: () => Navigator.pop(context, 'csv')),
          ListTile(title: const Text('XLSX'), onTap: () => Navigator.pop(context, 'xlsx')),
        ]),
      ),
    );
    if (format == null) return;

    setState(() => _loading = true);
    try {
      final file = await _api.downloadInventory(
        _depot.isEmpty ? null : _depot,
        _equipe.isEmpty ? null : _equipe,
        _zone.isEmpty ? null : _zone,
        format,
      );
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Saved to ${file.path}')));
      OpenFile.open(file.path);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Download failed')));
    }
    setState(() => _loading = false);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Inventory')),
      drawer: const DrawerMenu(currentRoute: '/inventory'),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(children: [
          DropdownButtonFormField<String>(
            value: _equipe.isEmpty ? null : _equipe,
            items: _equipes.map((e) => DropdownMenuItem(value: e, child: Text(e))).toList(),
            onChanged: (v) => setState(() => _equipe = v ?? ''),
            decoration: const InputDecoration(labelText: 'Équipe'),
          ),
          const SizedBox(height: 12),
          DropdownButtonFormField<String>(
            value: _depot.isEmpty ? null : _depot,
            items: _depots.map((e) => DropdownMenuItem(value: e, child: Text(e))).toList(),
            onChanged: (v) => setState(() => _depot = v ?? ''),
            decoration: const InputDecoration(labelText: 'Dépôt'),
          ),
          const SizedBox(height: 12),
          DropdownButtonFormField<String>(
            value: _zone.isEmpty ? null : _zone,
            items: _zones.map((e) => DropdownMenuItem(value: e, child: Text(e))).toList(),
            onChanged: (v) => setState(() => _zone = v ?? ''),
            decoration: const InputDecoration(labelText: 'Zone'),
          ),
          const SizedBox(height: 12),
          Row(children: [
            Expanded(child: TextField(controller: _articleCtrl, decoration: const InputDecoration(labelText: 'Article'))),
            IconButton(
              icon: const Icon(Icons.qr_code_scanner),
              onPressed: () async {
                final result = await Navigator.push<String>(context, MaterialPageRoute(builder: (_) => const _ScannerScreen()));
                if (result != null) _articleCtrl.text = result;
              },
            ),
          ]),
          const SizedBox(height: 12),
          TextField(controller: _palletCtrl, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'Pallet')),
          const SizedBox(height: 12),
          TextField(controller: _cartonCtrl, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'Carton')),
          const SizedBox(height: 12),
          TextField(controller: _metreCtrl, keyboardType: TextInputType.number, decoration: const InputDecoration(labelText: 'Mètre Carré (m²)')),
          const SizedBox(height: 24),
          SizedBox(width: double.infinity, child: ElevatedButton(
            onPressed: _loading ? null : _submit,
            child: _loading ? const CircularProgressIndicator() : const Text('Submit'),
          )),
          const SizedBox(height: 12),
          SizedBox(width: double.infinity, child: OutlinedButton(
            onPressed: _loading ? null : _download,
            child: const Text('Download'),
          )),
        ]),
      ),
    );
  }
}

class _ScannerScreen extends StatefulWidget {
  const _ScannerScreen();

  @override
  State<_ScannerScreen> createState() => _ScannerScreenState();
}

class _ScannerScreenState extends State<_ScannerScreen> {
  bool _scanned = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Scan QR')),
      body: MobileScanner(onDetect: (capture) {
        if (_scanned) return;
        final barcode = capture.barcodes.firstOrNull;
        if (barcode?.rawValue != null) {
          _scanned = true;
          Navigator.pop(context, barcode!.rawValue);
        }
      }),
    );
  }
}
