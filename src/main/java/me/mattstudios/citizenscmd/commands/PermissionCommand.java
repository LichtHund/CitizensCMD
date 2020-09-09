package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.Messages;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;
import static me.mattstudios.utils.MessageUtils.color;

@Command("npcmd")
public class PermissionCommand extends CommandBase {

    private CitizensCMD plugin;

    public PermissionCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("permission")
    @Permission("citizenscmd.permission")
    public void permission(CommandSender sender, @Completion("#set") String set, String permission) {

        if (npcNotSelected(plugin, sender)) return;

        switch (set.toLowerCase()) {
            case "set":
                plugin.getDataHandler().setCustomPermission(getSelectedNpcId(sender), permission, sender);
                break;

            case "remove":
                plugin.getDataHandler().removeCustomPermission(getSelectedNpcId(sender), sender);
                break;

            default:
                sender.sendMessage(color(HEADER));
                sender.sendMessage(plugin.getLang().getMessage(Messages.WRONG_USAGE));
        }
    }

}
