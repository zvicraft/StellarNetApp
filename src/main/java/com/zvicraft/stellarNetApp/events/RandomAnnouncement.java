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
import com.zvicraft.stellarNetApp.utils.SoundUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomAnnouncement {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static BukkitTask announcementTask;

    private final StellarNetApp plugin;

    private final Random random = new Random();
    private final SoundUtils sound;

    public RandomAnnouncement(StellarNetApp plugin) {
        this.plugin = plugin;
        this.sound = new SoundUtils(plugin);
    }

    // Call this once in onEnable()
    public void start() {
        if (announcementTask != null) {
            announcementTask.cancel();
        }
        scheduleNextMessage();
    }

    public void test() {
        messageSettings();
    }

    private void scheduleNextMessage() {
        int minMinutes = plugin.getConfig().getInt("minMinutes", 5);
        int maxMinutes = plugin.getConfig().getInt("maxMinutes", 30);

        int minutes = minMinutes + random.nextInt(maxMinutes - minMinutes + 1);
        long delayTicks = minutes * 1200L; // 1 min = 1200 ticks

        announcementTask = Bukkit.getScheduler().runTaskLater(plugin, () -> {
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

        String announcementPath = resolveAnnouncementPath();
        String hoverPath = announcementPath.replace(".messages", ".hover");
        String hoverText = LangUtiltis.hasLangPath(hoverPath)
                ? LangUtiltis.getLangString(hoverPath)
                : LangUtiltis.getLangString("announcements_messages.hover");

        Component announcement = LangUtiltis.getLangComponent(announcementPath, links, hoverText);
        String prefixText = LangUtiltis.getLangString("announcements_messages.prefix");
        if (!prefixText.isBlank()) {
            announcement = LEGACY.deserialize(prefixText + " ").append(announcement);
        }

        for (var player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(announcement);
            sound.playLevelUpSound(player, "announcements");
        }
    }

    private String resolveAnnouncementPath() {
        String variantsPath = "announcements_messages.variants";
        if (!LangUtiltis.hasLangPath(variantsPath)) {
            return "announcements_messages.messages";
        }

        List<String> variantKeys = LangUtiltis.getSectionKeys(variantsPath).stream().toList();
        if (variantKeys.isEmpty()) {
            return "announcements_messages.messages";
        }

        String selectedVariant = variantKeys.get(random.nextInt(variantKeys.size()));
        return variantsPath + "." + selectedVariant + ".messages";
    }
}
