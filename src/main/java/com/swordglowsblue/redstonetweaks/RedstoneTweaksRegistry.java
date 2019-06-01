package com.swordglowsblue.redstonetweaks;

import com.swordglowsblue.redstonetweaks.block.RedstoneSparkBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneLampBlock;
import com.swordglowsblue.redstonetweaks.block.container.HopperPipeBlock;
import com.swordglowsblue.redstonetweaks.block.container.HopperPipeBlockEntity;
import com.swordglowsblue.redstonetweaks.block.container.HopperPipeContainer;
import com.swordglowsblue.redstonetweaks.block.torch_levers.RedstoneTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.TorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.WallRedstoneTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.WallTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.item.FlintAndRedstoneItem;
import com.swordglowsblue.redstonetweaks.util.RegistryUtils;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class RedstoneTweaksRegistry implements RegistryUtils {
    public final ItemGroup itemGroupRTweaks;

    public final AnalogRedstoneBlock analogRedstoneBlock;
    public final AnalogRedstoneLampBlock analogRedstoneLamp;
    public final RedstoneSparkBlock redstoneSpark;
    public final HopperPipeBlock hopperPipe;

    public final FlintAndRedstoneItem flintAndRedstone;
    public final Identifier hopperPipeContainer = new Identifier("redstonetweaks:hopper_pipe");
    public final Identifier statInspectHopperPipe = new Identifier("redstonetweaks:inspect_hopper_pipe");

    private BlockEntityType<HopperPipeBlockEntity> hopperPipeBEType;
    public final Supplier<HopperPipeBlockEntity> createHopperPipeEntity = () -> new HopperPipeBlockEntity(hopperPipeBEType);

    RedstoneTweaksRegistry() {
        analogRedstoneBlock = registerBlock("analog_redstone_block", new AnalogRedstoneBlock());
        analogRedstoneLamp = registerBlock("analog_redstone_lamp", new AnalogRedstoneLampBlock());
        redstoneSpark = registerLoneBlock("redstone_spark", new RedstoneSparkBlock());
        torchLever = registerLoneBlock("torch_lever", new TorchLeverBlock());
        wallTorchLever = registerLoneBlock("wall_torch_lever", new WallTorchLeverBlock(torchLever));
        redstoneTorchLever = registerLoneBlock("redstone_torch_lever", new RedstoneTorchLeverBlock());
        redstoneWallTorchLever = registerLoneBlock("redstone_wall_torch_lever", new WallRedstoneTorchLeverBlock(redstoneTorchLever));
        hopperPipe = registerBlock("hopper_pipe", new HopperPipeBlock());

        flintAndRedstone = registerItem("flint_and_redstone", new FlintAndRedstoneItem());
        torchLeverItem = registerItem("torch_lever",
            new WallStandingBlockItem(torchLever, wallTorchLever, new Item.Settings()));
        redstoneTorchLeverItem = registerItem("redstone_torch_lever",
            new WallStandingBlockItem(redstoneTorchLever, redstoneWallTorchLever, new Item.Settings()));

        itemGroupRTweaks = FabricItemGroupBuilder.create(ID("main"))
            .icon(() -> new ItemStack(flintAndRedstone))
            .appendItems(RegistryUtils.itemGroupContents((blocks, items) -> {
                blocks.add(analogRedstoneBlock);
                blocks.add(analogRedstoneLamp);
                blocks.add(hopperPipe);

                items.add(flintAndRedstone);
                items.add(torchLeverItem);
                items.add(redstoneTorchLeverItem);
            }))
            .build();

        hopperPipeBEType = Registry.register(Registry.BLOCK_ENTITY, ID("hopper_pipe"),
            BlockEntityType.Builder.create(createHopperPipeEntity, hopperPipe).build(null));
        Registry.register(Registry.CUSTOM_STAT, statInspectHopperPipe, statInspectHopperPipe);
        Stats.CUSTOM.getOrCreateStat(statInspectHopperPipe, StatFormatter.DEFAULT);
        ContainerProviderRegistry.INSTANCE.registerFactory(hopperPipeContainer, (sid, id, pe, buf) ->
            new HopperPipeContainer(sid, pe.inventory,
                ((HopperPipeBlockEntity)pe.getEntityWorld().getBlockEntity(buf.readBlockPos()))));
    }
}
