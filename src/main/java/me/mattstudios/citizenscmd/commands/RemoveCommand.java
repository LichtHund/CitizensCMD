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
public class RemoveCommand extends CommandBase {

    private CitizensCMD plugin;

    public RemoveCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("remove")
    @Permission("citizenscmd.remove")
    @Completion("#click")
    public void remove(CommandSender sender, String clickString, Integer id) {

        if (npcNotSelected(plugin, sender)) return;

        int npc = getSelectedNpcId(sender);
        EnumTypes.ClickType click;

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

                if (id < 0 || id > rightCommandSize) {
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

        plugin.getDataHandler().removeCommand(npc, id, click, sender);

    }

}
