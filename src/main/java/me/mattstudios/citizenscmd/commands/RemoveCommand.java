package me.mattstudios.citizenscmd.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.EnumTypes;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

import java.util.OptionalInt;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.sendNotSelectedMessage;

public class RemoveCommand extends Npcmd {

    private final CitizensCMD plugin;

    public RemoveCommand(final CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("remove")
    @Permission("citizenscmd.remove")
    public void remove(final CommandSender sender, @Suggestion("click") final String clickString, final int id) {
        final OptionalInt selectedNpc = getSelectedNpcId(sender);

        final Audience audience = plugin.getAudiences().sender(sender);

        if (!selectedNpc.isPresent()) {
            sendNotSelectedMessage(plugin, audience);
            return;
        }

        EnumTypes.ClickType click;

        switch (clickString.toLowerCase()) {
            case "left":
                int leftCommandSize = plugin.getDataHandler().getClickCommandsData(selectedNpc.getAsInt(), EnumTypes.ClickType.LEFT).size();

                if (leftCommandSize == 0) {
                    audience.sendMessage(HEADER);
                    audience.sendMessage(plugin.getLang().getMessage(Messages.NO_COMMANDS));
                    return;
                }

                if (id < 1 || id > leftCommandSize) {
                    audience.sendMessage(HEADER);
                    audience.sendMessage(plugin.getLang().getMessage(Messages.INVALID_ID_NUMBER));
                    return;
                }

                click = EnumTypes.ClickType.LEFT;
                break;

            case "right":
                int rightCommandSize = plugin.getDataHandler().getClickCommandsData(selectedNpc.getAsInt(), EnumTypes.ClickType.RIGHT).size();

                if (rightCommandSize == 0) {
                    audience.sendMessage(HEADER);
                    audience.sendMessage(plugin.getLang().getMessage(Messages.NO_COMMANDS));
                    return;
                }

                if (id < 0 || id > rightCommandSize) {
                    audience.sendMessage(HEADER);
                    audience.sendMessage(plugin.getLang().getMessage(Messages.INVALID_ID_NUMBER));
                    return;
                }

                click = EnumTypes.ClickType.RIGHT;
                break;

            default:
                audience.sendMessage(HEADER);
                audience.sendMessage(plugin.getLang().getMessage(Messages.INVALID_CLICK_TYPE));
                return;
        }

        plugin.getDataHandler().removeCommand(selectedNpc.getAsInt(), id, click, audience);
    }
}
