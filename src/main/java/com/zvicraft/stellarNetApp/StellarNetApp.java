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


package com.zvicraft.stellarNetApp;

import com.zvicraft.stellarNetApp.commands.ReloadCommand;
import com.zvicraft.stellarNetApp.commands.Test;
import com.zvicraft.stellarNetApp.events.ChatGames;
import com.zvicraft.stellarNetApp.events.RandomAnnouncement;
import com.zvicraft.stellarNetApp.utils.LangUtiltis;
import com.zvicraft.stellarNetApp.utils.PlaceholderUtiliti;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class StellarNetApp extends JavaPlugin {
    private RandomAnnouncement randomAnnouncement;
    private ChatGames chatGames;

    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) { //
            new PlaceholderUtiliti(this).register(); //
        }
        registerEvents();
        registerCommands();
        getLogger().info("StellarNetApp enabled!");

    }

    private void registerEvents() {
        saveDefaultConfig();
        new LangUtiltis(this);
        LangUtiltis.loadLanguageConfig();

        randomAnnouncement = new RandomAnnouncement(this);
        randomAnnouncement.start();

        chatGames = new ChatGames(this);
        Bukkit.getPluginManager().registerEvents(chatGames, this);
        chatGames.start();
    }

    private void registerCommands() {
        getCommand("test").setExecutor(new Test(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));
    }

    public RandomAnnouncement getRandomAnnouncement() {
        return randomAnnouncement;
    }

    public ChatGames getChatGames() {
        return chatGames;
    }
}
