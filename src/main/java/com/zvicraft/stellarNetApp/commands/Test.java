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
import com.zvicraft.stellarNetApp.events.RandomAnnouncement;
import com.zvicraft.stellarNetApp.utils.LangUtiltis;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class Test implements CommandExecutor, TabCompleter {
    private final StellarNetApp plugin;

    public Test(StellarNetApp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("stellarnetapp.admin.test")) {
            sender.sendMessage(LangUtiltis.getLangString("no_permission"));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("chatgame")) {
            plugin.getChatGames().test();
            return true;
        }

        RandomAnnouncement ra = plugin.getRandomAnnouncement();
        if (ra != null) {
            ra.test();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("stellarnetapp.admin.test")) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("chatgame").stream()
                    .filter(option -> option.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        return List.of();
    }
}
