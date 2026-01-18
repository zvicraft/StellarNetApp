
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

import com.zvicraft.stellarNetApp.commands.Test;
import com.zvicraft.stellarNetApp.events.RandomAnnouncement;
import com.zvicraft.stellarNetApp.utils.LangUtiltis;
import com.zvicraft.stellarNetApp.utils.PlaceholderUtiliti;

import com.zvicraft.stellarNetApp.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class StellarNetApp extends JavaPlugin implements Listener {



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
        LangUtiltis langUtil = new LangUtiltis(this);
        langUtil.loadLanguageConfig();
        saveDefaultConfig();
        SoundUtils soundUtils = new SoundUtils(this);

        RandomAnnouncement ra = new RandomAnnouncement(this);
        ra.start();
    }
    private void registerCommands() {
        getCommand("test").setExecutor(new Test());
    }

}