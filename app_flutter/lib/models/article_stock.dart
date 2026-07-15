class ArticleStock {
  final int? siteId;
  final String? siteName;
  final int? quantity;
  final int? quantiteALouer;
  final double? prix;

  ArticleStock({this.siteId, this.siteName, this.quantity, this.quantiteALouer, this.prix});

  factory ArticleStock.fromJson(Map<String, dynamic> json) => ArticleStock(
    siteId: json['siteId'] as int?,
    siteName: json['siteName'] as String?,
    quantity: json['quantity'] as int?,
    quantiteALouer: json['quantiteALouer'] as int?,
    prix: (json['prix'] as num?)?.toDouble(),
  );
}
