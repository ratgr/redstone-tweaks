const fs = require('fs')
const resourcesRoot = '../main/resources'
// const resourcesRoot = 'test_output'
const modid = 'redstonetweaks'

const data = path => `${resourcesRoot}/data/${modid}/${path}`
const assets = path => `${resourcesRoot}/assets/${modid}/${path}`
const compautoTags = path => `${resourcesRoot}/data/composableautomation/tags/blocks/${path}`

function write(type, path, data) {
  if(typeof data === 'object') data = JSON.stringify(data, null, 2)
  fs.writeFileSync(type(path), data)
  console.log(`OUT: ${path}`)
}

function template(name) {
  return fs.readFileSync(`templates/${name}.json`).toString()
}

function ensureDir(type, path) {
  fs.mkdirSync(type(path), {recursive:true})
}

function copyFolder(from, to) {
  if(!fs.existsSync(from)) return;
  fs.readdirSync(from).forEach(file => {
    ensureDir(x=>x, to)
    fs.lstatSync(`${from}/${file}`).isFile()
      ? fs.copyFileSync(`${from}/${file}`, `${to}/${file}`)
      : copyFolder(`${from}/${file}`, `${to}/${file}`)
  })
}

function deleteFolderRecursive(path) {
  if(fs.existsSync(path)) {
    fs.readdirSync(path).forEach(file => {
      var curPath = path + "/" + file
      if(fs.lstatSync(curPath).isDirectory()) deleteFolderRecursive(curPath)
      else fs.unlinkSync(curPath)
    })
    fs.rmdirSync(path)
  }
}

function genResources() {
  console.log("Generating resources...")
  console.log("Resources root directory: "+resourcesRoot)
  console.log("Assets directory: /assets/"+modid)
  console.log("Data directory: /data/"+modid)

  process.stdout.write("\nClearing old resources... ")
  deleteFolderRecursive(`${resourcesRoot}/assets`)
  deleteFolderRecursive(`${resourcesRoot}/data`)
  console.log("Success.")

  process.stdout.write("Copying predefined resources... ")
  copyFolder('predef/assets', `${resourcesRoot}/assets`)
  copyFolder('predef/data', `${resourcesRoot}/data`)
  console.log("Success.\n")

  for(let mname of [ 'models', 'blockstates', 'recipes', 'tags', 'loot_tables' ]) {
    console.log(`Generating resources for module '${mname}'...`)
    let Module = require('./'+mname)
    for(let f of Module) { console.log(f); f() }
    console.log(`Successfully generated resources for module '${mname}'.\n`)
  }

  console.log("Successfully generated all resources.")
}

module.exports = {
  ensureDir: ensureDir,
  copyFolder: copyFolder,
  template: template,
  write: write,
  data: data,
  assets: assets,
  compautoTags: compautoTags,
  resourcesRoot: resourcesRoot,
  modid: modid
}

if(!module.parent) genResources()
