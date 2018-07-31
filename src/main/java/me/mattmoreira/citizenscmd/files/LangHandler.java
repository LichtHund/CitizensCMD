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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static me.mattmoreira.citizenscmd.utility.Util.*;

public class LangHandler {

    private String lang;

    private HashMap<String, String> messages;

    public LangHandler(String lang) {
        this.lang = lang;
    }

    /**
     * Send message to the console saying this is the language selected
     */
    public void initialize() {
        switch (lang) {
            case "en":
                info(color(TAG + "&7Using &aEnglish &7messages!"));
                break;

            case "pt":
                info(color(TAG + "&7Usando mensagens em Portugues!"));
                break;

            case "ro":
                info(color(TAG + "&7Folositi mesajele in Limba &aRomana&7!"));
                break;

            case "bg":
                info(color(TAG + "&7Използване на &aбългарски &7език!"));
                break;
        }

        messages = new HashMap<>();
        cacheMessage();
    }

    /**
     * Caches all messages into a HashMap for easier access
     */
    private void cacheMessage() {
        new Thread(() -> {
            File langFile;
            FileConfiguration langConf;

            try {
                langFile = new File(CitizensCMD.getPlugin().getDataFolder(), "lang/" + lang + ".yml");
                langConf = new YamlConfiguration();

                if (!langFile.exists()) CitizensCMD.getPlugin().saveResource("lang/" + lang + ".yml", false);

                langConf.load(langFile);

                for (String parent : langConf.getConfigurationSection("messages").getKeys(false)) {
                    for (String child : langConf.getConfigurationSection("messages." + parent).getKeys(false))
                        messages.put("messages." + parent + "." + child, langConf.getString("messages." + parent + "." + child));
                }

            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Gets message from the lang file
     *
     * @param path String with the path to the message
     * @return Returns String with colored message from file
     */
    public String getMessage(String path) {
        return color(messages.get(path));
    }

    /**
     * Gets message from the lang file without color
     *
     * @param path String with the path to the message
     * @return Returns String with message from file
     */
    public String getUncoloredMessage(String path) {
        return messages.get(path);
    }

    /**
     * Gets the selected language
     *
     * @return Returns the language being used
     */
    public String getLanguage() {
        return lang;
    }

}
