/*
 * Copyright (c) 2025 Zviel Koren
 * All rights reserved.
 *
 * This source code and its content are the intellectual property of Zviel Koren.
 * Unauthorized copying, modification, or distribution of this software,
 * via any medium, is strictly prohibited without written permission from the author.
 *
 */

package com.zvicraft.stellarNetApp.utils;

import com.zvicraft.stellarNetApp.StellarNetApp;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundUtils {
    private final StellarNetApp plugin;

    public SoundUtils(StellarNetApp plugin) {
        this.plugin = plugin;
    }

    public void playLevelUpSound(Player player) {
        ConfigurationSection soundConfig = plugin.getConfig().getConfigurationSection("level_up_sound");
        if (soundConfig == null) {
            plugin.getLogger().warning("level_up_sound configuration section is missing!");
            return;
        }

        String soundName = soundConfig.getString("name", "ENTITY_PLAYER_LEVELUP");
        float volume = (float) soundConfig.getDouble("volume", 1.0);
        float pitch = (float) soundConfig.getDouble("pitch", 1.0);

        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid level-up sound name in config: " + soundName);
        }
    }
}