package com.swordglowsblue.redstonetweaks.mixin;

import com.swordglowsblue.redstonetweaks.block.wire.DyedRedstoneBlock;
import com.swordglowsblue.redstonetweaks.block.wire.IWire;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntegerProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

@Mixin(RedstoneWireBlock.class)
public abstract class MixinRedstoneWireBlock implements IWire {
    @Shadow private boolean wiresGivePower;

    public Direction getAttachedFace(BlockState state) { return Direction.DOWN; }
    public IntegerProperty getPowerProperty() { return RedstoneWireBlock.POWER; }
    public void setWiresGivePower(boolean wgp) { this.wiresGivePower = wgp; }

    public EnumProperty<WireConnection> getConnectionProperty(Direction attachedFace, Direction of) {
        switch(of) {
            case NORTH: return RedstoneWireBlock.WIRE_CONNECTION_NORTH;
            case EAST: return RedstoneWireBlock.WIRE_CONNECTION_EAST;
            case SOUTH: return RedstoneWireBlock.WIRE_CONNECTION_SOUTH;
            case WEST: return RedstoneWireBlock.WIRE_CONNECTION_WEST;
            default: return null;
        }
    }

    @Inject(method = "connectsTo(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void shouldConnectTo(BlockState state, Direction dir, CallbackInfoReturnable<Boolean> cbi) {
        Direction direction = dir == null ? null : dir.getOpposite();
        if(state.getBlock() instanceof IWire)
            cbi.setReturnValue(((IWire)state.getBlock()).shouldConnect(state, Blocks.REDSTONE_WIRE.getDefaultState(), direction));
        if(state.getBlock() instanceof DyedRedstoneBlock)
            cbi.setReturnValue(false);
    }

    @Redirect(method = "updateLogic", at = @At(value = "INVOKE",
        target = "net.minecraft.world.World.getReceivedRedstonePower(Lnet/minecraft/util/math/BlockPos;)I", ordinal = 0))
    private int getReceivedRedstonePower(World world, BlockPos pos) {
        ArrayList<Direction> directions = new ArrayList<>();
        for(Direction direction : Direction.values()) {
            BlockState state = world.getBlockState(pos);
            BlockState other = world.getBlockState(pos.offset(direction));
            if(other.getBlock() instanceof DyedRedstoneBlock) continue;
            if(!(other.getBlock() instanceof IWire)) { directions.add(direction); continue; }
            if(shouldConnect(state, other, direction)) directions.add(direction);
        }
        return getReceivedRedstonePower(world, pos, directions);
    }

    @Inject(method = "increasePower(ILnet/minecraft/block/BlockState;)I", at = @At("HEAD"), cancellable = true)
    private void increasePower(int current, BlockState from, CallbackInfoReturnable<Integer> cbi) {
        if(from.getBlock() instanceof IWire && !((IWire)from.getBlock()).shouldConnect(from, Blocks.REDSTONE_WIRE.getDefaultState(), null))
            cbi.setReturnValue(current);
    }
}
