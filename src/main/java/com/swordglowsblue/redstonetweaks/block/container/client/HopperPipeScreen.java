package com.swordglowsblue.redstonetweaks.block.container.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.swordglowsblue.redstonetweaks.block.container.HopperPipeContainer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HopperPipeScreen extends AbstractContainerScreen<HopperPipeContainer> {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/hopper.png");

    public HopperPipeScreen(HopperPipeContainer container, PlayerInventory pe, Component component) {
        super(container, pe, component);
        this.passEvents = false;
        this.containerHeight = 133;
    }

    public void render(int int_1, int int_2, float float_1) {
        this.renderBackground();
        super.render(int_1, int_2, float_1);
        this.drawMouseoverTooltip(int_1, int_2);
    }

    protected void drawForeground(int int_1, int int_2) {
        this.font.draw(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.containerHeight - 96 + 2), 4210752);
    }

    protected void drawBackground(float float_1, int int_1, int int_2) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        int int_3 = (this.width - this.containerWidth) / 2;
        int int_4 = (this.height - this.containerHeight) / 2;
        this.blit(int_3, int_4, 0, 0, this.containerWidth, this.containerHeight);
    }
}
