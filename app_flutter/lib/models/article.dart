class Article {
  final int id;
  final String nom;
  final int quantiteALouer;
  final String? ref;
  final int? coefficient;

  Article({required this.id, required this.nom, required this.quantiteALouer, this.ref, this.coefficient});

  factory Article.fromJson(Map<String, dynamic> json) => Article(
    id: json['id'] as int,
    nom: json['nom'] as String,
    quantiteALouer: json['quantiteALouer'] as int,
    ref: json['ref'] as String?,
    coefficient: json['coefficient'] as int?,
  );
}
