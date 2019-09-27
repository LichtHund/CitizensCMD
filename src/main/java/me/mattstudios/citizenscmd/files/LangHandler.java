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
import me.mattstudios.citizenscmd.utility.Messages;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import static me.mattstudios.utils.MessageUtils.color;
import static me.mattstudios.utils.YamlUtils.copyDefaults;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LangHandler {

    private CitizensCMD plugin;
    private String lang;

    private HashMap<String, String> messages;

    public LangHandler(CitizensCMD plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;

        messages = new HashMap<>();
        cacheMessage();
    }

    /**
     * Caches all messages into a HashMap for easier access
     */
    private void cacheMessage() {
        try {
            File langFile = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
            FileConfiguration langConf = new YamlConfiguration();

            InputStream langStream = CitizensCMD.class.getClassLoader().getResourceAsStream("lang/" + lang + ".yml");

            System.out.println("Stream: " + (langStream == null));

            if (!langFile.exists()) {
                System.out.println("no exist");
                if (langStream == null) {
                    langFile.createNewFile();
                    copyDefaults(CitizensCMD.class.getClassLoader().getResourceAsStream("lang/en.yml"), langFile);
                } else {
                    plugin.saveResource("lang/" + lang + ".yml", false);
                }
            } else {
                System.out.println("exist");
                if (langStream == null) {
                    System.out.println("nuru");
                    copyDefaults(CitizensCMD.class.getClassLoader().getResourceAsStream("lang/en.yml"), langFile);
                } else {
                    System.out.println("noto nuru");
                    copyDefaults(langStream, langFile);
                }
            }

            langConf.load(langFile);

            for (String parent : Objects.requireNonNull(langConf.getConfigurationSection("messages")).getKeys(false)) {
                for (String child : Objects.requireNonNull(langConf.getConfigurationSection("messages." + parent)).getKeys(false)) {
                    messages.put("messages." + parent + "." + child, langConf.getString("messages." + parent + "." + child));
                }
            }

        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets message from the lang file
     *
     * @param path String with the path to the message
     * @return Returns String with colored message from file
     */
    public String getMessage(Messages path) {
        return color(messages.get(path.getPath()));
    }

    /**
     * Gets message from the lang file without color
     *
     * @param path String with the path to the message
     * @return Returns String with message from file
     */
    public String getUncoloredMessage(Messages path) {
        return messages.get(path.getPath());
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
