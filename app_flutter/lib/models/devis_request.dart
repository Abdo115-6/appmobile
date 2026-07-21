class DevisRequest {
  final String? site;
  final String? clientCode;
  final String? clientName;
  final String? articleRef;
  final String? articleName;
  final num? quantity;
  final num? price;
  final String? unit;
  final num? coefficient;
  final int? cartons;
  final String? creusr0;

  DevisRequest({this.site, this.clientCode, this.clientName, this.articleRef, this.articleName, this.quantity, this.price, this.unit, this.coefficient, this.cartons, this.creusr0});

  Map<String, dynamic> toJson() => {
    'site': site,
    'clientCode': clientCode,
    'clientName': clientName,
    'articleRef': articleRef,
    'articleName': articleName,
    'quantity': quantity,
    'price': price,
    'unit': unit,
    'coefficient': coefficient,
    'cartons': cartons,
    'creusr0': creusr0,
  };
}
