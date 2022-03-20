package me.mattstudios.citizenscmd.commands;

import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotation.Default;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.LEGACY;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;

public class HelpCommand extends Npcmd {

    private final CitizensCMD plugin;
    private final BukkitAudiences audiences;

    public HelpCommand(final CitizensCMD plugin) {
        this.plugin = plugin;
        this.audiences = plugin.getAudiences();
    }

    @Default
    @Permission("citizenscmd.npcmd")
    public void help(Player player) {
        final Audience audience = audiences.player(player);

        final TextComponent.Builder builder = Component.text();

        builder
                .append(HEADER)
                .append(newline())
                .append(plugin.getLang().getMessage(Messages.HELP_VERSION))
                .append(space())
                .append(text(plugin.getDescription().getVersion()).style(Style.style(NamedTextColor.RED, TextDecoration.ITALIC)))
                .append(newline())
                .append(plugin.getLang().getMessage(Messages.HELP_INFO))
                .append(newline())
                .append(
                        Component.text()
                                .append(LEGACY.deserialize("&3/npcmd &cadd &b<console &b| &bmessage &b| &bplayer | &bpermission &b| &bserver &b| &bsound &b> &6<command> &d[-l]"))
                                .clickEvent(ClickEvent.suggestCommand("/npcmd add "))
                                .hoverEvent(
                                        HoverEvent.showText(
                                                Component.text()
                                                        .append(plugin.getLang().getMessage(Messages.HELP_DESCRIPTION_ADD))
                                                        .append(newline())
                                                        .append(plugin.getLang().getMessage(Messages.HELP_EXAMPLE))
                                                        .append(newline())
                                                        .append(LEGACY.deserialize("&3&o/npcmd &c&oadd &b&ossentials.heal &6&oheal"))
                                                        .build()
                                        )
                                )
                )
                .append(newline())
                .append(
                        Component.text()
                                .append(LEGACY.deserialize("&3/npcmd &ccooldown &6<time>"))
                                .clickEvent(ClickEvent.suggestCommand("/npcmd cooldown "))
                                .hoverEvent(
                                        HoverEvent.showText(
                                                Component.text()
                                                        .append(plugin.getLang().getMessage(Messages.HELP_DESCRIPTION_COOLDOWN))
                                                        .append(newline())
                                                        .append(plugin.getLang().getMessage(Messages.HELP_EXAMPLE))
                                                        .append(newline())
                                                        .append(LEGACY.deserialize("&3&o/npcmd &c&ocooldown &6&o15"))
                                                        .build()
                                        )
                                ).build()
                )
                .append(newline())
                .append(
                        Component.text()
                                .append(LEGACY.deserialize("&3/npcmd &cprice &6<price>"))
                                .clickEvent(ClickEvent.suggestCommand("/npcmd price "))
                                .hoverEvent(
                                        HoverEvent.showText(
                                                Component.text()
                                                        .append(plugin.getLang().getMessage(Messages.HELP_DESCRIPTION_PRICE))
                                                        .append(newline())
                                                        .append(plugin.getLang().getMessage(Messages.HELP_EXAMPLE))
                                                        .append(newline())
                                                        .append(LEGACY.deserialize("&3&o/npcmd &c&oprice &6&o250"))
                                                        .build()
                                        )
                                ).build()
                )
                .append(newline())
                .append(
                        Component.text()
                                .append(LEGACY.deserialize("&3/npcmd &clist"))
                                .clickEvent(ClickEvent.suggestCommand("/npcmd list"))
                                .hoverEvent(
                                        HoverEvent.showText(
                                                Component.text()
                                                        .append(plugin.getLang().getMessage(Messages.HELP_DESCRIPTION_LIST))
                                                        .append(newline())
                                                        .append(plugin.getLang().getMessage(Messages.HELP_EXAMPLE))
                                                        .append(newline())
                                                        .append(LEGACY.deserialize("&3&o/npcmd &c&olist"))
                                                        .build()
                                        )
                                ).build()
                )
                .append(newline())
                .append(
                        Component.text()
                                .append(LEGACY.deserialize("&3/npcmd &cedit &b<cmd | perm> &b<left | right> &6<id> &6<new command | new permission>"))
                                .clickEvent(ClickEvent.suggestCommand("/npcmd edit "))
                                .hoverEvent(
                                        HoverEvent.showText(
                                                Component.text()
                                                        .append(plugin.getLang().getMessage(Messages.HELP_DESCRIPTION_EDIT))
                                                        .append(newline())
                                                        .append(plugin.getLang().getMessage(Messages.HELP_EXAMPLE))
                                                        .append(newline())
                                                        .append(LEGACY.deserialize("&3&o/npcmd &c&oedit &b&ocmd &b&oright &6&o1 fly"))
                                                        .build()
                                        )
                                ).build()
                )
                .append(newline())
                .append(
                        Component.text()
                                .append(LEGACY.deserialize("&3/npcmd &cremove &b<left | right> &6<id>"))
                                .clickEvent(ClickEvent.suggestCommand("/npcmd remove "))
                                .hoverEvent(
                                        HoverEvent.showText(
                                                Component.text()
                                                        .append(plugin.getLang().getMessage(Messages.HELP_DESCRIPTION_REMOVE))
                                                        .append(newline())
                                                        .append(plugin.getLang().getMessage(Messages.HELP_EXAMPLE))
                                                        .append(newline())
                                                        .append(LEGACY.deserialize("&3&o/npcmd &c&oremove &b&oright &6&o1"))
                                                        .build()
                                        )
                                ).build()
                )
                .append(newline())
                .append(
                        Component.text()
                                .append(LEGACY.deserialize("&3/npcmd &cpermission &b<set | remove> &6<custom.permission>"))
                                .clickEvent(ClickEvent.suggestCommand("/npcmd permission "))
                                .build()
                )
                .append(newline())
                .append(
                        Component.text()
                                .append(LEGACY.deserialize("&3/npcmd &creload"))
                                .clickEvent(ClickEvent.suggestCommand("/npcmd reload"))
                                .hoverEvent(
                                        HoverEvent.showText(
                                                Component.text()
                                                        .append(plugin.getLang().getMessage(Messages.HELP_DESCRIPTION_RELOAD))
                                                        .append(newline())
                                                        .append(plugin.getLang().getMessage(Messages.HELP_EXAMPLE))
                                                        .append(newline())
                                                        .append(LEGACY.deserialize("&3&o/npcmd &c&oreload"))
                                                        .build()
                                        )
                                )
                                .build()
                );

        audience.sendMessage(builder.build());
    }

}
