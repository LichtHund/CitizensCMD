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

package me.mattmoreira.citizenscmd.commands;

import me.mattmoreira.citizenscmd.CitizensCMD;
import me.mattmoreira.citizenscmd.commands.base.CommandBase;
import me.mattmoreira.citizenscmd.utility.Path;
import org.bukkit.entity.Player;

import static me.mattmoreira.citizenscmd.utility.Util.*;

public class CMDCooldown extends CommandBase {

    private CitizensCMD plugin;

    public CMDCooldown(CitizensCMD plugin) {
        super("cooldown", "citizenscmd.cooldown", false, new String[]{"cd"}, 1, 1);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {

        if (npcNotSelected(plugin, player)) return;

        if (notInteger(args[0])) {
            player.sendMessage(color(HEADER));
            player.sendMessage(plugin.getLang().getMessage(Path.INVALID_COOLDOWN));
            return;
        }

        plugin.getDataHandler().setCooldown(getSelectedNpcId(player), Integer.valueOf(args[0]), player);
    }

}
