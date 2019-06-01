package com.swordglowsblue.redstonetweaks.block.analog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.ChatFormat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public class RedstoneCapacitorBlock extends Block {
    private final Tier tier;

    public RedstoneCapacitorBlock(Tier tier) {
        super(FabricBlockSettings.of(Material.METAL).strength(5.0F, 6.0F).sounds(BlockSoundGroup.METAL).build());
        this.setDefaultState(getStateFactory().getDefaultState().with(Properties.POWER, 0));
        this.tier = tier;
    }

    public boolean emitsRedstonePower(BlockState state) { return true; }
    public int getWeakRedstonePower(BlockState bs, BlockView bv, BlockPos bp, Direction dir) { return bs.get(Properties.POWER); }

    protected void appendProperties(StateFactory.Builder sf) { sf.add(Properties.POWER); }

    public void neighborUpdate(BlockState bs, World world, BlockPos pos, Block blockFrom, BlockPos posFrom, boolean flag) {
        if(!world.isClient) {
            int inputPower = world.getReceivedRedstonePower(pos);
            if(bs.get(Properties.POWER) < inputPower) {
                world.setBlockState(pos, bs.with(Properties.POWER, inputPower));
            }
        }
    }

    public void onBlockAdded(BlockState bs, World world, BlockPos pos, BlockState bs2, boolean flag) {
        world.getBlockTickScheduler().schedule(pos, this, tier.tickRate);
    }

    public void onScheduledTick(BlockState bs, World world, BlockPos pos, Random rand) {
        int power = bs.get(Properties.POWER);
        if(power > world.getReceivedRedstonePower(pos))
            world.setBlockState(pos, bs.with(Properties.POWER, power-1));
        world.getBlockTickScheduler().schedule(pos, this, tier.tickRate);
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState bs, World world, BlockPos bp, Random rand) {
        int power = bs.get(Properties.POWER);
        if (power != 0) {
            float powerPercent = power / 15.0F;
            double x = bp.getX() + 0.5D + (rand.nextFloat() - 0.5D) * 1.5D;
            double y = bp.getY() + 0.5D + (rand.nextFloat() - 0.5D) * 1.5D;
            double z = bp.getZ() + 0.5D + (rand.nextFloat() - 0.5D) * 1.5D;
            float red = powerPercent * 0.6F + 0.4F;
            float green = Math.max(0.0F, powerPercent * powerPercent * 0.7F - 0.5F);
            float blue = Math.max(0.0F, powerPercent * powerPercent * 0.6F - 0.7F);
            world.addParticle(new DustParticleEffect(red, green, blue, 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }

    public BlockRenderLayer getRenderLayer() { return BlockRenderLayer.CUTOUT; }

    @Environment(EnvType.CLIENT)
    public void buildTooltip(ItemStack stack, BlockView bv, List<Component> lines, TooltipContext ctx) {
        lines.add(new TranslatableComponent("tooltip.redstonetweaks.redstone_capacitor.drain_speed",
            tier.tickRate / 2, tier.tickRate / 2 != 1 ? "s" : "").applyFormat(ChatFormat.GRAY));
    }


    public enum Tier {
        IRON("iron", 2),
        GOLD("golden", 4),
        DIAMOND("diamond", 10),
        EMERALD("emerald", 20);

        public final String name;
        public final int tickRate;

        Tier(String name, int tickRate) {
            this.name = name;
            this.tickRate = tickRate;
        }

        public String toString() { return name; }
    }
}
