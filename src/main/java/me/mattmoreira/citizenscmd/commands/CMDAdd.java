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
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.mattmoreira.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattmoreira.citizenscmd.utility.Util.npcNotSelected;

public class CMDAdd extends CommandBase {

    public CMDAdd() {
        super("add", "citizenscmd.add", false, null, 2, 512);
    }

    /**
     * Adds a command to an NPC via ingame command
     *
     * @param player Gets the player to check for which NPC is selected and send messages
     * @param args   Gets the command to be added to the NPC
     */
    @Override
    public void execute(Player player, String[] args) {

        if (npcNotSelected(player)) return;

        String permission = args[0];
        boolean left = false;
        boolean displayName = false;

        StringBuilder stringBuilder = new StringBuilder();
        String[] commandsArray = Arrays.copyOfRange(args, 1, args.length);
        commandsArray[0] = commandsArray[0].replace("/", "");

        for (int i = 0; i < commandsArray.length; i++) {

            if (commandsArray[i].equalsIgnoreCase("-d")) {
                displayName = true;
                commandsArray[i] = "";
            }

            if (commandsArray[i].equalsIgnoreCase("-l")) {
                left = true;
                break;
            }
            if (i == commandsArray.length - 1) stringBuilder.append(commandsArray[i]);
            else stringBuilder.append(commandsArray[i]).append(" ");
        }

        String finalString;

        if (displayName)
            finalString = "{display} " + stringBuilder.toString().trim();
        else
            finalString = stringBuilder.toString().trim();

        boolean finalLeft = left;

        CitizensCMD.getPlugin().getDataHandler().addCommand(getSelectedNpcId(player), permission, finalString, player, finalLeft);
    }

}
