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

package me.mattstudios.citizenscmd.schedulers;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.paths.Path;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmScheduler extends BukkitRunnable {

    private Player player;
    private int npc;
    private CitizensCMD plugin;

    public ConfirmScheduler(CitizensCMD plugin, Player player, int npc) {
        this.player = player;
        this.npc = npc;
        this.plugin = plugin;
    }

    /**
     * Removes the data from the player in case it hasn't click on the NPC to confirm the payment
     */
    @Override
    public void run() {
        if (plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
            player.sendMessage(plugin.getLang().getMessage(Path.PAY_CANCELED));
            plugin.getWaitingList().remove(player.getUniqueId().toString() + "." + npc);
        }
    }

}
