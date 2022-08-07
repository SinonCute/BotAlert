package sinon.botcheckattack.task;

import com.xism4.nullcordx.NullCordX;
import com.xism4.nullcordx.statistics.StatisticsManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import sinon.botcheckattack.BotAlert;

import java.util.concurrent.TimeUnit;

public class CPSChecker implements Runnable {

    private BotAlert main = BotAlert.getInstance();
    private final ScheduledTask task;
    private boolean isCanceled;

    NullCordX nullcordx = BungeeCord.getInstance().getNullCordX();
    StatisticsManager statisticsManager = nullcordx.getStatisticsManager();
    Configuration config = main.getConfig();

    public CPSChecker() {
        this.task = main.getProxy().getScheduler().schedule(main, this, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void run() {

        if (nullcordx.isUnderAttack()) {
            if (!nullcordx.isForceProtectionEnabled()) {
                int currentCPS = statisticsManager.getConnectionsPerSecond();
                int blockedCPS = statisticsManager.getBlockedConnectionsPerSecond();
                main.sendWebhook(currentCPS, blockedCPS);
            }
        }

    }

    public void cancel() {
        isCanceled = true;
        task.cancel();
    }

    public boolean isCancel() {
        return isCanceled;
    }

}
