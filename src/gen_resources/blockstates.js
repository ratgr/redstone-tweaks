const gen = require('./gen')
const def = require('./def')

function genAnalogBlockstates() {
  gen.ensureDir(gen.assets, 'blockstates')
  for(let type of def.analogTypes) {
    gen.write(gen.assets, `blockstates/analog_redstone_${type}.json`,
      gen.template(`blockstates/analog`).replace(/%type/g, type))
  }
}

module.exports = [
  genAnalogBlockstates,
]
