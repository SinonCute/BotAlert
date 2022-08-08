package sinon.botcheckattack;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import sinon.botcheckattack.command.Commands;
import sinon.botcheckattack.task.CPSChecker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import com.sun.management.OperatingSystemMXBean;

import com.xism4.nullcordx.NullCordX;
import com.xism4.nullcordx.statistics.StatisticsManager;

@SuppressWarnings("deprecation")
public final class BotAlert extends Plugin {

    public CPSChecker cpsCheckerTask;
    private static BotAlert instance;
    private Configuration config;

    NullCordX nullCordX;
    StatisticsManager statisticsManager;

    DiscordWebhook webhook;

    public static BotAlert getInstance() {
        return instance;
    }

    public Configuration getConfig() {
        return config;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadConfig();
        getProxy().getPluginManager().registerCommand(this, new Commands("botalert", "botalert.admin"));

        this.nullCordX = BungeeCord.getInstance().getNullCordX();
        this.statisticsManager = nullCordX.getStatisticsManager();
        this.cpsCheckerTask = new CPSChecker(this, statisticsManager);
        this.webhook = new DiscordWebhook(config.getString("webhook.url"));
        this.webhook.setUsername(config.getString("webhook.username"));
        this.webhook.setAvatarUrl(config.getString("webhook.avatar"));
        sendInitWebhook();
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
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendWebhook(int cps) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        DecimalFormat formatter = new DecimalFormat("#0.00");

        // Resources stats
        // long maxMem = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
        long totalMem = Runtime.getRuntime().totalMemory() / 1024L / 1024L;
        long freeMem = Runtime.getRuntime().freeMemory() / 1024L / 1024L;
        long usedMem = totalMem - freeMem;

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(
                OperatingSystemMXBean.class);
        String processCPULoad = formatter.format(osBean.getProcessCpuLoad() * 100.0D);
        String systemCPULoad = formatter.format(osBean.getSystemCpuLoad() * 100.0D);

        webhook.setContent(config.getString("webhook.content")
                .replace("{time}", dtf.format(now))
                .replace("{status}", nullCordX.isUnderAttack() ? "true" : "false")
                .replace("{cps}", String.valueOf(cps))
                // .replace("{maxMem}", String.valueOf(maxMem))
                .replace("{totalMem}", String.valueOf(totalMem))
                .replace("{freeMem}", String.valueOf(freeMem))
                .replace("{usedMem}", String.valueOf(usedMem))
                .replace("{processCPULoad}", String.valueOf(processCPULoad))
                .replace("{systemCPULoad}", String.valueOf(systemCPULoad)));

        try {
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInitWebhook() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        webhook.setContent("[" + dtf.format(now) + "]: " + "Webhook connected!");
        try {
            webhook.execute();
            getLogger().log(Level.INFO, "Webhook connected!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
