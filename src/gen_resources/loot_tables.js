const gen = require('./gen')
const def = require('./def')

function genSimpleDropsSelf() {
  gen.ensureDir(gen.data, 'loot_tables/blocks')
  for(let block of def.simpleDropsSelf) {
    gen.write(gen.data, `loot_tables/blocks/${block}.json`,
      gen.template('loot_tables/simple_drops_self')
        .replace(/%block/g, `redstonetweaks:${block}`))
  }
}

module.exports = [
  genSimpleDropsSelf,
]
