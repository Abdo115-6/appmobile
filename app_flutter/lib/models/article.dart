class Article {
  final int id;
  final String nom;
  final int quantiteALouer;

  Article({required this.id, required this.nom, required this.quantiteALouer});

  factory Article.fromJson(Map<String, dynamic> json) => Article(
    id: json['id'] as int,
    nom: json['nom'] as String,
    quantiteALouer: json['quantiteALouer'] as int,
  );
}
