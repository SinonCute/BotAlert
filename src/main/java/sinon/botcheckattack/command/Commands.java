package sinon.botcheckattack.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import sinon.botcheckattack.BotAlert;

@SuppressWarnings("deprecation")
public class Commands extends Command {

    public Commands(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    private BotAlert main = BotAlert.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                main.reloadConfig();
            }

            if (args[0].equalsIgnoreCase("testwebhook")) {
                main.sendInitWebhook();
            }
        } else {
            sender.sendMessage("/botalert <reload> | <testwebhook>");
        }
    }
}
