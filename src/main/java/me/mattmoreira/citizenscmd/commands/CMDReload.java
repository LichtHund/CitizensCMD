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
import me.mattmoreira.citizenscmd.utility.DisplayFormat;
import me.mattmoreira.citizenscmd.utility.Path;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.mattmoreira.citizenscmd.utility.Util.HEADER;
import static me.mattmoreira.citizenscmd.utility.Util.color;

public class CMDReload extends CommandBase {

    public CMDReload() {
        super("reload", "citizenscmd.reload", true, null, 0, 0);
    }

    @Override
    public void execute(Player player, String[] args) {
        CitizensCMD.getPlugin().reloadConfig();
        CitizensCMD.getPlugin().saveConfig();
        CitizensCMD.getPlugin().setLang(CitizensCMD.getPlugin().getConfig().getString("lang"));

        if (CitizensCMD.getPlugin().getConfig().contains("cooldonw-time-display")) {
            switch (CitizensCMD.getPlugin().getConfig().getString("cooldonw-time-display").toLowerCase()) {
                case "short":
                    CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.SHORT);
                    break;
                case "medium":
                    CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.MEDIUM);
                    break;
                case "full":
                    CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.FULL);
                    break;
                default:
                    CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.MEDIUM);
            }
        } else
            CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.MEDIUM);

        if (CitizensCMD.getEconomy() != null)
            CitizensCMD.getPlugin().setShift(CitizensCMD.getPlugin().getConfig().getBoolean("shift-confirm"));

        CitizensCMD.getPlugin().getDataHandler().reload();
        CitizensCMD.getPlugin().getCooldownHandler().reload();

        player.sendMessage(color(HEADER));
        player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.RELOAD));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CitizensCMD.getPlugin().reloadConfig();
        CitizensCMD.getPlugin().saveConfig();
        CitizensCMD.getPlugin().setLang(CitizensCMD.getPlugin().getConfig().getString("lang"));

        if (CitizensCMD.getPlugin().getConfig().contains("cooldonw-time-display")) {
            switch (CitizensCMD.getPlugin().getConfig().getString("cooldonw-time-display").toLowerCase()) {
                case "short":
                    CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.SHORT);
                    break;
                case "medium":
                    CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.MEDIUM);
                    break;
                case "full":
                    CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.FULL);
                    break;
                default:
                    CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.MEDIUM);
            }
        } else
            CitizensCMD.getPlugin().setDisplayFormat(DisplayFormat.MEDIUM);

        if (CitizensCMD.getEconomy() != null)
            CitizensCMD.getPlugin().setShift(CitizensCMD.getPlugin().getConfig().getBoolean("shift-confirm"));

        CitizensCMD.getPlugin().getDataHandler().reload();
        CitizensCMD.getPlugin().getCooldownHandler().reload();

        sender.sendMessage(color(HEADER));
        sender.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.RELOAD));
    }

}
