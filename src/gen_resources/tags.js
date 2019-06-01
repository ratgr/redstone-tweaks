const gen = require('./gen')
const def = require('./def')

function genDyedRBlockTags() {
  gen.ensureDir(gen.data, 'tags/blocks')
  gen.ensureDir(gen.data, 'tags/items')

  let tag = {replace: false, values: ['minecraft:redstone_block']}

  gen.write(gen.data, 'tags/blocks/redstone_block.json', tag)
  gen.write(gen.data, 'tags/items/redstone_block.json', tag)
}

function genTorchLeverTags() {
  gen.ensureDir(gen.data, 'tags/blocks')
  gen.ensureDir(gen.data, 'tags/items')

  let tag = {replace: false, values: []}
  for(let type of def.torchTypes)
    tag.values.push(`redstonetweaks:${type}torch_lever`)
  gen.write(gen.data, 'tags/items/torch_lever.json', tag)

  for(let type of def.torchTypes)
    tag.values.push(`redstonetweaks:${type}wall_torch_lever`)
  gen.write(gen.data, 'tags/blocks/torch_lever.json', tag)
}

module.exports = [
  genDyedRBlockTags,
  genTorchLeverTags
]
