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

package me.mattmoreira.citizenscmd.schedulers;

import me.mattmoreira.citizenscmd.CitizensCMD;
import me.mattmoreira.citizenscmd.utility.Path;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ConfirmScheduler extends BukkitRunnable {

    private Player player;
    private int npc;

    public ConfirmScheduler(Player player, int npc) {
        this.player = player;
        this.npc = npc;
    }

    /**
     * Removes the data from the player in case it hasn't click on the NPC to confirm the payment
     */
    @Override
    public void run() {
        if (CitizensCMD.getPlugin().getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
            player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.PAY_CANCELED));
            CitizensCMD.getPlugin().getWaitingList().remove(player.getUniqueId().toString() + "." + npc);
        }
    }

}
