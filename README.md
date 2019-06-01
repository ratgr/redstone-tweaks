# Redstone Tweaks
Some simple but useful Redstone additions built on the [FabricMC mod loader](https://github.com/fabricmc).

### Long-term TODO
- Redstone wire processing optimization (for either RT, vanilla, or both)
- Sticky redstone (attachable to walls/ceilings)
- As many neat and useful redstone features as possible
- Potentially split registry utils / IWire into separate library mods?

## Contributing

### Basic Rules
- Features should feel "vanilla-compatible" - that is, they should feel like
things that could potentially be in vanilla from a design perspective (not
necessarily things that Mojang would be likely to add, but that would fit the
existing style of the game).
    - No cables / conduits / etc.
    - No energy systems
    - No super-deep crafting rabbit holes (aim for 3 levels deep or less)
    - No major game mechanic overhauls or additions
    - No ridiculously complicated or super-animated models (shoot for less
    complex than capacitors, they're already really borderline)
    - Color variants are nice, but they aren't an excuse to implement a
    feature in a lackluster way. They make inventory management a nightmare
    and, if used to dictate functionality, can cause serious balance issues 
    if not handled carefully. If you're just adding color variants to add 
    color variants, consider finding something else to add instead.
- Try to avoid overlapping drastically with other Fabric mods. If you're not
sure whether a feature is unique, take a glance through [this CurseForge list](https://minecraft.curseforge.com/mc-mods/redstone?filter-game-version=1738749986%3A64806)
for other mods that implement it.
    - If you can make a feature work nicely with a feature from another mod,
    that's great! Interoperability and compatibility are good, just try not
    to wholesale add another mod's features.
- Make sure that all your bases are covered when it comes to resources. *Every
single block* needs blockstates, models, textures, loot tables, and recipes at
least. *Every single item* needs models, textures, and recipes at least. Create
and use tags liberally wherever it makes sense. Don't be afraid to add to the
resource generation scripts if you need to.
- Avoid introducing obvious or game-breaking bugs. Basically, have standards.
- Follow the guidelines of the existing files when it comes to formatting, etc.

### Package Structure
Top level packages under `com.swordglowsblue.redstonetweaks` are generally
named for overarching categories - `block`-related code, `item`-related code,
and so on. Subpackages are generally segmented by feature instead of category - 
`torch_levers`, `wire`, and so on. The same general structure is used for
resources, when subfolders are allowed by Minecraft's resource structure (see
`data/redstonetweaks/recipes` for a good example).

### Object Registry
All registry code is contained in `RedstoneTweaksRegistry.java`. This
includes block and item fields as well as miscellaneous resources and
registry code for use across the mod.

### Mixin Usage
Don't use mixins if at all possible. If the same job can reasonably be 
accomplished by wrapping a method call or class rather than mixing in, that 
is always preferable to adding a mixin. Additions to existing mixins should
be done sparingly.

### Resource Generation
Use the `genResources` Gradle task to generate all of this mod's resources. 
You will need Node.js 11.15.0 or higher installed to execute this task. It's
recommended to always run this before starting the game client since it doesn't
take much time and makes sure all resources are up to date; adding it to your
run configuration as a pre-run task is a good choice.

DO NOT directly edit resource files in `src/main/resources`, they will be
overwritten or deleted! Place them at the same subpath in 
`src/gen_resources/predef` instead, or add code to generate them to the 
appropriate module in `src/gen_resources`. `gen.js` is the entrypoint and
generally should only need to be edited when a new module is created. `def.js`
is for defining lists and variables to use in generation, such as dye colors.
All other files directly placed in `src/gen_resources` are modules for
generation - see those files for examples on how to generate resources, or
see `gen.js` for how to add a new module. 
