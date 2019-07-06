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

package me.mattstudios.citizenscmd.permissions;


import me.mattstudios.citizenscmd.CitizensCMD;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.UUID;

public class PermissionsManager {

    private HashMap<UUID, PermissionAttachment> permissionsData;
    private CitizensCMD plugin;

    public PermissionsManager(CitizensCMD plugin) {
        this.plugin = plugin;
        permissionsData = new HashMap<>();
    }

    /**
     * Sets the permission to a player
     *
     * @param player     The player to have the permission set
     * @param permission The permission node
     */
    public void setPermission(Player player, String permission) {
        PermissionAttachment permissionAttachment = player.addAttachment(plugin);
        permissionsData.put(player.getUniqueId(), permissionAttachment);
        PermissionAttachment permissionAttachment1 = permissionsData.get(player.getUniqueId());
        permissionAttachment1.setPermission(permission, true);
    }

    /**
     * Removes the permission from a player
     *
     * @param player     The player to remove the permission
     * @param permission The permission node to be removed
     */
    public void unsetPermission(Player player, String permission) {
        permissionsData.get(player.getUniqueId()).unsetPermission(permission);
    }

}
