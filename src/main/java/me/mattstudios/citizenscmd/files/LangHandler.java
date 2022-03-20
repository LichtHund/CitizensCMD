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

import ch.jalu.configme.SettingsManager;
import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.Settings;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static me.mattstudios.citizenscmd.utility.Util.LEGACY;
import static me.mattstudios.citizenscmd.utility.Util.MINI;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LangHandler {

    private final CitizensCMD plugin;
    private final SettingsManager settings;
    private final String lang;

    private final Map<String, String> messages = new HashMap<>();

    public LangHandler(final CitizensCMD plugin, final String lang) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
        this.lang = lang.toLowerCase(Locale.ROOT);
        cacheMessage();
    }

    /**
     * Caches all messages into a HashMap for easier access
     */
    private void cacheMessage() {
        try {
            File langFile = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
            FileConfiguration langConf = new YamlConfiguration();
            // InputStream langStream = CitizensCMD.class.getClassLoader().getResourceAsStream("lang/" + lang + ".yml");

            if (!langFile.exists()) {
                plugin.saveResource("lang/" + lang + ".yml", false);
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
    public Component getMessage(Messages path) {
        return getMessage(path, "", "");
    }

    public Component getMessage(Messages path, String match, String replace) {
        return getMessage(path, Collections.singletonMap(match, replace));
    }

    public Component getMessage(Messages path, Map<String, String> replacements) {
        String value = messages.get(path.getPath());
        for (final Map.Entry<String, String> entry : replacements.entrySet()) {
            value = value.replace(entry.getKey(), entry.getValue());
        }

        if (settings.getProperty(Settings.MINI_MESSAGE_LANG)) return MINI.deserialize(value);
        return LEGACY.deserialize(value);
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
