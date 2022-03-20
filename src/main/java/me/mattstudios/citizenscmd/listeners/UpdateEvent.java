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

package me.mattstudios.citizenscmd.listeners;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.text;

public class UpdateEvent implements Listener {

    private final CitizensCMD plugin;

    public UpdateEvent(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.isUpdateStatus() || !event.getPlayer().hasPermission("citizenscmd.update")) return;

        final Audience audience = plugin.getAudiences().player(event.getPlayer());

        final TextComponent.Builder builder = Component.text();
        builder.append(HEADER).append(newline());
        builder.append(plugin.getLang().getMessage(Messages.NEW_VERSION));
        builder.append(text(plugin.getNewVersion()));
        builder.append(newline());
        builder.append(
                Component.text()
                        .append(plugin.getLang().getMessage(Messages.DOWNLOAD_AT))
                        .append(text(" spigotmc.org/resources/citizens-CMD.30224/"))
                        .clickEvent(ClickEvent.openUrl("https://spigotmc.org/resources/citizens-CMD.30224/"))
                        .build()
        );

        audience.sendMessage(builder.build());
    }

}
