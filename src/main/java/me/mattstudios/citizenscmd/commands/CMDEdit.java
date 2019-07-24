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
import me.mattstudios.citizenscmd.utility.EnumTypes;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;
import static me.mattstudios.utils.MessageUtils.color;
import static me.mattstudios.utils.NumbersUtils.isInteger;


public class CMDEdit extends CommandBase {

    private CitizensCMD plugin;

    public CMDEdit(CitizensCMD plugin) {
        super("edit", "citizenscmd.edit", false, null, 4, 512);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {

        if (npcNotSelected(plugin, player)) return;

        if (!isInteger(args[2])) {
            player.sendMessage(color(HEADER));
            player.sendMessage(plugin.getLang().getMessage(Path.INVALID_ID_NUMBER));
            return;
        }

        int commandID = Integer.parseInt(args[2]);
        int npc = getSelectedNpcId(player);
        EnumTypes.ClickType click;
        EnumTypes.EditType type;

        switch (args[0].toLowerCase()) {
            case "cmd":
                type = EnumTypes.EditType.CMD;
                break;
            case "perm":
                if (args.length > 4) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.INVALID_PERMISSION));
                    return;
                }
                type = EnumTypes.EditType.PERM;
                break;
            default:
                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.INVALID_ARGUMENTS));
                return;
        }

        switch (args[1].toLowerCase()) {
            case "left":
                int leftCommandSize = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT).size();
                if (leftCommandSize == 0) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.NO_COMMANDS));
                    return;
                }
                if (commandID < 1 || commandID > leftCommandSize) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.INVALID_ID_NUMBER));
                    return;
                }
                click = EnumTypes.ClickType.LEFT;
                break;
            case "right":
                int rightCommandSize = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT).size();
                if (rightCommandSize == 0) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.NO_COMMANDS));
                    return;
                }
                if (commandID < 1 || commandID > rightCommandSize) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(plugin.getLang().getMessage(Path.INVALID_ID_NUMBER));
                    return;
                }
                click = EnumTypes.ClickType.RIGHT;
                break;
            default:
                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.INVALID_CLICK_TYPE));
                return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        String[] commandsArray = Arrays.copyOfRange(args, 3, args.length);
        commandsArray[0] = commandsArray[0].replace("/", "");

        for (int i = 0; i < commandsArray.length; i++) {
            if (i == commandsArray.length - 1) stringBuilder.append(commandsArray[i]);
            else stringBuilder.append(commandsArray[i]).append(" ");
        }

        plugin.getDataHandler().edit(npc, commandID, click, type, stringBuilder.toString().trim(), player);
    }
}
