package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.paths.Path;
import me.mattstudios.mf.annotations.Alias;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Default;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.base.CommandBase;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.entity.Player;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.utils.MessageUtils.color;

@Command("npcmd")
public class HelpCommand extends CommandBase {

    private CitizensCMD plugin;

    public HelpCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @Default
    @Alias("help")
    @Permission("citizenscmd.npcmd")
    public void help(Player player) {
        JSONMessage.create(color(HEADER)).send(player);
        JSONMessage.create(color(plugin.getLang().getUncoloredMessage(Path.HELP_VERSION) + " &c&o" + plugin.getDescription().getVersion())).send(player);
        JSONMessage.create(color(plugin.getLang().getUncoloredMessage(Path.HELP_INFO))).send(player);
        JSONMessage.create(color("&3/npcmd &cadd &b<console &b| &bmessage &b| &bplayer | &bpermission &b| &bserver &b| &bsound &b> &6<command> &d[-l]")).suggestCommand("/npcmd add ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_ADD) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oadd &b&ossentials.heal &6&oheal")).send(player);
        JSONMessage.create(color("&3/npcmd &ccooldown &6<time>")).suggestCommand("/npcmd cooldown ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_COOLDOWN) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&ocooldown &6&o15")).send(player);
        JSONMessage.create(color("&3/npcmd &cprice &6<price>")).suggestCommand("/npcmd price ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_PRICE) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oprice &6&o250")).send(player);
        JSONMessage.create(color("&3/npcmd &clist")).suggestCommand("/npcmd list").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_LIST) + "\n&8" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&olist")).send(player);
        JSONMessage.create(color("&3/npcmd &cedit &b<cmd | perm> &b<left | right> &6<id> &6<new command | new permission>")).suggestCommand("/npcmd edit ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_EDIT) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oedit &b&ocmd &b&oright &6&o1 fly")).send(player);
        JSONMessage.create(color("&3/npcmd &cremove &b<left | right> &6<id>")).suggestCommand("/npcmd remove ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_REMOVE) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oremove &b&oright &6&o1")).send(player);
        JSONMessage.create(color("&3/npcmd &cpermission &b<set | remove> &6<custom.permission>")).suggestCommand("/npcmd permission ").send(player);
        JSONMessage.create(color("&3/npcmd &creload")).suggestCommand("/npcmd reload").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_RELOAD) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oreload")).send(player);
    }

}
