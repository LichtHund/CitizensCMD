/*
  CitizensCMD - Add-on for Citizens
  Copyright (C) 2018 Mateus Moreira
  <p>
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.commands.base.CommandBase;
import me.mattstudios.citizenscmd.utility.paths.Path;
import org.bukkit.entity.Player;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;
import static me.mattstudios.utils.MessageUtils.color;

public class CMDPermission extends CommandBase {

    private CitizensCMD plugin;

    public CMDPermission(CitizensCMD plugin) {
        super("permission", "citizenscmd.permission", false, null, 1, 2);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {

        if (npcNotSelected(plugin, player)) return;

        switch (args[0]) {
            case "set":
                if (args.length < 2) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.WRONG_USAGE));
                    return;
                }
                plugin.getDataHandler().setCustomPermission(getSelectedNpcId(player), args[1], player);
                break;

            case "remove":
                if (args.length > 1) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.WRONG_USAGE));
                    return;
                }
                plugin.getDataHandler().removeCustomPermission(getSelectedNpcId(player), player);
                break;

            default:
                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.WRONG_USAGE));
        }
    }

}
