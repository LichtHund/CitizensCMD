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
import me.mattstudios.citizenscmd.paths.Path;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;
import static me.mattstudios.utils.MessageUtils.color;
import static me.mattstudios.utils.NumbersUtils.isDouble;


public class CMDAdd extends CommandBase {

    private CitizensCMD plugin;

    public CMDAdd(CitizensCMD plugin) {
        super("add", "citizenscmd.add", false, null, 2, 2048);
        this.plugin = plugin;
    }

    /**
     * Adds a command to an NPC via ingame command
     *
     * @param player Gets the player to check for which NPC is selected and send messages
     * @param args   Gets the command to be added to the NPC
     */
    @Override
    public void execute(Player player, String[] args) {

        if (npcNotSelected(plugin, player)) return;

        StringBuilder permission = new StringBuilder(args[0]);
        boolean left = false;
        boolean displayName = false;
        boolean hasDelayError = false;

        StringBuilder stringBuilder = new StringBuilder();
        String[] commandsArray = Arrays.copyOfRange(args, 1, args.length);
        commandsArray[0] = commandsArray[0].replace("/", "");

        for (int i = 0; i < commandsArray.length; i++) {

            if (commandsArray[i].equalsIgnoreCase("")) continue;

            if (commandsArray[i].equalsIgnoreCase("-n")) {
                displayName = true;
                continue;
            }

            if (commandsArray[i].equalsIgnoreCase("-l")) {
                left = true;
                continue;
            }

            if (commandsArray[i].equalsIgnoreCase("-d")) {
                if (i + 1 >= commandsArray.length) {
                    hasDelayError = true;
                    continue;
                }

                if (!isDouble(commandsArray[i + 1])) {
                    hasDelayError = true;
                    continue;
                }

                permission.append("(").append(commandsArray[i + 1]).append(")");
                commandsArray[i + 1] = "";
                continue;
            }

            if (i == commandsArray.length - 1) stringBuilder.append(commandsArray[i]);
            else stringBuilder.append(commandsArray[i]).append(" ");
        }

        if (hasDelayError) {
            player.sendMessage(color(HEADER));
            player.sendMessage(plugin.getLang().getMessage(Path.NPC_ADD_DELAY_FAIL));
            return;
        }

        String finalString;

        if (displayName) {
            finalString = "{display} " + stringBuilder.toString().trim();
        } else {
            finalString = stringBuilder.toString().trim();
        }

        if (permission.toString().equalsIgnoreCase("sound")) {
            if (commandsArray.length < 2) {
                finalString += " 1 1";
            } else {
                if (commandsArray.length < 3) {
                    finalString += " 1";
                }
            }
        }

        plugin.getDataHandler().addCommand(getSelectedNpcId(player), permission.toString(), finalString, player, left);
    }

}
