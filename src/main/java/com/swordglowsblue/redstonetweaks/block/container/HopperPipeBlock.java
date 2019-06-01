package com.swordglowsblue.redstonetweaks.block.container;

import com.swordglowsblue.redstonetweaks.RedstoneTweaks;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HopperPipeBlock extends HopperBlock {
    public HopperPipeBlock() {
        super(FabricBlockSettings.of(Material.METAL, MaterialColor.STONE)
            .strength(3.0F, 4.8F).sounds(BlockSoundGroup.METAL).build());
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if(stack.hasDisplayName()) {
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof HopperPipeBlockEntity) {
                ((HopperPipeBlockEntity)be).setCustomName(stack.getDisplayName());
            }
        }
    }

    public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity pe, Hand hand, BlockHitResult bhr) {
        if(!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof HopperPipeBlockEntity) {
                ContainerProviderRegistry.INSTANCE.openContainer(RedstoneTweaks.REGISTRY.hopperPipeContainer, pe, buf -> buf.writeBlockPos(pos));
                pe.incrementStat(RedstoneTweaks.REGISTRY.statInspectHopperPipe);
            }
        }

        return true;
    }

    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState other, boolean boolean_1) {
        if(state.getBlock() != other.getBlock()) {
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof HopperPipeBlockEntity) {
                ItemScatterer.spawn(world, pos, (HopperPipeBlockEntity)be);
                world.updateHorizontalAdjacent(pos, this);
            }

            super.onBlockRemoved(state, world, pos, other, boolean_1);
        }
    }

    public BlockEntity createBlockEntity(BlockView view) { return RedstoneTweaks.REGISTRY.createHopperPipeEntity.get(); }
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {}

    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ctx) {
        switch(state.get(FACING)) {
            case DOWN:  return DOWN_SHAPE;
            case NORTH: return NORTH_SHAPE;
            case SOUTH: return SOUTH_SHAPE;
            case WEST:  return WEST_SHAPE;
            case EAST:  return EAST_SHAPE;
            default:    return null;
        }
    }

    public VoxelShape getRayTraceShape(BlockState state, BlockView view, BlockPos pos) {
        return getOutlineShape(state, view, pos, null);
    }

    private static final VoxelShape DOWN_SHAPE;
    private static final VoxelShape EAST_SHAPE;
    private static final VoxelShape NORTH_SHAPE;
    private static final VoxelShape SOUTH_SHAPE;
    private static final VoxelShape WEST_SHAPE;

    static {
        DOWN_SHAPE = VoxelShapes.union(Block.createCuboidShape(5, 12, 5, 11, 16, 11), Block.createCuboidShape(6, 0, 6, 10, 12, 10));
        EAST_SHAPE = VoxelShapes.union(Block.createCuboidShape(0, 3, 5, 4, 9, 11), Block.createCuboidShape(4, 4, 6, 16, 8, 10));
        WEST_SHAPE = VoxelShapes.union(Block.createCuboidShape(12, 3, 5, 16, 9, 11), Block.createCuboidShape(0, 4, 6, 12, 8, 10));
        NORTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(5, 3, 12, 11, 9, 16), Block.createCuboidShape(6, 4, 0, 10, 8, 12));
        SOUTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(5, 3, 0, 11, 9, 4), Block.createCuboidShape(6, 4, 4, 10, 8, 16));
    }
}
