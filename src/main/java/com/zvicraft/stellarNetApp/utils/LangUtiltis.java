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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class LangUtiltis {
    public static StellarNetApp plugin;
    private static FileConfiguration langConfig;

    public LangUtiltis(StellarNetApp plugin) {
        this.plugin = plugin;
    }

    public static void loadLanguageConfig() {
        String lang = plugin.getConfig().getString("language", "en");
        File langFile = new File(plugin.getDataFolder(), "lang_" + lang + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang_" + lang + ".yml", false);
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);
    }

    public static String getLangString(String key, String... placeholders) {
        if (langConfig == null) {
            plugin.getLogger().warning("Language configuration is not loaded. Key: " + key);
            return "Configuration error: " + key;
        }

        String message = langConfig.getString(key);
        if (message == null) {
            plugin.getLogger().warning("Missing language key: " + key);
            return "Missing key: " + key;
        }

        message = ChatColor.translateAlternateColorCodes('&', message);

        if (placeholders != null && placeholders.length > 0) {
            if (placeholders.length % 2 != 0) {
                plugin.getLogger().warning("Odd number of placeholder arguments for key: " + key);
            }
            for (int i = 0; i < placeholders.length - 1; i += 2) {
                message = message.replace(placeholders[i], placeholders[i + 1]);
            }
        }

        return message;
    }

    public static void reloadLang() {
        loadLanguageConfig();
    }
    public static Component getLangComponent(String key, String clickUrl, String hoverText) {
        if (langConfig == null) {
            plugin.getLogger().warning("Language configuration is not loaded. Key: " + key);
            return Component.text("Configuration error: " + key);
        }

        List<String> lines = langConfig.getStringList(key);
        if (lines == null || lines.isEmpty()) {
            plugin.getLogger().warning("Missing language key or empty list: " + key);
            return Component.text("Missing key: " + key);
        }

        Component comp = Component.empty();
        for (int i = 0; i < lines.size(); i++) {
            String line = ChatColor.translateAlternateColorCodes('&', lines.get(i));
            Component lineComp = Component.text(line);

            // Apply click and hover only on specific lines, e.g., second line
            if (i == 1 && clickUrl != null && hoverText != null) {
                lineComp = lineComp.clickEvent(ClickEvent.openUrl(clickUrl))
                        .hoverEvent(HoverEvent.showText(Component.text(hoverText)));
            }

            comp = comp.append(lineComp);
            if (i < lines.size() - 1) comp = comp.append(Component.text("\n"));
        }

        return comp;
    }
}
