package com.swordglowsblue.redstonetweaks.block.wire;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

/**
 * Interface for a surface-agnostic version of redstone wire.
 * @see AbstractWireBlock
 *
 * @author swordglowsblue
 */
public interface IWire {
    /** Get the block face a given blockstate is attached to. */
    Direction getAttachedFace(BlockState state);
    /** Get the appropriate connection property for this attached face and direction. */
    EnumProperty<WireConnection> getConnectionProperty(Direction attachedFace, Direction of);
    /** Get the blockstate property that stores this wire's power level. */
    IntegerProperty getPowerProperty();

    boolean emitsRedstonePower(BlockState state);

    // === DIRECTION UTILS === //

    /** Get the direction used as "north" for the given attached face. */
    default Direction getRelativeNorth(Direction attachedFace) {
        return attachedFace.getAxis().isVertical() ? Direction.NORTH : Direction.UP;
    }

    /** Test whether a direction is "horizontal" relative to the given attached face. */
    default boolean isRelativeHorizontal(Direction attachedFace, Direction toTest) {
        return toTest.getAxis() != attachedFace.getAxis();
    }

    /** Test whether a direction is "vertical" relative to the given attached face. */
    default boolean isRelativeVertical(Direction attachedFace, Direction toTest) {
        return toTest.getAxis() == attachedFace.getAxis();
    }

    /** Rotate the given direction clockwise around the attached face. */
    default Direction rotateClockwise(Direction attachedFace, Direction toRotate) {
        if(toRotate.getAxis() == attachedFace.getAxis()) return toRotate;

        if(attachedFace.getAxis() == Direction.Axis.Y) {
            ArrayList<Direction> aroundY = Lists.newArrayList(Direction.Type.HORIZONTAL.iterator());
            return aroundY.get((aroundY.indexOf(toRotate)+1) % 4);
        }

        boolean posV = attachedFace.getDirection() == Direction.AxisDirection.POSITIVE;
        Direction relR = attachedFace.getAxis() == Direction.Axis.X ? Direction.NORTH : Direction.WEST;
        Direction relL = relR.getOpposite();

        ArrayList<Direction> around = Lists.newArrayList(
            Direction.UP,   posV ? relR : relL,
            Direction.DOWN, posV ? relL : relR
        );

        return around.get((around.indexOf(toRotate)+1) % 4);
    }

    /** Rotate the given direction counter-clockwise around the attached face. */
    default Direction rotateCounterclockwise(Direction attachedFace, Direction toRotate) {
        if(toRotate.getAxis() == attachedFace.getAxis()) return toRotate;
        return rotateClockwise(attachedFace, rotateClockwise(attachedFace, rotateClockwise(attachedFace, toRotate)));
    }

    /** Get a list of directions that are "horizontal" relative to the given attached face. */
    default EnumSet<Direction> getRelativeHorizontals(Direction attachedFace) {
        return EnumSet.complementOf(getRelativeVerticals(attachedFace));
    }

    /** Get a list of directions that are "vertical" relative to the given attached face. */
    default EnumSet<Direction> getRelativeVerticals(Direction attachedFace) {
        return EnumSet.of(attachedFace, attachedFace.getOpposite());
    }

    // === BASIC FUNCTIONALITY === //

    /** Get a list of directions which power should be received from.
     *  Useful to implement wires that don't power each other or similar. */
    default Set<Direction> getPowerReceivableDirections(World world, BlockPos pos) {
        Set<Direction> directions = Sets.newHashSet(Direction.values());
        BlockState state = world.getBlockState(pos);
        for(Direction direction : Direction.values()) {
            BlockState other = world.getBlockState(pos.offset(direction));
            if(!canStatePowerWire(state, other)) directions.remove(direction);
        }
        return directions;
    }

    /** Check whether the given blockstate can provide power to this wire.
     *  This is designed for blacklisting, not whitelisting. Blocks without an explicit reason to not power
     *    this wire should cause this method to return true. */
    default boolean canStatePowerWire(BlockState state, BlockState other) {
        if(other.getBlock() instanceof IWire && !shouldConnect(state, other, null)) return false;
        return true;
    }

    /** Check whether redstone wire should give power to redstone wire. */
    static boolean getWiresGivePower() { return ((IWire)Blocks.REDSTONE_WIRE).rt_internal_get_wgp(); }
    /** Set whether redstone wire should give power to redstone wire. */
    static void setWiresGivePower(boolean wgp) { ((IWire)Blocks.REDSTONE_WIRE).rt_internal_set_wgp(wgp); }

    /** Hook for {@link RedstoneWireBlock#wiresGivePower}. Should only be implemented/called on {@link Blocks#REDSTONE_WIRE}. */
    @Deprecated default boolean rt_internal_get_wgp() { return false; }
    /** Hook for {@link RedstoneWireBlock#wiresGivePower}. Should only be implemented/called on {@link Blocks#REDSTONE_WIRE}. */
    @Deprecated default void rt_internal_set_wgp(boolean wgp) {}

    /** Check whether the given blockstates should connect to each other.
     *  DO NOT OVERRIDE THIS VERSION! Override {@link IWire#shouldConnect(BlockState, BlockState, Direction, boolean)} instead. */
    default boolean shouldConnect(BlockState state, BlockState other, Direction dir)
        { return shouldConnect(state, other, dir, true); }
    /** Check whether the given blockstates should connect to each other. */
    default boolean shouldConnect(BlockState state, BlockState other, Direction dir, boolean allowRecurse) {
        Direction dirOpp = dir == null ? null : dir.getOpposite();
        if(other.getBlock() instanceof IWire)
            return ((IWire)other.getBlock()).getAttachedFace(other) != getAttachedFace(state).getOpposite() &&
                   (!allowRecurse || ((IWire)other.getBlock()).shouldConnect(other, state, dirOpp, false));
        if(other.getBlock() == Blocks.REPEATER)
            return other.get(RepeaterBlock.FACING) == dir ||
                   other.get(RepeaterBlock.FACING) == dirOpp;
        if(other.getBlock() == Blocks.OBSERVER)
            return other.get(ObserverBlock.FACING) == dir;
        return other.emitsRedstonePower();
    }

    /** Get the appropriate WireConnection for the given position and direction.
     *  Will return {@link WireConnection#NONE} rather than null. */
    default WireConnection getConnectionTypeAt(BlockView view, BlockState state, BlockPos pos, Direction dir) {
        Direction attachedFace = getAttachedFace(state);

        BlockState blockAbove = view.getBlockState(pos.offset(attachedFace.getOpposite()));
        if(blockAbove.getBlock() instanceof IWire && shouldConnect(state, blockAbove, attachedFace.getOpposite()))
            if(((IWire)blockAbove.getBlock()).getAttachedFace(blockAbove) == dir) return WireConnection.UP;

        BlockState otherState = view.getBlockState(pos.offset(dir));
        BlockState aboveState = view.getBlockState(pos.offset(dir).offset(attachedFace.getOpposite()));
        BlockState belowState = view.getBlockState(pos.offset(dir).offset(attachedFace));

        boolean otherFull = otherState.isSimpleFullBlock(view, pos.offset(dir));
        boolean aboveFull = aboveState.isSimpleFullBlock(view, pos.offset(dir).offset(attachedFace.getOpposite()));

        if(otherFull && !aboveFull && aboveState.getBlock() instanceof IWire && shouldConnect(state, aboveState, dir)) return WireConnection.UP;
        if(shouldConnect(state, otherState, dir)) return WireConnection.SIDE;
        if(!otherFull && belowState.getBlock() instanceof IWire && shouldConnect(state, belowState, dir)) return WireConnection.SIDE;

        return WireConnection.NONE;
    }

    // === UPDATE LOGIC === //

    default BlockState update(World world, BlockPos pos, BlockState state) {
        Pair<BlockState, ArrayList<BlockPos>> pair = this.updateLogic(world, pos, state);
        for(BlockPos neighbor : pair.getRight()) world.updateNeighborsAlways(neighbor, state.getBlock());
        return pair.getLeft();
    }

    default Pair<BlockState, ArrayList<BlockPos>> updateLogic(World world, BlockPos pos, BlockState state) {
        Direction attachedFace = getAttachedFace(state);
        ArrayList<BlockPos> affectedNeighbors = new ArrayList<>();
        Set<Direction> receivableDirections = getPowerReceivableDirections(world, pos);

        int currentPower = state.get(getPowerProperty());
        setWiresGivePower(false);
        int incomingPower = getReceivedRedstonePower(world, pos, Lists.newArrayList(receivableDirections));
        setWiresGivePower(true);

        int wirePower = 0;
        if(incomingPower < 15) {
            for(Direction direction : getRelativeHorizontals(attachedFace)) {
                if(!receivableDirections.contains(direction)) continue;

                BlockPos otherPos = pos.offset(direction);
                BlockState otherState = world.getBlockState(otherPos);
                wirePower = this.increasePower(wirePower, otherState);

                BlockPos abovePos = pos.offset(attachedFace.getOpposite());
                BlockState aboveState = world.getBlockState(abovePos);
                if(otherState.isSimpleFullBlock(world, otherPos) && !aboveState.isSimpleFullBlock(world, abovePos)) {
                    BlockState state2 = world.getBlockState(otherPos.offset(attachedFace.getOpposite()));
                    if(state2.getBlock() instanceof IWire && canStatePowerWire(state, state2))
                        wirePower = this.increasePower(wirePower, state2);
                } else if(!otherState.isSimpleFullBlock(world, otherPos)) {
                    BlockState state2 = world.getBlockState(otherPos.offset(attachedFace));
                    if(state2.getBlock() instanceof IWire && canStatePowerWire(state, state2))
                        wirePower = this.increasePower(wirePower, state2);
                }
            }

            if(receivableDirections.contains(attachedFace.getOpposite())) {
                BlockState otherState = world.getBlockState(pos.offset(attachedFace.getOpposite()));
                if(otherState instanceof IWire && shouldConnect(state, otherState, attachedFace.getOpposite()))
                    wirePower = this.increasePower(wirePower, world.getBlockState(pos.offset(attachedFace.getOpposite())));
            }
        }

        int finalPower = wirePower - 1;
        if(incomingPower > finalPower) finalPower = incomingPower;

        if(currentPower != finalPower) {
            world.setBlockState(pos, state.with(getPowerProperty(), finalPower), 2);
            affectedNeighbors.add(pos);
            for(Direction direction : Direction.values()) affectedNeighbors.add(pos.offset(direction));
        }

        return new Pair<>(state, affectedNeighbors);
    }

    default void updateNeighbors(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        if(block == this) {
            world.updateNeighborsAlways(pos, block);
            for(Direction direction : Direction.values())
                world.updateNeighborsAlways(pos.offset(direction), block);
        }
    }

    default int increasePower(int from, BlockState state) {
        return state.getBlock() instanceof IWire ? Math.max(state.get(getPowerProperty()), from) : from; }

    default int getReceivedRedstonePower(World world, BlockPos pos, ArrayList<Direction> fromDirections) {
        int power = 0;
        for(Direction direction : fromDirections)
            power = Math.max(power, world.getEmittedRedstonePower(pos.offset(direction), direction));
        return Math.min(power, 15);
    }

    // === RENDER === //

    /** Get the color of the wire for use with color mapping. */
    @Environment(EnvType.CLIENT)
    default Color getWireColor(int power) {
        return new Color(RedstoneWireBlock.getWireColor(power)); }

    /** Get the color of the wire for use with color mapping. */
    @Environment(EnvType.CLIENT)
    default Color getWireColor(BlockState state) {
        return getWireColor(state.get(getPowerProperty())); }
}
