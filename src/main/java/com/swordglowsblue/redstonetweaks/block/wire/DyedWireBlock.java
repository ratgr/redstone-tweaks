package com.swordglowsblue.redstonetweaks.block.wire;

import com.swordglowsblue.redstonetweaks.util.ColorUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.client.block.ColoredBlock;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;

import java.awt.*;

public class DyedWireBlock extends AbstractWireBlock implements ColoredBlock {
    private final DyeColor color;
    public DyeColor getColor() { return color; }

    public DyedWireBlock(DyeColor color) {
        super(FabricBlockSettings.copy(Blocks.REDSTONE_WIRE).materialColor(color).build());
        this.color = color;
    }

    public boolean shouldConnect(BlockState state, BlockState otherState, Direction dir, boolean allowRecurse) {
        if(otherState.getBlock() instanceof DyedWireBlock || otherState.getBlock() instanceof DyedRedstoneBlock)
            return ((ColoredBlock)otherState.getBlock()).getColor() == this.color;
        if(otherState.getBlock() instanceof IWire) return false;
        if(otherState.getBlock() instanceof RedstoneBlock) return false;
        return super.shouldConnect(state, otherState, dir, allowRecurse);
    }

    public boolean canStatePowerWire(BlockState state, BlockState other) {
        Block block = other.getBlock();
        if(block instanceof DyedRedstoneBlock) return ((ColoredBlock)block).getColor() == this.color;
        if(block instanceof RedstoneBlock) return false;
        return super.canStatePowerWire(state, other);
    }

    @Environment(EnvType.CLIENT)
    public Color getWireColor(int power) {
        return ColorUtils.multiply(
            ColorUtils.colorFromDye(this.color),
            ColorUtils.getPowerBrightnessMask(power)
        );
    }
}
