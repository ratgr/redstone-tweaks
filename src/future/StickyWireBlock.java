package com.swordglowsblue.redstonetweaks.block.wire;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class StickyWireBlock extends AbstractWireBlock {
    public StickyWireBlock() {
        super(FabricBlockSettings.copy(Blocks.REDSTONE_WIRE).build());
        this.setDefaultState(super.getDefaultState().with(Properties.FACING, Direction.DOWN));
    }

    public Direction getAttachedFace(BlockState state) { return state.get(Properties.FACING); }

    protected void appendProperties(StateFactory.Builder<Block, BlockState> sf) {
        sf.add(CONN_0, CONN_90, CONN_180, CONN_270, POWER, Properties.FACING);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = this.getDefaultState();
        return super.applyConnectionsToState(state.with(Properties.FACING, ctx.getFacing().getOpposite()), ctx);
    }
}
