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
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
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
        if (args.length != 0) {
            sender.sendMessage(LangUtiltis.getLangString("admin_unknown_subcommand").replace("{command}", "/stellarnetapp reload config|lang"));
            return true;
        }
        if (args[1].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            LangUtiltis.reloadLang();
            sender.sendMessage(LangUtiltis.getLangString("admin_reload_success"));
        }


        if (args[2].equalsIgnoreCase("config")) {
            plugin.reloadConfig();
            sender.sendMessage(LangUtiltis.getLangString("admin_reload_success"));
        }
        if (args[2].equalsIgnoreCase("lang")) {
            LangUtiltis.reloadLang();
            sender.sendMessage(LangUtiltis.getLangString("admin_reload_success"));
        }
        return true;
    }
}
