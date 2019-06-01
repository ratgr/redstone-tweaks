package com.swordglowsblue.redstonetweaks.block.container;

import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class HopperPipeContainer extends Container {
    private final Inventory inventory;

    public HopperPipeContainer(int syncId, PlayerInventory playerInv, Inventory inv) {
        super(null, syncId);
        this.inventory = inv;
        checkContainerSize(inv, 5);
        inv.onInvOpen(playerInv.player);

        for(int i = 0; i < 5; ++i)
            this.addSlot(new Slot(inv, i, 44 + i * 18, 20));
        for(int i = 0; i < 3; ++i) for(int j = 0; j < 9; ++j)
            this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, i * 18 + 51));
        for(int i = 0; i < 9; ++i)
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 109));
    }

    public boolean canUse(PlayerEntity pe) { return this.inventory.canPlayerUseInv(pe); }

    public ItemStack transferSlot(PlayerEntity pe, int slotNum) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slotList.get(slotNum);
        if(slot != null && slot.hasStack()) {
            ItemStack stack2 = slot.getStack();
            stack = stack2.copy();
            if(slotNum < this.inventory.getInvSize()) {
                if(!this.insertItem(stack2, this.inventory.getInvSize(), this.slotList.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if(!this.insertItem(stack2, 0, this.inventory.getInvSize(), false))
                return ItemStack.EMPTY;

            if(stack2.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }

        return stack;
    }

    public void close(PlayerEntity pe) {
        super.close(pe);
        this.inventory.onInvClose(pe);
    }
}
