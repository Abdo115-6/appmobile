class DevisRequest {
  final String? site;
  final String? clientCode;
  final String? clientName;
  final String? articleRef;
  final String? articleName;
  final num? quantity;
  final num? price;
  final num? coefficient;
  final int? cartons;
  final String? creusr0;

  DevisRequest({this.site, this.clientCode, this.clientName, this.articleRef, this.articleName, this.quantity, this.price, this.coefficient, this.cartons, this.creusr0});

  Map<String, dynamic> toJson() => {
    'site': site,
    'clientCode': clientCode,
    'clientName': clientName,
    'articleRef': articleRef,
    'articleName': articleName,
    'quantity': quantity,
    'price': price,
    'coefficient': coefficient,
    'cartons': cartons,
    'creusr0': creusr0,
  };
}
