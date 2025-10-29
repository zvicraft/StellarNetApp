/*
 * Copyright (c) 2025 Zviel Koren
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
import org.bukkit.event.Listener;


/**
 * R
 */
public class RandomAnnouncement implements Listener {

    private StellarNetApp plugin;
    private static LangUtiltis lang;

    private RandomAnnouncement(StellarNetApp plugin) {
        this.plugin = plugin;
    }

    // Initialize Class
    public static void Initialize() {
    // TODO: add Initialize logic
    }
}
