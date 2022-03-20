package me.mattstudios.citizenscmd.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.CommandFlags;
import dev.triumphteam.cmd.core.annotation.Flag;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import dev.triumphteam.cmd.core.flag.Flags;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

import java.util.Optional;
import java.util.OptionalInt;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.sendNotSelectedMessage;

public class AddCommand extends Npcmd {

    private final CitizensCMD plugin;

    public AddCommand(final CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("add")
    @Permission("citizenscmd.add")
    @CommandFlags({
            @Flag(flag = "n"),
            @Flag(flag = "l"),
            @Flag(flag = "d", argument = double.class)
    })
    public void addCommand(
            final CommandSender sender,
            @Suggestion("permissions") final String permission,
            final Flags flags
    ) {
        final OptionalInt selectedNpc = getSelectedNpcId(sender);

        final Audience audience = plugin.getAudiences().sender(sender);

        if (!selectedNpc.isPresent()) {
            sendNotSelectedMessage(plugin, audience);
            return;
        }

        final StringBuilder permissionBuilder = new StringBuilder(permission);
        if (flags.hasFlag("d")) {
            final Optional<Double> delay = flags.getValue("d", Double.TYPE);
            if (!delay.isPresent()) {
                audience.sendMessage(HEADER);
                audience.sendMessage(plugin.getLang().getMessage(Messages.NPC_ADD_DELAY_FAIL));
                return;
            }
            permissionBuilder.append("(").append(delay.get()).append(")");
        }

        final String command = flags.getText();

        if (command.isEmpty()) {
            audience.sendMessage(HEADER);
            audience.sendMessage(plugin.getLang().getMessage(Messages.WRONG_USAGE));
            return;
        }

        final String finalString = (flags.hasFlag("n") ? "{display} " + command : command).trim();

        plugin
                .getDataHandler()
                .addCommand(selectedNpc.getAsInt(), permissionBuilder.toString(), finalString, audience, flags.hasFlag("l"));
    }

}
