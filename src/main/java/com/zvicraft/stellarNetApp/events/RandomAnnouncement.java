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

import java.util.Random;

public class RandomAnnouncement {

    private final StellarNetApp plugin;
    private final Random random = new Random();

    public RandomAnnouncement(StellarNetApp plugin) {
        this.plugin = plugin;
    }

    // Call this once in onEnable()
    public void start() {
        scheduleNextMessage();
    }

    public static void test() {
        messageSettings();
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

    private static void messageSettings() {
        //TODO need fix the link and error

        // Generate the announcement each time (supports dynamic reloads)
        Component announcement = LangUtiltis.getLangComponent(
                "announcements_messages.messages",
                LangUtiltis.getLangString("announcements_messages.url"),
                LangUtiltis.getLangString("announcements_messages.hover")
        );

        Bukkit.getServer().broadcast(announcement);
    }
}
