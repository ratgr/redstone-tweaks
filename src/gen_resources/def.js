module.exports = {
  capacitorTiers: ["iron","golden","diamond","emerald"],
  capacitorIngredients: ['iron_ingot','gold_ingot','diamond','emerald'],
  torchTypes: ['', 'redstone_'],
  analogTypes: ['block', 'lamp'],
  analogIngredients: ['"tag": "redstonetweaks:redstone_block"', '"item": "minecraft:redstone_lamp"'],
  simpleBlocks: [ 'reinforced_cobblestone' ],
  simpleItems: [ 'flint_and_redstone', 'hopper_pipe' ],
  simpleBlockItems: [ 'reinforced_cobblestone', 'translocator' ],

  simpleTextures: {
    'reinforced_cobblestone': 'block/piston_bottom'
  }
}

module.exports.simpleDropsSelf = [
  'reinforced_cobblestone',
  'translocator',
  'hopper_pipe',
  ...module.exports.analogTypes.map(t => 'analog_redstone_'+t),
  ...module.exports.capacitorTiers.map(t => t+'_redstone_capacitor'),
  ...module.exports.torchTypes.map(t => t+'torch_lever'),
]
