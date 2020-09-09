package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.EnumTypes;
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
public class EditCommand extends CommandBase {

    private CitizensCMD plugin;

    public EditCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("edit")
    @Permission("citizenscmd.edit")
    @Completion({"#type", "#click"})
    public void edit(CommandSender sender, String typeString, String clickString, Integer id, String[] arguments) {

        if (npcNotSelected(plugin, sender)) return;

        int npc = getSelectedNpcId(sender);

        EnumTypes.ClickType click;
        EnumTypes.EditType type;

        switch (typeString.toLowerCase()) {
            case "cmd":
                type = EnumTypes.EditType.CMD;
                break;

            case "perm":
                if (arguments.length > 1) {
                    sender.sendMessage(color(HEADER));
                    sender.sendMessage(plugin.getLang().getMessage(Messages.INVALID_PERMISSION));
                    return;
                }

                type = EnumTypes.EditType.PERM;
                break;

            default:
                sender.sendMessage(color(HEADER));
                sender.sendMessage(plugin.getLang().getMessage(Messages.INVALID_ARGUMENTS));
                return;
        }

        switch (clickString.toLowerCase()) {
            case "left":
                int leftCommandSize = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT).size();

                if (leftCommandSize == 0) {
                    sender.sendMessage(color(HEADER));
                    sender.sendMessage(plugin.getLang().getMessage(Messages.NO_COMMANDS));
                    return;
                }

                if (id < 1 || id > leftCommandSize) {
                    sender.sendMessage(color(HEADER));
                    sender.sendMessage(plugin.getLang().getMessage(Messages.INVALID_ID_NUMBER));
                    return;
                }

                click = EnumTypes.ClickType.LEFT;
                break;

            case "right":
                int rightCommandSize = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT).size();

                if (rightCommandSize == 0) {
                    sender.sendMessage(color(HEADER));
                    sender.sendMessage(plugin.getLang().getMessage(Messages.NO_COMMANDS));
                    return;
                }

                if (id < 1 || id > rightCommandSize) {
                    sender.sendMessage(color(HEADER));
                    sender.sendMessage(plugin.getLang().getMessage(Messages.INVALID_ID_NUMBER));
                    return;
                }
                click = EnumTypes.ClickType.RIGHT;
                break;

            default:
                sender.sendMessage(color(HEADER));
                sender.sendMessage(plugin.getLang().getMessage(Messages.INVALID_CLICK_TYPE));
                return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        arguments[0] = arguments[0].replace("/", "");

        for (int i = 0; i < arguments.length; i++) {
            if (i == arguments.length - 1) stringBuilder.append(arguments[i]);
            else stringBuilder.append(arguments[i]).append(" ");
        }

        plugin.getDataHandler().edit(npc, id, click, type, stringBuilder.toString().trim(), sender);
    }

}
