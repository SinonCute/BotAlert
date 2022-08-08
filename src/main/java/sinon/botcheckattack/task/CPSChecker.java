package sinon.botcheckattack.task;

import com.xism4.nullcordx.statistics.StatisticsManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import sinon.botcheckattack.BotAlert;

import java.util.concurrent.TimeUnit;

public class CPSChecker implements Runnable {

    private BotAlert main;
    private StatisticsManager statisticsManager;

    private ScheduledTask task;
    private boolean isCanceled;

    private Configuration config;

    public CPSChecker(BotAlert main, StatisticsManager statisticsManager) {
        this.main = main;
        this.statisticsManager = statisticsManager;
        this.config = main.getConfig();
        this.task = main.getProxy().getScheduler().schedule(main, this, 1, config.getLong("checker.interval"),
                TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        /*
         * if (nullcordx.isUnderAttack()) {
         * if (!nullcordx.isForceProtectionEnabled()) {
         * int currentCPS = statisticsManager.getConnectionsPerSecond();
         * // int blockedCPS = statisticsManager.getBlockedConnectionsPerSecond();
         * main.sendWebhook(currentCPS);
         * }
         * }
         */

        int currentCPS = statisticsManager.getConnectionsPerSecond();
        main.sendWebhook(currentCPS);
    }

    public void cancel() {
        isCanceled = true;
        task.cancel();
    }

    public boolean isCancel() {
        return isCanceled;
    }

}