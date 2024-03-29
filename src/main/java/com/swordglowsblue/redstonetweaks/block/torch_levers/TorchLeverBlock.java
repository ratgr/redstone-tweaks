package com.swordglowsblue.redstonetweaks.block.torch_levers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class TorchLeverBlock extends TorchBlock {
    public static final BooleanProperty POWERED = LeverBlock.POWERED;

    public TorchLeverBlock() { this(14, Blocks.TORCH); }
    TorchLeverBlock(int lightLevel, Block copyFrom) {
        super(FabricBlockSettings.copy(copyFrom).lightLevel(lightLevel).build());
        this.setDefaultState(getStateFactory().getDefaultState().with(POWERED, false));
    }

    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity pe, Hand hand, BlockHitResult bhr) {
        state = state.cycle(POWERED);
        if(!world.isClient) {
            world.setBlockState(pos, state, 3);
            float soundPitch = state.get(POWERED) ? 0.6F : 0.5F;
            world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, soundPitch);
            this.updateNeighbors(state, world, pos);
        }
        return true;
    }

    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState other, boolean flag) {
        if(!flag && state.getBlock() != other.getBlock()) {
            if(state.get(POWERED)) this.updateNeighbors(state, world, pos);
            super.onBlockRemoved(state, world, pos, other, flag);
        }
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        this.updateNeighbors(state, world, pos);
    }

    public int getWeakRedstonePower(BlockState state, BlockView bv, BlockPos pos, Direction dir) {
        return state.get(POWERED) ? 15 : 0;
    }

    public int getStrongRedstonePower(BlockState state, BlockView bv, BlockPos pos, Direction dir) {
        return state.get(POWERED) && Direction.DOWN == dir.getOpposite() ? 15 : 0;
    }

    public boolean emitsRedstonePower(BlockState state) { return state.get(POWERED); }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.down(), this);
    }

    protected void appendProperties(StateFactory.Builder<Block, BlockState> sf) { sf.add(POWERED); }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + (state.get(POWERED) ? 0.475D : 0.7D);
        double z = pos.getZ() + 0.5D;
        world.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
    }
}
