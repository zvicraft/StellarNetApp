

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
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangUtiltis {
    public static StellarNetApp plugin;
    private static FileConfiguration langConfig;

    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacyAmpersand();
    private static final Pattern LINK_TOKEN = Pattern.compile("\\{LINK(?::([a-zA-Z0-9_-]+))?}");

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

        // Keep '&' codes here; we'll deserialize to Components where needed.
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

    public static Component getLangComponent(String key, Map<String, String> linkUrls, String hoverText) {
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
            String rawLine = lines.get(i);
            if (rawLine == null) rawLine = "";

            Matcher m = LINK_TOKEN.matcher(rawLine);
            boolean makeClickable = m.find();

            String linkName = "default";
            if (makeClickable) {
                linkName = (m.group(1) != null && !m.group(1).isBlank()) ? m.group(1) : "default";
                rawLine = m.replaceAll("");
            }

            Component lineComp = legacy.deserialize(rawLine);

            String url = (linkUrls == null) ? null : linkUrls.get(linkName);
            if (makeClickable && url != null && !url.isBlank()) {
                lineComp = lineComp.clickEvent(ClickEvent.openUrl(url));

                if (hoverText != null && !hoverText.isBlank()) {
                    lineComp = lineComp.hoverEvent(
                            HoverEvent.showText(legacy.deserialize(hoverText))
                    );
                }
            }

            comp = comp.append(lineComp);
            if (i < lines.size() - 1) comp = comp.append(Component.text("\n"));
        }

        return comp;
    }
}
