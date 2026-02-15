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
package com.zvicraft.stellarNetApp.events;

import com.zvicraft.stellarNetApp.StellarNetApp;
import com.zvicraft.stellarNetApp.utils.LangUtiltis;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomAnnouncement {

    private final StellarNetApp plugin;
    private final Random random = new Random();

    public RandomAnnouncement(StellarNetApp plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("plugin cannot be null");
        }
        this.plugin = plugin;
    }

    public void test() {
        messageSettings();
    }

    // Call this once in onEnable()
    public void start() {
        scheduleNextMessage();
    }

    private void scheduleNextMessage() {
        int minMinutes = plugin.getConfig().getInt("minMinutes", 5);
        int maxMinutes = plugin.getConfig().getInt("maxMinutes", 30);

        int minutes = minMinutes + random.nextInt(maxMinutes - minMinutes + 1);
        long delayTicks = minutes * 1200L; // 1 min = 1200 ticks

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            messageSettings();
            // Schedule the next random announcement
            scheduleNextMessage();
        }, delayTicks);
    }

    private void messageSettings() {
        Map<String, String> links = new HashMap<>();
        ConfigurationSection sec = plugin.getConfig().getConfigurationSection("announcements.links");
        if (sec != null) {
            for (String k : sec.getKeys(false)) {
                String url = sec.getString(k);
                if (url != null && !url.isBlank()) links.put(k, url);
            }
        }

        Component announcement = LangUtiltis.getLangComponent(
                "announcements_messages.messages",
                links,
                LangUtiltis.getLangString("announcements_messages.hover")
        );

        Bukkit.getServer().broadcast(announcement);
    }
}
