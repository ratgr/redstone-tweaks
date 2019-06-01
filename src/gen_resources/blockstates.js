const gen = require('./gen')
const def = require('./def')

function genSimpleBlockstates() {
  gen.ensureDir(gen.assets, `blockstates`)

  for(let block of def.simpleBlocks) {
    gen.write(gen.assets, `blockstates/${block}.json`,
      { "variants": { "": { "model": `redstonetweaks:block/${block}` }}})
  }
}

function genAnalogBlockstates() {
  gen.ensureDir(gen.assets, 'blockstates')
  for(let type of def.analogTypes) {
    gen.write(gen.assets, `blockstates/analog_redstone_${type}.json`,
      gen.template(`blockstates/analog`).replace(/%type/g, type))
  }
}

function genCapacitorBlockstates() {
  gen.ensureDir(gen.assets, 'blockstates')
  for(let tier of def.capacitorTiers) {
    gen.write(gen.assets, `blockstates/${tier}_redstone_capacitor.json`,
      { "variants": { "": { "model": `redstonetweaks:block/redstone_capacitor/${tier}` } } })
  }
}

function genDyedWireBlockstates() {
  gen.ensureDir(gen.assets, 'blockstates')
  for(let color of def.dyeColors) {
    gen.write(gen.assets, `blockstates/${color}_redstone_wire.json`,
      gen.template('blockstates/dyed_redstone_wire'))
  }
}

function genTorchLeverBlockstates() {
  gen.ensureDir(gen.assets, 'blockstates')
  for(let type of def.torchTypes) {
    let i = def.torchTypes.indexOf(type);
    gen.write(gen.assets, `blockstates/${type}torch_lever.json`,
      gen.template('blockstates/torch_lever')
        .replace(/%off/g, ['block/torch', 'redstonetweaks:block/torch_lever/redstone_off'][i])
        .replace(/%on/g, ['redstonetweaks:block/torch_lever/on', 'block/redstone_torch'][i]))
    gen.write(gen.assets, `blockstates/${type}wall_torch_lever.json`,
      gen.template('blockstates/wall_torch_lever')
        .replace(/%off/g, ['block/wall_torch', 'redstonetweaks:block/torch_lever/redstone_wall_off'][i])
        .replace(/%on/g, ['redstonetweaks:block/torch_lever/wall_on', 'block/redstone_wall_torch'][i]))
  }
}

module.exports = [
  genSimpleBlockstates,
  genAnalogBlockstates,
  genCapacitorBlockstates,
  genDyedWireBlockstates,
  genTorchLeverBlockstates
]
