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
import me.mattstudios.citizenscmd.utility.DisplayFormat;
import me.mattstudios.citizenscmd.utility.Path;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.color;

public class CMDReload extends CommandBase {

    private CitizensCMD plugin;

    public CMDReload(CitizensCMD plugin) {
        super("reload", "citizenscmd.reload", true, null, 0, 0);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {
        plugin.reloadConfig();
        plugin.saveDefaultConfig();
        plugin.setLang(Objects.requireNonNull(plugin.getConfig().getString("lang")));

        if (plugin.getConfig().contains("cooldown-time-display")) {
            switch (Objects.requireNonNull(plugin.getConfig().getString("cooldown-time-display")).toLowerCase()) {
                case "short":
                    plugin.setDisplayFormat(DisplayFormat.SHORT);
                    break;
                case "full":
                    plugin.setDisplayFormat(DisplayFormat.FULL);
                    break;
                default:
                    plugin.setDisplayFormat(DisplayFormat.MEDIUM);
            }
        } else
            plugin.setDisplayFormat(DisplayFormat.MEDIUM);

        if (CitizensCMD.getEconomy() != null)
            plugin.setShift(plugin.getConfig().getBoolean("shift-confirm"));

        plugin.getDataHandler().reload();
        plugin.getCooldownHandler().reload();

        player.sendMessage(color(HEADER));
        player.sendMessage(plugin.getLang().getMessage(Path.RELOAD));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        plugin.reloadConfig();
        plugin.saveConfig();
        plugin.setLang(Objects.requireNonNull(plugin.getConfig().getString("lang")));

        if (plugin.getConfig().contains("cooldown-time-display")) {
            switch (Objects.requireNonNull(plugin.getConfig().getString("cooldown-time-display")).toLowerCase()) {
                case "short":
                    plugin.setDisplayFormat(DisplayFormat.SHORT);
                    break;
                case "full":
                    plugin.setDisplayFormat(DisplayFormat.FULL);
                    break;
                default:
                    plugin.setDisplayFormat(DisplayFormat.MEDIUM);
            }
        } else
            plugin.setDisplayFormat(DisplayFormat.MEDIUM);

        if (CitizensCMD.getEconomy() != null)
            plugin.setShift(plugin.getConfig().getBoolean("shift-confirm"));

        plugin.getDataHandler().reload();
        plugin.getCooldownHandler().reload();

        sender.sendMessage(color(HEADER));
        sender.sendMessage(plugin.getLang().getMessage(Path.RELOAD));
    }

}
