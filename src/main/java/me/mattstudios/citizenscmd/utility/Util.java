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

package me.mattstudios.citizenscmd.utility;

import ch.jalu.configme.SettingsManager;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.Settings;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Util {

    private Util() {}

    public static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();
    public static final MiniMessage MINI = MiniMessage.miniMessage();

    /**
     * String with CitizensCMD default header and tag
     */
    public static final Component HEADER = LEGACY.deserialize("&c&m-&6&m-&e&m-&a&m-&b&m-&3&l CitizensCMD &b&m-&a&m-&e&m-&6&m-&c&m-");
    public static final Component TAG = LEGACY.deserialize("&f[&3Citizens&cCMD&f]&r ");

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static List<String> color(final List<String> messages) {
        return messages.stream()
                .map((message) -> ChatColor.translateAlternateColorCodes('&', message))
                .collect(Collectors.toList());
    }

    /**
     * Checks if player has or not selected an NPC
     *
     * @param player The player to check if it has any NPC selected or not
     * @return Returns true if has an NPC selected and false if not
     */
    public static boolean npcNotSelectedNM(Player player) {
        return CitizensAPI.getDefaultNPCSelector().getSelected(player) == null;
    }

    public static void sendNotSelectedMessage(final CitizensCMD plugin, final Audience audience) {
        audience.sendMessage(HEADER);
        audience.sendMessage(plugin.getLang().getMessage(Messages.NO_NPC));
    }

    /**
     * Gets the NPC id
     *
     * @param sender To get the id of the NPC the player has selected
     * @return Returns the id of the NPC
     */
    public static OptionalInt getSelectedNpcId(final CommandSender sender) {
        final NPC npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
        if (npc == null) return OptionalInt.empty();
        return OptionalInt.of(npc.getId());
    }

    public static void setUpMetrics(Metrics metrics, SettingsManager settings) {
        metrics.addCustomChart(new SimplePie("lang", () -> {
            switch (settings.getProperty(Settings.LANG).toLowerCase()) {
                case "en":
                    return "English";

                case "bg":
                    return "Bulgarian";

                case "fr":
                    return "French";

                case "no":
                    return "Norwegian";

                case "pt":
                    return "Portuguese";

                case "Ro":
                    return "Romanian";

                case "ch":
                    return "Chinese";

                default:
                    return "Other";
            }
        }));

        metrics.addCustomChart(new SimplePie("cooldown_display", () -> {
            switch (settings.getProperty(Settings.TIME_DISPLAY).toLowerCase()) {
                case "full":
                    return "Full";

                case "small":
                    return "Small";

                default:
                    return "Medium";
            }
        }));
    }

    /**
     * Gets default cooldown set on the config
     *
     * @return returns the seconds from the config
     */
    public static int getDefaultCooldown(CitizensCMD plugin) {
        return plugin.getSettings().getProperty(Settings.DEFAULT_COOLDOWN);
    }

    /**
     * Gets the difference in seconds between times
     *
     * @param storedTime the stored time to compare
     * @return returns the difference in seconds
     */
    public static long getSecondsDifference(long storedTime) {
        return TimeUnit.SECONDS.convert((System.currentTimeMillis() - storedTime), TimeUnit.MILLISECONDS);
    }

    /**
     * Disables the plugin if Citizens is not present.
     *
     * @param plugin The plugin to disable.
     */
    public static void disablePlugin(CitizensCMD plugin) {
        final Logger logger = plugin.getLogger();
        logger.info(color(TAG + "&cCitizens &7is needed for this plugin to work!"));
        logger.info(color(TAG + "&cCitizens.jar &7is not installed on the server!"));
        logger.info(color(TAG + "&cDisabling CitizensCMD..."));
        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
    }

    /**
     * Bungee cord connection method
     *
     * @param player The player to be sent to the server
     * @param server the server name
     */
    public static void changeServer(CitizensCMD plugin, Player player, String server) {
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

    /**
     * Gets formatted time from seconds
     *
     * @param seconds The time in seconds to be converted
     * @return String with the time like "2d 2h 2m 2s"
     */

    public static String getFormattedTime(CitizensCMD plugin, long seconds, DisplayFormat format) {

        String dayPlural = "";
        String hourPlural = "";
        String minutePlural = "";
        String secondPlural = "";

        TimeUtil timeUtil = new TimeUtil(seconds);

        String[] messagesString = new String[4];
        messagesString[0] = plugin.getLang().getUncoloredMessage(Messages.SECONDS);
        messagesString[1] = plugin.getLang().getUncoloredMessage(Messages.MINUTES);
        messagesString[2] = plugin.getLang().getUncoloredMessage(Messages.HOURS);
        messagesString[3] = plugin.getLang().getUncoloredMessage(Messages.DAYS);

        String[] shorts = new String[4];
        String[] mediums = new String[4];
        String[] fulls = new String[4];

        Pattern pattern = Pattern.compile("\\[([^]]*)], \\[([^]]*)], \\[([^]]*)]");
        for (int i = 0; i < messagesString.length; i++) {
            Matcher matcher = pattern.matcher(messagesString[i]);
            if (matcher.find()) {
                shorts[i] = matcher.group(1);
                mediums[i] = matcher.group(2);
                fulls[i] = matcher.group(3);
            }
        }

        String dayFormat;
        String hourFormat;
        String minuteFormat;
        String secondFormat;

        switch (format) {
            case MEDIUM:
                String[] mediumsAfter = new String[4];
                String[] mediumsPlurals = new String[4];
                Pattern patternMediums = Pattern.compile("([^]]*)\\(([^]]*)\\)");

                for (int i = 0; i < mediums.length; i++) {
                    if (mediums[i].contains("(") && mediums[i].contains(")")) {
                        Matcher matcher = patternMediums.matcher(mediums[i]);
                        if (matcher.find()) {
                            mediumsAfter[i] = matcher.group(1);
                            mediumsPlurals[i] = matcher.group(2);
                        }
                    } else {
                        mediumsAfter[i] = mediums[i];
                        mediumsPlurals[i] = "";
                    }
                }

                dayFormat = " " + mediumsAfter[3];
                dayPlural = mediumsPlurals[3];
                hourFormat = " " + mediumsAfter[2];
                hourPlural = mediumsPlurals[2];
                minuteFormat = " " + mediumsAfter[1];
                minutePlural = mediumsPlurals[1];
                secondFormat = " " + mediumsAfter[0];
                secondPlural = mediumsPlurals[0];
                break;

            case FULL:
                String[] fullsAfter = new String[4];
                String[] fullsPlurals = new String[4];
                Pattern patternFulls = Pattern.compile("([^]]*)\\(([^]]*)\\)");

                for (int i = 0; i < fulls.length; i++) {
                    if (fulls[i].contains("(") && fulls[i].contains(")")) {
                        Matcher matcher = patternFulls.matcher(fulls[i]);
                        if (matcher.find()) {
                            fullsAfter[i] = matcher.group(1);
                            fullsPlurals[i] = matcher.group(2);
                        }
                    } else {
                        fullsAfter[i] = fulls[i];
                        fullsPlurals[i] = "";
                    }
                }

                dayFormat = " " + fullsAfter[3];
                dayPlural = fullsPlurals[3];
                hourFormat = " " + fullsAfter[2];
                hourPlural = fullsPlurals[2];
                minuteFormat = " " + fullsAfter[1];
                minutePlural = fullsPlurals[1];
                secondFormat = " " + fullsAfter[0];
                secondPlural = fullsPlurals[0];
                break;

            default:
                dayFormat = shorts[3];
                hourFormat = shorts[2];
                minuteFormat = shorts[1];
                secondFormat = shorts[0];
                break;
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (timeUtil.getDays() != 0) {
            if (format != DisplayFormat.SHORT) {
                if (timeUtil.getDays() == 1) {
                    stringBuilder.append(timeUtil.getDays()).append(dayFormat).append(" ");
                } else {
                    stringBuilder.append(timeUtil.getDays()).append(dayFormat).append(dayPlural).append(" ");
                }
            } else {
                stringBuilder.append(timeUtil.getDays()).append(dayFormat).append(" ");
            }
        }

        if (timeUtil.getHours() != 0 || timeUtil.getDays() != 0) {
            if (format != DisplayFormat.SHORT) {
                if (timeUtil.getHours() == 1) {
                    stringBuilder.append(timeUtil.getHours()).append(hourFormat).append(" ");
                } else {
                    stringBuilder.append(timeUtil.getHours()).append(hourFormat).append(hourPlural).append(" ");
                }
            } else {
                stringBuilder.append(timeUtil.getHours()).append(hourFormat).append(" ");
            }
        }

        if (timeUtil.getMinutes() != 0 || timeUtil.getHours() != 0) {
            if (format != DisplayFormat.SHORT) {
                if (timeUtil.getMinutes() == 1) {
                    stringBuilder.append(timeUtil.getMinutes()).append(minuteFormat).append(" ");
                } else {
                    stringBuilder.append(timeUtil.getMinutes()).append(minuteFormat).append(minutePlural).append(" ");
                }
            } else {
                stringBuilder.append(timeUtil.getMinutes()).append(minuteFormat).append(" ");
            }
        }

        if (timeUtil.getSeconds() != 0 || timeUtil.getMinutes() != 0) {
            if (format != DisplayFormat.SHORT) {
                if (timeUtil.getSeconds() == 1) {
                    stringBuilder.append(timeUtil.getSeconds()).append(secondFormat);
                } else {
                    stringBuilder.append(timeUtil.getSeconds()).append(secondFormat).append(secondPlural);
                }
            } else {
                stringBuilder.append(timeUtil.getSeconds()).append(secondFormat);
            }
        }

        return stringBuilder.toString();
    }

    public static boolean soundExists(String soundName) {
        for (Sound sound : Sound.values()) {
            if (sound.name().equalsIgnoreCase(soundName)) return true;
        }

        return false;
    }
}
