package sinon.botcheckattack;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import sinon.botcheckattack.command.Commands;
import sinon.botcheckattack.discord.DiscordWebhook;
import sinon.botcheckattack.task.CheckCps;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.TimeZone;


public final class BotCheckAttack extends Plugin {

    private static BotCheckAttack instance;

    private Configuration config;

    public static BotCheckAttack getInstance() {
        return instance;
    }

    private String prefix;

    public CheckCps task;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        getLogger().info(color(prefix + " &bPlugin đã khởi động"));
        getProxy().getPluginManager().registerCommand(this, new Commands("bot", "bot.admin"));
        task = new CheckCps();
    }

    @Override
    public void onDisable() {
        getLogger().info(color(prefix + " &bPlugin đã tắt"));
    }


    public Configuration getConfig() {
        return config;
    }

    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));
            prefix = config.getString("prefix");
            getLogger().info(color(prefix + " &aconfig đã tải thành công"));
        } catch (IOException e) {
            getLogger().info(color("BotAlert" + " &cconfig không tải thành công"));
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

    public void sendWebhook(String url, int cps, int bot) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        DiscordWebhook webhook = new DiscordWebhook(url);

        DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();

        embed.setColor(Color.yellow);
        embed.setTitle(config.getString("webhook.title", "⚠️ Bot Attack is detected"));
        embed.addField("Time", calendar.getTime().toString(), false);
        embed.addField("CPS", cps + "", false);
        embed.addField("Bot counter", bot + "", false);
        embed.setFooter("Made by Sinon with Love <3", "https://cdn.discordapp.com/avatars/305665547465523202/740d5016b90c97face7057f0ef58b4c8.png");
        embed.setThumbnail(config.getString("webhook.thumb", "https://cdn.discordapp.com/attachments/937775077758480485/1002809301473173634/unknown.png"));
        webhook.addEmbed(embed);

        try {
            webhook.execute();
        } catch ( IOException error) {
            getLogger().severe("url webhook is fuckup, pls check again or bi chit");
        }
    }

    public String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
