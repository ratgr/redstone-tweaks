package com.swordglowsblue.redstonetweaks;

import com.swordglowsblue.redstonetweaks.block.RedstoneSparkBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneLampBlock;
import com.swordglowsblue.redstonetweaks.item.FlintAndRedstoneItem;
import com.swordglowsblue.redstonetweaks.util.RegistryUtils;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

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
        flintAndRedstone = registerItem("flint_and_redstone", new FlintAndRedstoneItem());
        itemGroupRTweaks = FabricItemGroupBuilder.create(ID("main"))
            .icon(() -> new ItemStack(flintAndRedstone))
            .appendItems(RegistryUtils.itemGroupContents((blocks, items) -> {
                blocks.add(analogRedstoneBlock);
                blocks.add(analogRedstoneLamp);

                items.add(flintAndRedstone);
            }))
            .build();
    }
}
