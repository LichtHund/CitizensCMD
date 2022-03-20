package me.mattstudios.citizenscmd.commands;

import ch.jalu.configme.SettingsManager;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.Settings;
import me.mattstudios.citizenscmd.utility.DisplayFormat;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;

public class ReloadCommand extends Npcmd {

    private final CitizensCMD plugin;
    private final SettingsManager settings;

    public ReloadCommand(final CitizensCMD plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    @SubCommand("reload")
    @Permission("citizenscmd.reload")
    public void reload(final CommandSender player) {

        final Audience audience = plugin.getAudiences().sender(player);

        settings.reload();
        plugin.setLang(settings.getProperty(Settings.LANG));

        final String timeFormat = settings.getProperty(Settings.TIME_DISPLAY);

        switch (timeFormat.toLowerCase()) {
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

        if (CitizensCMD.getEconomy() != null) {
            plugin.setShift(settings.getProperty(Settings.SHIT_CONFIRM));
        }

        plugin.getDataHandler().reload();
        plugin.getCooldownHandler().reload();

        audience.sendMessage(HEADER);
        audience.sendMessage(plugin.getLang().getMessage(Messages.RELOAD));
    }

}
