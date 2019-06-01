package com.swordglowsblue.redstonetweaks;

import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneBlock;
import com.swordglowsblue.redstonetweaks.block.analog.AnalogRedstoneLampBlock;
import com.swordglowsblue.redstonetweaks.util.RegistryUtils;

public class RedstoneTweaksRegistry implements RegistryUtils {
    public final AnalogRedstoneBlock analogRedstoneBlock;
    public final AnalogRedstoneLampBlock analogRedstoneLamp;

    RedstoneTweaksRegistry() {
        analogRedstoneBlock = registerBlock("analog_redstone_block", new AnalogRedstoneBlock());
        analogRedstoneLamp = registerBlock("analog_redstone_lamp", new AnalogRedstoneLampBlock());
    }
}
