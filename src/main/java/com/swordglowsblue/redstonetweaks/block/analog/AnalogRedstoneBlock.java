package com.swordglowsblue.redstonetweaks.block.analog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public class AnalogRedstoneBlock extends AbstractGlassBlock {
    public AnalogRedstoneBlock() {
        super(FabricBlockSettings.of(Material.GLASS).strength(0.5f, 0.5f).sounds(BlockSoundGroup.GLASS).build());
        this.setDefaultState(getStateFactory().getDefaultState().with(Properties.POWER, 15));
    }

    public boolean activate(BlockState bs, World world, BlockPos pos, PlayerEntity pe, Hand hand, BlockHitResult bhr) {
        if(!pe.isSneaking()) {
            BlockState nbs = bs.cycle(Properties.POWER);
            world.setBlockState(pos, nbs);
        }
        return true;
    }

    public boolean emitsRedstonePower(BlockState state) { return true; }
    public int getWeakRedstonePower(BlockState bs, BlockView bv, BlockPos bp, Direction dir) { return bs.get(Properties.POWER); }

    protected void appendProperties(StateFactory.Builder sf) { sf.add(Properties.POWER); }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState bs, World world, BlockPos bp, Random rand) {
        int power = bs.get(Properties.POWER);
        if (power != 0) {
            float powerPercent = power / 15.0F;
            double x = bp.getX() + 0.5D + (rand.nextFloat() - 0.5D) * powerPercent;
            double y = bp.getY() + 0.5D + (rand.nextFloat() - 0.5D) * powerPercent;
            double z = bp.getZ() + 0.5D + (rand.nextFloat() - 0.5D) * powerPercent;
            float red = powerPercent * 0.6F + 0.4F;
            float green = Math.max(0.0F, powerPercent * powerPercent * 0.7F - 0.5F);
            float blue = Math.max(0.0F, powerPercent * powerPercent * 0.6F - 0.7F);
            world.addParticle(new DustParticleEffect(red, green, blue, 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
