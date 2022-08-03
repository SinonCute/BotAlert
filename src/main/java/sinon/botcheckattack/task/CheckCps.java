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
        this.task = main.getProxy().getScheduler().schedule(main, this,1, config.getInt("check-cps.interval", 30), TimeUnit.SECONDS);
    }
    private boolean isCanceled;

    @Override
    public void run() {
        int CurrentCPS = statisticsManager.getConnectionsPerSecond();
        if (CurrentCPS >= config.getInt("check-cps.mark-cps", 1000)){
            int bots = nullCordX.getBotCounter();
            main.sendWebhook(config.getString("webhook.url", ""), CurrentCPS, bots);
            main.setIsUnderAttack(true);
            cancel();
        } else {
            main.setIsUnderAttack(false);
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
