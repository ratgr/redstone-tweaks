package com.swordglowsblue.redstonetweaks;

import com.swordglowsblue.redstonetweaks.block.RedstoneSparkBlock;
import com.swordglowsblue.redstonetweaks.block.TranslocatorBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneLampBlock;
import com.swordglowsblue.redstonetweaks.block.analog.RedstoneCapacitorBlock;
import com.swordglowsblue.redstonetweaks.block.container.HopperPipeBlock;
import com.swordglowsblue.redstonetweaks.block.container.HopperPipeBlockEntity;
import com.swordglowsblue.redstonetweaks.block.container.HopperPipeContainer;
import com.swordglowsblue.redstonetweaks.block.torch_levers.RedstoneTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.TorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.WallRedstoneTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.WallTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.wire.DyedRedstoneBlock;
import com.swordglowsblue.redstonetweaks.block.wire.DyedWireBlock;
import com.swordglowsblue.redstonetweaks.block.wire.IWire;
import com.swordglowsblue.redstonetweaks.item.FlintAndRedstoneItem;
import com.swordglowsblue.redstonetweaks.util.ColorUtils;
import com.swordglowsblue.redstonetweaks.util.EnumVariantRegistry;
import com.swordglowsblue.redstonetweaks.util.RegistryUtils;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.EnumSet;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public class RedstoneTweaksRegistry implements RegistryUtils {
    public final ItemGroup itemGroupRTweaks;

    public final AnalogRedstoneBlock analogRedstoneBlock;
    public final AnalogRedstoneLampBlock analogRedstoneLamp;
    public final RedstoneSparkBlock redstoneSpark;
    public final TorchLeverBlock torchLever;
    public final WallTorchLeverBlock wallTorchLever;
    public final RedstoneTorchLeverBlock redstoneTorchLever;
    public final WallRedstoneTorchLeverBlock redstoneWallTorchLever;
    public final HopperPipeBlock hopperPipe;
    public final TranslocatorBlock translocator;
    public final Block reinforcedCobblestone;

    public final FlintAndRedstoneItem flintAndRedstone;
    public final WallStandingBlockItem torchLeverItem;
    public final WallStandingBlockItem redstoneTorchLeverItem;

    public final EnumVariantRegistry<DyeColor> dyedWire;
    public final EnumVariantRegistry<DyeColor> dyedRBlocks;
    public final EnumVariantRegistry<RedstoneCapacitorBlock.Tier> redstoneCapacitors;

    public final Identifier hopperPipeContainer = new Identifier("redstonetweaks:hopper_pipe");
    public final Identifier statInspectHopperPipe = new Identifier("redstonetweaks:inspect_hopper_pipe");

    private BlockEntityType<HopperPipeBlockEntity> hopperPipeBEType;
    public final Supplier<HopperPipeBlockEntity> createHopperPipeEntity = () -> new HopperPipeBlockEntity(hopperPipeBEType);

    public static Consumer<RedstoneTweaksRegistry> registerBrewingRecipes;

    RedstoneTweaksRegistry() {
        analogRedstoneBlock = registerBlock("analog_redstone_block", new AnalogRedstoneBlock());
        analogRedstoneLamp = registerBlock("analog_redstone_lamp", new AnalogRedstoneLampBlock());
        redstoneSpark = registerLoneBlock("redstone_spark", new RedstoneSparkBlock());
        torchLever = registerLoneBlock("torch_lever", new TorchLeverBlock());
        wallTorchLever = registerLoneBlock("wall_torch_lever", new WallTorchLeverBlock(torchLever));
        redstoneTorchLever = registerLoneBlock("redstone_torch_lever", new RedstoneTorchLeverBlock());
        redstoneWallTorchLever = registerLoneBlock("redstone_wall_torch_lever", new WallRedstoneTorchLeverBlock(redstoneTorchLever));
        hopperPipe = registerBlock("hopper_pipe", new HopperPipeBlock());
        translocator = registerBlock("translocator", new TranslocatorBlock());
        reinforcedCobblestone = registerBlock("reinforced_cobblestone", new Block(Block.Settings.copy(Blocks.COBBLESTONE)) {
            public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.BLOCK; } });

        flintAndRedstone = registerItem("flint_and_redstone", new FlintAndRedstoneItem());
        torchLeverItem = registerItem("torch_lever",
            new WallStandingBlockItem(torchLever, wallTorchLever, new Item.Settings()));
        redstoneTorchLeverItem = registerItem("redstone_torch_lever",
            new WallStandingBlockItem(redstoneTorchLever, redstoneWallTorchLever, new Item.Settings()));

        dyedWire = EnumVariantRegistry.create(DyeColor.class)
            .setValidKeys(EnumSet.complementOf(EnumSet.of(DyeColor.RED)))
            .setIdentifierFormat(RedstoneTweaks.MODID, "%s_redstone_wire", "%s_redstone")
            .addBlocks(DyedWireBlock::new).addBlockItems()
            .setBlockColorProvider((color, state) -> ((IWire)state.getBlock()).getWireColor(state).getRGB())
            .setItemColorProvider(ColorUtils::rgbIntFromDye)
            .registerAll();
        dyedRBlocks = EnumVariantRegistry.create(DyeColor.class)
            .setValidKeys(EnumSet.complementOf(EnumSet.of(DyeColor.RED)))
            .setIdentifierFormat(RedstoneTweaks.MODID, "%s_redstone_block")
            .addBlocks(DyedRedstoneBlock::new).addBlockItems()
            .setBlockColorProvider(ColorUtils::rgbIntFromDye)
            .setItemColorProvider(ColorUtils::rgbIntFromDye)
            .registerAll();
        redstoneCapacitors = EnumVariantRegistry.create(RedstoneCapacitorBlock.Tier.class)
            .setIdentifierFormat(RedstoneTweaks.MODID, "%s_redstone_capacitor")
            .addBlocks(RedstoneCapacitorBlock::new).addBlockItems()
            .setBlockColorProvider(Properties.POWER, ColorUtils::getPowerBrightnessMaskInt)
            .registerAll();

        itemGroupRTweaks = FabricItemGroupBuilder.create(ID("main"))
            .icon(() -> new ItemStack(flintAndRedstone))
            .appendItems(RegistryUtils.itemGroupContents((blocks, items) -> {
                blocks.add(analogRedstoneBlock);
                blocks.add(analogRedstoneLamp);
                blocks.add(reinforcedCobblestone);
                blocks.addAll(redstoneCapacitors.getBlocks().values());
                blocks.add(hopperPipe);
                blocks.add(translocator);

                items.add(flintAndRedstone);
                items.add(torchLeverItem);
                items.add(redstoneTorchLeverItem);
                items.addAll(dyedWire.getItems().values());
                items.addAll(dyedRBlocks.getItems().values());
            }))
            .build();

        hopperPipeBEType = Registry.register(Registry.BLOCK_ENTITY, ID("hopper_pipe"),
            BlockEntityType.Builder.create(createHopperPipeEntity, hopperPipe).build(null));
        Registry.register(Registry.CUSTOM_STAT, statInspectHopperPipe, statInspectHopperPipe);
        Stats.CUSTOM.getOrCreateStat(statInspectHopperPipe, StatFormatter.DEFAULT);
        ContainerProviderRegistry.INSTANCE.registerFactory(hopperPipeContainer, (sid, id, pe, buf) ->
            new HopperPipeContainer(sid, pe.inventory,
                ((HopperPipeBlockEntity)pe.getEntityWorld().getBlockEntity(buf.readBlockPos()))));

        DispenserBlock.registerBehavior(flintAndRedstone, new FallibleItemDispenserBehavior() {
            protected ItemStack dispenseStack(BlockPointer pnt, ItemStack stack) {
                this.success = false;

                BlockPos pos = pnt.getBlockPos().offset(pnt.getBlockState().get(DispenserBlock.FACING));
                if(pnt.getWorld().getBlockState(pos).isAir()) {
                    pnt.getWorld().setBlockState(pos, redstoneSpark.stateForDuration(8), 11);
                    FlintAndRedstoneItem.spawnUsageParticles(pnt.getWorld(), pos);
                    this.success = true;
                }

                if(this.success && stack.applyDamage(1, pnt.getWorld().getRandom(), null)) stack.setAmount(0);
                return stack;
            }
        });

        registerBrewingRecipes.accept(this);
    }
}
