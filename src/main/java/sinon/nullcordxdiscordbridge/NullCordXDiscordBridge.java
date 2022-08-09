package sinon.nullcordxdiscordbridge;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import sinon.nullcordxdiscordbridge.command.Commands;
import sinon.nullcordxdiscordbridge.task.CPSChecker;

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
public final class NullCordXDiscordBridge extends Plugin {

    public CPSChecker cpsCheckerTask;
    private static NullCordXDiscordBridge instance;
    private Configuration config;

    NullCordX nullCordX;
    StatisticsManager statisticsManager;

    DiscordWebhook webhook;

    public static NullCordXDiscordBridge getInstance() {
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
        getProxy().getPluginManager().registerCommand(this, new Commands("ncdb", "ncdb.admin", "botalert"));

        this.nullCordX = BungeeCord.getInstance().getNullCordX();
        this.statisticsManager = nullCordX.getStatisticsManager();
        this.cpsCheckerTask = new CPSChecker(this, statisticsManager);
        this.webhook = new DiscordWebhook(config.getString("webhook.url"));
        this.webhook.setUsername(config.getString("webhook.username"));
        this.webhook.setAvatarUrl(config.getString("webhook.avatar"));
        sendPingWebhook(true);
    }

    @Override
    public void onDisable() {
        if (!this.cpsCheckerTask.isCancel()) {
            this.cpsCheckerTask.cancel();
        }
        sendPingWebhook(false);
    }

    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), "config.yml"));
            getLogger().log(Level.INFO, "Đã load config!");
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

    public void sendLogWebhook(int cps) {
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

    public void sendPingWebhook(boolean isProxyEnabled) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        if (isProxyEnabled) {
            webhook.setContent("[" + dtf.format(now) + "]: " + "Proxy instance or plugin enabled, webhook connected!");
            getLogger().log(Level.INFO, "Webhook connected!");
        } else {
            webhook.setContent("[" + dtf.format(now) + "]: " + "Proxy instance or plugin disabled, webhook disconnected!");
        }
        try {
            webhook.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
