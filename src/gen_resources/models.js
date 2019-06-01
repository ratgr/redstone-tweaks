const gen = require('./gen')
const def = require('./def')

function genSimpleBlockModels() {
  gen.ensureDir(gen.assets, `models/block`)
  for(let block of def.simpleBlocks) {
    gen.write(gen.assets, `models/block/${block}.json`,
      { "parent": "block/cube_all",
        "textures": { "all": def.simpleTextures[block] || `redstonetweaks:block/${block}` }})
  }
}

function genSimpleItemModels() {
  gen.ensureDir(gen.assets, `models/item`)
  for(let item of def.simpleItems) {
    gen.write(gen.assets, `models/item/${item}.json`,
      { "parent": "item/generated",
        "textures": { "layer0": def.simpleTextures[item] || `redstonetweaks:item/${item}` }})
  }
  for(let item of def.simpleBlockItems) {
    gen.write(gen.assets, `models/item/${item}.json`,
      { "parent": `redstonetweaks:block/${item}` })
  }
}

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

function genCapacitorModels() {
  gen.ensureDir(gen.assets, `models/block/redstone_capacitor`)
  gen.ensureDir(gen.assets, `models/item`)
  for(let tier of def.capacitorTiers) {
    gen.write(gen.assets, `models/block/redstone_capacitor/${tier}.json`,
      { "parent": "redstonetweaks:block/redstone_capacitor/base",
        "textures": { "corners": `block/${tier.replace(/en$/, '')}_block` }})
    gen.write(gen.assets, `models/item/${tier}_redstone_capacitor.json`,
      { "parent": `redstonetweaks:block/redstone_capacitor/${tier}` })
  }
}

function genDyedWireItemModels() {
  gen.ensureDir(gen.assets, `models/item`)
  for(let color of def.dyeColors) {
    if(color === 'red') continue;
    gen.write(gen.assets, `models/item/${color}_redstone.json`,
      { "parent": "item/generated", 
        "textures": { "layer0": "redstonetweaks:item/dyed_redstone" }})
  }
}

function genTorchLeverItemModels() {
  gen.ensureDir(gen.assets, `models/item`)
  for(let type of def.torchTypes) {
    gen.write(gen.assets, `models/item/${type}torch_lever.json`,
      { "parent": "item/generated", 
        "textures": { "layer0": `block/${type}torch` }})
  }
}

module.exports = [
  genSimpleBlockModels,
  genSimpleItemModels,
  genBlockCoreModels,
  genAnalogModels,
  genCapacitorModels,
  genDyedWireItemModels,
  genTorchLeverItemModels
]
