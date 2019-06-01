package com.swordglowsblue.redstonetweaks;

import com.swordglowsblue.redstonetweaks.block.container.HopperPipeBlockEntity;
import com.swordglowsblue.redstonetweaks.block.container.client.HopperPipeScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;

@Environment(EnvType.CLIENT)
public class RedstoneTweaksClient implements ClientModInitializer {
    public void onInitializeClient() {
        // === GUIS === //

        ScreenProviderRegistry.INSTANCE.registerFactory(RedstoneTweaks.REGISTRY.hopperPipeContainer, (sid, id, pe, buf) -> {
            HopperPipeBlockEntity be = ((HopperPipeBlockEntity)pe.getEntityWorld().getBlockEntity(buf.readBlockPos()));
            return new HopperPipeScreen(be.createContainer(sid, pe.inventory), pe.inventory, be.getDisplayName());
        });

        // === COLOR MAPPERS === //

        RedstoneTweaks.REGISTRY.dyedWire.registerColorProviders();
        RedstoneTweaks.REGISTRY.redstoneCapacitors.registerColorProviders();
    }
}
