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
import me.mattstudios.citizenscmd.paths.Path;
import me.mattstudios.citizenscmd.utility.Util;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static me.mattstudios.utils.MessageUtils.color;
import static me.mattstudios.utils.MessageUtils.info;

public class LangHandler {

    private CitizensCMD plugin;
    private String lang;

    private HashMap<String, String> messages;

    public LangHandler(CitizensCMD plugin, String lang) {
        this.plugin = plugin;
        this.lang = lang;
    }

    /**
     * Send message to the console saying this is the language selected
     */
    public void initialize() {
        switch (lang) {
            case "en":
                info(color(Util.TAG + "&7Using &aEnglish &7messages!"));
                break;

            case "pt":
                info(color(Util.TAG + "&7Usando mensagens em Portugues!"));
                break;

            case "ro":
                info(color(Util.TAG + "&7Folositi mesajele in Limba &aRomana&7!"));
                break;

            case "bg":
                info(color(Util.TAG + "&7Използване на &aбългарски &7език!"));
                break;
            case "no":
                info(color(Util.TAG + "&aNorsk &7Oversettelse!"));
                break;
            case "ch":
                info(color(Util.TAG + "&7使用&a中文&7消息!"));
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
                langFile = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
                langConf = new YamlConfiguration();

                if (!langFile.exists()) plugin.saveResource("lang/" + lang + ".yml", false);

                langConf.load(langFile);

                if (!langConf.contains(Path.MESSAGE_DISPLAY))
                    langConf.set(Path.MESSAGE_DISPLAY, "{name}:&r");

                if (!langConf.contains(Path.HELP_DESCRIPTION_SOUND)) {
                    switch (lang) {
                        case "en":
                            langConf.set(Path.HELP_DESCRIPTION_SOUND, "&7Adds a sound to an NPC.");
                            break;

                        case "pt":
                            langConf.set(Path.HELP_DESCRIPTION_SOUND, "&7Adiciona um som a um NPC.");
                            break;

                        case "ro":
                            langConf.set(Path.HELP_DESCRIPTION_SOUND, "&7Adaugă un sunet unui NPC.");
                            break;

                        case "bg":
                            langConf.set(Path.HELP_DESCRIPTION_SOUND, "&7Добавя звук към NPC.");
                            break;

                        case "no":
                            langConf.set(Path.HELP_DESCRIPTION_SOUND, "&7Legger til en lyd til en NPC.");
                            break;

                        case "ch":
                            langConf.set(Path.HELP_DESCRIPTION_SOUND, "&7向NPC添加声音.");
                            break;
                    }
                }

                if (!langConf.contains(Path.NPC_ADD_DELAY_FAIL)) {
                    switch (lang) {
                        case "en":
                            langConf.set(Path.NPC_ADD_DELAY_FAIL, "&cWhen adding &d-d &cthe delay must be a number!");
                            break;

                        case "pt":
                            langConf.set(Path.NPC_ADD_DELAY_FAIL, "&cAo adicionar &d-d &co atraso deve ser um número!");
                            break;

                        case "ro":
                            langConf.set(Path.NPC_ADD_DELAY_FAIL, "&cCând adăugați &d-d &cdelay-ul trebuie să fie un număr!");
                            break;

                        case "bg":
                            langConf.set(Path.NPC_ADD_DELAY_FAIL, "&cПри добавяне &d-d &cзакъснението трябва да бъде число!");
                            break;

                        case "no":
                            langConf.set(Path.NPC_ADD_DELAY_FAIL, "&cNår du legger til &d-d &cforsinkelsen må være et tall!");
                            break;

                        case "ch":
                            langConf.set(Path.NPC_ADD_DELAY_FAIL, "&c添加&d-d&c时延迟必须是数字!");
                            break;

                        case "fr":
                            langConf.set(Path.NPC_ADD_DELAY_FAIL, "&cLors de l'ajout de &d-d &cle délai doit être un nombre!");
                            break;
                    }
                }

                if (!langConf.contains(Path.LIST_COUNT_RIGHT)) {
                    switch (lang) {
                        case "en":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- Right click commands:");
                            break;

                        case "pt":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- Comandos de clique direito:");
                            break;

                        case "ro":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- Comenzile cu click dreapta:");
                            break;

                        case "bg":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- Команди с Десен Клик:");
                            break;

                        case "no":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- Høyre klikk:");
                            break;

                        case "ch":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- 右鍵指令:");
                            break;

                        case "fr":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- Commandes de clic droit:");
                            break;
                    }
                }

                if (!langConf.contains(Path.LIST_COUNT_LEFT)) {
                    switch (lang) {
                        case "en":
                            langConf.set(Path.LIST_COUNT_LEFT, "&c&o{count} &7&o- left click commands:");
                            break;

                        case "pt":
                            langConf.set(Path.LIST_COUNT_LEFT, "&c&o{count} &7&o- Comandos de clique esquerdo:");
                            break;

                        case "ro":
                            langConf.set(Path.LIST_COUNT_LEFT, "&c&o{count} &7&o- Comenzile cu click stanga:");
                            break;

                        case "bg":
                            langConf.set(Path.LIST_COUNT_LEFT, "&c&o{count} &7&o- Команди с Ляв Клик:");
                            break;

                        case "no":
                            langConf.set(Path.LIST_COUNT_LEFT, "&c&o{count} &7&o- Venstre klikk:");
                            break;

                        case "ch":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- 左鍵指令:");
                            break;

                        case "fr":
                            langConf.set(Path.LIST_COUNT_RIGHT, "&c&o{count} &7&o- Commandes de clic gauche:");
                            break;
                    }
                }

                if (!langConf.contains(Path.PERMISSION_SET)) {
                    switch (lang) {
                        case "en":
                            langConf.set(Path.PERMISSION_SET, "&aPermission set successfully!");
                            break;

                        case "pt":
                            langConf.set(Path.PERMISSION_SET, "&aPermissão definida com sucesso!");
                            break;

                        case "ro":
                            langConf.set(Path.PERMISSION_SET, "&aPermisiunea este setată cu succes!");
                            break;

                        case "bg":
                            langConf.set(Path.PERMISSION_SET, "&aРазрешението е успешно!");
                            break;

                        case "no":
                            langConf.set(Path.PERMISSION_SET, "&aTillatelse vellykket!");
                            break;

                        case "ch":
                            langConf.set(Path.PERMISSION_SET, "&a权限设置成功!");
                            break;

                        case "fr":
                            langConf.set(Path.PERMISSION_SET, "&aPermission définie avec succès!");
                            break;
                    }
                }

                if (!langConf.contains(Path.PERMISSION_REMOVED)) {
                    switch (lang) {
                        case "en":
                            langConf.set(Path.PERMISSION_REMOVED, "&aPermission removed successfully!");
                            break;

                        case "pt":
                            langConf.set(Path.PERMISSION_REMOVED, "&aPermissão removida com sucesso!");
                            break;

                        case "ro":
                            langConf.set(Path.PERMISSION_REMOVED, "&aPermisiunea a fost eliminată cu succes!");
                            break;

                        case "bg":
                            langConf.set(Path.PERMISSION_REMOVED, "&aРазрешението бе премахнато успешно!");
                            break;

                        case "no":
                            langConf.set(Path.PERMISSION_REMOVED, "&aTillatelse fjernet vellykket!");
                            break;

                        case "ch":
                            langConf.set(Path.PERMISSION_REMOVED, "&a权限已成功删除!");
                            break;

                        case "fr":
                            langConf.set(Path.PERMISSION_REMOVED, "&aPermission supprimée avec succès!");
                            break;
                    }
                }

                for (String parent : Objects.requireNonNull(langConf.getConfigurationSection("messages")).getKeys(false)) {
                    for (String child : Objects.requireNonNull(langConf.getConfigurationSection("messages." + parent)).getKeys(false))
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
