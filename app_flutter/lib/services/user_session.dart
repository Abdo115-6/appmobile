import 'package:shared_preferences/shared_preferences.dart';
import '../models/auth_response.dart';

class UserSession {
  static final UserSession _instance = UserSession._();
  factory UserSession() => _instance;
  UserSession._();

  int? _id;
  String? _name;
  String? _email;
  String? _role;

  int? get id => _id;
  String? get name => _name;
  String? get email => _email;
  String? get role => _role;
  bool get isAdmin => _role?.toLowerCase() == 'admin';

  void login(AuthResponse r) {
    _id = r.id;
    _name = r.name;
    _email = r.email;
    _role = r.role;
  }

  void logout() {
    _id = null;
    _name = null;
    _email = null;
    _role = null;
  }
}
