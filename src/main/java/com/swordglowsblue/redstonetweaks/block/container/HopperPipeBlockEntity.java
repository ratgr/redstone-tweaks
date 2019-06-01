package com.swordglowsblue.redstonetweaks.block.container;

import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.stream.IntStream;

public class HopperPipeBlockEntity extends LootableContainerBlockEntity
        implements Hopper, Tickable, BlockEntityClientSerializable {
    private DefaultedList<ItemStack> inventory;
    private int transferCooldown;
    private long lastTickTime;

    public HopperPipeBlockEntity(BlockEntityType<HopperPipeBlockEntity> type) {
        super(type);
        this.inventory = DefaultedList.create(5, ItemStack.EMPTY);
        this.transferCooldown = -1;
    }

    public void fromTag(CompoundTag nbt) {
        super.fromTag(nbt);
        this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
        if(!this.deserializeLootTable(nbt)) Inventories.fromTag(nbt, this.inventory);
        this.transferCooldown = nbt.getInt("TransferCooldown");
    }

    public CompoundTag toTag(CompoundTag nbt) {
        super.toTag(nbt);
        if(!this.serializeLootTable(nbt)) Inventories.toTag(nbt, this.inventory);
        nbt.putInt("TransferCooldown", this.transferCooldown);
        return nbt;
    }

    public void fromClientTag(CompoundTag nbt) { fromTag(nbt); }
    public CompoundTag toClientTag(CompoundTag nbt) { return toTag(nbt); }

    public int getInvSize() { return this.inventory.size(); }

    public ItemStack takeInvStack(int slot, int amount) {
        this.checkLootInteraction(null);
        return Inventories.splitStack(this.getInvStackList(), slot, amount);
    }

    public void setInvStack(int slot, ItemStack stack) {
        this.checkLootInteraction(null);
        this.getInvStackList().set(slot, stack);
        if (stack.getAmount() > this.getInvMaxStackAmount()) {
            stack.setAmount(this.getInvMaxStackAmount());
        }
    }

    protected Component getContainerName() { return new TranslatableComponent("container.hopper"); }

    public void tick() {
        if(this.world != null && !this.world.isClient) {
            --this.transferCooldown;
            this.lastTickTime = this.world.getTime();
            if(!this.needsCooldown()) {
                this.setCooldown(0);
                this.insertAndExtract();
            }
        }
    }

    private boolean insertAndExtract() {
        if(this.world == null || this.world.isClient) return false;
        if(this.needsCooldown() || !this.getCachedState().get(HopperPipeBlock.ENABLED)) return false;

        if(!this.isInvEmpty() && this.insert()) {
            this.setCooldown(8);
            this.markDirty();
            return true;
        }

        return false;
    }

    public boolean isInvEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    private boolean insert() {
        Inventory inv = this.getOutputInventory();
        if(inv == null) return false;

        Direction dir = this.getCachedState().get(HopperPipeBlock.FACING).getOpposite();
        if(this.isInventoryFull(inv, dir)) return false;

        for(int slot = 0; slot < this.getInvSize(); ++slot) {
            if(!this.getInvStack(slot).isEmpty()) {
                ItemStack stack = this.getInvStack(slot).copy();
                ItemStack newStack = transfer(this, inv, this.takeInvStack(slot, 1), dir);
                if(newStack.isEmpty()) {
                    inv.markDirty();
                    return true;
                }

                this.setInvStack(slot, stack);
            }
        }

        return false;
    }

    private static IntStream getAvailableSlots(Inventory inv, Direction dir) {
        return inv instanceof SidedInventory ? IntStream.of(((SidedInventory)inv).getInvAvailableSlots(dir)) : IntStream.range(0, inv.getInvSize());
    }

    private boolean isInventoryFull(Inventory inv, Direction dir) {
        return getAvailableSlots(inv, dir).allMatch(i -> {
            ItemStack stack = inv.getInvStack(i);
            return stack.getAmount() >= stack.getMaxAmount();
        });
    }

    public static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, Direction dir) {
        if(to instanceof SidedInventory && dir != null) {
            SidedInventory sidedTo = (SidedInventory)to;
            int[] slots = sidedTo.getInvAvailableSlots(dir);
            for(int slot = 0; slot < slots.length && !stack.isEmpty(); ++slot)
                stack = transfer(from, to, stack, slots[slot], dir);
        } else {
            int invSize = to.getInvSize();
            for(int slot = 0; slot < invSize && !stack.isEmpty(); ++slot)
                stack = transfer(from, to, stack, slot, dir);
        }
        return stack;
    }

    private static boolean canInsert(Inventory inv, ItemStack stack, int slot, Direction dir) {
        if(!inv.isValidInvStack(slot, stack)) return false;
        return !(inv instanceof SidedInventory) || ((SidedInventory)inv).canInsertInvStack(slot, stack, dir);
    }

    private static ItemStack transfer(Inventory from, Inventory to, ItemStack stack, int slot, Direction dir) {
        ItemStack existingStack = to.getInvStack(slot);
        if (canInsert(to, stack, slot, dir)) {
            boolean success = false;
            boolean toEmpty = to.isInvEmpty();
            if(existingStack.isEmpty()) {
                to.setInvStack(slot, stack);
                stack = ItemStack.EMPTY;
                success = true;
            } else if(canMergeItems(existingStack, stack)) {
                int remainingSpace = stack.getMaxAmount() - existingStack.getAmount();
                int numToInsert = Math.min(stack.getAmount(), remainingSpace);
                stack.subtractAmount(numToInsert);
                existingStack.addAmount(numToInsert);
                success = numToInsert > 0;
            }

            if(success) {
                if(toEmpty && to instanceof HopperPipeBlockEntity) {
                    HopperPipeBlockEntity toBE = (HopperPipeBlockEntity)to;
                    if(!toBE.isDisabled()) {
                        int cooldown = 0;
                        if(from instanceof HopperPipeBlockEntity) {
                            HopperPipeBlockEntity fromBE = (HopperPipeBlockEntity)from;
                            if (toBE.lastTickTime >= fromBE.lastTickTime) cooldown = 1;
                        }

                        toBE.setCooldown(8 - cooldown);
                    }
                }

                to.markDirty();
            }
        }

        return stack;
    }

    private Inventory getOutputInventory() {
        Direction dir = this.getCachedState().get(HopperBlock.FACING);
        return getInventoryAt(this.getWorld(), this.pos.offset(dir));
    }

    public static Inventory getInventoryAt(World world, BlockPos pos) {
        return getInventoryAt(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
    }

    public static Inventory getInventoryAt(World world, double x, double y, double z) {
        Inventory inv = null;
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if(block instanceof InventoryProvider) {
            inv = ((InventoryProvider)block).getInventory(state, world, pos);
        } else if(block.hasBlockEntity()) {
            BlockEntity be = world.getBlockEntity(pos);
            if(be instanceof Inventory) {
                inv = (Inventory)be;
                if(inv instanceof ChestBlockEntity && block instanceof ChestBlock)
                    inv = ChestBlock.getInventory(state, world, pos, true);
            }
        }

        return inv;
    }

    private static boolean canMergeItems(ItemStack stack1, ItemStack stack2) {
        if(stack1.getItem() != stack2.getItem()) return false;
        if(stack1.getDamage() != stack2.getDamage()) return false;
        if(stack1.getAmount() > stack1.getMaxAmount()) return false;
        return ItemStack.areTagsEqual(stack1, stack2);
    }

    public double getHopperX() { return this.pos.getX() + 0.5D; }
    public double getHopperY() { return this.pos.getY() + 0.5D; }
    public double getHopperZ() { return this.pos.getZ() + 0.5D; }

    private void setCooldown(int cd) { this.transferCooldown = cd; }
    private boolean needsCooldown() { return this.transferCooldown > 0; }
    private boolean isDisabled() { return this.transferCooldown > 8; }

    protected DefaultedList<ItemStack> getInvStackList() { return this.inventory; }
    protected void setInvStackList(DefaultedList<ItemStack> list) { this.inventory = list; }

    public HopperPipeContainer createContainer(int syncId, PlayerInventory playerInv) {
        return new HopperPipeContainer(syncId, playerInv, this);
    }
}
