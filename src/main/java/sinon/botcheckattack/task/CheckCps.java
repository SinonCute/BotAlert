package sinon.botcheckattack.task;

import com.xism4.nullcordx.NullCordX;
import com.xism4.nullcordx.statistics.StatisticsManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import sinon.botcheckattack.BotCheckAttack;

import java.util.concurrent.TimeUnit;

public class CheckCps implements Runnable{

    private final BotCheckAttack main = BotCheckAttack.getInstance();
    private final ScheduledTask task;

    NullCordX nullCordX = BungeeCord.getInstance().getNullCordX();
    StatisticsManager statisticsManager = nullCordX.getStatisticsManager();
    Configuration config = main.getConfig();


    public CheckCps() {
        this.task = main.getProxy().getScheduler().schedule(main, this, config.getInt("check-cps.interval"), 1, TimeUnit.SECONDS);
    }
    private boolean isCanceled;

    @Override
    public void run() {
        if (nullCordX.isUnderAttack()){
            if (!nullCordX.isForceProtectionEnabled()) {
                int CurrentCPS = statisticsManager.getConnectionsPerSecond();
                int bots = nullCordX.getBotCounter();
                main.sendWebhook(config.getString("webhook.url", ""), CurrentCPS, bots);
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
