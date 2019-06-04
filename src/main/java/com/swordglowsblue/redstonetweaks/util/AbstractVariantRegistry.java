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
import net.minecraft.state.property.AbstractProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractVariantRegistry<T> {
    protected final Set<T> keySet;
    protected final IdFormatter idFormatter;

    protected final Map<T, Block> blocks = new LinkedHashMap<>();
    protected final Map<T, Item> items = new LinkedHashMap<>();

    protected BiFunction<T, BlockState, Integer> blockColorProvider = null;
    protected BiFunction<T, ItemStack, Integer> itemColorProvider = null;

    protected AbstractVariantRegistry(Set<T> keySet, IdFormatter idFormatter) {
        this.keySet = keySet;
        this.idFormatter = idFormatter;
    }

    public Block getBlock(T key) { return this.blocks.getOrDefault(key, null); }
    public Item getItem(T key) { return this.items.getOrDefault(key, null); }

    public Set<T> keySet() { return this.keySet; }
    public ImmutableMap<T, Block> getBlocks() { return ImmutableMap.copyOf(this.blocks); }
    public ImmutableMap<T, Item> getItems() { return ImmutableMap.copyOf(this.items); }

    public final boolean usesSeparateIDs() { return this.idFormatter.usesSeparateIDs(); }
    public Identifier blockIdFor(T key) { return this.idFormatter.blockIdFor(key.toString()); }
    public Identifier itemIdFor(T key) { return this.idFormatter.itemIdFor(key.toString()); }

    public final boolean contains(Identifier id) { return this.containsBlock(id) || this.containsItem(id); }
    public final boolean containsBlock(Identifier id) {
        return this.blocks.keySet().stream().anyMatch(key -> id.equals(this.blockIdFor(key))); }
    public final boolean containsItem(Identifier id) {
        return this.items.keySet().stream().anyMatch(key -> id.equals(this.itemIdFor(key))); }

    protected boolean registryDone = false;
    public void registerAll() {
        if(this.registryDone) return;
        this.registryDone = true;

        for(T key : this.blocks.keySet())
            Registry.register(Registry.BLOCK, this.blockIdFor(key), this.blocks.get(key));
        for(T key : this.items.keySet())
            Registry.register(Registry.ITEM, this.itemIdFor(key), this.items.get(key));
    }

    @Environment(EnvType.CLIENT)
    protected boolean colorProvidersDone = false;
    @Environment(EnvType.CLIENT)
    public void registerColorProviders() {
        if(this.colorProvidersDone) return;
        this.colorProvidersDone = true;

        if(this.itemColorProvider != null) for(T key : this.items.keySet())
            ColorProviderRegistry.ITEM.register((s, i) -> this.itemColorProvider.apply(key, s), this.items.get(key));
        if(this.blockColorProvider != null) for(T key : this.blocks.keySet())
            ColorProviderRegistry.BLOCK.register((s,v,p,i) -> this.blockColorProvider.apply(key, s), this.blocks.get(key));
    }

    protected static class IdFormatter {
        private final String modid;
        private final String blockIdFmt;
        private final String itemIdFmt;

        IdFormatter(String modid, String blockIdFmt, String itemIdFmt) {
            this.modid = modid;
            this.blockIdFmt = blockIdFmt;
            this.itemIdFmt = itemIdFmt;
        }

        public final boolean usesSeparateIDs() { return !blockIdFmt.equals(itemIdFmt); }
        public Identifier blockIdFor(String key) {
            return new Identifier(modid, String.format(blockIdFmt, key.toLowerCase())); }
        public Identifier itemIdFor(String key) {
            return new Identifier(modid, String.format(itemIdFmt, key.toLowerCase())); }
    }

    protected static class Builder<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>>
        implements
            IBuilderAwaitingKeySet<T,B,R>,
            IBuilderAwaitingIDInfo<T,B,R>,
            IBuilderAwaitingBlocks<T,B,R>,
            IBuilderAwaitingBlockItems<T,B,R>,
            IBuilderAwaitingItems<T,B,R>,
            IBuilderAwaitingColors<T,B,R>,
            IBuilderAwaitingBlockColor<T,B,R>,
            IBuilderAwaitingItemColor<T,B,R>,
            IBuilderAwaitingFinalize<T,B,R>
    {
        protected R registry;
        protected Set<T> keySet;
        private BiFunction<Set<T>, IdFormatter, R> registryFactory;
        protected boolean editable = true;

        protected Builder(BiFunction<Set<T>, IdFormatter, R> registryFactory, Set<T> defaultKeySet) {
            this.registryFactory = registryFactory;
            this.keySet = defaultKeySet;
        }

        private void verifyEditable() {
            if(!this.editable) throw new IllegalStateException("Builder already finalized");
        }

        public IBuilderAwaitingIDInfo<T, B, R> setValidKeys(Set<T> keySet) {
            verifyEditable();
            this.keySet = keySet;
            return this;
        }

        public IBuilderAwaitingBlocks<T, B, R> setIdentifierFormat(String modid, String idFmt) {
            return this.setIdentifierFormat(modid, idFmt, idFmt); }
        public IBuilderAwaitingBlocks<T, B, R> setIdentifierFormat(String modid, String blockIdFmt, String itemIdFmt) {
            verifyEditable();
            this.registry = this.registryFactory.apply(this.keySet, new IdFormatter(modid, blockIdFmt, itemIdFmt));
            return this;
        }

        public IBuilderAwaitingBlockItems<T, B, R> addBlocks(Function<T, Block> provider) {
            verifyEditable();
            for(T key : this.keySet) this.registry.blocks.put(key, provider.apply(key));
            return this;
        }

        public IBuilderAwaitingColors<T, B, R> addBlockItems() {
            return this.addBlockItems(new Item.Settings()); }
        public IBuilderAwaitingColors<T, B, R> addBlockItems(Item.Settings settings) {
            return this.registry.usesSeparateIDs()
                ? this.addBlockItems((key, block) -> new AliasedBlockItem(block, settings))
                : this.addBlockItems((key, block) -> new BlockItem(block, settings)); }
        public IBuilderAwaitingColors<T, B, R> addBlockItems(Function<Block, Item> provider) {
            return this.addBlockItems((key, block) -> provider.apply(block)); }
        public IBuilderAwaitingColors<T, B, R> addBlockItems(BiFunction<T, Block, Item> provider) {
            return this.addItems(key -> provider.apply(key, this.registry.blocks.get(key)));
        }

        public IBuilderAwaitingColors<T, B, R> addItems(Function<T, Item> provider) {
            verifyEditable();
            for(T key : this.keySet) this.registry.items.put(key, provider.apply(key));
            return this;
        }

        public IBuilderAwaitingItemColor<T, B, R> setBlockColorProvider(Function<T, Integer> provider) {
            return this.setBlockColorProvider((key, state) -> provider.apply(key)); }
        public <P extends Comparable<P>> IBuilderAwaitingItemColor<T, B, R> setBlockColorProvider(AbstractProperty<P> property, Function<P, Integer> provider) {
            return this.setBlockColorProvider((key, state) -> provider.apply(state.get(property))); }
        public IBuilderAwaitingItemColor<T, B, R> setBlockColorProvider(BiFunction<T, BlockState, Integer> provider) {
            verifyEditable();
            this.registry.blockColorProvider = provider;
            return this;
        }

        public IBuilderAwaitingBlockColor<T, B, R> setItemColorProvider(Function<T, Integer> provider) {
            return this.setItemColorProvider((key, stack) -> provider.apply(key)); }
        public IBuilderAwaitingBlockColor<T, B, R> setItemColorProvider(BiFunction<T, ItemStack, Integer> provider) {
            verifyEditable();
            this.registry.itemColorProvider = provider;
            return this;
        }

        public R build() {
            verifyEditable();
            this.editable = false;
            return this.registry;
        }

        public R registerAll() {
            this.build();
            this.registry.registerAll();
            return this.registry;
        }
    }

    public interface IBuilderAwaitingKeySet<T, B extends Builder<T, B, R>, R extends AbstractVariantRegistry<T>>
        extends IBuilderAwaitingIDInfo<T, B, R> {
        IBuilderAwaitingIDInfo<T, B, R> setValidKeys(Set<T> keySet);
    }

    public interface IBuilderAwaitingIDInfo<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>> {
        IBuilderAwaitingBlocks<T, B, R> setIdentifierFormat(String modid, String idFmt);
        IBuilderAwaitingBlocks<T, B, R> setIdentifierFormat(String modid, String blockIdFmt, String itemIdFmt);
    }

    public interface IBuilderAwaitingBlocks<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>>
        extends IBuilderAwaitingItems<T, B, R> {
        IBuilderAwaitingBlockItems<T, B, R> addBlocks(Function<T, Block> provider);
    }

    public interface IBuilderAwaitingBlockItems<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>>
        extends IBuilderAwaitingItems<T, B, R> {
        IBuilderAwaitingColors<T, B, R> addBlockItems();
        IBuilderAwaitingColors<T, B, R> addBlockItems(Item.Settings settings);
        IBuilderAwaitingColors<T, B, R> addBlockItems(Function<Block, Item> provider);
        IBuilderAwaitingColors<T, B, R> addBlockItems(BiFunction<T, Block, Item> provider);
    }

    public interface IBuilderAwaitingItems<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>>
        extends IBuilderAwaitingFinalize<T, B, R> {
        IBuilderAwaitingColors<T, B, R> addItems(Function<T, Item> provider);
    }

    public interface IBuilderAwaitingColors<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>>
        extends IBuilderAwaitingFinalize<T, B, R> {
        IBuilderAwaitingItemColor<T, B, R> setBlockColorProvider(Function<T, Integer> provider);
        <P extends Comparable<P>> IBuilderAwaitingItemColor<T, B, R> setBlockColorProvider(AbstractProperty<P> property, Function<P, Integer> provider);
        IBuilderAwaitingItemColor<T, B, R> setBlockColorProvider(BiFunction<T, BlockState, Integer> provider);
        IBuilderAwaitingBlockColor<T, B, R> setItemColorProvider(Function<T, Integer> provider);
        IBuilderAwaitingBlockColor<T, B, R> setItemColorProvider(BiFunction<T, ItemStack, Integer> provider);
    }

    public interface IBuilderAwaitingBlockColor<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>>
        extends IBuilderAwaitingFinalize<T, B, R> {
        IBuilderAwaitingFinalize<T, B, R> setBlockColorProvider(Function<T, Integer> provider);
        IBuilderAwaitingFinalize<T, B, R> setBlockColorProvider(BiFunction<T, BlockState, Integer> provider);
        <P extends Comparable<P>> IBuilderAwaitingFinalize<T, B, R> setBlockColorProvider(AbstractProperty<P> property, Function<P, Integer> provider);
    }

    public interface IBuilderAwaitingItemColor<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>>
        extends IBuilderAwaitingFinalize<T, B, R> {
        IBuilderAwaitingFinalize<T, B, R> setItemColorProvider(Function<T, Integer> provider);
        IBuilderAwaitingFinalize<T, B, R> setItemColorProvider(BiFunction<T, ItemStack, Integer> provider);
    }

    public interface IBuilderAwaitingFinalize<T, B extends Builder<T,B,R>, R extends AbstractVariantRegistry<T>> {
        R build();
        R registerAll();
    }
}
