const gen = require('./gen')
const def = require('./def')

function genDyedWireBlockTags() {
  gen.ensureDir(gen.data, 'tags/blocks')

  let tag = {replace: false, values: ['minecraft:redstone_wire']}
  for(let color of def.dyeColors) {
    if(color === 'red') continue;
    tag.values.push(`redstonetweaks:${color}_redstone_wire`)
  }

  gen.write(gen.data, 'tags/blocks/redstone_wire.json', tag)
}

function genDyedWireItemTags() {
  gen.ensureDir(gen.data, 'tags/items')

  let tag = {replace: false, values: ['minecraft:redstone']}
  for(let color of def.dyeColors) {
    if(color === 'red') continue;
    tag.values.push(`redstonetweaks:${color}_redstone`)
  }

  gen.write(gen.data, 'tags/items/redstone.json', tag)
}

function genDyedRBlockTags() {
  gen.ensureDir(gen.data, 'tags/blocks')
  gen.ensureDir(gen.data, 'tags/items')

  let tag = {replace: false, values: ['minecraft:redstone_block']}
  for(let color of def.dyeColors) {
    if(color === 'red') continue;
    tag.values.push(`redstonetweaks:${color}_redstone_block`)
  }

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

function genCapacitorTags() {
  gen.ensureDir(gen.data, 'tags/blocks')
  gen.ensureDir(gen.data, 'tags/items')

  let tag = {replace: false, values: []}
  for(let tier of def.capacitorTiers) {
    tag.values.push(`redstonetweaks:${tier}_redstone_capacitor`)
  }

  gen.write(gen.data, 'tags/blocks/redstone_capacitor.json', tag)
  gen.write(gen.data, 'tags/items/redstone_capacitor.json', tag)
}

function genCompAutoTags() {
  gen.ensureDir(gen.compautoTags,'')

  let red = {replace: false, values: [
    'redstone_wire', 'redstone_block',
    ...def.compautoRed.filter(c => c != 'red').map(c => `redstonetweaks:${c}_redstone_wire`),
    ...def.compautoRed.filter(c => c != 'red').map(c => `redstonetweaks:${c}_redstone_block`)
  ]}
  let strongRed = {replace: false, values: [
    ...def.compautoStrongRed.map(c => `redstonetweaks:${c}_redstone_wire`),
    ...def.compautoStrongRed.map(c => `redstonetweaks:${c}_redstone_block`)
  ]}
  let green = {replace: false, values: [
    ...def.compautoGreen.map(c => `redstonetweaks:${c}_redstone_wire`),
    ...def.compautoGreen.map(c => `redstonetweaks:${c}_redstone_block`)
  ]}
  let strongGreen = {replace: false, values: [
    ...def.compautoStrongGreen.map(c => `redstonetweaks:${c}_redstone_wire`),
    ...def.compautoStrongGreen.map(c => `redstonetweaks:${c}_redstone_block`)
  ]}
  let blue = {replace: false, values: [
    ...def.compautoBlue.map(c => `redstonetweaks:${c}_redstone_wire`),
    ...def.compautoBlue.map(c => `redstonetweaks:${c}_redstone_block`)
  ]}
  let strongBlue = {replace: false, values: [
    ...def.compautoStrongBlue.map(c => `redstonetweaks:${c}_redstone_wire`),
    ...def.compautoStrongBlue.map(c => `redstonetweaks:${c}_redstone_block`)
  ]}

  gen.write(gen.compautoTags, 'red.json', red)
  gen.write(gen.compautoTags, 'strong_red.json', strongRed)
  gen.write(gen.compautoTags, 'green.json', green)
  gen.write(gen.compautoTags, 'strong_green.json', strongGreen)
  gen.write(gen.compautoTags, 'blue.json', blue)
  gen.write(gen.compautoTags, 'strong_blue.json', strongBlue)
}

module.exports = [
  genDyedWireBlockTags,
  genDyedWireItemTags,
  genDyedRBlockTags,
  genTorchLeverTags,
  genCapacitorTags,
  genCompAutoTags
]
