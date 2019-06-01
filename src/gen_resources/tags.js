const gen = require('./gen')
const def = require('./def')

function genDyedRBlockTags() {
  gen.ensureDir(gen.data, 'tags/blocks')
  gen.ensureDir(gen.data, 'tags/items')

  let tag = {replace: false, values: ['minecraft:redstone_block']}

  gen.write(gen.data, 'tags/blocks/redstone_block.json', tag)
  gen.write(gen.data, 'tags/items/redstone_block.json', tag)
}

module.exports = [
  genDyedRBlockTags,
]
