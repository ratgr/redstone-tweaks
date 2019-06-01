package com.swordglowsblue.redstonetweaks.block.analog;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class AnalogRedstoneLampBlock extends AbstractGlassBlock {
    public AnalogRedstoneLampBlock() {
        super(FabricBlockSettings.of(Material.GLASS).strength(0.5f, 0.5f).sounds(BlockSoundGroup.GLASS).build());
        this.setDefaultState(getStateFactory().getDefaultState().with(Properties.POWER, 15));
    }

    public int getLuminance(BlockState bs) { return bs.get(Properties.POWER); }

    public void neighborUpdate(BlockState bs, World world, BlockPos pos, Block blockFrom, BlockPos posFrom, boolean flag) {
        if(!world.isClient) {
            int inputPower = world.getReceivedRedstonePower(pos);
            if (bs.get(Properties.POWER) != inputPower) {
                world.setBlockState(pos, bs.with(Properties.POWER, inputPower), 2);
            }
        }
    }

    protected void appendProperties(StateFactory.Builder sf) { sf.add(Properties.POWER); }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(Properties.POWER, ctx.getWorld().getReceivedRedstonePower(ctx.getBlockPos()));
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
