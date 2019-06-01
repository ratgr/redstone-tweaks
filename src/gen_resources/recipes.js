const gen = require('./gen')
const def = require('./def')

function genDyedWireRecipes() {
  gen.ensureDir(gen.data, 'recipes/dyed_redstone')
  for(let color of def.dyeColors) {
    gen.write(gen.data, `recipes/dyed_redstone/${color}.json`,
      gen.template('recipes/dyed_wire')
        .replace(/%color/g, color)
        .replace(/%output/g, color === 'red' ? 'minecraft:redstone' : `redstonetweaks:${color}_redstone`))
  }
}

function genCapacitorRecipes() {
  gen.ensureDir(gen.data, 'recipes/redstone_capacitor')
  for(let tier of def.capacitorTiers) {
    gen.write(gen.data, `recipes/redstone_capacitor/${tier}.json`,
      gen.template('recipes/capacitor')
        .replace(/%tiering/g, def.capacitorIngredients[def.capacitorTiers.indexOf(tier)])
        .replace(/%tier/g, tier))
  }
}

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
  genDyedWireRecipes,
  genCapacitorRecipes,
  genTorchLeverRecipes,
  genAnalogRecipes
]
