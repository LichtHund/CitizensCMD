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

import com.google.common.primitives.Floats;
import me.clip.placeholderapi.PlaceholderAPI;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.Settings;
import me.mattstudios.citizenscmd.schedulers.ConfirmScheduler;
import me.mattstudios.citizenscmd.utility.EnumTypes;
import me.mattstudios.citizenscmd.utility.Messages;
import me.mattstudios.citizenscmd.utility.Util;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.mattstudios.citizenscmd.utility.Util.LEGACY;
import static me.mattstudios.citizenscmd.utility.Util.MINI;
import static me.mattstudios.citizenscmd.utility.Util.getFormattedTime;
import static org.bukkit.Bukkit.getScheduler;

public class NPCClickListener implements Listener {

    private static final Pattern MAIN_PATTERN = Pattern.compile("\\[([^]]*)] (.*)");
    private static final Pattern PERMISSION_PATTERN = Pattern.compile("(.*)\\(([^]]*)\\)");
    private static final Pattern SOUND_PATTERN = Pattern.compile("(?<sound>\\w+)\\s?(?<volume>[\\d.]+) ?(?<pitch>[\\d.]+)?");

    private final CitizensCMD plugin;

    public NPCClickListener(CitizensCMD plugin) {
        this.plugin = plugin;
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        final Audience audience = plugin.getAudiences().player(player);

        if (!player.hasPermission("citizenscmd.use")) return;

        if (plugin.getDataHandler().hasCustomPermission(npc.getId())) {
            if (!player.hasPermission(plugin.getDataHandler().getCustomPermission(npc.getId()))) return;
        }

        if (!plugin.getWaitingList().containsKey(player.getUniqueId() + "." + npc.getId())) {
            if (!player.hasPermission("citizenscmd.bypass")) {
                if (plugin.getCooldownHandler().onCooldown(npc.getId(), player.getUniqueId().toString())) {
                    final Component cooldownMessage;
                    final String time = getFormattedTime(plugin, plugin.getCooldownHandler().getTimeLeft(npc.getId(), player.getUniqueId().toString()), plugin.getDisplayFormat());
                    if (plugin.getDataHandler().getNPCCooldown(npc.getId()) == -1) {
                        cooldownMessage = plugin.getLang().getMessage(Messages.ONE_TIME_CLICK, "{time}", time);
                    } else {
                        cooldownMessage = plugin.getLang().getMessage(Messages.ON_COOLDOWN, "{time}", time);
                    }

                    audience.sendMessage(cooldownMessage);
                    return;
                }
            }

            if (plugin.getDataHandler().hasNoCommands(npc.getId(), EnumTypes.ClickType.RIGHT)) return;
        }

        double price = plugin.getDataHandler().getPrice(npc.getId());

        if (price > 0.0) {
            if (CitizensCMD.getEconomy() != null) {

                if (!plugin.getWaitingList().containsKey(player.getUniqueId() + "." + npc.getId())) {
                    final Map<String, String> replacements = new HashMap<>();
                    replacements.put("{price}", String.valueOf(price));

                    if (!plugin.isShift()) replacements.put("{shift}", "");
                    else replacements.put("{shift}", "Shift ");

                    final Component messageConfirm = plugin.getLang().getMessage(Messages.PAY_CONFIRM, replacements);

                    audience.sendMessage(messageConfirm);
                    plugin.getWaitingList().put(player.getUniqueId() + "." + npc.getId(), true);
                    new ConfirmScheduler(plugin, player, npc.getId()).runTaskLaterAsynchronously(plugin, 300L);
                    return;
                }

                if (plugin.isShift() && !player.isSneaking()) {
                    return;
                }

                if (CitizensCMD.getEconomy().getBalance(player) < price) {
                    audience.sendMessage(plugin.getLang().getMessage(Messages.PAY_NO_MONEY));
                    return;
                }

                plugin.getWaitingList().remove(player.getUniqueId() + "." + npc.getId());
                CitizensCMD.getEconomy().withdrawPlayer(player, price);
                audience.sendMessage(plugin.getLang().getMessage(Messages.PAY_COMPLETED, "price", String.valueOf(price)));
            }
        }

        doCommands(npc, player, EnumTypes.ClickType.RIGHT);

        if (!player.hasPermission("citizenscmd.bypass") || plugin.getDataHandler().getNPCCooldown(npc.getId()) != 0) {
            plugin.getCooldownHandler().addInteraction(npc.getId(), player.getUniqueId().toString(), System.currentTimeMillis());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClick(NPCLeftClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        final Audience audience = plugin.getAudiences().player(player);

        if (!player.hasPermission("citizenscmd.use")) return;

        if (plugin.getDataHandler().hasCustomPermission(npc.getId())) {
            if (!player.hasPermission(plugin.getDataHandler().getCustomPermission(npc.getId()))) return;
        }

        if (!plugin.getWaitingList().containsKey(player.getUniqueId() + "." + npc.getId())) {
            if (!player.hasPermission("citizenscmd.bypass")) {
                if (plugin.getCooldownHandler().onCooldown(npc.getId(), player.getUniqueId().toString())) {
                    final Component cooldownMessage;
                    final String time = getFormattedTime(plugin, plugin.getCooldownHandler().getTimeLeft(npc.getId(), player.getUniqueId().toString()), plugin.getDisplayFormat());

                    if (plugin.getDataHandler().getNPCCooldown(npc.getId()) == -1) {
                        cooldownMessage = plugin.getLang().getMessage(Messages.ONE_TIME_CLICK, "{time}", time);
                    } else {
                        cooldownMessage = plugin.getLang().getMessage(Messages.ON_COOLDOWN, "{time}", time);
                    }

                    audience.sendMessage(cooldownMessage);
                    return;
                }
            }

            if (plugin.getDataHandler().hasNoCommands(npc.getId(), EnumTypes.ClickType.LEFT)) return;
        }

        double price = plugin.getDataHandler().getPrice(npc.getId());

        if (price > 0.0) {
            if (CitizensCMD.getEconomy() != null) {

                if (!plugin.getWaitingList().containsKey(player.getUniqueId() + "." + npc.getId())) {
                    final Map<String, String> replacements = new HashMap<>();
                    replacements.put("{price}", String.valueOf(price));

                    if (!plugin.isShift()) replacements.put("{shift}", "");
                    else replacements.put("{shift}", "Shift ");

                    final Component messageConfirm = plugin.getLang().getMessage(Messages.PAY_CONFIRM, replacements);

                    audience.sendMessage(messageConfirm);
                    plugin.getWaitingList().put(player.getUniqueId() + "." + npc.getId(), true);
                    new ConfirmScheduler(plugin, player, npc.getId()).runTaskLaterAsynchronously(plugin, 300L);
                    return;
                }

                if (plugin.isShift() && !player.isSneaking()) return;

                plugin.getWaitingList().remove(player.getUniqueId() + "." + npc.getId());
                audience.sendMessage(plugin.getLang().getMessage(Messages.PAY_CANCELED));

            }
        }

        doCommands(npc, player, EnumTypes.ClickType.LEFT);

        if (!player.hasPermission("citizenscmd.bypass") || plugin.getDataHandler().getNPCCooldown(npc.getId()) != 0) {
            plugin.getCooldownHandler().addInteraction(npc.getId(), player.getUniqueId().toString(), System.currentTimeMillis());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRemoveNPC(NPCRemoveEvent event) {
        if (!plugin.getDataHandler().hasNPCData(event.getNPC().getId())) return;

        plugin.getDataHandler().removeNPCData(event.getNPC().getId());
    }

    /**
     * Does the main commands for both left and right clicks.
     *
     * @param npc       The NPC to get ID.
     * @param player    The player using the NPC.
     * @param clickType The type of click, either left or right.
     */
    private void doCommands(NPC npc, Player player, EnumTypes.ClickType clickType) {
        List<String> permissions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        for (String list : plugin.getDataHandler().getClickCommandsData(npc.getId(), clickType)) {
            Matcher matcher = MAIN_PATTERN.matcher(list);
            if (matcher.find()) {

                permissions.add(matcher.group(1));
                String command = matcher.group(2);
                command = command.replace("%p%", player.getName());
                command = command.replace("%player%", player.getName());

                if (plugin.papiEnabled()) {
                    commands.add(PlaceholderAPI.setPlaceholders((OfflinePlayer) player, command));
                } else {
                    commands.add(command);
                }

            }
        }

        if (permissions.size() != commands.size()) return;

        for (int i = 0; i < permissions.size(); i++) {

            double delay = 0;

            if (permissions.get(i).contains("(")) {
                Matcher matcher = PERMISSION_PATTERN.matcher(permissions.get(i));
                if (matcher.find()) {
                    delay = Double.parseDouble(matcher.group(2));
                    String permission = matcher.group(1);
                    permissions.set(i, permission);
                }
            }

            int finalI = i;
            switch (permissions.get(i).toLowerCase()) {
                case "console":
                    getScheduler().runTaskLater(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), commands.get(finalI)), (int) delay * 20);
                    break;

                case "none":
                    getScheduler().runTaskLater(plugin, () -> player.chat("/" + commands.get(finalI)), (int) delay * 20L);
                    break;

                case "server":
                    getScheduler().runTaskLater(plugin, () -> Util.changeServer(plugin, player, commands.get(finalI)), (int) delay * 20L);
                    break;

                case "message":
                    getScheduler().runTaskLater(plugin, () -> {
                        final String finalMessage = commands.get(finalI)
                                .replace("{display}", plugin.getLang().getUncoloredMessage(Messages.MESSAGE_DISPLAY))
                                .replace("{name}", npc.getFullName());

                        final Audience audience = plugin.getAudiences().player(player);

                        if (plugin.getSettings().getProperty(Settings.MINI_MESSAGE)) {
                            audience.sendMessage(MINI.deserialize(finalMessage));
                            return;
                        }

                        audience.sendMessage(LEGACY.deserialize(finalMessage));
                    }, (int) delay * 20L);
                    break;

                case "sound":
                    getScheduler().runTaskLater(plugin, () -> {
                        String sound = commands.get(finalI);
                        final Matcher matcher = SOUND_PATTERN.matcher(sound);

                        float volume = 1f;
                        final float pitch = 1f;

                        if (matcher.find()) {
                            sound = matcher.group("sound");

                            String volumeString = matcher.group("volume");
                            String pitchString = matcher.group("pitch");

                            if (volumeString != null) {
                                final Float nullableVolume = Floats.tryParse(volumeString);
                                if (nullableVolume != null) volume = nullableVolume;
                            }

                            if (pitchString != null) {
                                final Float nullablePitch = Floats.tryParse(pitchString);
                                if (nullablePitch != null) volume = nullablePitch;
                            }
                        }

                        if (!Util.soundExists(sound)) {
                            player.playSound(player.getLocation(), sound, volume, pitch);
                            return;
                        }

                        final Sound bukkitSound = Sound.valueOf(sound);

                        player.playSound(player.getLocation(), bukkitSound, volume, pitch);
                    }, (int) delay * 20L);
                    break;

                default:
                    getScheduler().runTaskLater(plugin, () -> {
                        plugin.getPermissionsManager().setPermission(player, permissions.get(finalI));
                        player.chat("/" + commands.get(finalI));
                        plugin.getPermissionsManager().unsetPermission(player, permissions.get(finalI));
                    }, (int) delay * 20L);
                    break;
            }
        }
    }

}
