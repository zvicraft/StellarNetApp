/*
 * Copyright (c) 2026 Zviel Koren
 * All rights reserved.
 *
 * This source code and its content are the intellectual property of Zviel Koren.
 * Unauthorized copying, modification, or distribution of this software,
 * via any medium, is strictly prohibited without written permission from the author.
 *
 */

package com.zvicraft.stellarNetApp.commands;

import com.zvicraft.stellarNetApp.StellarNetApp;
import com.zvicraft.stellarNetApp.utils.LangUtiltis;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReloadCommand implements CommandExecutor, TabCompleter {
    private final StellarNetApp plugin;

    public ReloadCommand(StellarNetApp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission("stellarnetapp.admin.reload")) {
            sender.sendMessage(LangUtiltis.getLangString("no_permission"));
            return true;
        }

        if (args.length > 1) {
            sender.sendMessage(LangUtiltis.getLangString("admin_unknown_subcommand", "{subcommand}", "/reload [config|lang|all]"));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("all")) {
            plugin.reloadConfig();
            LangUtiltis.reloadLang();
            plugin.getRandomAnnouncement().start();
            plugin.getChatGames().start();
            sender.sendMessage(LangUtiltis.getLangString("admin_reload_success"));
            return true;
        }

        if (args[0].equalsIgnoreCase("config")) {
            plugin.reloadConfig();
            plugin.getRandomAnnouncement().start();
            plugin.getChatGames().start();
            sender.sendMessage(LangUtiltis.getLangString("admin_reload_success"));
            return true;
        }

        if (args[0].equalsIgnoreCase("lang")) {
            LangUtiltis.reloadLang();
            sender.sendMessage(LangUtiltis.getLangString("admin_reload_success"));
            return true;
        }

        sender.sendMessage(LangUtiltis.getLangString("admin_unknown_subcommand", "{subcommand}", args[0]));
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("stellarnetapp.admin.reload")) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("all", "config", "lang").stream()
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}
