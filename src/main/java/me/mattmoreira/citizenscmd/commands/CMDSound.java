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

import me.mattmoreira.citizenscmd.commands.base.CommandBase;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static me.mattmoreira.citizenscmd.utility.Util.*;

public class CMDSound extends CommandBase {

    public CMDSound() {
        super("sound", "citizenscmd.sound", false, null, 0, 3);
    }

    public void execute(Player player, String[] args) {

        //if (npcNotSelected(player)) return;

        int npc = getSelectedNpcId(player);

        if (args.length == 0) {
            //CitizensCMD.getPlugin().getDataHandler().removeSound(npc, player);
            return;
        }

        String soundString = args[0];
        Sound sound;
        float volume = 1f;
        float pitch = 1f;

        if (args.length > 1)
            if (isFloat(args[1]))
                volume = Float.valueOf(args[1]);

        if (args.length > 2)
            if (isFloat(args[2]))
                pitch = Float.valueOf(args[2]);

        try {
            sound = Sound.valueOf(soundString);
        } catch (Exception e) {
            player.sendMessage(color(HEADER));
            //player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.INVALID_SOUND));
            return;
        }

        //CitizensCMD.getPlugin().getDataHandler().setSound(npc, sound, volume, pitch, player);

    }

}
