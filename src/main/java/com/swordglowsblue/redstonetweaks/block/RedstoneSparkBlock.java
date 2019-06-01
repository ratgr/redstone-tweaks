package com.swordglowsblue.redstonetweaks.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class RedstoneSparkBlock extends Block {
    public static final IntegerProperty DURATION = IntegerProperty.create("duration", 2, 1200);

    public RedstoneSparkBlock() {
        super(Block.Settings.of(new Material(
            MaterialColor.AIR,
            false, // liquid
            false, // solid
            false, // blocksMovement
            false, // blocksLight
            true, // breakByHand
            false, // burnable
            true, // replaceable
            PistonBehavior.DESTROY // pistonBehavior
        )));
    }

    public boolean emitsRedstonePower(BlockState bs) { return true; }
    public int getWeakRedstonePower(BlockState bs, BlockView bv, BlockPos bp, Direction dir) { return 15; }

    @Environment(EnvType.CLIENT)
    public boolean isSideInvisible(BlockState bs, BlockState bs2, Direction dir) { return false; }
    public BlockRenderType getRenderType(BlockState bs) { return BlockRenderType.INVISIBLE; }
    public VoxelShape getOutlineShape(BlockState bs, BlockView bv, BlockPos pos, EntityContext ctx) { return VoxelShapes.empty(); }

    public void onBlockAdded(BlockState bs, World world, BlockPos pos, BlockState bs2, boolean flag) {
        world.getBlockTickScheduler().schedule(pos, this, bs.get(DURATION));
    }

    public void onScheduledTick(BlockState bs, World world, BlockPos pos, Random rand) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
    }

    public BlockState stateForDuration(int duration) {
        return this.getDefaultState().with(DURATION, duration);
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState bs, World world, BlockPos bp, Random rand) {
        double x = bp.getX() + 0.5D + (rand.nextFloat() - 0.5D) * 0.2f;
        double y = bp.getY() + 0.5D + (rand.nextFloat() - 0.5D) * 0.2f;
        double z = bp.getZ() + 0.5D + (rand.nextFloat() - 0.5D) * 0.2f;
        float red = 0.6F + 0.4F;
        float green = Math.max(0.0F, 0.7F - 0.5F);
        float blue = Math.max(0.0F, 0.6F - 0.7F);
        world.addParticle(new DustParticleEffect(red, green, blue, 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
    }

    protected void appendProperties(StateFactory.Builder sf) { sf.add(DURATION); }
}
