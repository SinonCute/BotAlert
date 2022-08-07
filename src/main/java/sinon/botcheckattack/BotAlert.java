package sinon.botcheckattack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import sinon.botcheckattack.command.Commands;
import sinon.botcheckattack.task.CPSChecker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public final class BotAlert extends Plugin {

    private static BotAlert instance;
    private Configuration config;

    public CPSChecker task;

    public static BotAlert getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        getProxy().getPluginManager().registerCommand(this, new Commands("botalert", "botalert.admin"));
        task = new CPSChecker();
    }

    public Configuration getConfig() {
        return config;
    }

    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));
            getLogger().log(Level.INFO, "Đã tải lại config!");
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Có lỗi xảy ra khi tải lại config!");
        }
    }

    public void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendWebhook(int cps, int blocked) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        DiscordWebhook webhook = new DiscordWebhook(config.getString("webhook.url"));

        webhook.setUsername(config.getString("webhook.username"));
        webhook.setAvatarUrl(config.getString("webhook.avatar"));
        webhook.setContent(config.getString("webhook.content")
                .replace("{cps}", String.valueOf(cps))
                .replace("{time}", dtf.format(now)));
        try {
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
