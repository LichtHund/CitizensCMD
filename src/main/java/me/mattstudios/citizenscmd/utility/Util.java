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

import me.clip.placeholderapi.PlaceholderAPI;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.paths.Path;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.mattstudios.utils.MessageUtils.color;
import static me.mattstudios.utils.MessageUtils.info;
import static org.bukkit.Bukkit.getScheduler;

public class Util {

    /**
     * String with CitizensCMD default header and tag
     */
    public static final String HEADER = "&c&m-&6&m-&e&m-&a&m-&b&m-&3&l CitizensCMD &b&m-&a&m-&e&m-&6&m-&c&m-";
    public static final String TAG = "&f[&3Citizens&cCMD&f]&r ";

    /**
     * Checks if player has or not selected an NPC
     *
     * @param player The player to check if it has any NPC selected or not
     * @return Returns true if has an NPC selected and false if not
     */
    public static boolean npcNotSelected(CitizensCMD plugin, Player player) {
        if (CitizensAPI.getDefaultNPCSelector().getSelected(player) != null) return false;

        player.sendMessage(color(HEADER));
        player.sendMessage(plugin.getLang().getMessage(Path.NO_NPC));
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
     * @param player To get the id of the NPC the player has selected
     * @return Returns the id of the NPC
     */
    public static int getSelectedNpcId(Player player) {
        return CitizensAPI.getDefaultNPCSelector().getSelected(player).getId();
    }

    /**
     * Checks whether or not it should check for updates
     *
     * @return Returns true if CheckUpdates is true on the config and false if not
     */
    public static boolean upCheck(CitizensCMD plugin) {
        return plugin.getConfig().getBoolean("check-updates");
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
     * Gets arguments from each command for the tab completion
     *
     * @return Returns 2d string array with arguments for tab completion
     */
    public static String[][] getTabCompleteArgs(CitizensCMD plugin, String subCMD, Player player) {
        String[][] argComplete = new String[5][];

        switch (subCMD) {
            case "add":
                argComplete[0] = new String[]{"console", "none", "permission", "server", "message", "sound"};
                argComplete[1] = getSoundsList();
                argComplete[2] = new String[]{"1", "0.5"};
                argComplete[3] = new String[]{"1", "0.5"};
                break;

            case "remove":
                argComplete[0] = new String[]{"left", "right"};
                argComplete[1] = plugin.getDataHandler().getCompleteCommandsNumbers(getSelectedNpcId(player), EnumTypes.ClickType.LEFT);
                argComplete[2] = plugin.getDataHandler().getCompleteCommandsNumbers(getSelectedNpcId(player), EnumTypes.ClickType.RIGHT);
                break;

            case "cooldown":
                argComplete[0] = new String[]{"1", "2", "5", "10"};
                break;

            case "permission":
                argComplete[0] = new String[]{"set", "remove"};
                break;

            case "edit":
                argComplete[0] = new String[]{"perm", "cmd"};
                argComplete[1] = new String[]{"left", "right"};
                argComplete[2] = plugin.getDataHandler().getCompleteCommandsNumbers(getSelectedNpcId(player), EnumTypes.ClickType.LEFT);
                argComplete[3] = plugin.getDataHandler().getCompleteCommandsNumbers(getSelectedNpcId(player), EnumTypes.ClickType.RIGHT);
                argComplete[4] = new String[]{"console", "none", "permission", "server", "message"};
                break;
        }
        return argComplete;
    }

    private static String[] getSoundsList() {
        Sound[] sounds = Sound.values();
        String[] soundString = new String[sounds.length];

        for (int i = 0; i < sounds.length; i++) {
            soundString[i] = sounds[i].name();
        }

        return soundString;
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
     * Checks for old config and renames it
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void checkOldConfig(CitizensCMD plugin) {
        File configFile;
        File configFileNew;
        FileConfiguration configConf;

        boolean isNew = true;

        boolean[] contains = new boolean[5];
        for (int i = 0; i < contains.length; i++) {
            contains[i] = false;
        }

        try {
            configFile = new File(plugin.getDataFolder(), "config.yml");
            configFileNew = new File(plugin.getDataFolder(), "config_old.yml");
            configConf = new YamlConfiguration();

            if (configFile.exists()) {
                configConf.load(configFile);

                if (configConf.contains("check-updates")) contains[0] = true;
                if (configConf.contains("lang")) contains[1] = true;
                if (configConf.contains("default-cooldown")) contains[2] = true;
                if (configConf.contains("shift-confirm")) contains[3] = true;
                if (configConf.contains("cooldown-time-display")) contains[4] = true;
            }

            for (boolean bool : contains) {
                if (!bool) {
                    isNew = false;
                }
            }

            if (!isNew) {
                configFile.renameTo(configFileNew);
            }

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
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
    private static void changeServer(CitizensCMD plugin, Player player, String server) {
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
     * Does the main commands for both left and right clicks.
     *
     * @param plugin    The CitizensCMD plugin.
     * @param npc       The NPC to get ID.
     * @param player    The player using the NPC.
     * @param clickType The type of click, either left or right.
     */
    public static void doCommands(CitizensCMD plugin, NPC npc, Player player, EnumTypes.ClickType clickType) {
        List<String> permissions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        for (String list : plugin.getDataHandler().getClickCommandsData(npc.getId(), clickType)) {
            Pattern pattern = Pattern.compile("\\[([^]]*)] (.*)");
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

            double delay = 0;

            if (permissions.get(i).contains("(")) {
                Pattern pattern = Pattern.compile("(.*)\\(([^]]*)\\)");
                Matcher matcher = pattern.matcher(permissions.get(i));
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
                    getScheduler().runTaskLater(plugin, () -> player.chat("/" + commands.get(finalI)), (int) delay * 20);
                    break;

                case "server":
                    getScheduler().runTaskLater(plugin, () -> changeServer(plugin, player, commands.get(finalI)), (int) delay * 20);
                    break;

                case "message":
                    getScheduler().runTaskLater(plugin, () -> {
                        String finalMessage;
                        if (commands.get(finalI).contains("{display}")) {
                            String tmpStr = commands.get(finalI).replace("{display}", plugin.getLang().getMessage(Path.MESSAGE_DISPLAY));
                            finalMessage = tmpStr.replace("{name}", npc.getFullName());
                        } else
                            finalMessage = commands.get(finalI);
                        player.sendMessage(color(finalMessage));
                    }, (int) delay * 20);
                    break;

                case "sound":
                    getScheduler().runTaskLater(plugin, () -> {
                        Pattern pattern = Pattern.compile("(\\w+)\\s([\\d.]+)\\s([\\d.]+)");
                        Matcher matcher = pattern.matcher(commands.get(finalI));
                        if (matcher.find() && soundExists(matcher.group(1))) {
                            player.playSound(player.getLocation(), Sound.valueOf(matcher.group(1)), Float.parseFloat(matcher.group(2)), Float.parseFloat(matcher.group(3)));
                        }
                    }, (int) delay * 20);
                    break;

                default:
                    getScheduler().runTaskLater(plugin, () -> {
                        plugin.getPermissionsManager().setPermission(player, permissions.get(finalI));
                        player.chat("/" + commands.get(finalI));
                        plugin.getPermissionsManager().unsetPermission(player, permissions.get(finalI));
                    }, (int) delay * 20);
                    break;
            }
        }
    }

    private static boolean soundExists(String soundName) {
        for (Sound sound : Sound.values()) {
            if (sound.name().equalsIgnoreCase(soundName)) return true;
        }

        return false;
    }
}
