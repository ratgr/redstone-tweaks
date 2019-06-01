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

function genDyedWireLoot() {
  gen.ensureDir(gen.data, 'loot_tables/blocks')
  for(let color of def.dyeColors) {
    if(color === 'red') continue;
    gen.write(gen.data, `loot_tables/blocks/${color}_redstone_wire.json`,
      gen.template('loot_tables/simple_drops_self')
        .replace(/%block/g, `redstonetweaks:${color}_redstone`))
  }
}

module.exports = [
  genSimpleDropsSelf,
  genDyedWireLoot
]
