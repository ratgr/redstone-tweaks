package com.swordglowsblue.redstonetweaks.item;

import com.swordglowsblue.redstonetweaks.RedstoneTweaks;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlintAndRedstoneItem extends Item {
    public FlintAndRedstoneItem() { super(new Item.Settings().durability(64)); }

    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        World world = ctx.getWorld();
        BlockPos posClicked = ctx.getBlockPos();
        BlockPos posToPlace = posClicked.offset(ctx.getFacing());

        if(world.getBlockState(posToPlace).isAir()) {
            world.playSound(player, posToPlace, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(posToPlace, RedstoneTweaks.REGISTRY.redstoneSpark.stateForDuration(4), 11);

            ItemStack stack = ctx.getItemStack();
            if(player instanceof ServerPlayerEntity) {
                Criterions.PLACED_BLOCK.handle((ServerPlayerEntity)player, posToPlace, stack);
                stack.applyDamage(1, player, pe -> pe.sendToolBreakStatus(ctx.getHand()));
            }

            double x = posToPlace.getX() + 0.5D + (world.getRandom().nextFloat() - 0.5D) * 0.2f;
            double y = posToPlace.getY() + 0.5D + (world.getRandom().nextFloat() - 0.5D) * 0.2f;
            double z = posToPlace.getZ() + 0.5D + (world.getRandom().nextFloat() - 0.5D) * 0.2f;
            float red = 0.6F + 0.4F;
            float green = Math.max(0.0F, 0.7F - 0.5F);
            float blue = Math.max(0.0F, 0.6F - 0.7F);
            world.addParticle(new DustParticleEffect(red, green, blue, 1.0F), x, y, z, 0.0D, 0.0D, 0.0D);

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }
}
