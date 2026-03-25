/*
 * Copyright (c) 2026 Zviel Koren
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
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ChatGames implements Listener {
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private final StellarNetApp plugin;
    private final Random random = new Random();

    private BukkitTask startTask;
    private BukkitTask timeoutTask;

    private boolean active;
    private String answer;
    private String winner;
    private String winnerMessage;

    public ChatGames(StellarNetApp plugin) {
        this.plugin = plugin;
    }

    public void start() {
        stop();
        if (!plugin.getConfig().getBoolean("chatgames.enabled", true)) {
            return;
        }
        scheduleNextGame();
    }

    public void stop() {
        if (startTask != null) {
            startTask.cancel();
            startTask = null;
        }
        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }
        clearGame();
    }

    public void test() {
        Bukkit.getScheduler().runTask(plugin, this::startGame);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        String currentAnswer;
        synchronized (this) {
            if (!active || answer == null) {
                return;
            }
            currentAnswer = answer;
        }

        String message = normalizeText(PLAIN.serialize(event.message()));
        if (!message.equals(currentAnswer)) {
            return;
        }

        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> handleWinner(player.getName(), message));
    }

    private void scheduleNextGame() {
        int intervalMinutes = Math.max(1, plugin.getConfig().getInt("chatgames.interval_minutes", 10));
        long delayTicks = intervalMinutes * 1200L;
        startTask = Bukkit.getScheduler().runTaskLater(plugin, this::startGame, delayTicks);
    }

    private void startGame() {
        if (active || Bukkit.getOnlinePlayers().isEmpty()) {
            scheduleNextGame();
            return;
        }

        int gameType = random.nextInt(2) + 1;
        if (gameType == 1) {
            startScrambleGame();
        } else {
            startMathGame();
        }

        int timeoutSeconds = Math.max(5, plugin.getConfig().getInt("chatgames.time_limit_seconds", 120));
        timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, this::handleTimeout, timeoutSeconds * 20L);
    }

    private void startScrambleGame() {
        List<String> words = plugin.getConfig().getStringList("chatgames.scramble_words");
        if (words.isEmpty()) {
            words = List.of("minecraft", "survival", "diamond", "nether", "warden");
        }

        String word = words.get(random.nextInt(words.size()));
        String scramble = scrambleString(word);

        synchronized (this) {
            active = true;
            answer = normalizeText(word);
            winner = null;
            winnerMessage = null;
        }

        chatgameBroadcast(scramble, 1);
    }

    private void startMathGame() {
        int max = Math.max(1, plugin.getConfig().getInt("chatgames.max_number", 100));
        String math = mathQuestion(random.nextInt(3) + 1, random.nextInt(max + 1), random.nextInt(max + 1));
        String[] split = math.split(" \\| ", 2);

        synchronized (this) {
            active = true;
            answer = normalizeText(split[1]);
            winner = null;
            winnerMessage = null;
        }

        chatgameBroadcast(split[0], 2);
    }

    private void handleWinner(String winnerName, String message) {
        synchronized (this) {
            if (!active || answer == null) {
                return;
            }
            winner = winnerName;
            winnerMessage = message;
            active = false;
            answer = null;
        }

        if (timeoutTask != null) {
            timeoutTask.cancel();
            timeoutTask = null;
        }

        broadcastBlock(LangUtiltis.getLangStringList(
                "chatgames.winner.messages",
                "{winner}", winnerName,
                "{message}", message
        ));

        synchronized (this) {
            winner = null;
            winnerMessage = null;
        }

        scheduleNextGame();
    }

    private void handleTimeout() {
        String missedAnswer;
        synchronized (this) {
            if (!active || answer == null) {
                return;
            }
            missedAnswer = answer;
            active = false;
            answer = null;
            winner = null;
            winnerMessage = null;
        }

        timeoutTask = null;

        broadcastBlock(LangUtiltis.getLangStringList(
                "chatgames.timeout.messages",
                "{answer}", missedAnswer
        ));

        scheduleNextGame();
    }

    private void clearGame() {
        synchronized (this) {
            active = false;
            answer = null;
            winner = null;
            winnerMessage = null;
        }
    }

    private void chatgameBroadcast(String message, int option) {
        String rewardText = plugin.getConfig().getString("chatgames.reward_text", "something cool");
        if (option == 2) {
            broadcastBlock(LangUtiltis.getLangStringList(
                    "chatgames.math.messages",
                    "{message}", message,
                    "{reward}", rewardText
            ));
            return;
        }

        broadcastBlock(LangUtiltis.getLangStringList(
                "chatgames.scramble.messages",
                "{message}", message,
                "{reward}", rewardText
        ));
    }

    private void broadcastBlock(List<String> lines) {
        for (String line : lines) {
            Component component = LEGACY.deserialize(line);
            Bukkit.broadcast(component);
        }
    }

    private String scrambleString(String input) {
        List<String> chars = new ArrayList<>();
        for (char c : input.toCharArray()) {
            chars.add(String.valueOf(c));
        }

        for (int i = 0; i < 25; i++) {
            List<String> shuffled = new ArrayList<>(chars);
            Collections.shuffle(shuffled, random);
            String result = String.join("", shuffled);
            if (!result.equalsIgnoreCase(input)) {
                return result;
            }
        }

        return input + "?";
    }

    private String mathQuestion(int operation, int n1, int n2) {
        if (operation == 1) {
            return addSubMath(random.nextInt(2) + 1, n1, n2);
        }
        if (operation == 2) {
            return divMultMath(1, n1, n2);
        }
        return exponentRootMath(1, n1);
    }

    private String addSubMath(int operation, int n1, int n2) {
        if (operation == 1) {
            int num = n1 + n2;
            return n1 + " + " + n2 + " | " + num;
        }

        int num = n1 - n2;
        return n1 + " - " + n2 + " | " + num;
    }

    private String divMultMath(int operation, int n1, int n2) {
        if (operation == 1) {
            int num = n1 * n2;
            return n1 + " * " + n2 + " | " + num;
        }

        if (n2 == 0) {
            n2 = 1;
        }

        BigDecimal result = BigDecimal.valueOf((double) n1 / n2).setScale(2, RoundingMode.HALF_UP);
        return n1 + " / " + n2 + " | " + trimDecimal(result);
    }

    private String exponentRootMath(int operation, int n1) {
        if (operation == 1) {
            int num = n1 * n1;
            return n1 + "^2 | " + num;
        }

        BigDecimal result = BigDecimal.valueOf(Math.sqrt(n1)).setScale(2, RoundingMode.HALF_UP);
        return "sqrt(" + n1 + ") | " + trimDecimal(result);
    }

    private String trimDecimal(BigDecimal value) {
        return value.stripTrailingZeros().toPlainString();
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
    }
}
