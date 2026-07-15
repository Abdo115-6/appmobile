import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';
import '../models/auth_response.dart';
import '../models/login_request.dart';
import '../models/article.dart';
import '../models/article_stock.dart';
import '../models/inventory_request.dart';

class ApiService {
  // For emulator: 'http://10.0.2.2:8080/'
  // For physical device: use your PC's LAN IP
  static const String _baseUrl = 'http://192.168.1.53:8080/';

  Future<AuthResponse> login(LoginRequest request) async {
    final res = await http.post(
      Uri.parse('${_baseUrl}api/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );
    return AuthResponse.fromJson(jsonDecode(res.body));
  }

  Future<List<Article>> getArticles() async {
    final res = await http.get(Uri.parse('${_baseUrl}api/articles'));
    return (jsonDecode(res.body) as List).map((e) => Article.fromJson(e)).toList();
  }

  Future<List<Article>> searchArticles(String query) async {
    final res = await http.get(Uri.parse('${_baseUrl}api/articles/search?q=$query'));
    return (jsonDecode(res.body) as List).map((e) => Article.fromJson(e)).toList();
  }

  Future<Article> getArticleByBarcode(String ean) async {
    final ref = ean.trim().split(' ')[0];
    final res = await http.get(Uri.parse('${_baseUrl}api/articles/barcode/$ref'));
    return Article.fromJson(jsonDecode(res.body));
  }

  Future<List<ArticleStock>> getArticleStocks(int articleId) async {
    final res = await http.get(Uri.parse('${_baseUrl}api/articles/$articleId/stocks'));
    return (jsonDecode(res.body) as List).map((e) => ArticleStock.fromJson(e)).toList();
  }

  Future<void> submitInventory(InventoryRequest request) async {
    await http.post(
      Uri.parse('${_baseUrl}api/inventory'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );
  }

  Future<File> downloadInventory(String? depot, String? equipe, String? zone, String format) async {
    final params = <String, String>{'format': format};
    if (depot != null) params['depot'] = depot;
    if (equipe != null) params['equipe'] = equipe;
    if (zone != null) params['zone'] = zone;

    final uri = Uri.parse('${_baseUrl}api/inventory/export').replace(queryParameters: params);
    final res = await http.get(uri);
    final dir = await getTemporaryDirectory();
    final ext = format == 'xlsx' ? '.xlsx' : '.csv';
    final file = File('${dir.path}/inventory$ext');
    await file.writeAsBytes(res.bodyBytes);
    return file;
  }
}
