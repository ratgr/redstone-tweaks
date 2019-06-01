module.exports = {
  capacitorTiers: ["iron","golden","diamond","emerald"],
  capacitorIngredients: ['iron_ingot','gold_ingot','diamond','emerald'],
  torchTypes: ['', 'redstone_'],
  analogTypes: ['block', 'lamp'],
  analogIngredients: ['"tag": "redstonetweaks:redstone_block"', '"item": "minecraft:redstone_lamp"'],
  
  dyeColors: [
    "white","orange","magenta","light_blue",
    "yellow","lime","pink","gray",
    "light_gray","cyan","purple","blue",
    "brown","green","red","black"
  ],

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
  ...module.exports.dyeColors.filter(c => c !== 'red').map(c => c+'_redstone_block')
]

module.exports.compautoStrongRed = ['white','orange','magenta','yellow','pink']
module.exports.compautoStrongGreen = ['white','light_blue','yellow','lime']
module.exports.compautoStrongBlue = ['white','magenta','light_blue','pink','purple']
module.exports.compautoRed = [...module.exports.compautoStrongRed,'lime','light_gray','purple','brown','red']
module.exports.compautoGreen = [...module.exports.compautoStrongGreen,'orange','pink','light_gray','cyan','green']
module.exports.compautoBlue = [...module.exports.compautoStrongBlue,'light_gray','cyan','blue']
