package sinon.nullcordxdiscordbridge.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import sinon.nullcordxdiscordbridge.NullCordXDiscordBridge;

@SuppressWarnings("deprecation")
public class Commands extends Command {

    public Commands(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    private NullCordXDiscordBridge main = NullCordXDiscordBridge.getInstance();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                main.reloadConfig();
                main.sendPingWebhook(true);
            }

            if (args[0].equalsIgnoreCase("pingwebhook") || args[0].equalsIgnoreCase("ping")) {
                main.sendPingWebhook(true);
            }
        } else {
            sender.sendMessage("/ncdb <reload> | <ping>");
        }
    }
}
