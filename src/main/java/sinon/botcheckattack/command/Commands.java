package sinon.botcheckattack.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;
import sinon.botcheckattack.BotCheckAttack;
import sinon.botcheckattack.task.CheckCps;

public class Commands extends Command {


    public Commands(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    private final BotCheckAttack main = BotCheckAttack.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        Configuration config = main.getConfig();
        String prefix = config.getString("prefix");
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                main.reloadConfig();
            }
            if (args[0].equalsIgnoreCase("debug")) {
                main.sendWebhook(main.getConfig().getString("webhook.url"), 1, 1);
            }
            if (args[0].equalsIgnoreCase("reset")) {
                if (main.isUnderAttack && main.task.isCancel()) {
                    main.setIsUnderAttack(false);
                    main.task = new CheckCps();
                } else {
                    main.getLogger().info(prefix + " &cserver hiện không bị tấn công để reset");
                }
            }
        }
    }
}
