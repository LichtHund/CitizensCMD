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
import me.mattstudios.citizenscmd.schedulers.ConfirmScheduler;
import me.mattstudios.citizenscmd.utility.EnumTypes;
import me.mattstudios.citizenscmd.utility.Path;
import me.mattstudios.citizenscmd.utility.TimeUtil;
import me.mattstudios.citizenscmd.utility.Util;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NPCClickListener implements Listener {

    private CitizensCMD plugin;

    public NPCClickListener(CitizensCMD plugin) {
        this.plugin = plugin;
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();

        if (!player.hasPermission("citizenscmd.use")) return;

        if (plugin.getDataHandler().hasCustomPermission(npc.getId())) {
            if (!player.hasPermission(plugin.getDataHandler().getCustomPermission(npc.getId()))) return;
        }

        if (!plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc.getId())) {
            if (!player.hasPermission("citizenscmd.bypass")) {
                if (plugin.getCooldownHandler().onCooldown(npc.getId(), player.getUniqueId().toString())) {
                    String cooldownMessage;
                    if (plugin.getDataHandler().getNPCCooldown(npc.getId()) == -1)
                        cooldownMessage = plugin.getLang().getMessage(Path.ONE_TIME_CLICK);
                    else
                        cooldownMessage = plugin.getLang().getMessage(Path.ON_COOLDOWN);
                    player.sendMessage(cooldownMessage.replace("{time}", TimeUtil.getFormattedTime(plugin, plugin.getCooldownHandler().getTimeLeft(npc.getId(), player.getUniqueId().toString()), plugin.getDisplayFormat())));
                    return;
                }
            }

            if (plugin.getDataHandler().hasNoCommands(npc.getId(), EnumTypes.ClickType.RIGHT)) return;
        }

        double price = plugin.getDataHandler().getPrice(npc.getId());

        if (price > 0.0) {
            if (CitizensCMD.getEconomy() != null) {

                if (!plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc.getId())) {
                    String messageConfirm = plugin.getLang().getMessage(Path.PAY_CONFIRM);
                    if (!plugin.isShift())
                        messageConfirm = messageConfirm.replace("{shift}", "");
                    else
                        messageConfirm = messageConfirm.replace("{shift}", "Shift ");
                    messageConfirm = messageConfirm.replace("{price}", String.valueOf(price));
                    player.sendMessage(messageConfirm);
                    plugin.getWaitingList().put(player.getUniqueId().toString() + "." + npc.getId(), true);
                    new ConfirmScheduler(plugin, player, npc.getId()).runTaskLaterAsynchronously(plugin, 300L);
                    return;
                }

                if (plugin.isShift() && !player.isSneaking()) {
                    return;
                }

                if (CitizensCMD.getEconomy().getBalance(player) < price) {
                    player.sendMessage(plugin.getLang().getMessage(Path.PAY_NO_MONEY));
                    return;
                }

                plugin.getWaitingList().remove(player.getUniqueId().toString() + "." + npc.getId());
                CitizensCMD.getEconomy().withdrawPlayer(player, price);
                player.sendMessage(plugin.getLang().getMessage(Path.PAY_COMPLETED).replace("{price}", String.valueOf(price)));

            }
        }

        Util.doCommands(plugin, npc, player, EnumTypes.ClickType.RIGHT);

        if (!player.hasPermission("citizenscmd.bypass") || plugin.getDataHandler().getNPCCooldown(npc.getId()) != 0) {
            plugin.getCooldownHandler().addInteraction(npc.getId(), player.getUniqueId().toString(), System.currentTimeMillis());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClick(NPCLeftClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();

        if (!player.hasPermission("citizenscmd.use")) return;

        if (plugin.getDataHandler().hasCustomPermission(npc.getId())) {
            if (!player.hasPermission(plugin.getDataHandler().getCustomPermission(npc.getId()))) return;
        }

        if (!plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc.getId())) {
            if (!player.hasPermission("citizenscmd.bypass")) {
                if (plugin.getCooldownHandler().onCooldown(npc.getId(), player.getUniqueId().toString())) {
                    String cooldownMessage;
                    if (plugin.getDataHandler().getNPCCooldown(npc.getId()) == -1)
                        cooldownMessage = plugin.getLang().getMessage(Path.ONE_TIME_CLICK);
                    else
                        cooldownMessage = plugin.getLang().getMessage(Path.ON_COOLDOWN);
                    player.sendMessage(cooldownMessage.replace("{time}", TimeUtil.getFormattedTime(plugin, plugin.getCooldownHandler().getTimeLeft(npc.getId(), player.getUniqueId().toString()), plugin.getDisplayFormat())));
                    return;
                }
            }

            if (plugin.getDataHandler().hasNoCommands(npc.getId(), EnumTypes.ClickType.LEFT)) return;
        }

        double price = plugin.getDataHandler().getPrice(npc.getId());

        if (price > 0.0) {
            if (CitizensCMD.getEconomy() != null) {

                if (!plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc.getId())) {
                    String messageConfirm = plugin.getLang().getMessage(Path.PAY_CONFIRM);
                    if (!plugin.isShift())
                        messageConfirm = messageConfirm.replace("{shift}", "");
                    else
                        messageConfirm = messageConfirm.replace("{shift}", "Shift ");
                    messageConfirm = messageConfirm.replace("{price}", String.valueOf(price));
                    player.sendMessage(messageConfirm);
                    plugin.getWaitingList().put(player.getUniqueId().toString() + "." + npc.getId(), true);
                    new ConfirmScheduler(plugin, player, npc.getId()).runTaskLaterAsynchronously(plugin, 300L);
                    return;
                }

                if (plugin.isShift() && !player.isSneaking()) return;

                plugin.getWaitingList().remove(player.getUniqueId().toString() + "." + npc.getId());
                player.sendMessage(plugin.getLang().getMessage(Path.PAY_CANCELED));

            }
        }

        Util.doCommands(plugin, npc, player, EnumTypes.ClickType.LEFT);

        if (!player.hasPermission("citizenscmd.bypass") || plugin.getDataHandler().getNPCCooldown(npc.getId()) != 0) {
            plugin.getCooldownHandler().addInteraction(npc.getId(), player.getUniqueId().toString(), System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemoveNPC(NPCRemoveEvent event) {
        if (!plugin.getDataHandler().hasNPCData(event.getNPC().getId())) return;

        plugin.getDataHandler().removeNPCData(event.getNPC().getId());
    }

}
