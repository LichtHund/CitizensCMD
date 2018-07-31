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
import me.mattmoreira.citizenscmd.updater.SpigotUpdater;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateScheduler extends BukkitRunnable {

    /**
     * Checks for updates every hour and tells the player on join
     */
    @Override
    public void run() {
        SpigotUpdater updater = new SpigotUpdater(CitizensCMD.getPlugin(), 30224);
        try {
            if (updater.checkForUpdates()) {
                CitizensCMD.getPlugin().setUpdateStatus(true);
                CitizensCMD.getPlugin().setNewVersion(updater.getLatestVersion());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
