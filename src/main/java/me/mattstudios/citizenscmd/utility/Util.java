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

import me.mattstudios.citizenscmd.CitizensCMD;
import net.citizensnpcs.api.CitizensAPI;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.mattstudios.utils.MessageUtils.color;
import static me.mattstudios.utils.MessageUtils.info;

public class Util {

    private Util() {}

    /**
     * String with CitizensCMD default header and tag
     */
    public static final String HEADER = "&c&m-&6&m-&e&m-&a&m-&b&m-&3&l CitizensCMD &b&m-&a&m-&e&m-&6&m-&c&m-";
    public static final String TAG = "&f[&3Citizens&cCMD&f]&r ";

    /**
     * Checks if player has or not selected an NPC
     *
     * @param sender The player to check if it has any NPC selected or not
     * @return Returns true if has an NPC selected and false if not
     */
    public static boolean npcNotSelected(final CitizensCMD plugin, final CommandSender sender) {
        if (CitizensAPI.getDefaultNPCSelector().getSelected(sender) != null) return false;

        sender.sendMessage(color(HEADER));
        sender.sendMessage(plugin.getLang().getMessage(Messages.NO_NPC));
        return true;
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

    /**
     * Gets the NPC id
     *
     * @param sender To get the id of the NPC the player has selected
     * @return Returns the id of the NPC
     */
    public static int getSelectedNpcId(final CommandSender sender) {
        return CitizensAPI.getDefaultNPCSelector().getSelected(sender).getId();
    }

    public static void setUpMetrics(Metrics metrics, FileConfiguration config) {
        metrics.addCustomChart(new Metrics.SimplePie("lang", () -> {
            switch (Objects.requireNonNull(config.getString("lang", "en")).toLowerCase()) {
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

        metrics.addCustomChart(new Metrics.SimplePie("cooldown_display", () -> {
            switch (Objects.requireNonNull(config.getString("cooldown-time-display", "MEDIUM")).toLowerCase()) {
                case "FULL":
                    return "Full";

                case "SMALL":
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
        return plugin.getConfig().getInt("default-cooldown");
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
        info(color(TAG + "&cCitizens &7is needed for this plugin to work!"));
        info(color(TAG + "&cCitizens.jar &7is not installed on the server!"));
        info(color(TAG + "&cDisabling CitizensCMD..."));
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
        messagesString[0] = plugin.getLang().getMessage(Messages.SECONDS);
        messagesString[1] = plugin.getLang().getMessage(Messages.MINUTES);
        messagesString[2] = plugin.getLang().getMessage(Messages.HOURS);
        messagesString[3] = plugin.getLang().getMessage(Messages.DAYS);

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
