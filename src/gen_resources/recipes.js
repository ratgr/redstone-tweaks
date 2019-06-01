const gen = require('./gen')
const def = require('./def')

function genTorchLeverRecipes() {
  gen.ensureDir(gen.data, 'recipes/torch_lever')
  for(let type of def.torchTypes) {
    gen.write(gen.data, `recipes/torch_lever/${type.slice(0,-1) || 'normal'}.json`,
      gen.template('recipes/torch_lever')
        .replace(/%type/g, type))
  }
}

function genAnalogRecipes() {
  gen.ensureDir(gen.data, 'recipes')
  for(let type of def.analogTypes) {
    gen.write(gen.data, `recipes/analog_redstone_${type}.json`,
      gen.template('recipes/analog')
        .replace(/%coreing/g, def.analogIngredients[def.analogTypes.indexOf(type)])
        .replace(/%type/g, type))
  }
}

module.exports = [
  genTorchLeverRecipes,
  genAnalogRecipes
]
