package me.mattstudios.citizenscmd.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.EnumTypes;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.LEGACY;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.sendNotSelectedMessage;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;

public class ListCommand extends Npcmd {

    private final CitizensCMD plugin;

    public ListCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("list")
    @Permission("citizenscmd.list")
    public void list(Player player) {
        final Audience audience = plugin.getAudiences().player(player);

        final OptionalInt selectedNpc = getSelectedNpcId(player);

        if (!selectedNpc.isPresent()) {
            sendNotSelectedMessage(plugin, audience);
            return;
        }

        final TextComponent.Builder builder = Component.text();

        final int npc = selectedNpc.getAsInt();
        List<String> leftCommands = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT) != null ? plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT) : new ArrayList<>();
        List<String> rightCommands = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT) != null ? plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT) : new ArrayList<>();

        builder.append(HEADER).append(newline());
        builder.append(
                Component.text()
                        .append(plugin.getLang().getMessage(Messages.LIST_COOLDOWN))
                        .append(text(plugin.getDataHandler().getNPCCooldown(npc)))
                        .hoverEvent(showText(plugin.getLang().getMessage(Messages.LIST_TOOLTIP)))
                        .clickEvent(suggestCommand("/npcmd cooldown "))
                        .build()
        );
        builder.append(newline());
        builder.append(
                Component.text()
                        .append(plugin.getLang().getMessage(Messages.LIST_PRICE))
                        .append(text(plugin.getDataHandler().getPrice(npc)))
                        .hoverEvent(showText(plugin.getLang().getMessage(Messages.LIST_TOOLTIP)))
                        .clickEvent(suggestCommand("/npcmd price "))
                        .build()
        );
        builder.append(newline());
        builder.append(newline());
        builder.append(plugin.getLang().getMessage(Messages.LIST_COUNT_RIGHT, "{count}", String.valueOf(rightCommands.size())));
        builder.append(newline());

        int rightCount = 1;
        for (String command : rightCommands) {
            builder.append(
                    Component.text()
                            .append(LEGACY.deserialize("&c" + rightCount + " &7- &7" + command.replace("[", "&8[&c").replace("]", "&8]&b")))
                            .clickEvent(suggestCommand("/npcmd edit cmd right " + rightCount + " "))
                            .hoverEvent(showText(plugin.getLang().getMessage(Messages.LIST_TOOLTIP)))
                            .build()
            );
            builder.append(newline());
            rightCount++;
        }

        builder.append(plugin.getLang().getMessage(Messages.LIST_COUNT_LEFT, "{count}", String.valueOf(leftCommands.size())));

        int leftCount = 1;
        for (String command : leftCommands) {
            builder.append(
                    Component.text()
                            .append(LEGACY.deserialize("&c" + leftCount + " &7- &7" + command.replace("[", "&8[&c").replace("]", "&8]&b")))
                            .clickEvent(suggestCommand("/npcmd edit cmd left " + leftCount + " "))
                            .hoverEvent(showText(plugin.getLang().getMessage(Messages.LIST_TOOLTIP)))
                            .build()
            );
            builder.append(newline());
            leftCount++;
        }

        audience.sendMessage(builder.build());
    }
}
