package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.paths.Path;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;

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
    public void permission(Player player, @Completion("#set") String set, String permission) {

        if (npcNotSelected(plugin, player)) return;

        switch (set.toLowerCase()) {
            case "set":
                plugin.getDataHandler().setCustomPermission(getSelectedNpcId(player), permission, player);
                break;

            case "remove":
                plugin.getDataHandler().removeCustomPermission(getSelectedNpcId(player), player);
                break;

            default:
                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.WRONG_USAGE));
        }
    }

}
