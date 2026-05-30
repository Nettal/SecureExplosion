package com.github.nettal.secureexplosion.client;

import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.client.ConfigScreenFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

import static com.github.nettal.secureexplosion.SecureExplosion.MOD_ID;

public class SecureExplosionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        ConfigScreenFactoryRegistry.INSTANCE.register(MOD_ID, ConfigurationScreen::new);
    }
}