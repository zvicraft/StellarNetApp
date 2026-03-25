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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LangUtiltis {
    public static StellarNetApp plugin;
    private static FileConfiguration langConfig;

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final Pattern LINK_TOKEN = Pattern.compile("\\{LINK(?::([a-zA-Z0-9_-]+))?}");

    public LangUtiltis(StellarNetApp plugin) {
        LangUtiltis.plugin = plugin;
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

    public static List<String> getLangStringList(String key, String... placeholders) {
        if (langConfig == null) {
            plugin.getLogger().warning("Language configuration is not loaded. Key: " + key);
            return List.of("Configuration error: " + key);
        }

        List<String> lines = langConfig.getStringList(key);
        if (lines.isEmpty()) {
            plugin.getLogger().warning("Missing language key or empty list: " + key);
            return List.of("Missing key: " + key);
        }

        List<String> result = new ArrayList<>();
        for (String line : lines) {
            String parsedLine = line;
            if (placeholders != null && placeholders.length > 0) {
                for (int i = 0; i < placeholders.length - 1; i += 2) {
                    parsedLine = parsedLine.replace(placeholders[i], placeholders[i + 1]);
                }
            }
            result.add(parsedLine);
        }

        return result;
    }

    public static boolean hasLangPath(String key) {
        return langConfig != null && langConfig.contains(key);
    }

    public static Set<String> getSectionKeys(String key) {
        if (langConfig == null) {
            plugin.getLogger().warning("Language configuration is not loaded. Key: " + key);
            return Collections.emptySet();
        }

        var section = langConfig.getConfigurationSection(key);
        if (section == null) {
            plugin.getLogger().warning("Missing language section: " + key);
            return Collections.emptySet();
        }

        return section.getKeys(false);
    }

    public static Component getLangComponent(String key, Map<String, String> linkUrls, String hoverText) {
        if (langConfig == null) {
            plugin.getLogger().warning("Language configuration is not loaded. Key: " + key);
            return Component.text("Configuration error: " + key);
        }

        List<String> lines = langConfig.getStringList(key);
        if (lines.isEmpty()) {
            plugin.getLogger().warning("Missing language key or empty list: " + key);
            return Component.text("Missing key: " + key);
        }

        return buildMultilineComponent(lines, linkUrls, hoverText);
    }

    private static Component buildMultilineComponent(List<String> lines, Map<String, String> linkUrls, String hoverText) {
        Component result = Component.empty();

        for (int i = 0; i < lines.size(); i++) {
            result = result.append(buildLineComponent(lines.get(i), linkUrls, hoverText));

            if (i < lines.size() - 1) {
                result = result.append(Component.text("\n"));
            }
        }

        return result;
    }

    private static Component buildLineComponent(String rawLine, Map<String, String> linkUrls, String hoverText) {
        String safeLine = rawLine == null ? "" : rawLine;

        LinkParseResult parseResult = parseLinkToken(safeLine);
        Component lineComponent = LEGACY.deserialize(parseResult.textWithoutToken());

        if (!parseResult.clickable()) {
            return lineComponent;
        }

        String url = resolveUrl(linkUrls, parseResult.linkName());
        if (url == null || url.isBlank()) {
            return lineComponent;
        }

        return applyInteraction(lineComponent, url, hoverText);
    }

    private static LinkParseResult parseLinkToken(String line) {
        Matcher matcher = LINK_TOKEN.matcher(line);

        if (!matcher.find()) {
            return new LinkParseResult(line, false, "default");
        }

        String linkName = matcher.group(1);
        if (linkName == null || linkName.isBlank()) {
            linkName = "default";
        }

        String textWithoutToken = matcher.replaceAll("");
        return new LinkParseResult(textWithoutToken, true, linkName);
    }

    private static String resolveUrl(Map<String, String> linkUrls, String linkName) {
        if (linkUrls == null) {
            return null;
        }
        return linkUrls.get(linkName);
    }

    private static Component applyInteraction(Component component, String url, String hoverText) {
        Component result = component.clickEvent(ClickEvent.openUrl(url));

        if (hoverText != null && !hoverText.isBlank()) {
            result = result.hoverEvent(HoverEvent.showText(LEGACY.deserialize(hoverText)));
        }

        return result;
    }

    private record LinkParseResult(String textWithoutToken, boolean clickable, String linkName) {
    }
}
