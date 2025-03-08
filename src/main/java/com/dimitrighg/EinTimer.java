package com.dimitrighg;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EinTimer extends JavaPlugin implements Listener, TabCompleter {

    private long startTime;
    private long pausedTime;
    private boolean isPaused = false;
    private boolean deathPauseEnabled = false;
    private BukkitRunnable timerTask;
    private Color cachedColor = new Color(255, 105, 180);
    private long lastColorUpdate = 0;
    private static final int COLOR_UPDATE_INTERVAL = 50;
    private static final int TIMER_UPDATE_INTERVAL = 20;

    private static final Color PINK = new Color(255, 105, 180);
    private static final Color PURPLE = new Color(147, 112, 219);

    private final List<String> commands = Arrays.asList("pause", "resume", "death", "reset", "enable");
    private final Map<String, Consumer<CommandSender>> commandHandlers = new HashMap<>();

    @Override
    public void onEnable() {
        startTime = System.currentTimeMillis();
        getServer().getPluginManager().registerEvents(this, this);

        var timerCommand = getCommand("timer");
        timerCommand.setExecutor(this);
        timerCommand.setTabCompleter(this);

        initCommandHandlers();
        startTimer();
        pauseTimer();
    }

    @Override
    public void onDisable() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private void initCommandHandlers() {
        commandHandlers.put("pause", sender -> pauseTimer());
        commandHandlers.put("resume", sender -> resumeTimer());
        commandHandlers.put("death", sender -> toggleDeathPause(sender));
        commandHandlers.put("reset", sender -> resetTimer());
        commandHandlers.put("enable", sender -> startTimer());
    }

    private void toggleDeathPause(CommandSender sender) {
        deathPauseEnabled = !deathPauseEnabled;
        sender.sendMessage("If dead, pause: " + (deathPauseEnabled ? "§aAn" : "§cAus")); //German: Wenn gestorben, pausieren:
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("timer") && args.length == 1) {
            return commands.stream()
                    .filter(c -> c.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return false;

        Consumer<CommandSender> handler = commandHandlers.get(args[0].toLowerCase());
        if (handler != null) {
            handler.accept(sender);
            return true;
        }
        return false;
    }

    private void startTimer() {
        if (timerTask != null) timerTask.cancel();

        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                updateActionBar();
            }
        };
        timerTask.runTaskTimer(this, 0, TIMER_UPDATE_INTERVAL);
    }

    private void updateActionBar() {
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = isPaused ? pausedTime : currentTime - startTime;
        String timeString = formatTime(elapsedMillis);
        String coloredMessage = isPaused
                ? "<bold><color:#ff0000>The Timer is paused...</color></bold>" //German: Der Timer ist pausiert...
                : createColoredMessage(timeString, currentTime);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize(coloredMessage));
        }
    }

    private void pauseTimer() {
        if (!isPaused) {
            pausedTime = System.currentTimeMillis() - startTime;
            isPaused = true;
        }
    }

    private void resumeTimer() {
        if (isPaused) {
            startTime = System.currentTimeMillis() - pausedTime;
            isPaused = false;
        }
    }

    private void resetTimer() {
        startTime = System.currentTimeMillis();
        pausedTime = 0;
        isPaused = true;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (deathPauseEnabled && !isPaused) {
            pauseTimer();
        }
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long days = TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder(16);

        if (days > 0) {
            sb.append(days).append("d ")
                    .append(hours).append("h ")
                    .append(minutes).append("m ")
                    .append(secs).append("s");
        } else if (hours > 0) {
            sb.append(hours).append("h ")
                    .append(minutes).append("m ")
                    .append(secs).append("s");
        } else if (minutes > 0) {
            sb.append(minutes).append("m ")
                    .append(secs).append("s");
        } else {
            sb.append(secs).append("s");
        }

        return sb.toString();
    }

    private String createColoredMessage(String text, long currentTime) {
        updateColorIfNeeded(currentTime);
        String hex = String.format("#%02x%02x%02x",
                cachedColor.getRed(),
                cachedColor.getGreen(),
                cachedColor.getBlue());

        return "<bold><color:" + hex + ">" + text + "</color></bold>";
    }

    private void updateColorIfNeeded(long currentTime) {
        if (currentTime - lastColorUpdate > COLOR_UPDATE_INTERVAL) {
            lastColorUpdate = currentTime;
            float progress = (float) (Math.sin(currentTime * 0.002) * 0.5 + 0.5);
            cachedColor = interpolateColor(PINK, PURPLE, progress);
        }
    }

    private Color interpolateColor(Color start, Color end, float progress) {
        int red = (int) (start.getRed() + (end.getRed() - start.getRed()) * progress);
        int green = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * progress);
        int blue = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * progress);
        return new Color(red, green, blue);
    }
}