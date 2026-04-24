package com.tundrey.lightspeed;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ModConstants.MOD_ID)
public class Main {
    public Main(IEventBus modEventBus) {
        ModConstants.LOGGER.info("Lightspeed mod initialized!");
    }
}