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

package me.mattmoreira.citizenscmd.commands.base;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Thank you GlareMasters for creating this class!
 */
public abstract class CommandBase {

    private String name;
    private String permission;

    private boolean allowConsole;

    private List<String> aliases;

    private int minimumArguments;
    private int maximumArguments;

    protected CommandBase(String name, String permission, boolean allowConsole,
                          String[] aliases, int minimumArguments, int maximumArguments) {
        this.name = name;
        this.permission = permission;

        this.allowConsole = allowConsole;

        this.aliases = aliases == null ? new ArrayList<>() : Arrays.asList(aliases);

        this.minimumArguments = minimumArguments;
        this.maximumArguments = maximumArguments;
    }

    public void execute(CommandSender sender, String[] args) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public void execute(Player sender, String[] args) {
        throw new UnsupportedOperationException("Method not implemented");
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    boolean allowConsole() {
        return allowConsole;
    }

    List<String> getAliases() {
        return aliases;
    }

    int getMinimumArguments() {
        return minimumArguments;
    }

    int getMaximumArguments() {
        return maximumArguments;
    }
}
