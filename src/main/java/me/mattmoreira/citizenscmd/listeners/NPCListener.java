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

    public NPCListener() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(CitizensCMD.getPlugin(), "BungeeCord");
    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onRightClick(NPCRightClickEvent event) {
        int npc = event.getNPC().getId();
        Player player = event.getClicker();

        if (!player.hasPermission("citizenscmd.use")) return;

        if (!CitizensCMD.getPlugin().getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
            if (!player.hasPermission("citizenscmd.bypass")) {
                if (CitizensCMD.getPlugin().getCooldownHandler().onCooldown(npc, player.getUniqueId().toString())) {
                    String cooldownMessage;
                    if (CitizensCMD.getPlugin().getDataHandler().getNPCCooldown(npc) == -1)
                        cooldownMessage = CitizensCMD.getPlugin().getLang().getMessage(Path.ONE_TIME_CLICK);
                    else
                        cooldownMessage = CitizensCMD.getPlugin().getLang().getMessage(Path.ON_COOLDOWN);
                    player.sendMessage(cooldownMessage.replace("{time}", getFormattedTime(CitizensCMD.getPlugin().getCooldownHandler().getTimeLeft(npc, player.getUniqueId().toString()), CitizensCMD.getPlugin().getDisplayFormat())));
                    return;
                }
            }

            if (CitizensCMD.getPlugin().getDataHandler().hasSound(npc)) {
                List<String> soundProperties = CitizensCMD.getPlugin().getDataHandler().getNPCSound(npc);
                player.playSound(player.getLocation(), Sound.valueOf(soundProperties.get(0).toUpperCase()), Float.parseFloat(soundProperties.get(1)), Float.parseFloat(soundProperties.get(2)));
            }

            if (CitizensCMD.getPlugin().getDataHandler().hasNoCommands(npc, EnumTypes.ClickType.RIGHT)) return;
        }

        double price = CitizensCMD.getPlugin().getDataHandler().getPrice(npc);

        if (price > 0.0) {
            if (CitizensCMD.getEconomy() != null) {

                if (!CitizensCMD.getPlugin().getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
                    String messageConfirm = CitizensCMD.getPlugin().getLang().getMessage(Path.PAY_CONFIRM);
                    if (!CitizensCMD.getPlugin().shouldShift())
                        messageConfirm = messageConfirm.replace("{shift}", "");
                    else
                        messageConfirm = messageConfirm.replace("{shift}", "Shift ");
                    messageConfirm = messageConfirm.replace("{price}", String.valueOf(price));
                    player.sendMessage(messageConfirm);
                    CitizensCMD.getPlugin().getWaitingList().put(player.getUniqueId().toString() + "." + npc, true);
                    new ConfirmScheduler(player, npc).runTaskLaterAsynchronously(CitizensCMD.getPlugin(), 300L);
                    return;
                }

                if (CitizensCMD.getPlugin().shouldShift() && !player.isSneaking()) return;

                if (CitizensCMD.getEconomy().getBalance(player) < price) {
                    player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.PAY_NO_MONEY));
                    return;
                }

                CitizensCMD.getPlugin().getWaitingList().remove(player.getUniqueId().toString() + "." + npc);
                CitizensCMD.getEconomy().withdrawPlayer(player, price);
                player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.PAY_COMPLETED).replace("{price}", String.valueOf(price)));

            }
        }

        List<String> permissions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        for (String list : CitizensCMD.getPlugin().getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT)) {
            Pattern pattern = Pattern.compile("\\[([^]]*)] ([^]]*)");
            Matcher matcher = pattern.matcher(list);
            if (matcher.find()) {
                permissions.add(matcher.group(1));
                String command = matcher.group(2);
                if (command.contains("%p%"))
                    command = command.replace("%p%", player.getName());
                if (command.contains("%player%"))
                    command = command.replace("%player%", player.getName());
                if (CitizensCMD.getPlugin().papiEnabled())
                    commands.add(PlaceholderAPI.setPlaceholders((OfflinePlayer) player, command));
                else
                    commands.add(command);
            }
        }

        if (permissions.size() != commands.size()) return;

        for (int i = 0; i < permissions.size(); i++) {
            switch (permissions.get(i).toLowerCase()) {

                case "console":
                    CitizensCMD.getPlugin().getServer().dispatchCommand(CitizensCMD.getPlugin().getServer().getConsoleSender(), commands.get(i));
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
                        String tmpStr = commands.get(i).replace("{display}", CitizensCMD.getPlugin().getLang().getMessage(Path.MESSAGE_DISPLAY));
                        finalMessage = tmpStr.replace("{name}", event.getNPC().getFullName());
                    } else
                        finalMessage = commands.get(i);
                    player.sendMessage(color(finalMessage));
                    break;

                default:
                    CitizensCMD.getPlugin().getPermissionsManager().setPermission(player, permissions.get(i));
                    player.chat("/" + commands.get(i));
                    CitizensCMD.getPlugin().getPermissionsManager().unsetPermission(player, permissions.get(i));
            }
        }

        if (!player.hasPermission("citizenscmd.bypass") || CitizensCMD.getPlugin().getDataHandler().getNPCCooldown(npc) != 0)
            CitizensCMD.getPlugin().getCooldownHandler().addInteraction(npc, player.getUniqueId().toString(), System.nanoTime());

    }

    @EventHandler (priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLeftClick(NPCLeftClickEvent event) {
        int npc = event.getNPC().getId();
        Player player = event.getClicker();

        if (!player.hasPermission("citizenscmd.use")) return;

        if (!CitizensCMD.getPlugin().getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
            if (!player.hasPermission("citizenscmd.bypass")) {
                if (CitizensCMD.getPlugin().getCooldownHandler().onCooldown(npc, player.getUniqueId().toString())) {
                    String cooldownMessage;
                    if (CitizensCMD.getPlugin().getDataHandler().getNPCCooldown(npc) == -1)
                        cooldownMessage = CitizensCMD.getPlugin().getLang().getMessage(Path.ONE_TIME_CLICK);
                    else
                        cooldownMessage = CitizensCMD.getPlugin().getLang().getMessage(Path.ON_COOLDOWN);
                    player.sendMessage(cooldownMessage.replace("{time}", getFormattedTime(CitizensCMD.getPlugin().getCooldownHandler().getTimeLeft(npc, player.getUniqueId().toString()), CitizensCMD.getPlugin().getDisplayFormat())));
                    return;
                }
            }

            if (CitizensCMD.getPlugin().getDataHandler().hasSound(npc)) {
                List<String> soundProperties = CitizensCMD.getPlugin().getDataHandler().getNPCSound(npc);
                player.playSound(player.getLocation(), Sound.valueOf(soundProperties.get(0)), Float.parseFloat(soundProperties.get(1)), Float.parseFloat(soundProperties.get(2)));
            }

            if (CitizensCMD.getPlugin().getDataHandler().hasNoCommands(npc, EnumTypes.ClickType.LEFT)) return;
        }

        double price = CitizensCMD.getPlugin().getDataHandler().getPrice(npc);

        if (price > 0.0) {
            if (CitizensCMD.getEconomy() != null) {

                if (!CitizensCMD.getPlugin().getWaitingList().containsKey(player.getUniqueId().toString() + "." + npc)) {
                    String messageConfirm = CitizensCMD.getPlugin().getLang().getMessage(Path.PAY_CONFIRM);
                    if (!CitizensCMD.getPlugin().shouldShift())
                        messageConfirm = messageConfirm.replace("{shift}", "");
                    else
                        messageConfirm = messageConfirm.replace("{shift}", "Shift ");
                    messageConfirm = messageConfirm.replace("{price}", String.valueOf(price));
                    player.sendMessage(messageConfirm);
                    CitizensCMD.getPlugin().getWaitingList().put(player.getUniqueId().toString() + "." + npc, true);
                    new ConfirmScheduler(player, npc).runTaskLaterAsynchronously(CitizensCMD.getPlugin(), 300L);
                    return;
                }

                if (CitizensCMD.getPlugin().shouldShift() && !player.isSneaking()) return;

                CitizensCMD.getPlugin().getWaitingList().remove(player.getUniqueId().toString() + "." + npc);
                player.sendMessage(CitizensCMD.getPlugin().getLang().getMessage(Path.PAY_CANCELED));

            }
        }

        List<String> permissions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        for (String list : CitizensCMD.getPlugin().getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT)) {
            Pattern pattern = Pattern.compile("\\[([^]]*)] ([^]]*)");
            Matcher matcher = pattern.matcher(list);
            if (matcher.find()) {
                permissions.add(matcher.group(1));
                String command = matcher.group(2);
                if (command.contains("%p%"))
                    command = command.replace("%p%", player.getName());
                if (command.contains("%player%"))
                    command = command.replace("%player%", player.getName());
                if (CitizensCMD.getPlugin().papiEnabled())
                    commands.add(PlaceholderAPI.setPlaceholders((OfflinePlayer) player, command));
                else
                    commands.add(command);
            }
        }

        if (permissions.size() != commands.size()) return;

        for (int i = 0; i < permissions.size(); i++) {
            switch (permissions.get(i).toLowerCase()) {

                case "console":
                    CitizensCMD.getPlugin().getServer().dispatchCommand(CitizensCMD.getPlugin().getServer().getConsoleSender(), commands.get(i));
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
                        String tmpStr = commands.get(i).replace("{display}", CitizensCMD.getPlugin().getLang().getMessage(Path.MESSAGE_DISPLAY));
                        finalMessage = tmpStr.replace("{name}", event.getNPC().getFullName());
                    } else
                        finalMessage = commands.get(i);
                    player.sendMessage(color(finalMessage));
                    break;

                default:
                    CitizensCMD.getPlugin().getPermissionsManager().setPermission(player, permissions.get(i));
                    player.chat("/" + commands.get(i));
                    CitizensCMD.getPlugin().getPermissionsManager().unsetPermission(player, permissions.get(i));
            }
        }

        if (!player.hasPermission("citizenscmd.bypass") || CitizensCMD.getPlugin().getDataHandler().getNPCCooldown(npc) != 0)
            CitizensCMD.getPlugin().getCooldownHandler().addInteraction(npc, player.getUniqueId().toString(), System.nanoTime());
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onRemoveNPC(NPCRemoveEvent event) {
        CitizensCMD.getPlugin().getDataHandler().removeNPCData(event.getNPC().getId());
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
        player.sendPluginMessage(CitizensCMD.getPlugin(), "BungeeCord", byteArrayOutputStream.toByteArray());
    }

}
