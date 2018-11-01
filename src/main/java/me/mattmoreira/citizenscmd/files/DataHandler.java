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

package me.mattmoreira.citizenscmd.files;

import me.mattmoreira.citizenscmd.CitizensCMD;
import me.mattmoreira.citizenscmd.utility.EnumTypes;
import me.mattmoreira.citizenscmd.utility.Path;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.mattmoreira.citizenscmd.utility.Util.*;

public class DataHandler {

    private CitizensCMD plugin;
    private static File savesFile;
    private static File dir;

    private static FileConfiguration dataConfigurator;

    private HashMap<String, Object> data;

    public DataHandler(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates file and folder
     */
    public void initialize() {
        File pluginFolder = plugin.getDataFolder();
        dir = new File(pluginFolder + "/data");
        savesFile = new File(dir.getPath(), "saves.yml");
        dataConfigurator = new YamlConfiguration();

        data = new HashMap<>();

        createBasics();
        cacheData();
    }

    /**
     * Creates files and folders
     */
    private void createBasics() {
        if (!dir.exists()) dir.mkdirs();

        if (!savesFile.exists()) {
            try {
                savesFile.createNewFile();
            } catch (IOException e) {
                info(color("&cError creating saves file.."));
            }
        }
    }

    /**
     * Caches the data from the file
     */
    private void cacheData() {
        new Thread(() -> {

            try {
                dataConfigurator.load(savesFile);

                if (!dataConfigurator.contains("npc-data")) return;

                for (String parent : dataConfigurator.getConfigurationSection("npc-data").getKeys(false)) {
                    for (String child : dataConfigurator.getConfigurationSection("npc-data." + parent).getKeys(false)) {
                        switch (child.toLowerCase()) {
                            case "cooldown":
                                data.put("npc-data." + parent + "." + child, dataConfigurator.getInt("npc-data." + parent + "." + child));
                                break;

                            case "sound":
                            case "right-click-commands":
                            case "left-click-commands":
                                data.put("npc-data." + parent + "." + child, dataConfigurator.getStringList("npc-data." + parent + "." + child));
                                break;

                            case "price":
                                data.put("npc-data." + parent + "." + child, dataConfigurator.getDouble("npc-data." + parent + "." + child));
                                break;
                        }

                    }
                }

            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

        }).start();
    }

    /**
     * Clears the data from the cache
     */
    private void clearCache() {
        data.clear();
    }

    /**
     * Adds a new command to the NPC
     *
     * @param npc        The NPC id
     * @param permission The permission to access the command
     * @param command    The command
     * @param player     The player who run the command
     * @param left       If the command should be added to the left or right click
     */
    public void addCommand(int npc, String permission, String command, Player player, boolean left) {
        new Thread(() -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> commandList = data.containsKey("npc-data.npc-" + npc + ".right-click-commands") ? (List<String>) data.get("npc-data.npc-" + npc + ".right-click-commands") : new ArrayList<>();
                List<String> commandListLeft = data.containsKey("npc-data.npc-" + npc + ".left-click-commands") ? (List<String>) data.get("npc-data.npc-" + npc + ".left-click-commands") : new ArrayList<>();

                if (!data.containsKey("npc-data.npc-" + npc + ".cooldown")) {
                    data.put("npc-data.npc-" + npc + ".cooldown", getDefaultCooldown(plugin));
                    dataConfigurator.set("npc-data.npc-" + npc + ".cooldown", getDefaultCooldown(plugin));
                }

                if (left) commandListLeft.add("[" + permission + "] " + command);
                else commandList.add("[" + permission + "] " + command);

                if (data.containsKey("npc-data.npc-" + npc + ".right-click-commands"))
                    data.replace("npc-data.npc-" + npc + ".right-click-commands", commandList);
                else
                    data.put("npc-data.npc-" + npc + ".right-click-commands", commandList);
                dataConfigurator.set("npc-data.npc-" + npc + ".right-click-commands", commandList);

                if (data.containsKey("npc-data.npc-" + npc + ".left-click-commands"))
                    data.replace("npc-data.npc-" + npc + ".left-click-commands", commandListLeft);
                else
                    data.put("npc-data.npc-" + npc + ".left-click-commands", commandListLeft);
                dataConfigurator.set("npc-data.npc-" + npc + ".left-click-commands", commandListLeft);

                if (!data.containsKey("npc-data.npc-" + npc + ".price")) {
                    data.put("npc-data.npc-" + npc + ".price", 0);
                    dataConfigurator.set("npc-data.npc-" + npc + ".price", 0);
                }

                if (!data.containsKey("npc-data.npc-" + npc + ".sound")) {
                    data.put("npc-data.npc-" + npc + ".sound", new ArrayList<>());
                    dataConfigurator.set("npc-data.npc-" + npc + ".sound", new ArrayList<>());
                }

                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.NPC_ADDED));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.NPC_ADD_FAIL));
            }
        }).start();
    }

    /**
     * Sets the cooldown of the NPC command
     *
     * @param npc      The NPC id
     * @param cooldown The cooldown in seconds to be added
     * @param player   The player who run the command
     */
    public void setCooldown(int npc, int cooldown, Player player) {
        new Thread(() -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                dataConfigurator.set("npc-data.npc-" + npc + ".cooldown", cooldown);

                data.replace("npc-data.npc-" + npc + ".cooldown", cooldown);

                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.NPC_COOLDOWN_SET));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.NPC_COOLDOWN_SET_ERROR));
            }
        }).start();
    }

    /**
     * Sets the price of the NPC command
     *
     * @param npc    The NPC id
     * @param price  The price in seconds to be added
     * @param player The player who run the command
     */
    public void setPrice(int npc, double price, Player player) {
        new Thread(() -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                dataConfigurator.set("npc-data.npc-" + npc + ".price", price);

                data.replace("npc-data.npc-" + npc + ".price", price);

                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.NPC_PRICE_SET));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Sets the sound of the NPC command
     *
     * @param npc    The NPC id
     * @param sound  The sound to play
     * @param volume the volume
     * @param pitch  the pitch
     * @param player The player who run the command
     */
    public void setSound(int npc, Sound sound, float volume, float pitch, Player player) {
        new Thread(() -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> soundProperties = new ArrayList<>();

                soundProperties.add(sound.name());
                soundProperties.add(String.valueOf(volume));
                soundProperties.add(String.valueOf(pitch));

                dataConfigurator.set("npc-data.npc-" + npc + ".sound", soundProperties);

                if (data.containsKey("npc-data.npc-" + npc + ".sound"))
                    data.replace("npc-data.npc-" + npc + ".sound", soundProperties);
                else
                    data.put("npc-data.npc-" + npc + ".sound", soundProperties);

                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.SOUND_ADDED));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Gets the click commands from the saves.yml
     *
     * @param npc   The NPC id
     * @param click The string with left or right
     * @return Returns String list with commands
     */
    public List<String> getClickCommandsData(int npc, EnumTypes.ClickType click) {
        return (List<String>) data.get("npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands");
    }

    /**
     * Gets the number of commands to tab complete
     *
     * @param npc   The NPC id
     * @param click The type of click, either left or right
     * @return Returns arrays with strings to show on tab completion event
     */
    public String[] getCompleteCommandsNumbers(int npc, EnumTypes.ClickType click) {
        List<String> commandList = (List<String>) data.get("npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands");
        String commandSet[] = new String[commandList.size()];
        for (int i = 0; i < commandList.size(); i++)
            commandSet[i] = "" + (i + 1);
        return commandSet;
    }

    /**
     * Checks if the npc has any command in it
     *
     * @param npc   The NPC id
     * @param click The type of click either left or right
     * @return Returns true if has and false if not
     */
    public boolean hasNoCommands(int npc, EnumTypes.ClickType click) {
        if (data.containsKey("npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands"))
            return ((List<String>) data.get("npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands")).isEmpty();
        return true;
    }

    public boolean hasSound(int npc) {
        if (data.containsKey("npc-data.npc-" + npc + ".sound"))
            return !(((List<String>) data.get("npc-data.npc-" + npc + ".sound")).isEmpty());
        return false;
    }

    /**
     * Gets the cooldown of the NPC
     *
     * @param npc the NPC id
     * @return Returns the NPC cooldown
     */
    public int getNPCCooldown(int npc) {
        return data.containsKey("npc-data.npc-" + npc + ".cooldown") ? (int) data.get("npc-data.npc-" + npc + ".cooldown") : 0;
    }

    public List<String> getNPCSound(int npc) {
        return (List<String>) data.get("npc-data.npc-" + npc + ".sound");
    }

    /**
     * Gets the price of the NPC
     *
     * @param npc The NPC id
     * @return Returns the NPC price
     */
    public double getPrice(int npc) {
        return data.containsKey("npc-data.npc-" + npc + ".price") ? Double.parseDouble(data.get("npc-data.npc-" + npc + ".price").toString()) : 0.0;
    }

    /**
     * Removes a command from an NPC
     *
     * @param npc       The NPC id
     * @param commandID The command id
     * @param click     The click type, either left or right
     * @param player    The player to send the message to
     */
    public void removeCommand(int npc, int commandID, EnumTypes.ClickType click, Player player) {
        new Thread(() -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> commands = getClickCommandsData(npc, click);

                commands.remove(commandID - 1);

                data.replace("npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands", commands);
                dataConfigurator.set("npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands", commands);

                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.REMOVED_COMMAND));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void removeSound(int npc, Player player) {
        new Thread(() -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> soundProperties = new ArrayList<>();

                data.replace("npc-data.npc-" + npc + ".sound", soundProperties);
                dataConfigurator.set("npc-data.npc-" + npc + ".sound", soundProperties);

                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.SOUND_REMOVED));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Edits a command or permission from the NPC
     *
     * @param npc       The NPC id
     * @param commandID The command id
     * @param click     The click type either, left or right
     * @param type      The type to edit, either CMD or PERM
     * @param newValue  the new value for either the command or the permission
     * @param player    The player to send messages
     */
    public void edit(int npc, int commandID, EnumTypes.ClickType click, EnumTypes.EditType type, String newValue, Player player) {
        new Thread(() -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> commandsData = getClickCommandsData(npc, click);

                String typeText = "";

                switch (type) {
                    case CMD:
                        String tempCommand = commandsData.get(commandID - 1);
                        tempCommand = tempCommand.replaceAll(" ([^]]*)", " " + newValue);
                        commandsData.set(commandID - 1, tempCommand);
                        typeText = "CMD";
                        break;
                    case PERM:
                        String tempPerm = commandsData.get(commandID - 1);
                        tempPerm = tempPerm.replaceAll("\\[([^]]*)]", "[" + newValue + "]");
                        commandsData.set(commandID - 1, tempPerm);
                        typeText = "PERM";
                        break;
                }

                data.replace("npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands", commandsData);
                dataConfigurator.set("npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands", commandsData);

                player.sendMessage(color(HEADER));
                player.sendMessage(plugin.getLang().getMessage(Path.EDITED_COMMAND).replace("{type}", typeText));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Removes the NPC data from the file
     *
     * @param npc the NPC id
     */
    public void removeNPCData(int npc) {
        new Thread(() -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                if (dataConfigurator.contains("npc-data.npc-" + npc))
                    dataConfigurator.set("npc-data.npc-" + npc, null);

                /*clearCache();
                cacheData();*/

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Gets the cached cooldowns of all NPCs to cache only the cooldowns that matter
     *
     * @return The cached cooldowns
     */
    HashMap<String, Integer> getCachedCooldownByID() {
        HashMap<String, Integer> cachedData = new HashMap<>();

        for (String key : data.keySet()) {
            String components[] = key.split("\\.");
            if (components[2].equalsIgnoreCase("cooldown"))
                cachedData.put(components[1], (Integer) data.get(key));
        }
        return cachedData;
    }

    public void reload() {
        cacheData();
        initialize();
    }
}
