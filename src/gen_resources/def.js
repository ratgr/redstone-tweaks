module.exports = {
  analogTypes: ['block', 'lamp'],
  analogIngredients: ['"tag": "redstonetweaks:redstone_block"', '"item": "minecraft:redstone_lamp"'],
  simpleItems: [ 'flint_and_redstone', 'hopper_pipe' ],
}

module.exports.simpleDropsSelf = [
  ...module.exports.analogTypes.map(t => 'analog_redstone_'+t),
]
