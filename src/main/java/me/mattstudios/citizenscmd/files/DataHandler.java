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

package me.mattstudios.citizenscmd.files;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.EnumTypes;
import me.mattstudios.citizenscmd.utility.Messages;
import me.mattstudios.citizenscmd.utility.Util;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.color;
import static me.mattstudios.citizenscmd.utility.Util.info;

@SuppressWarnings("unchecked")
public class DataHandler {

    private final CitizensCMD plugin;
    private static File savesFile;
    private static File dir;

    private static FileConfiguration dataConfigurator;

    private final Map<String, Object> data = new ConcurrentHashMap<>();

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

        createBasics();
        cacheData();
    }

    /**
     * Creates files and folders
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                dataConfigurator.load(savesFile);

                if (!dataConfigurator.contains("npc-data")) return;

                for (String parent : Objects.requireNonNull(dataConfigurator.getConfigurationSection("npc-data")).getKeys(false)) {
                    for (String child : Objects.requireNonNull(dataConfigurator.getConfigurationSection("npc-data." + parent)).getKeys(false)) {
                        switch (child.toLowerCase()) {
                            case "permission":
                                data.put("npc-data." + parent + "." + child, dataConfigurator.getString("npc-data." + parent + "." + child));
                                break;

                            case "cooldown":
                                data.put("npc-data." + parent + "." + child, dataConfigurator.getInt("npc-data." + parent + "." + child));
                                break;

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

        });
    }

    /**
     * Adds a new command to the NPC
     *
     * @param npc        The NPC id
     * @param permission The permission to access the command
     * @param command    The command
     * @param sender     The player who run the command
     * @param left       If the command should be added to the left or right click
     */
    public void addCommand(int npc, String permission, String command, Audience sender, boolean left) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> commandList = data.containsKey("npc-data.npc-" + npc + ".right-click-commands") ? (List<String>) data.get("npc-data.npc-" + npc + ".right-click-commands") : new ArrayList<>();
                List<String> commandListLeft = data.containsKey("npc-data.npc-" + npc + ".left-click-commands") ? (List<String>) data.get("npc-data.npc-" + npc + ".left-click-commands") : new ArrayList<>();

                if (!data.containsKey("npc-data.npc-" + npc + ".cooldown")) {
                    data.put("npc-data.npc-" + npc + ".cooldown", Util.getDefaultCooldown(plugin));
                    dataConfigurator.set("npc-data.npc-" + npc + ".cooldown", Util.getDefaultCooldown(plugin));
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

                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.NPC_ADDED));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.NPC_ADD_FAIL));
            }
        });
    }

    /**
     * Adds a new command to the NPC
     *
     * @param npc        The NPC id
     * @param permission The permission to access the command
     * @param command    The command
     * @param left       If the command should be added to the left or right click
     */
    public void addCommand(int npc, String permission, String command, boolean left) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> commandList = data.containsKey("npc-data.npc-" + npc + ".right-click-commands") ? (List<String>) data.get("npc-data.npc-" + npc + ".right-click-commands") : new ArrayList<>();
                List<String> commandListLeft = data.containsKey("npc-data.npc-" + npc + ".left-click-commands") ? (List<String>) data.get("npc-data.npc-" + npc + ".left-click-commands") : new ArrayList<>();

                if (!data.containsKey("npc-data.npc-" + npc + ".cooldown")) {
                    data.put("npc-data.npc-" + npc + ".cooldown", Util.getDefaultCooldown(plugin));
                    dataConfigurator.set("npc-data.npc-" + npc + ".cooldown", Util.getDefaultCooldown(plugin));
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

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException ignored) {
            }
        });
    }

    /**
     * Sets the cooldown of the NPC command
     *
     * @param npc      The NPC id
     * @param cooldown The cooldown in seconds to be added
     * @param sender   The player who run the command
     */
    public void setCooldown(int npc, int cooldown, Audience sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                dataConfigurator.set("npc-data.npc-" + npc + ".cooldown", cooldown);

                data.replace("npc-data.npc-" + npc + ".cooldown", cooldown);

                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.NPC_COOLDOWN_SET));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.NPC_COOLDOWN_SET_ERROR));
            }
        });
    }

    /**
     * Sets the price of the NPC command
     *
     * @param npc    The NPC id
     * @param price  The price in seconds to be added
     * @param sender The player who run the command
     */
    public void setPrice(int npc, double price, Audience sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                dataConfigurator.set("npc-data.npc-" + npc + ".price", price);

                data.replace("npc-data.npc-" + npc + ".price", price);

                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.NPC_PRICE_SET));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets the permission of the NPC
     *
     * @param npc        The NPC id
     * @param permission The permission to be added
     * @param sender     The player who run the command
     */
    public void setCustomPermission(int npc, String permission, Audience sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                dataConfigurator.set("npc-data.npc-" + npc + ".permission", permission);

                data.replace("npc-data.npc-" + npc + ".permission", permission);

                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.PERMISSION_SET));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Remove the permission of the NPC
     *
     * @param npc    The NPC id
     * @param sender The player who run the command
     */
    public void removeCustomPermission(int npc, Audience sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                if (dataConfigurator.contains("npc-data.npc-" + npc + ".permission"))
                    dataConfigurator.set("npc-data.npc-" + npc + ".permission", null);

                data.remove("npc-data.npc-" + npc + ".permission");

                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.PERMISSION_REMOVED));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });
    }

    public String getCustomPermission(int npc) {
        return (String) data.get("npc-data.npc-" + npc + ".permission");
    }

    public boolean hasCustomPermission(int npc) {
        return data.containsKey("npc-data.npc-" + npc + ".permission");
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
        String[] commandSet = new String[commandList.size()];
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
        String key = "npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands";
        if (data.containsKey(key))
            return ((List<String>) data.get(key)).isEmpty();
        return true;
    }

    /**
     * Checks if there is NPC data for the ID.
     *
     * @param npc The NPC ID.
     * @return True or false depending if it has or not.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasNPCData(int npc) {
        for (String key : data.keySet()) {
            if (key.contains("npc-" + npc)) return true;
        }
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
     * @param sender    The player to send the message to
     */
    public void removeCommand(int npc, int commandID, EnumTypes.ClickType click, Audience sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> commands = getClickCommandsData(npc, click);

                commands.remove(commandID - 1);

                String key = "npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands";
                data.replace(key, commands);
                dataConfigurator.set(key, commands);

                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.REMOVED_COMMAND));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Edits a command or permission from the NPC
     *
     * @param npc       The NPC id
     * @param commandID The command id
     * @param click     The click type either, left or right
     * @param type      The type to edit, either CMD or PERM
     * @param newValue  the new value for either the command or the permission
     * @param sender    The player to send messages
     */
    public void edit(int npc, int commandID, EnumTypes.ClickType click, EnumTypes.EditType type, String newValue, Audience sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                List<String> commandsData = getClickCommandsData(npc, click);

                final String typeText;
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
                    default:
                        typeText = "";
                }

                String key = "npc-data.npc-" + npc + "." + click.toString().toLowerCase() + "-click-commands";
                data.replace(key, commandsData);
                dataConfigurator.set(key, commandsData);

                sender.sendMessage(HEADER);
                sender.sendMessage(plugin.getLang().getMessage(Messages.EDITED_COMMAND, "{type}", typeText));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Removes the NPC data from the file
     *
     * @param npc the NPC id
     */
    public void removeNPCData(int npc) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                if (dataConfigurator.contains("npc-data.npc-" + npc)) dataConfigurator.set("npc-data.npc-" + npc, null);

                data.keySet().removeIf(key -> key.contains("npc-data.npc-" + npc));

                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Clones the NPC data to the new NPC when /npc copy.
     *
     * @param npc      The NPC ID to copy.
     * @param npcClone The ID of the new NPC.
     */
    public void cloneData(int npc, int npcClone) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                createBasics();
                dataConfigurator.load(savesFile);

                Map<String, Object> newNpcData = new HashMap<>();

                for (String key : data.keySet()) {
                    if (key.contains("npc-" + npc)) {
                        String newKey = key.replace("npc-" + npc, "npc-" + npcClone);
                        newNpcData.put(newKey, data.get(key));
                        dataConfigurator.set(newKey, data.get(key));
                    }
                }

                data.putAll(newNpcData);
                dataConfigurator.save(savesFile);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Gets the cached cooldowns of all NPCs to cache only the cooldowns that matter
     *
     * @return The cached cooldowns
     */
    Map<String, Integer> getCachedCooldownByID() {
        Map<String, Integer> cachedData = new HashMap<>();

        for (String key : data.keySet()) {
            String[] components = key.split("\\.");
            if (components[2].equalsIgnoreCase("cooldown"))
                cachedData.put(components[1], (Integer) data.get(key));
        }
        return cachedData;
    }

    public void reload() {
        initialize();
    }
}
