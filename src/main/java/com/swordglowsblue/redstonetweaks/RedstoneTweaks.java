package com.swordglowsblue.redstonetweaks;

import net.fabricmc.api.ModInitializer;

public class RedstoneTweaks implements ModInitializer {
    public static final String MODID = "redstonetweaks";
    public static RedstoneTweaksRegistry REGISTRY;

    public void onInitialize() {
        REGISTRY = new RedstoneTweaksRegistry();
    }
}
