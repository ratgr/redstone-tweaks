package com.swordglowsblue.redstonetweaks.block.wire;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * A surface-agnostic reimplementation of Redstone wire.
 * <br><br>
 * Does not assume the presence of a FACING property or any specific method of determining attachable faces.
 *   Instead, override {@link AbstractWireBlock#getAttachedFace} to return the correct face.
 *   By default, it always returns DOWN to maintain vanilla-like functionality.
 * <br><br>
 * The CONN_ constants refer to the properties for the different connection directions.
 *   {@link AbstractWireBlock#CONN_0} is north/up, with the other properties rotating clockwise from there.
 *   Ingame, 0 is referred to as north to keep parity with Vanilla redstone.
 * <br><br>
 * Many of the implementation details are actually in {@link IWire}. This is just a wrapper class for
 *   "standard" wire types and block hooks.
 *
 * @author swordglowsblue
 */
@SuppressWarnings({"deprecation","unchecked"})
public abstract class AbstractWireBlock extends Block implements IWire {
    public static final EnumProperty<WireConnection> CONN_0   = RedstoneWireBlock.WIRE_CONNECTION_NORTH;
    public static final EnumProperty<WireConnection> CONN_90  = RedstoneWireBlock.WIRE_CONNECTION_EAST;
    public static final EnumProperty<WireConnection> CONN_180 = RedstoneWireBlock.WIRE_CONNECTION_SOUTH;
    public static final EnumProperty<WireConnection> CONN_270 = RedstoneWireBlock.WIRE_CONNECTION_WEST;
    private static final EnumProperty<WireConnection>[] DIR_PROPS = new EnumProperty[] {CONN_0, CONN_90, CONN_180, CONN_270};
    public static final IntegerProperty POWER = RedstoneWireBlock.POWER;

    private boolean wiresGivePower;

    public AbstractWireBlock(Block.Settings settings) {
        super(settings);
        this.setDefaultState(getStateFactory().getDefaultState()
            .with(CONN_0, WireConnection.NONE)
            .with(CONN_90, WireConnection.NONE)
            .with(CONN_180, WireConnection.NONE)
            .with(CONN_270, WireConnection.NONE)
            .with(POWER, 0));
    }

    // === HOOKS === //

    public Direction getAttachedFace(BlockState state) { return Direction.DOWN; }
    public final EnumProperty<WireConnection> getConnectionProperty(Direction attachedFace, Direction of) {
        if(attachedFace.getAxis() == of.getAxis()) return null;
        Direction dir = getRelativeNorth(attachedFace);
        for(EnumProperty<WireConnection> prop : DIR_PROPS) {
            if(dir == of) return prop;
            dir = rotateClockwise(attachedFace, dir);
        }
        return null;
    }

    public final IntegerProperty getPowerProperty() { return POWER; }
    public final void setWiresGivePower(boolean wgp) { this.wiresGivePower = wgp; }

    // === BLOCK STATE === //

    protected void appendProperties(StateFactory.Builder<Block, BlockState> sf) {
        sf.add(CONN_0, CONN_90, CONN_180, CONN_270, POWER);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = this.getDefaultState();
        return this.applyConnectionsToState(state, ctx);
    }

    protected BlockState applyConnectionsToState(BlockState state, ItemPlacementContext ctx) {
        Direction attachedFace = getAttachedFace(state);
        Direction dir;
        return state
            .with(CONN_0, this.getConnectionTypeAt(ctx.getWorld(), state, ctx.getBlockPos(), dir = getRelativeNorth(attachedFace)))
            .with(CONN_90, this.getConnectionTypeAt(ctx.getWorld(), state, ctx.getBlockPos(), dir = rotateClockwise(attachedFace, dir)))
            .with(CONN_180, this.getConnectionTypeAt(ctx.getWorld(), state, ctx.getBlockPos(), dir = rotateClockwise(attachedFace, dir)))
            .with(CONN_270, this.getConnectionTypeAt(ctx.getWorld(), state, ctx.getBlockPos(), rotateClockwise(attachedFace, dir)));
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction neighborDirection, BlockState other, IWorld world, BlockPos pos, BlockPos otherPos) {
        Direction attachedFace = getAttachedFace(state);
        Direction dir;
        if(neighborDirection == attachedFace) return state;
        if(neighborDirection == attachedFace.getOpposite()) return state
            .with(CONN_0, this.getConnectionTypeAt(world, state, pos, dir = getRelativeNorth(attachedFace)))
            .with(CONN_90, this.getConnectionTypeAt(world, state, pos, dir = rotateClockwise(attachedFace, dir)))
            .with(CONN_180, this.getConnectionTypeAt(world, state, pos, dir = rotateClockwise(attachedFace, dir)))
            .with(CONN_270, this.getConnectionTypeAt(world, state, pos, rotateClockwise(attachedFace, dir)));
        return state
            .with(getConnectionProperty(attachedFace, neighborDirection), this.getConnectionTypeAt(world, state, pos, neighborDirection));
    }

    // === POWER === //

    public boolean canPlaceAt(BlockState state, ViewableWorld world, BlockPos pos) {
        Direction attachedFace = getAttachedFace(state);
        BlockState stateBelow = world.getBlockState(pos.offset(attachedFace));
        return Block.isSolidFullSquare(stateBelow, world, pos.down(), attachedFace.getOpposite()) || stateBelow.getBlock() == Blocks.HOPPER;
    }

    public boolean emitsRedstonePower(BlockState state) { return this.wiresGivePower; }

    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
        return state.getWeakRedstonePower(view, pos, dir); }
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        Direction attachedFace = getAttachedFace(state);
        if(!this.wiresGivePower) return 0;
        int power = state.get(POWER);
        if(power == 0) return 0;
        if(dir == attachedFace.getOpposite()) return power;

        EnumSet<Direction> dirs = EnumSet.noneOf(Direction.class);
        for(Direction dir2 : getRelativeHorizontals(attachedFace))
            if(getConnectionTypeAt(world, state, pos, dir2.getOpposite()) != WireConnection.NONE) dirs.add(dir2);

        if(isRelativeHorizontal(attachedFace, dir) && dirs.isEmpty()) return power;
        if(dirs.contains(dir)) return power;
        if(dirs.contains(dir.getOpposite()) &&
            !dirs.contains(rotateClockwise(attachedFace, dir)) &&
            !dirs.contains(rotateCounterclockwise(attachedFace, dir))) return power;

        return 0;
    }

    // === UPDATE === //

    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState state2, boolean flag) {
        Direction attachedFace = getAttachedFace(state);
        if(state2.getBlock() != state.getBlock() && !world.isClient) {
            this.update(world, pos, state);

            for(Direction direction : getRelativeVerticals(attachedFace))
                world.updateNeighborsAlways(pos.offset(direction), state.getBlock());

            for(Direction direction : getRelativeHorizontals(attachedFace))
                this.updateNeighbors(world, pos.offset(direction));

            for(Direction direction : getRelativeHorizontals(attachedFace)) {
                BlockPos pos2 = pos.offset(direction);
                if(world.getBlockState(pos2).isSimpleFullBlock(world, pos2)) this.updateNeighbors(world, pos2.offset(attachedFace.getOpposite()));
                else this.updateNeighbors(world, pos2.offset(attachedFace));
            }
        }
    }

    public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState state2, boolean flag) {
        Direction attachedFace = getAttachedFace(state);
        if(!flag && state.getBlock() != state2.getBlock()) {
            super.onBlockRemoved(state, world, pos, state2, flag);

            if(!world.isClient) {
                for(Direction direction : Direction.values())
                    world.updateNeighborsAlways(pos.offset(direction), state.getBlock());
                this.update(world, pos, state);

                for(Direction direction : getRelativeHorizontals(attachedFace))
                    this.updateNeighbors(world, pos.offset(direction));
                for(Direction direction : getRelativeHorizontals(attachedFace)) {
                    BlockPos pos2 = pos.offset(direction);
                    if(world.getBlockState(pos2).isSimpleFullBlock(world, pos2)) this.updateNeighbors(world, pos2.offset(attachedFace.getOpposite()));
                    else this.updateNeighbors(world, pos2.offset(attachedFace));
                }
            }
        }
    }

    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos pos2, boolean flag) {
        if(!world.isClient) {
            if(state.canPlaceAt(world, pos)) this.update(world, pos, state);
            else {
                dropStacks(state, world, pos);
                world.clearBlockState(pos, false);
            }
        }
    }

    // === RENDER === //

    public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
        Direction attachedFace = getAttachedFace(state);
        int power = state.get(POWER);

        if(power != 0) {
            double relHTransform = 0.5D + (rand.nextFloat() - 0.5D) * 0.2D;
            double relVTransform = 0.0625D + (attachedFace.getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 0);
            double x = pos.getX();
            double y = pos.getY();
            double z = pos.getZ();

            switch(attachedFace.getAxis()) {
                case X:
                    x += relVTransform;
                    y += relHTransform;
                    z += relHTransform;
                    break;
                case Y:
                    x += relHTransform;
                    y += relVTransform;
                    z += relHTransform;
                    break;
                case Z:
                    x += relHTransform;
                    y += relHTransform;
                    z += relVTransform;
                    break;
            }

            float[] c = getWireColor(power).getRGBColorComponents(null);
            world.addParticle(new DustParticleEffect(c[0], c[1], c[2], 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    // TODO: Fix outline shape for non-DOWN attachedFaces
    public VoxelShape getOutlineShape(BlockState state, BlockView bv, BlockPos pos, EntityContext ctx) {
        boolean north = state.get(CONN_0) != WireConnection.NONE;
        boolean east =  state.get(CONN_90) != WireConnection.NONE;
        boolean south = state.get(CONN_180) != WireConnection.NONE;
        boolean west =  state.get(CONN_270) != WireConnection.NONE;

        Direction attachedFace = getAttachedFace(state);
        boolean posV = attachedFace.getDirection() == Direction.AxisDirection.POSITIVE;
        VoxelShape[] shapes = SHAPES.get(attachedFace.getAxis());

        return VoxelShapes.cuboid(VoxelShapes.union(shapes[0],
            north || south && !east && !west ? shapes[1] : VoxelShapes.empty(),
            east || west && !north && !south ? shapes[2] : VoxelShapes.empty(),
            south || north && !east && !west ? shapes[3] : VoxelShapes.empty(),
            west || east && !north && !south ? shapes[4] : VoxelShapes.empty()
        ).getBoundingBox().offset(
            attachedFace.getAxis() == Direction.Axis.X && posV ? 15d : 0d,
            attachedFace.getAxis() == Direction.Axis.Y && posV ? 15d : 0d,
            attachedFace.getAxis() == Direction.Axis.Z && posV ? 15d : 0d
        ));
    }

    private static final Map<Direction.Axis, VoxelShape[]> SHAPES = new EnumMap(Direction.Axis.class);
    static {
        SHAPES.put(Direction.Axis.X, new VoxelShape[] {
            Block.createCuboidShape(0d, 3d, 3d, 1d, 13d, 13d),
            Block.createCuboidShape(0d, 3d, 0d, 1d, 13d, 13d),
            Block.createCuboidShape(0d, 3d, 3d, 1d, 16d, 13d),
            Block.createCuboidShape(0d, 3d, 3d, 1d, 13d, 16d),
            Block.createCuboidShape(0d, 0d, 3d, 1d, 13d, 13d)
        });
        SHAPES.put(Direction.Axis.Y, new VoxelShape[] {
            Block.createCuboidShape(3d, 0d, 3d, 13d, 1d, 13d),
            Block.createCuboidShape(3d, 0d, 0d, 13d, 1d, 13d),
            Block.createCuboidShape(3d, 0d, 3d, 16d, 1d, 13d),
            Block.createCuboidShape(3d, 0d, 3d, 13d, 1d, 16d),
            Block.createCuboidShape(0d, 0d, 3d, 13d, 1d, 13d)
        });
        SHAPES.put(Direction.Axis.Z, new VoxelShape[] {
            Block.createCuboidShape(3d, 3d, 0d, 13d, 13d, 1d),
            Block.createCuboidShape(3d, 0d, 0d, 13d, 13d, 1d),
            Block.createCuboidShape(3d, 3d, 0d, 16d, 13d, 1d),
            Block.createCuboidShape(3d, 3d, 0d, 13d, 16d, 1d),
            Block.createCuboidShape(0d, 3d, 0d, 13d, 13d, 1d)
        });
    }

    // === MISCELLANEOUS === //

    /** No idea what this does but {@link net.minecraft.block.RedstoneWireBlock} has it, so... */
    @SuppressWarnings("ConstantConditions")
    public void method_9517(BlockState state, IWorld world, BlockPos pos, int flag) {
        Direction attachedFace = getAttachedFace(state);
        BlockPos.PooledMutable pool = BlockPos.PooledMutable.get();
        Throwable error = null;

        Consumer<Direction> replace = (Direction direction) -> {
            BlockState state2 = world.getBlockState(pool);
            if(state2.getBlock() != Blocks.OBSERVER) {
                BlockPos pos2 = pool.offset(direction.getOpposite());
                BlockState state3 = state2.getStateForNeighborUpdate(direction.getOpposite(), world.getBlockState(pos2), world, pool, pos2);
                replaceBlock(state2, state3, world, pool, flag);
            }
        };

        try {
            for(Direction direction : getRelativeHorizontals(attachedFace)) {
                WireConnection connection = state.get(getConnectionProperty(attachedFace, direction));
                if(connection != WireConnection.NONE && world.getBlockState(pool.method_10114(pos).method_10118(direction)).getBlock() != this) {
                    pool.method_10118(attachedFace);
                    replace.accept(direction);
                    pool.method_10114(pos).method_10118(direction).method_10118(attachedFace.getOpposite());
                    replace.accept(direction);
                }
            }
        } catch(Throwable e) {
            error = e;
            throw e;
        } finally {
            if(pool != null) {
                if(error != null) {
                    try { pool.close(); } catch (Throwable e) { error.addSuppressed(e); }
                } else {
                    pool.close();
                }
            }
        }
    }
}
