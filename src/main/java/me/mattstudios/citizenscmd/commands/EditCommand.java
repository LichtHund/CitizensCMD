package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.EnumTypes;
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
public class EditCommand extends CommandBase {

    private CitizensCMD plugin;

    public EditCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("edit")
    @Permission("citizenscmd.edit")
    @Completion({"#type", "#click"})
    public void edit(Player player, String typeString, String clickString, int id, String[] arguments) {

        if (npcNotSelected(plugin, player)) return;

        int npc = getSelectedNpcId(player);

        EnumTypes.ClickType click;
        EnumTypes.EditType type;

        switch (typeString.toLowerCase()) {
            case "cmd":
                type = EnumTypes.EditType.CMD;
                break;
            case "perm":
                if (arguments.length > 1) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.INVALID_PERMISSION));
                    return;
                }
                type = EnumTypes.EditType.PERM;
                break;
            default:
                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.INVALID_ARGUMENTS));
                return;
        }

        switch (clickString.toLowerCase()) {
            case "left":
                int leftCommandSize = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT).size();

                if (leftCommandSize == 0) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.NO_COMMANDS));
                    return;
                }

                if (id < 1 || id > leftCommandSize) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.INVALID_ID_NUMBER));
                    return;
                }

                click = EnumTypes.ClickType.LEFT;
                break;

            case "right":
                int rightCommandSize = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT).size();

                if (rightCommandSize == 0) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.NO_COMMANDS));
                    return;
                }

                if (id < 1 || id > rightCommandSize) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.INVALID_ID_NUMBER));
                    return;
                }
                click = EnumTypes.ClickType.RIGHT;
                break;

            default:
                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.INVALID_CLICK_TYPE));
                return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        arguments[0] = arguments[0].replace("/", "");

        for (int i = 0; i < arguments.length; i++) {
            if (i == arguments.length - 1) stringBuilder.append(arguments[i]);
            else stringBuilder.append(arguments[i]).append(" ");
        }

        plugin.getDataHandler().edit(npc, id, click, type, stringBuilder.toString().trim(), player);
    }

}
