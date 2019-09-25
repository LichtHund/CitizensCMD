package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.DisplayFormat;
import me.mattstudios.citizenscmd.utility.paths.Path;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;

import java.util.Objects;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.utils.MessageUtils.color;

@Command("npcmd")
public class ReloadCommand extends CommandBase {

    private CitizensCMD plugin;

    public ReloadCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("reload")
    @Permission("citizenscmd.reload")
    public void reload(CommandSender player) {

        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        plugin.setLang(Objects.requireNonNull(plugin.getConfig().getString("lang")));

        if (plugin.getConfig().contains("cooldown-time-display")) {
            switch (Objects.requireNonNull(plugin.getConfig().getString("cooldown-time-display")).toLowerCase()) {
                case "short":
                    plugin.setDisplayFormat(DisplayFormat.SHORT);
                    break;
                case "full":
                    plugin.setDisplayFormat(DisplayFormat.FULL);
                    break;
                default:
                    plugin.setDisplayFormat(DisplayFormat.MEDIUM);
                    break;
            }
        } else
            plugin.setDisplayFormat(DisplayFormat.MEDIUM);

        if (CitizensCMD.getEconomy() != null)
            plugin.setShift(plugin.getConfig().getBoolean("shift-confirm"));

        plugin.getDataHandler().reload();
        plugin.getCooldownHandler().reload();

        player.sendMessage(color(HEADER));
        player.sendMessage(plugin.getLang().getMessage(Path.RELOAD));
    }

}
