module.exports = {
  capacitorTiers: ["iron","golden","diamond","emerald"],
  capacitorIngredients: ['iron_ingot','gold_ingot','diamond','emerald'],
  torchTypes: ['', 'redstone_'],
  analogTypes: ['block', 'lamp'],
  analogIngredients: ['"tag": "redstonetweaks:redstone_block"', '"item": "minecraft:redstone_lamp"'],
  simpleItems: [ 'flint_and_redstone', 'hopper_pipe' ],
}

module.exports.simpleDropsSelf = [
  'translocator',
  'hopper_pipe',
  ...module.exports.analogTypes.map(t => 'analog_redstone_'+t),
  ...module.exports.capacitorTiers.map(t => t+'_redstone_capacitor'),
  ...module.exports.torchTypes.map(t => t+'torch_lever'),
]
