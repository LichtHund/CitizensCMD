package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;

import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;

@Command("npcmd")
public class CooldownCommand extends CommandBase {

    private CitizensCMD plugin;

    public CooldownCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("cooldown")
    @Permission("citizenscmd.cooldown")
    public void cooldown(CommandSender sender, @Completion("#range:9") Integer cooldown) {
        if (npcNotSelected(plugin, sender)) return;

        plugin.getDataHandler().setCooldown(getSelectedNpcId(sender), cooldown, sender);
    }

}
