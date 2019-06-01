package com.swordglowsblue.redstonetweaks.util;

import com.swordglowsblue.redstonetweaks.RedstoneTweaks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public interface RegistryUtils {
    default <T extends Item> T registerItem(String id, T item) { return registerItem(ID(id), item); }
    default <T extends Item> T registerItem(Identifier id, T item) { return Registry.register(Registry.ITEM, id, item); }

    default <T extends Block> T registerLoneBlock(String id, T block) { return registerLoneBlock(ID(id), block); }
    default <T extends Block> T registerLoneBlock(Identifier id, T block) { return Registry.register(Registry.BLOCK, id, block); }

    default <T extends Block> T registerBlock(String id, T block) { return registerBlock(id, block, new Item.Settings()); }
    default <T extends Block> T registerBlock(Identifier id, T block) { return registerBlock(id, block, new Item.Settings()); }

    default <T extends Block> T registerBlock(String id, T block, Item.Settings blockItemSettings) {
        return registerBlock(ID(id), block, blockItemSettings); }
    default <T extends Block> T registerBlock(Identifier id, T block, Item.Settings blockItemSettings) {
        registerItem(id, new BlockItem(block, blockItemSettings));
        return registerLoneBlock(id, block);
    }

    default Identifier ID(String id) { return new Identifier(RedstoneTweaks.MODID, id); }
    static Item getBlockItem(Block block) { return Item.getItemFromBlock(block); }

    static Consumer<List<ItemStack>> itemGroupContents(BiConsumer<List<Block>, List<Item>> body) {
        return list -> {
            List<Block> blockList = new ArrayList<>();
            List<Item> itemList = new ArrayList<>();
            body.accept(blockList, itemList);
            list.addAll(blockList.stream().map(RegistryUtils::getBlockItem).map(ItemStack::new).collect(Collectors.toList()));
            list.addAll(itemList.stream().map(ItemStack::new).collect(Collectors.toList()));
        };
    }
}
