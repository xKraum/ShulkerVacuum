package com.shulkervacuum;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShulkerVacuum implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("shulkervacuum");

    @Override
    public void onInitialize() {
        LOGGER.info("Hello from ShulkerVacuum!");
    }
}