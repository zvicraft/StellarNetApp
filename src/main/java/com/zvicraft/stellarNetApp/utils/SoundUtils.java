/*
 * Copyright (c) 2026 Zviel Koren
 * All rights reserved.
 *
 * This source code and its content are the intellectual property of Zviel Koren.
 * Unauthorized copying, modification, or distribution of this software,
 * via any medium, is strictly prohibited without written permission from the author.
 *
 */

/*
 * Copyright (c) 2026 Zviel Koren
 * All rights reserved.
 *
 * This source code and its content are the intellectual property of Zviel Koren.
 * Unauthorized copying, modification, or distribution of this software,
 * via any medium, is strictly prohibited without written permission from the author.
 *
 */

/*
 * Copyright (c) 2026 Zviel Koren
 * All rights reserved.
 *
 * This source code and its content are the intellectual property of Zviel Koren.
 * Unauthorized copying, modification, or distribution of this software,
 * via any medium, is strictly prohibited without written permission from the author.
 *
 */

package com.zvicraft.stellarNetApp.utils;

import com.zvicraft.stellarNetApp.StellarNetApp;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Locale;

public class SoundUtils {

    private final StellarNetApp plugin;

    public SoundUtils(StellarNetApp plugin) {
        this.plugin = plugin;
    }

    public void playLevelUpSound(Player player, String pathSound) {
        ConfigurationSection soundConfig = plugin.getConfig().getConfigurationSection(pathSound + ".sound");
        if (soundConfig == null) {
            plugin.getLogger().warning("'sound' configuration section is missing!");
            return;
        }

        String soundName = soundConfig.getString("name", "BLOCK_ANVIL_PLACE");
        float volume = (float) soundConfig.getDouble("volume", 1.0);
        float pitch = (float) soundConfig.getDouble("pitch", 1.0);

        Sound sound = resolveSound(soundName);
        if (sound == null) {
            plugin.getLogger().warning("Invalid sound configured at '" + pathSound + ".sound.name': " + soundName);
            return;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    private Sound resolveSound(String soundName) {
        if (soundName == null || soundName.isBlank()) {
            return null;
        }

        String normalizedKey = soundName
                .toLowerCase(Locale.ROOT)
                .replace("minecraft:", "")
                .replace('_', '.');

        return Registry.SOUNDS.get(NamespacedKey.minecraft(normalizedKey));
    }
}
