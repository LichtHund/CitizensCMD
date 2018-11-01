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
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.mattmoreira.citizenscmd.utility.Util.*;

public class CMDList extends CommandBase {

    private CitizensCMD plugin;

    public CMDList(CitizensCMD plugin) {
        super("list", "citizenscmd.list", false, new String[]{"l"}, 0, 0);
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player, String[] args) {

        if (npcNotSelected(plugin, player)) return;

        int npc = getSelectedNpcId(player);

        List<String> leftCommands = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT) != null ? plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT) : new ArrayList<>();
        List<String> rightCommands = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT) != null ? plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT) : new ArrayList<>();

        player.sendMessage(color(HEADER));
        JSONMessage.create(color(plugin.getLang().getUncoloredMessage(Path.LIST_COOLDOWN) + plugin.getDataHandler().getNPCCooldown(npc))).tooltip(plugin.getLang().getMessage(Path.LIST_TOOLTIP)).suggestCommand("/npcmd cooldown ").send(player);
        JSONMessage.create(color(plugin.getLang().getUncoloredMessage(Path.LIST_PRICE) + plugin.getDataHandler().getPrice(npc))).tooltip(plugin.getLang().getMessage(Path.LIST_TOOLTIP)).suggestCommand("/npcmd price ").send(player);
        player.sendMessage("");
        player.sendMessage(plugin.getLang().getMessage(Path.LIST_COUNT_RIGHT).replace("{count}", String.valueOf(rightCommands.size())));

        int rightCount = 1;
        for (String command : rightCommands) {
            JSONMessage.create(color("&c" + rightCount + " &7- &7" + command.replace("[", "&8[&c").replace("]", "&8]&b"))).suggestCommand("/npcmd edit cmd right " + rightCount + " ").tooltip(plugin.getLang().getMessage(Path.LIST_TOOLTIP)).send(player);
            rightCount++;
        }

        player.sendMessage("");
        player.sendMessage(plugin.getLang().getMessage(Path.LIST_COUNT_LEFT).replace("{count}", String.valueOf(leftCommands.size())));

        int leftCount = 1;
        for (String command : leftCommands) {
            JSONMessage.create(color("&c" + leftCount + " &7- &7" + command.replace("[", "&8[&c").replace("]", "&8]&b"))).suggestCommand("/npcmd edit cmd left " + leftCount + " ").tooltip(plugin.getLang().getMessage(Path.LIST_TOOLTIP)).send(player);
            leftCount++;
        }
    }

}