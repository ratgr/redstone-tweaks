package com.swordglowsblue.redstonetweaks.block.wire;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.client.block.ColoredBlock;
import net.minecraft.util.DyeColor;

public class DyedRedstoneBlock extends RedstoneBlock implements ColoredBlock {
    private final DyeColor color;
    public DyeColor getColor() { return color; }

    public DyedRedstoneBlock(DyeColor color) {
        super(FabricBlockSettings.copy(Blocks.REDSTONE_BLOCK).materialColor(color).build());
        this.color = color;
    }
}
