class Article {
  final int id;
  final String nom;
  final int quantiteALouer;
  final String? ref;
  final double? coefficient;
  final String? sau;

  Article({required this.id, required this.nom, required this.quantiteALouer, this.ref, this.coefficient, this.sau});

  factory Article.fromJson(Map<String, dynamic> json) => Article(
    id: json['id'] as int,
    nom: json['nom'] as String,
    quantiteALouer: json['quantiteALouer'] as int,
    ref: json['ref'] as String?,
    coefficient: (json['coefficient'] as num?)?.toDouble(),
    sau: json['sau'] as String?,
  );
}
