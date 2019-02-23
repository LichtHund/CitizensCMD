/**
 * CitizensCMD - Add-on for Citizens
 * Copyright (C) 2018 Mateus Moreira
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.mattmoreira.citizenscmd.commands;

import me.mattmoreira.citizenscmd.CitizensCMD;
import me.mattmoreira.citizenscmd.commands.base.CommandBase;
import me.mattmoreira.citizenscmd.utility.Path;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.entity.Player;

import static me.mattmoreira.citizenscmd.utility.Util.HEADER;
import static me.mattmoreira.citizenscmd.utility.Util.color;

/**
 * Thank you GlareMasters for creating this class as an example!
 */
public class CMDHelp extends CommandBase {

    private CitizensCMD plugin;

    public CMDHelp(CitizensCMD plugin) {
        super("help", "citizenscmd.npcmd", false, null, 0, 0);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        JSONMessage.create(color(HEADER)).send(player);
        JSONMessage.create(color(plugin.getLang().getUncoloredMessage(Path.HELP_VERSION) + " &c&o" + plugin.getDescription().getVersion())).send(player);
        JSONMessage.create(color(plugin.getLang().getUncoloredMessage(Path.HELP_INFO))).send(player);
        JSONMessage.create(color("&3/npcmd &cadd &b<console &b| &bmessage &b| &bnone | &bpermission &b| &bserver &b| &bsound &b> &6<command> &d[-l]")).suggestCommand("/npcmd add ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_ADD) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oadd &b&ossentials.heal &6&oheal")).send(player);
        JSONMessage.create(color("&3/npcmd &ccooldown &6<time>")).suggestCommand("/npcmd cooldown ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_COOLDOWN) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&ocooldown &6&o15")).send(player);
        JSONMessage.create(color("&3/npcmd &cprice &6<price>")).suggestCommand("/npcmd price ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_PRICE) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oprice &6&o250")).send(player);
        JSONMessage.create(color("&3/npcmd &clist")).suggestCommand("/npcmd list").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_LIST) + "\n&8" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&olist")).send(player);
        JSONMessage.create(color("&3/npcmd &cedit &b<cmd | perm> &b<left | right> &6<id> &6<new command | new permission>")).suggestCommand("/npcmd edit ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_EDIT) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oedit &b&ocmd &b&oright &6&o1 fly")).send(player);
        JSONMessage.create(color("&3/npcmd &cremove &b<left | right> &6<id>")).suggestCommand("/npcmd remove ").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_REMOVE) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oremove &b&oright &6&o1")).send(player);
        JSONMessage.create(color("&3/npcmd &creload")).suggestCommand("/npcmd reload").tooltip(color(plugin.getLang().getUncoloredMessage(Path.HELP_DESCRIPTION_RELOAD) + "\n" + plugin.getLang().getUncoloredMessage(Path.HELP_EXAMPLE) + "\n&3&o/npcmd &c&oreload")).send(player);

    }

}
