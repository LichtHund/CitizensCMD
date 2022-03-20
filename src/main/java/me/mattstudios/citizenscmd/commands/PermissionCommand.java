package me.mattstudios.citizenscmd.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

import java.util.OptionalInt;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.sendNotSelectedMessage;

public class PermissionCommand extends Npcmd {

    private final CitizensCMD plugin;

    public PermissionCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("permission")
    @Permission("citizenscmd.permission")
    public void permission(
            final CommandSender sender,
            @Suggestion("set") final String set,
            final String permission
    ) {
        final OptionalInt selectedNpc = getSelectedNpcId(sender);

        final Audience audience = plugin.getAudiences().sender(sender);

        if (!selectedNpc.isPresent()) {
            sendNotSelectedMessage(plugin, audience);
            return;
        }

        switch (set.toLowerCase()) {
            case "set":
                plugin.getDataHandler().setCustomPermission(selectedNpc.getAsInt(), permission, audience);
                break;

            case "remove":
                plugin.getDataHandler().removeCustomPermission(selectedNpc.getAsInt(), audience);
                break;

            default:
                audience.sendMessage(HEADER);
                audience.sendMessage(plugin.getLang().getMessage(Messages.WRONG_USAGE));
        }
    }

}
