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

package com.zvicraft.stellarNetApp.commands;

import com.zvicraft.stellarNetApp.StellarNetApp;
import com.zvicraft.stellarNetApp.events.RandomAnnouncement;
import com.zvicraft.stellarNetApp.utils.LangUtiltis;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Test implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String label,  String  [] args) {
        if (args.length < 0) {
            sender.sendMessage(LangUtiltis.getLangString("invalid_command_usage"));
            return true;
        }
        String commandName = args[0].toLowerCase();
        if (commandName.equals("test")) {
            if(!sender.hasPermission("stellarnetapp.admin.test"))
                sender.sendMessage(LangUtiltis.getLangString("no_permission"));
            RandomAnnouncement.test();
        }
        return false;
    }
}
