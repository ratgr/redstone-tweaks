const gen = require('./gen')
const def = require('./def')

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
  genAnalogRecipes
]
