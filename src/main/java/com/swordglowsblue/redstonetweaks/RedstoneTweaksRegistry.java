package com.swordglowsblue.redstonetweaks;

import com.swordglowsblue.redstonetweaks.block.RedstoneSparkBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneLampBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.RedstoneTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.TorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.WallRedstoneTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.block.torch_levers.WallTorchLeverBlock;
import com.swordglowsblue.redstonetweaks.item.FlintAndRedstoneItem;
import com.swordglowsblue.redstonetweaks.util.RegistryUtils;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.WallStandingBlockItem;

public class RedstoneTweaksRegistry implements RegistryUtils {
    public final ItemGroup itemGroupRTweaks;

    public final AnalogRedstoneBlock analogRedstoneBlock;
    public final AnalogRedstoneLampBlock analogRedstoneLamp;
    public final RedstoneSparkBlock redstoneSpark;

    public final FlintAndRedstoneItem flintAndRedstone;

    RedstoneTweaksRegistry() {
        analogRedstoneBlock = registerBlock("analog_redstone_block", new AnalogRedstoneBlock());
        analogRedstoneLamp = registerBlock("analog_redstone_lamp", new AnalogRedstoneLampBlock());
        redstoneSpark = registerLoneBlock("redstone_spark", new RedstoneSparkBlock());
        torchLever = registerLoneBlock("torch_lever", new TorchLeverBlock());
        wallTorchLever = registerLoneBlock("wall_torch_lever", new WallTorchLeverBlock(torchLever));
        redstoneTorchLever = registerLoneBlock("redstone_torch_lever", new RedstoneTorchLeverBlock());
        redstoneWallTorchLever = registerLoneBlock("redstone_wall_torch_lever", new WallRedstoneTorchLeverBlock(redstoneTorchLever));

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

                items.add(flintAndRedstone);
                items.add(torchLeverItem);
                items.add(redstoneTorchLeverItem);
            }))
            .build();
    }
}
