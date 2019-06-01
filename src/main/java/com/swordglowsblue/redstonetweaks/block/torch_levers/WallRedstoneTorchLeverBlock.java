package com.swordglowsblue.redstonetweaks.block.torch_levers;

import com.swordglowsblue.redstonetweaks.RedstoneTweaks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class WallRedstoneTorchLeverBlock extends WallTorchLeverBlock {
    public WallRedstoneTorchLeverBlock(Block standing) {
        super(7, standing);
        this.setDefaultState(super.getDefaultState().with(POWERED, true));
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(state.get(POWERED)) {
            Direction direction = state.get(FACING).getOpposite();
            double x = pos.getX() + (rand.nextDouble() - 0.5D) * 0.2D + 0.5D + 0.27D * direction.getOffsetX();
            double y = pos.getY() + (rand.nextDouble() - 0.5D) * 0.2D + 0.7D + 0.22D;
            double z = pos.getZ() + (rand.nextDouble() - 0.5D) * 0.2D + 0.5D + 0.27D * direction.getOffsetZ();
            world.addParticle(DustParticleEffect.RED, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
