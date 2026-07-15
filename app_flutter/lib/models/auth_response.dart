class AuthResponse {
  final int? id;
  final String? name;
  final String? email;
  final String? role;
  final String? message;

  AuthResponse({this.id, this.name, this.email, this.role, this.message});

  factory AuthResponse.fromJson(Map<String, dynamic> json) => AuthResponse(
    id: json['id'] as int?,
    name: json['name'] as String?,
    email: json['email'] as String?,
    role: json['role'] as String?,
    message: json['message'] as String?,
  );
}
