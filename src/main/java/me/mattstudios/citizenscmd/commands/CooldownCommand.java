package me.mattstudios.citizenscmd.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import me.mattstudios.citizenscmd.CitizensCMD;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;

import java.util.OptionalInt;

import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.sendNotSelectedMessage;

public class CooldownCommand extends Npcmd {

    private final CitizensCMD plugin;

    public CooldownCommand(final CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("cooldown")
    @Permission("citizenscmd.cooldown")
    public void cooldown(final CommandSender sender, @Suggestion("range") final int cooldown) {
        final OptionalInt selectedNpc = getSelectedNpcId(sender);

        final Audience audience = plugin.getAudiences().sender(sender);

        if (!selectedNpc.isPresent()) {
            sendNotSelectedMessage(plugin, audience);
            return;
        }

        plugin.getDataHandler().setCooldown(selectedNpc.getAsInt(), cooldown, audience);
    }

}
