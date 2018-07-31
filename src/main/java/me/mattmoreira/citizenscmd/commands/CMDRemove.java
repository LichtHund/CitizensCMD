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
import me.mattmoreira.citizenscmd.utility.EnumTypes;
import me.mattmoreira.citizenscmd.utility.Path;
import org.bukkit.entity.Player;

import static me.mattmoreira.citizenscmd.utility.Util.*;

public class CMDRemove extends CommandBase {

    public CMDRemove() {
        super("remove", "citizenscmd.remove", false, new String[]{"delete", "del", "rem"}, 2, 2);
    }

    @Override
    public void execute(Player player, String[] args) {

        if (npcNotSelected(player)) return;

        if (notInteger(args[1])) {
            player.sendMessage(color(HEADER));
            player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.INVALID_ID_NUMBER));
            return;
        }

        int commandID = Integer.parseInt(args[1]);
        int npc = getSelectedNpcId(player);
        EnumTypes.ClickType click;

        switch (args[0]) {
            case "left":
                int leftCommandSize = CitizensCMD.getPlugin().getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT).size();
                if (leftCommandSize == 0) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.NO_COMMANDS));
                    return;
                }
                if (commandID < 1 || commandID > leftCommandSize) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.INVALID_ID_NUMBER));
                    return;
                }
                click = EnumTypes.ClickType.LEFT;
                break;
            case "right":
                int rightCommandSize = CitizensCMD.getPlugin().getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT).size();
                if (rightCommandSize == 0) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.NO_COMMANDS));
                    return;
                }
                if (commandID < 0 || commandID > rightCommandSize) {
                    player.sendMessage(color(HEADER));
                    player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.INVALID_ID_NUMBER));
                    return;
                }
                click = EnumTypes.ClickType.RIGHT;
                break;
            default:
                player.sendMessage(color(HEADER));
                player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.INVALID_CLICK_TYPE));
                return;
        }

        CitizensCMD.getPlugin().getDataHandler().removeCommand(npc, commandID, click, player);

    }
}
