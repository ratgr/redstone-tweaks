package com.swordglowsblue.redstonetweaks.block;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class TranslocatorBlock extends Block {
    protected static final DirectionProperty FACING = Properties.FACING;
    protected static final BooleanProperty TRIGGERED = Properties.TRIGGERED;

    public TranslocatorBlock() {
        super(FabricBlockSettings.copy(Blocks.OBSERVER).build());
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.SOUTH));
    }

    protected void appendProperties(StateFactory.Builder<Block, BlockState> sf) { sf.add(FACING, TRIGGERED); }
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    public int getTickRate(ViewableWorld world) { return 4; }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos from, boolean flag) {
        boolean powered = world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up());
        boolean triggered = state.get(TRIGGERED);

        if(powered && !triggered) {
            world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
            world.setBlockState(pos, state.with(TRIGGERED, true), 4);
        } else if(!powered && triggered) {
            world.setBlockState(pos, state.with(TRIGGERED, false), 4);
        }
    }

    public void onScheduledTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(!world.isClient) {
            BlockPos front = pos.offset(state.get(FACING));
            BlockPos back = pos.offset(state.get(FACING).getOpposite());
            Direction.Axis axis = state.get(FACING).getAxis();
            double targetY = back.getY() - (state.get(FACING) == Direction.UP ? 1 : 0);

            List<Entity> entities = world.getEntities(LivingEntity.class, new BoundingBox(front));
            entities.forEach(entity -> entity.requestTeleport(
                axis == Direction.Axis.X ? back.getX() + 0.5d : entity.x,
                axis == Direction.Axis.Y ? targetY : entity.y,
                axis == Direction.Axis.Z ? back.getZ() + 0.5d : entity.z
            ));

            SoundEvent sound = !entities.isEmpty() ? SoundEvents.ENTITY_ENDERMAN_TELEPORT : SoundEvents.BLOCK_DISPENSER_FAIL;
            world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1f, 1f);
        }
    }
}
