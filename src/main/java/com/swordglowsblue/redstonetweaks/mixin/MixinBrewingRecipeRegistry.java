package com.swordglowsblue.redstonetweaks.mixin;

import com.swordglowsblue.redstonetweaks.RedstoneTweaksRegistry;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewingRecipeRegistry.class)
public abstract class MixinBrewingRecipeRegistry {
    @Shadow private static void registerPotionRecipe(Potion from, Item via, Potion to) {}

    @Inject(method = "registerDefaults()V", at = @At("RETURN"))
    private static void registerCustomBrewingRecipes(CallbackInfo cbi) {
        RedstoneTweaksRegistry.registerBrewingRecipes = (registry) -> {
            for(Item dyedRedstone : registry.dyedWire.getItems().values()) {
                registerPotionRecipe(Potions.WATER, dyedRedstone, Potions.MUNDANE);
                registerPotionRecipe(Potions.NIGHT_VISION, dyedRedstone, Potions.LONG_NIGHT_VISION);
                registerPotionRecipe(Potions.INVISIBILITY, dyedRedstone, Potions.LONG_INVISIBILITY);
                registerPotionRecipe(Potions.FIRE_RESISTANCE, dyedRedstone, Potions.LONG_FIRE_RESISTANCE);
                registerPotionRecipe(Potions.LEAPING, dyedRedstone, Potions.LONG_LEAPING);
                registerPotionRecipe(Potions.SLOWNESS, dyedRedstone, Potions.LONG_SLOWNESS);
                registerPotionRecipe(Potions.TURTLE_MASTER, dyedRedstone, Potions.LONG_TURTLE_MASTER);
                registerPotionRecipe(Potions.SWIFTNESS, dyedRedstone, Potions.LONG_SWIFTNESS);
                registerPotionRecipe(Potions.WATER_BREATHING, dyedRedstone, Potions.LONG_WATER_BREATHING);
                registerPotionRecipe(Potions.POISON, dyedRedstone, Potions.LONG_POISON);
                registerPotionRecipe(Potions.REGENERATION, dyedRedstone, Potions.LONG_REGENERATION);
                registerPotionRecipe(Potions.STRENGTH, dyedRedstone, Potions.LONG_STRENGTH);
                registerPotionRecipe(Potions.WEAKNESS, dyedRedstone, Potions.LONG_WEAKNESS);
                registerPotionRecipe(Potions.SLOW_FALLING, dyedRedstone, Potions.LONG_SLOW_FALLING);
            }
        };
    }
}
