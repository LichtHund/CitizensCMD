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

package me.mattmoreira.citizenscmd.listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import me.mattmoreira.citizenscmd.CitizensCMD;
import me.mattmoreira.citizenscmd.schedulers.ConfirmScheduler;
import me.mattmoreira.citizenscmd.utility.EnumTypes;
import me.mattmoreira.citizenscmd.utility.Path;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.mattmoreira.citizenscmd.utility.TimeUtil.getFormattedTime;
import static me.mattmoreira.citizenscmd.utility.Util.color;

public class NPCListener implements Listener {

    private CitizensCMD plugin;

    public NPCListener(CitizensCMD plugin) {
        this.plugin = plugin;
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRightClick(NPCRightClickEvent event) {
        int npc = event.getNPC().getId();
        Player player = event.getClicker();

        if (!player.hasPermission("citizenscmd.use")) return;

        if (!plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
            if (!player.hasPermission("citizenscmd.bypass")) {
                if (plugin.getCooldownHandler().onCooldown(npc, player.getUniqueId().toString())) {
                    String cooldownMessage;
                    if (plugin.getDataHandler().getNPCCooldown(npc) == -1)
                        cooldownMessage = plugin.getLang().getMessage(Path.ONE_TIME_CLICK);
                    else
                        cooldownMessage = plugin.getLang().getMessage(Path.ON_COOLDOWN);
                    player.sendMessage(cooldownMessage.replace("{time}", getFormattedTime(plugin, plugin.getCooldownHandler().getTimeLeft(npc, player.getUniqueId().toString()), plugin.getDisplayFormat())));
                    return;
                }
            }

            if (plugin.getDataHandler().hasNoCommands(npc, EnumTypes.ClickType.RIGHT)) return;
        }

        double price = plugin.getDataHandler().getPrice(npc);

        if (price > 0.0) {
            if (CitizensCMD.getEconomy() != null) {

                if (!plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
                    String messageConfirm = plugin.getLang().getMessage(Path.PAY_CONFIRM);
                    if (!plugin.shouldShift())
                        messageConfirm = messageConfirm.replace("{shift}", "");
                    else
                        messageConfirm = messageConfirm.replace("{shift}", "Shift ");
                    messageConfirm = messageConfirm.replace("{price}", String.valueOf(price));
                    player.sendMessage(messageConfirm);
                    plugin.getWaitingList().put(player.getUniqueId().toString() + "." + npc, true);
                    new ConfirmScheduler(plugin, player, npc).runTaskLaterAsynchronously(plugin, 300L);
                    return;
                }

                if (plugin.shouldShift() && !player.isSneaking()) return;

                if (CitizensCMD.getEconomy().getBalance(player) < price) {
                    player.sendMessage(plugin.getLang().getMessage(Path.PAY_NO_MONEY));
                    return;
                }

                plugin.getWaitingList().remove(player.getUniqueId().toString() + "." + npc);
                CitizensCMD.getEconomy().withdrawPlayer(player, price);
                player.sendMessage(plugin.getLang().getMessage(Path.PAY_COMPLETED).replace("{price}", String.valueOf(price)));

            }
        }

        List<String> permissions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        for (String list : plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT)) {
            Pattern pattern = Pattern.compile("\\[([^]]*)] ([^]]*)");
            Matcher matcher = pattern.matcher(list);
            if (matcher.find()) {
                permissions.add(matcher.group(1));
                String command = matcher.group(2);
                if (command.contains("%p%")) command = command.replace("%p%", player.getName());
                if (command.contains("%player%")) command = command.replace("%player%", player.getName());
                if (plugin.papiEnabled())
                    commands.add(PlaceholderAPI.setPlaceholders((OfflinePlayer) player, command));
                else commands.add(command);

            }
        }

        if (permissions.size() != commands.size()) return;

        for (int i = 0; i < permissions.size(); i++) {
            switch (permissions.get(i).toLowerCase()) {

                case "console":
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commands.get(i));
                    break;

                case "none":
                    player.chat("/" + commands.get(i));
                    break;

                case "server":
                    changeServer(player, commands.get(i));
                    break;

                case "message":
                    String finalMessage;
                    if (commands.get(i).contains("{display}")) {
                        String tmpStr = commands.get(i).replace("{display}", plugin.getLang().getMessage(Path.MESSAGE_DISPLAY));
                        finalMessage = tmpStr.replace("{name}", event.getNPC().getFullName());
                    } else
                        finalMessage = commands.get(i);
                    player.sendMessage(color(finalMessage));
                    break;

                case "sound":
                    Pattern pattern = Pattern.compile("(\\w+)\\s([\\d\\.]+)\\s([\\d\\.]+)");
                    Matcher matcher = pattern.matcher(commands.get(i));
                    if (matcher.find()) {
                        player.playSound(player.getLocation(), Sound.valueOf(matcher.group(1)), Float.parseFloat(matcher.group(2)), Float.parseFloat(matcher.group(3)));
                    }
                    break;

                default:
                    plugin.getPermissionsManager().setPermission(player, permissions.get(i));
                    player.chat("/" + commands.get(i));
                    plugin.getPermissionsManager().unsetPermission(player, permissions.get(i));
            }
        }

        if (!player.hasPermission("citizenscmd.bypass") || plugin.getDataHandler().getNPCCooldown(npc) != 0) {
            plugin.getCooldownHandler().addInteraction(npc, player.getUniqueId().toString(), System.currentTimeMillis());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLeftClick(NPCLeftClickEvent event) {
        int npc = event.getNPC().getId();
        Player player = event.getClicker();

        if (!player.hasPermission("citizenscmd.use")) return;

        if (!plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
            if (!player.hasPermission("citizenscmd.bypass")) {
                if (plugin.getCooldownHandler().onCooldown(npc, player.getUniqueId().toString())) {
                    String cooldownMessage;
                    if (plugin.getDataHandler().getNPCCooldown(npc) == -1)
                        cooldownMessage = plugin.getLang().getMessage(Path.ONE_TIME_CLICK);
                    else
                        cooldownMessage = plugin.getLang().getMessage(Path.ON_COOLDOWN);
                    player.sendMessage(cooldownMessage.replace("{time}", getFormattedTime(plugin, plugin.getCooldownHandler().getTimeLeft(npc, player.getUniqueId().toString()), plugin.getDisplayFormat())));
                    return;
                }
            }

            if (plugin.getDataHandler().hasNoCommands(npc, EnumTypes.ClickType.LEFT)) return;
        }

        double price = plugin.getDataHandler().getPrice(npc);

        if (price > 0.0) {
            if (CitizensCMD.getEconomy() != null) {

                if (!plugin.getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
                    String messageConfirm = plugin.getLang().getMessage(Path.PAY_CONFIRM);
                    if (!plugin.shouldShift())
                        messageConfirm = messageConfirm.replace("{shift}", "");
                    else
                        messageConfirm = messageConfirm.replace("{shift}", "Shift ");
                    messageConfirm = messageConfirm.replace("{price}", String.valueOf(price));
                    player.sendMessage(messageConfirm);
                    plugin.getWaitingList().put(player.getUniqueId().toString() + "." + npc, true);
                    new ConfirmScheduler(plugin, player, npc).runTaskLaterAsynchronously(plugin, 300L);
                    return;
                }

                if (plugin.shouldShift() && !player.isSneaking()) return;

                plugin.getWaitingList().remove(player.getUniqueId().toString() + "." + npc);
                player.sendMessage(plugin.getLang().getMessage(Path.PAY_CANCELED));

            }
        }

        List<String> permissions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        for (String list : plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT)) {
            Pattern pattern = Pattern.compile("\\[([^]]*)] ([^]]*)");
            Matcher matcher = pattern.matcher(list);
            if (matcher.find()) {
                permissions.add(matcher.group(1));
                String command = matcher.group(2);
                if (command.contains("%p%"))
                    command = command.replace("%p%", player.getName());
                if (command.contains("%player%"))
                    command = command.replace("%player%", player.getName());
                if (plugin.papiEnabled())
                    commands.add(PlaceholderAPI.setPlaceholders((OfflinePlayer) player, command));
                else
                    commands.add(command);
            }
        }

        if (permissions.size() != commands.size()) return;

        for (int i = 0; i < permissions.size(); i++) {
            switch (permissions.get(i).toLowerCase()) {

                case "console":
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commands.get(i));
                    break;

                case "none":
                    player.chat("/" + commands.get(i));
                    break;

                case "server":
                    changeServer(player, commands.get(i));
                    break;

                case "message":
                    String finalMessage;
                    if (commands.get(i).contains("{display}")) {
                        String tmpStr = commands.get(i).replace("{display}", plugin.getLang().getMessage(Path.MESSAGE_DISPLAY));
                        finalMessage = tmpStr.replace("{name}", event.getNPC().getFullName());
                    } else
                        finalMessage = commands.get(i);
                    player.sendMessage(color(finalMessage));
                    break;

                case "sound":
                    Pattern pattern = Pattern.compile("(\\w+)\\s([\\d\\.]+)\\s([\\d\\.]+)");
                    Matcher matcher = pattern.matcher(commands.get(i));
                    if (matcher.find()) {
                        player.playSound(player.getLocation(), Sound.valueOf(matcher.group(1)), Float.parseFloat(matcher.group(2)), Float.parseFloat(matcher.group(3)));
                    }
                    break;

                default:
                    plugin.getPermissionsManager().setPermission(player, permissions.get(i));
                    player.chat("/" + commands.get(i));
                    plugin.getPermissionsManager().unsetPermission(player, permissions.get(i));
            }
        }

        if (!player.hasPermission("citizenscmd.bypass") || plugin.getDataHandler().getNPCCooldown(npc) != 0) {
            plugin.getCooldownHandler().addInteraction(npc, player.getUniqueId().toString(), System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemoveNPC(NPCRemoveEvent event) {
        plugin.getDataHandler().removeNPCData(event.getNPC().getId());
    }

    /**
     * Bungee cord connection method
     *
     * @param player The player to be sent to the server
     * @param server the server name
     */
    private void changeServer(Player player, String server) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeUTF("Connect");
            dataOutputStream.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", byteArrayOutputStream.toByteArray());
    }

}
