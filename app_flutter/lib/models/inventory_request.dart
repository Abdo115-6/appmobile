class InventoryRequest {
  final String ynum0;
  final String ydepot0;
  final String yequipe0;
  final String yzone0;
  final String yitmref0;
  final double yqtyplt0;
  final double yqtycrt0;
  final double yqtymtr0;
  final String creusr0;

  InventoryRequest({
    required this.ynum0,
    required this.ydepot0,
    required this.yequipe0,
    required this.yzone0,
    required this.yitmref0,
    required this.yqtyplt0,
    required this.yqtycrt0,
    required this.yqtymtr0,
    required this.creusr0,
  });

  Map<String, dynamic> toJson() => {
    'ynum0': ynum0,
    'ydepot0': ydepot0,
    'yequipe0': yequipe0,
    'yzone0': yzone0,
    'yitmref0': yitmref0,
    'yqtyplt0': yqtyplt0,
    'yqtycrt0': yqtycrt0,
    'yqtymtr0': yqtymtr0,
    'creusr0': creusr0,
  };
}
