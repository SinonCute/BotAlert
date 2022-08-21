package sinon.nullcordxdiscordbridge.task;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.shieldcommunity.nullcordx.statistics.StatisticsManager;
import sinon.nullcordxdiscordbridge.NullCordXDiscordBridge;

import java.util.concurrent.TimeUnit;

public class CPSChecker implements Runnable {

    private NullCordXDiscordBridge main;
    private StatisticsManager statisticsManager;

    private ScheduledTask task;
    private boolean isCanceled;

    private Configuration config;

    public CPSChecker(NullCordXDiscordBridge main, StatisticsManager statisticsManager) {
        this.main = main;
        this.statisticsManager = statisticsManager;
        this.config = main.getConfig();
        this.task = main.getProxy().getScheduler().schedule(main, this, 1, config.getLong("checker.interval"),
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        int currentCPS = statisticsManager.getConnectionsPerSecond();
        main.sendLogWebhook(currentCPS);
    }

    public void cancel() {
        isCanceled = true;
        task.cancel();
    }

    public boolean isCancel() {
        return isCanceled;
    }

}