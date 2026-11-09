package me.pingplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PingPlugin extends JavaPlugin implements TabExecutor {

    private static final TextColor ACCENT = TextColor.fromHexString("#00a2ff");

    @Override
    public void onEnable() {
        if (getCommand("ping") != null) {
            getCommand("ping").setExecutor(this);
            getCommand("ping").setTabCompleter(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // /ping  -> your own ping
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("Usage: /ping <player>", NamedTextColor.RED));
                return true;
            }

            player.sendMessage(
                    Component.text("Your ping is ", NamedTextColor.WHITE)
                            .append(Component.text(player.getPing(), ACCENT))
                            .append(Component.text("ms", NamedTextColor.WHITE))
            );
            return true;
        }

        // /ping <player> -> someone else's ping
        if (!sender.hasPermission("pingplugin.ping.others")) {
            sender.sendMessage(Component.text("You don't have permission to check other players' ping.", NamedTextColor.RED));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found or not online.", NamedTextColor.RED));
            return true;
        }

        sender.sendMessage(
                Component.text(target.getName() + " has a ping of ", NamedTextColor.WHITE)
                        .append(Component.text(target.getPing(), ACCENT))
                        .append(Component.text("ms", NamedTextColor.WHITE))
        );
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("pingplugin.ping.others")) {
            List<String> names = new ArrayList<>();
            String prefix = args[0].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(prefix)) {
                    names.add(p.getName());
                }
            }
            return names;
        }
        return Collections.emptyList();
    }
}
