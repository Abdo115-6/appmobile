class BpCustomer {
  final String code;
  final String name;
  final String? shortName;

  BpCustomer({required this.code, required this.name, this.shortName});

  factory BpCustomer.fromJson(Map<String, dynamic> json) => BpCustomer(
    code: json['code'] as String,
    name: json['name'] as String,
    shortName: json['shortName'] as String?,
  );
}
