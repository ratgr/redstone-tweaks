const gen = require('./gen')
const def = require('./def')

function genBlockCoreModels() {
  gen.ensureDir(gen.assets, `models/block/block_core`)
  for(let size = 0; size < 15; size++) {
    gen.write(gen.assets, `models/block/block_core/${size+1}.json`,
      gen.template('models/block_core')
        .replace(/%min/g, Math.max(7 - size / 2, 0.1))
        .replace(/%max/g, Math.min(9 + size / 2, 15.9)))
  }
}

function genAnalogModels() {
  gen.ensureDir(gen.assets, `models/block/analog_redstone_block`)
  gen.ensureDir(gen.assets, `models/block/analog_redstone_lamp`)
  gen.ensureDir(gen.assets, `models/item`)

  for(let size = 0; size <= 15; size++) {
    gen.write(gen.assets, `models/block/analog_redstone_block/core_${size}.json`,
      { "parent": `redstonetweaks:block/block_core/${Math.max(size,1)}`,
        "textures": { "core": size > 0 ? 'block/redstone_block' : 'block/coal_block' }})
    gen.write(gen.assets, `models/block/analog_redstone_lamp/core_${size}.json`,
      { "parent": `redstonetweaks:block/block_core/${Math.max(size,1)}`,
        "textures": { "core": `block/redstone_lamp${size > 0 ? '_on' : ''}` }})
  }

  let item = core => ({
    "parent": "redstonetweaks:item/core_block",
    "textures": { "case": "block/glass", "core": core }})
  gen.write(gen.assets, `models/item/analog_redstone_block.json`, item('block/redstone_block'))
  gen.write(gen.assets, `models/item/analog_redstone_lamp.json`, item('block/redstone_lamp_on'))
}

module.exports = [
  genBlockCoreModels,
  genAnalogModels,
]
