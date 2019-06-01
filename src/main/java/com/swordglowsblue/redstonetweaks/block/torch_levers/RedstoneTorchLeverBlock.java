package com.swordglowsblue.redstonetweaks.block.torch_levers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class RedstoneTorchLeverBlock extends TorchLeverBlock {
    public RedstoneTorchLeverBlock() {
        super(7, Blocks.REDSTONE_TORCH);
        this.setDefaultState(getStateFactory().getDefaultState().with(POWERED, true));
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random rand) {
        if(state.get(POWERED)) {
            double x = pos.getX() + (rand.nextDouble() - 0.5D) * 0.2D + 0.5D;
            double y = pos.getY() + (rand.nextDouble() - 0.5D) * 0.2D + 0.7D;
            double z = pos.getZ() + (rand.nextDouble() - 0.5D) * 0.2D + 0.5D;
            world.addParticle(DustParticleEffect.RED, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
}
