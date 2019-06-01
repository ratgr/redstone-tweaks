package com.swordglowsblue.redstonetweaks.util;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A simple builder-pattern registry holder for blocks/items with numerous variants, using an enum to
 *   differentiate between the variants. Useful for dyed or tiered blocks, wood types, or any other category
 *   of blocks/items that can be easily constructed from an enum list of types.
 * @param <T> The enum type to use as a key.
 */
public class EnumVariantRegistry<T extends Enum<T>> {
    /** The format string used for block IDs. */
    protected final String blockIdFmt;
    /** The format string used for item IDs. */
    protected final String itemIdFmt;
    /** The internal map from enum keys to blocks. */
    protected final Map<T, Block> blocks = new LinkedHashMap<>();
    /** The internal map from enum keys to items. */
    protected final Map<T, Item> items = new LinkedHashMap<>();
    /** The registered item color provider, if applicable. */
    protected BiFunction<T, ItemStack, Integer> itemColorProvider = null;
    /** The registered block color provider, if applicable. */
    protected BiFunction<T, BlockState, Integer> blockColorProvider = null;
    /** If false, calling {@link EnumVariantRegistry#registerAll} is a no-op. */
    protected boolean registryDone = false;
    /** If false, calling {@link EnumVariantRegistry#registerColorProviders} is a no-op. */
    protected boolean colorProvidersDone = false;

    /**
     * @param blockIdFmt The format string to be used for block IDs.
     * @param itemIdFmt The format string to be used for item IDs.
     */
    protected EnumVariantRegistry(String blockIdFmt, String itemIdFmt) {
        this.blockIdFmt = blockIdFmt;
        this.itemIdFmt = itemIdFmt;
    }

    /** Get a block from this registry based on the given key. */
    public Block getBlock(T key) { return blocks.getOrDefault(key, null); }
    /** Get an item from this registry based on the given key. */
    public Item getItem(T key) { return items.getOrDefault(key, null); }

    /** Get a map from keys to blocks based on this registry. */
    public ImmutableMap<T, Block> getBlocks() { return ImmutableMap.copyOf(blocks); }
    /** Get a map from keys to items based on this registry. */
    public ImmutableMap<T, Item> getItems() { return ImmutableMap.copyOf(items); }

    /** Returns true if {@link EnumVariantRegistry#blockIdFmt} and {@link EnumVariantRegistry#itemIdFmt} are different. */
    public final boolean usesSeparateIds() { return !blockIdFmt.equals(itemIdFmt); }
    /** Construct an {@link Identifier} for the given key based on this registry's block ID format. */
    public Identifier blockIdFor(T key) {
        return new Identifier("redstonetweaks", String.format(blockIdFmt, key.toString().toLowerCase())); }
    /** Construct an {@link Identifier} for the given key based on this registry's item ID format. */
    public Identifier itemIdFor(T key) {
        return new Identifier("redstonetweaks", String.format(itemIdFmt, key.toString().toLowerCase())); }

    /** Check whether this registry contains a block or item for the given {@link Identifier}. */
    public final boolean contains(Identifier id) { return containsBlock(id) || containsItem(id); }

    /** Check whether this registry contains a block for the given {@link Identifier}. */
    public final boolean containsBlock(Identifier id) {
        for(T key : blocks.keySet()) if(id.equals(blockIdFor(key))) return true;
        return false;
    }

    /** Check whether this registry contains an item for the given {@link Identifier}. */
    public final boolean containsItem(Identifier id) {
        for(T key : items.keySet()) if(id.equals(itemIdFor(key))) return true;
        return false;
    }

    /** Register all blocks and items contained in this registry. */
    public void registerAll() {
        if(this.registryDone) return;
        this.registryDone = true;

        for(T key : blocks.keySet())
            Registry.register(Registry.BLOCK, blockIdFor(key), blocks.get(key));
        for(T key : items.keySet())
            Registry.register(Registry.ITEM, itemIdFor(key), items.get(key));
    }

    /** Register any appropriate color mappers for this registry. */
    @Environment(EnvType.CLIENT)
    public void registerColorProviders() {
        if(this.colorProvidersDone) return;
        this.colorProvidersDone = true;

        if(itemColorProvider != null) for(T key : items.keySet())
            ColorProviderRegistry.ITEM.register((s, i) -> itemColorProvider.apply(key, s), items.get(key));
        if(blockColorProvider != null) for(T key : blocks.keySet())
            ColorProviderRegistry.BLOCK.register((s,v,p,i) -> blockColorProvider.apply(key, s), blocks.get(key));
    }

    /**
     * A basic {@link EnumVariantRegistry} builder type.
     * @param <T> The enum type to build from.
     * @see AbstractBuilder
     */
    public static final class Builder<T extends Enum<T>> extends AbstractBuilder<Builder<T>,T,EnumVariantRegistry<T>> {
        /**
         * @param enumClass The class of the enum to build from.
         * @param idFmt A format string to build block and item IDs from (see {@link String#format}).
         *   Will be passed the associated enum key after calling {@code .toString().toLowerCase()} on it.
         */
        public Builder(Class<T> enumClass, String idFmt) { this(enumClass, idFmt, idFmt); }

        /**
         * @param enumClass The class of the enum to build from.
         * @param blockIdFmt A format string to build block IDs from (see {@link String#format}).
         *   Will be passed the associated enum key after calling {@code .toString().toLowerCase()} on it.
         * @param itemIdFmt A format string to build item IDs from (see {@link String#format}).
         *   Will be passed the associated enum key after calling {@code .toString().toLowerCase()} on it.
         */
        public Builder(Class<T> enumClass, String blockIdFmt, String itemIdFmt) {
            super(enumClass, new EnumVariantRegistry<>(blockIdFmt, itemIdFmt)); }
    }

    /**
     * Base class for {@link EnumVariantRegistry} builder implementations.
     * @param <B> The builder type. Must be the same type as is being implemented (see {@link Builder}).
     * @param <T> The enum type to build from.
     * @param <R> The registry type to build. Must match the type of {@code T}.
     * @see Builder
     */
    @SuppressWarnings("unchecked")
    protected static abstract class AbstractBuilder<
        B extends AbstractBuilder<B,T,R>,
        T extends Enum<T>,
        R extends EnumVariantRegistry<T>
    > {
        /** The registry being built. */
        protected final R registry;
        /** The subset of the key enum to use. */
        protected EnumSet<T> keys;
        /** Whether this builder can currently be edited. */
        protected boolean canEdit = true;
        /** Whether this builder's {@link AbstractBuilder#keys} can be edited. */
        protected boolean canEditKeys = true;

        /**
         * @param enumClass The class of the enum to build from.
         * @param registry The {@link EnumVariantRegistry} to build. Must match the type of {@code enumClass}.
         */
        protected AbstractBuilder(Class<T> enumClass, R registry) {
            this.keys = EnumSet.allOf(enumClass);
            this.registry = registry;
        }

        protected void verifyEditable() {
            if(!canEdit) throw new IllegalStateException("Builder already finalized");
            this.canEditKeys = false;
        }

        /** If this registry should only contain a subset of the enum as keys, use this method to set which keys to use.
         *  Will throw {@link IllegalStateException} if called multiple times or after a different builder method. */
        public B keys(EnumSet<T> set) {
            if(canEdit && canEditKeys) {
                this.keys = set;
                this.canEditKeys = false;
            } else
                throw new IllegalStateException("Builder keyset already finalized");
            return (B)this;
        }

        /** Add a block to this registry for the given key.
         *  Will throw {@link IllegalArgumentException} if a block already exists for that key. */
        public B block(T key, Block block) {
            verifyEditable();
            if(registry.blocks.containsKey(key))
                throw new IllegalArgumentException("Tried to register multiple blocks for the same key "+key);
            registry.blocks.put(key, block);
            return (B)this;
        }

        /** Add a block to this registry for every possible key.
         *  The given factory method will be passed each key and should return the appropriate {@link Block}.
         *  @see AbstractBuilder#block */
        public B blocks(Function<T, Block> factory) {
            verifyEditable();
            for(T key : keys) block(key, factory.apply(key));
            return (B)this;
        }

        /** Add an item to this registry for the given key.
         *  Will throw {@link IllegalArgumentException} if an item already exists for that key. */
        public B item(T key, Item item) {
            verifyEditable();
            if(registry.items.containsKey(key))
                throw new IllegalArgumentException("Tried to register multiple items for the same key "+key);
            registry.items.put(key, item);
            return (B)this;
        }

        /** Add an item to this registry for every possible key.
         *  The given factory method will be passed each key and should return the appropriate {@link Item}.
         *  @see AbstractBuilder#item */
        public B items(Function<T, Item> factory) {
            verifyEditable();
            for(T key : keys) item(key, factory.apply(key));
            return (B)this;
        }

        /** Auto-generate {@link BlockItem}s for each block in this registry, using an empty {@link Item.Settings}.
         *  Will use {@link BlockItem} if no alternate item ID format is set, otherwise will use {@link AliasedBlockItem}. */
        public B blockItems() { return blockItems(new Item.Settings()); }

        /** Auto-generate {@link BlockItem}s for each block in this registry, using the given {@link Item.Settings}.
         *  Will use {@link BlockItem} if no alternate item ID format is set, otherwise will use {@link AliasedBlockItem}. */
        public B blockItems(Item.Settings settings) {
            return registry.usesSeparateIds()
                ? items(key -> new AliasedBlockItem(registry.blocks.get(key), settings))
                : items(key -> new BlockItem(registry.blocks.get(key), settings));
        }

        /** Add {@link BlockItem}s for each block in this registry via the given factory method.
         *  The given factory method will be passed each key and the associated block, and should return the appropriate {@link BlockItem}. */
        public B blockItems(BiFunction<T, Block, BlockItem> factory) {
            return items(key -> factory.apply(key, registry.blocks.get(key)));
        }

        /** Register a block color provider for this registry, based on the associated key.
         *  Note that you will need to call {@link EnumVariantRegistry#registerColorProviders} on client initialization for this to work. */
        public B blockColor(Function<T, Integer> provider) {
            return blockColor((key, state) -> provider.apply(key)); }

        /** Register a block color provider for this registry, based on the associated key and current {@link BlockState}.
         *  Note that you will need to call {@link EnumVariantRegistry#registerColorProviders} on client initialization for this to work. */
        public B blockColor(BiFunction<T, BlockState, Integer> provider) {
            verifyEditable();
            registry.blockColorProvider = provider;
            return (B)this;
        }

        /** Register an item color provider for this registry, based on the associated key.
         *  Note that you will need to call {@link EnumVariantRegistry#registerColorProviders} on client initialization for this to work. */
        public B itemColor(Function<T, Integer> provider) {
            return itemColor((key, stack) -> provider.apply(key)); }

        /** Register an item color provider for this registry, based on the associated key and current {@link ItemStack}.
         *  Note that you will need to call {@link EnumVariantRegistry#registerColorProviders} on client initialization for this to work. */
        public B itemColor(BiFunction<T, ItemStack, Integer> provider) {
            verifyEditable();
            registry.itemColorProvider = provider;
            return (B)this;
        }

        /** Finalize and return this registry.
         *  Calling any other builder methods after this will throw an {@link IllegalStateException}. */
        public R build() {
            verifyEditable();
            this.canEdit = false;
            return (R)registry;
        }

        /** Finalize and return this registry, automatically calling {@link EnumVariantRegistry#registerAll}.
         *  Calling any other builder methods after this will throw an {@link IllegalStateException}. */
        public R registerAll() {
            R reg = build();
            reg.registerAll();
            return reg;
        }
    }
}
