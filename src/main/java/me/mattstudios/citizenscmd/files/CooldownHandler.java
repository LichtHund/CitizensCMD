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
import me.mattstudios.citizenscmd.utility.Util;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static me.mattstudios.citizenscmd.utility.Util.color;
import static me.mattstudios.citizenscmd.utility.Util.info;

public class CooldownHandler {

    private CitizensCMD plugin;
    private File cooldownsFile;
    private File dir;

    private FileConfiguration cooldownsConfigurator;

    private final Map<String, Long> cooldownData = new ConcurrentHashMap<>();

    public CooldownHandler(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates the basic of the class and starts the HashMap
     */
    public void initialize() {
        File pluginFolder = plugin.getDataFolder();
        dir = new File(pluginFolder + "/data");
        cooldownsFile = new File(dir.getPath(), "cooldowns.yml");
        cooldownsConfigurator = new YamlConfiguration();

        createBasics();
        cacheData();
    }


    /**
     * Creates files and folders
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createBasics() {
        if (!dir.exists()) dir.mkdirs();

        if (!cooldownsFile.exists()) {
            try {
                cooldownsFile.createNewFile();
            } catch (IOException e) {
                info(color("&cError creating cooldowns file.."));
            }
        }
    }

    /**
     * Puts the data from the file in cache
     */
    private void cacheData() {
        try {
            cooldownsConfigurator.load(cooldownsFile);

            if (!cooldownsConfigurator.contains("cooldown-data")) return;

            Map<String, Integer> cachedDataFromSaves = plugin.getDataHandler().getCachedCooldownByID();

            for (String parent : Objects.requireNonNull(cooldownsConfigurator.getConfigurationSection("cooldown-data")).getKeys(false)) {
                for (String child : Objects.requireNonNull(cooldownsConfigurator.getConfigurationSection("cooldown-data." + parent)).getKeys(false)) {
                    for (String npc : cachedDataFromSaves.keySet()) {
                        if (npc.equalsIgnoreCase(parent) && ((Util.getSecondsDifference(cooldownsConfigurator.getLong("cooldown-data." + parent + "." + child)) < cachedDataFromSaves.get(npc)) || cachedDataFromSaves.get(npc) == -1))
                            cooldownData.put("cooldown-data." + parent + "." + child, cooldownsConfigurator.getLong("cooldown-data." + parent + "." + child));

                    }
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves cached data to file
     */
    public void saveToFile() {
        try {
            createBasics();
            cooldownsConfigurator.load(cooldownsFile);

            cooldownsConfigurator.set("cooldown-data", null);

            for (String path : cooldownData.keySet()) {
                cooldownsConfigurator.set(path, cooldownData.get(path));
            }

            cooldownsConfigurator.save(cooldownsFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an interaction, when ever a player clicks on the NPC
     *
     * @param npc  the NPC id
     * @param uuid The player UUID
     * @param time the time it was clicked from System.nanoTime();
     */
    public void addInteraction(int npc, String uuid, long time) {
        if (cooldownData.containsKey("cooldown-data.npc-" + npc + "." + uuid))
            cooldownData.replace("cooldown-data.npc-" + npc + "." + uuid, time);
        else
            cooldownData.put("cooldown-data.npc-" + npc + "." + uuid, time);
    }

    /**
     * Get's the time left on cooldown
     *
     * @param npc  The NPC id to check
     * @param uuid The player uuid
     * @return returns in seconds the time left
     */
    public long getTimeLeft(int npc, String uuid) {
        return plugin.getDataHandler().getNPCCooldown(npc) - Util.getSecondsDifference(cooldownData.get("cooldown-data.npc-" + npc + "." + uuid));
    }

    /**
     * Checks if the NPC is on cooldown or not
     *
     * @param npc  The NPC id
     * @param uuid The player uuid
     * @return returns true if on cooldown and false if not
     */
    public boolean onCooldown(int npc, String uuid) {
        if (cooldownData.containsKey("cooldown-data.npc-" + npc + "." + uuid)) {
            if (plugin.getDataHandler().getNPCCooldown(npc) == -1)
                return true;
            else
                return Util.getSecondsDifference(cooldownData.get("cooldown-data.npc-" + npc + "." + uuid)) < plugin.getDataHandler().getNPCCooldown(npc);
        }
        return false;
    }

    /**
     * Reloads the cooldowns
     */
    public void reload() {
        saveToFile();
    }

}
