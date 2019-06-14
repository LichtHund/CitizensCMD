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

package me.mattmoreira.citizenscmd;

import lombok.Getter;
import lombok.Setter;
import me.mattmoreira.citizenscmd.API.CitizensCMDAPI;
import me.mattmoreira.citizenscmd.commands.*;
import me.mattmoreira.citizenscmd.commands.base.CommandHandler;
import me.mattmoreira.citizenscmd.files.CooldownHandler;
import me.mattmoreira.citizenscmd.files.DataHandler;
import me.mattmoreira.citizenscmd.files.LangHandler;
import me.mattmoreira.citizenscmd.listeners.NPCListener;
import me.mattmoreira.citizenscmd.listeners.UpdateEvent;
import me.mattmoreira.citizenscmd.metrics.Metrics;
import me.mattmoreira.citizenscmd.permissions.PermissionsManager;
import me.mattmoreira.citizenscmd.schedulers.CooldownScheduler;
import me.mattmoreira.citizenscmd.schedulers.UpdateScheduler;
import me.mattmoreira.citizenscmd.updater.SpigotUpdater;
import me.mattmoreira.citizenscmd.utility.DisplayFormat;
import me.mattmoreira.citizenscmd.utility.Util;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import static me.mattmoreira.citizenscmd.utility.Util.*;

@Getter
@Setter
public final class CitizensCMD extends JavaPlugin {

    /**
     * Supported languages
     */
    private final String[] REGISTERED_LANG_FILES = {"en", "pt", "ro", "bg", "no", "ch"};

    private LangHandler lang = null;
    private DataHandler dataHandler = null;
    private CooldownHandler cooldownHandler = null;
    private PermissionsManager permissionsManager = null;

    @Getter
    private static CitizensCMDAPI api;
    @Getter
    private static Economy economy = null;

    private boolean papi = false;
    private CommandHandler commandHandler = null;

    private boolean updateStatus = false;
    private boolean shift = false;

    private String newVersion;
    private DisplayFormat displayFormat;

    private HashMap<String, Boolean> waitingList;

    public void onEnable() {

        checkOldConfig(this);
        saveDefaultConfig();

        if (!hasCitizens() && getConfig().getBoolean("citizens-check")) {
            Util.disablePlugin(this);
            return;
        }

        commandHandler = new CommandHandler(this);
        commandHandler.enable();

        new Metrics(this);

        info(color(TAG + "&3Citizens&cCMD &8&o" + getDescription().getVersion() + " &8By &3Mateus Moreira &c@LichtHund"));

        permissionsManager = new PermissionsManager(this);

        dataHandler = new DataHandler(this);
        dataHandler.initialize();

        cooldownHandler = new CooldownHandler(this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> cooldownHandler.initialize(), 30L);

        registerCommands();
        registerEvents();

        registerLangs(this);
        setLang(Objects.requireNonNull(getConfig().getString("lang")));

        if (hasPAPI()) {
            switch (lang.getLanguage()) {
                case "en":
                    info(color(TAG + "&7Using &aPlaceholderAPI&7!"));
                    break;
                case "pt":
                    info(color(TAG + "&7Usando &aPlaceholderAPI&7!"));
                    break;
                case "ro":
                    info(color(TAG + "&7Folositi &aPlaceholderAPI&7!"));
                    break;
                case "bg":
                    info(color(TAG + "&7Използвайки &aPlaceholderAPI&7!"));
                    break;
                case "no":
                    info(color(TAG + "&7Bruk &aPlaceholderAPI&7!"));
                    break;
                case "ch":
                    info(color(TAG + "&7运用 &aPlaceholderAPI&7!"));
                    break;
            }
            papi = true;
        }

        if (setupEconomy()) {
            switch (lang.getLanguage()) {
                case "en":
                    info(color(TAG + "&7Using &aVault&7!"));
                    break;
                case "pt":
                    info(color(TAG + "&7Usando &aVault&7!"));
                    break;
                case "ro":
                    info(color(TAG + "&7Folositi &aVault&7!"));
                    break;
                case "bg":
                    info(color(TAG + "&7Използвайки &aVault&7!"));
                    break;
                case "no":
                    info(color(TAG + "&7Bruk &aVault&7!"));
                    break;
                case "ch":
                    info(color(TAG + "&7运用 &aVault&7!"));
                    break;
            }
        }

        waitingList = new HashMap<>();

        if (getConfig().contains("cooldown-time-display")) {
            switch (Objects.requireNonNull(getConfig().getString("cooldown-time-display")).toLowerCase()) {
                case "short":
                    displayFormat = DisplayFormat.SHORT;
                    break;
                case "full":
                    displayFormat = DisplayFormat.FULL;
                    break;
                default:
                    displayFormat = DisplayFormat.MEDIUM;
            }
        } else
            displayFormat = DisplayFormat.MEDIUM;

        if (upCheck(this)) {
            SpigotUpdater updater = new SpigotUpdater(this, 30224);
            try {
                // If there's an update, tell the user that they can update
                if (updater.checkForUpdates()) {
                    updateStatus = true;
                    newVersion = updater.getLatestVersion();
                    switch (lang.getLanguage()) {
                        case "en":
                            info(color(TAG + "&cA new version of CitizensCMD is now available:"));
                            break;
                        case "pt":
                            info(color(TAG + "&cA nova versão de CitizensCMD está disponivel:"));
                            break;
                        case "ro":
                            info(color(TAG + "&cO noua versiune a CitizensCMD este acum valabila:"));
                            break;
                        case "bg":
                            info(color(TAG + "&cНалична е нова версия на CitizensCMD:"));
                            break;
                        case "no":
                            info(color(TAG + "&cEn ny versjon av CitizensCMD er nå tilgjengelig:"));
                            break;
                        case "ch":
                            info(color(TAG + "&cCitizensCMD的新版本现已推出:"));
                            break;
                    }
                    info(color(TAG + "&b&o" + updater.getResourceURL()));
                }
            } catch (Exception e) {
                // If it can't check for an update, tell the user and throw an error.
                info("Could not check for updates! Stacktrace:");
                e.printStackTrace();
            }
        }

        api = new CitizensCMDAPI(dataHandler);

        new UpdateScheduler(this).runTaskTimerAsynchronously(this, 72000L, 72000L);
        new CooldownScheduler(this).runTaskTimerAsynchronously(this, 36000L, 36000L);
    }

    @Override
    public void onDisable() {
        if (commandHandler != null) {
            commandHandler.disable();
            cooldownHandler.saveToFile();
        }
    }

    private boolean hasCitizens() {
        return Bukkit.getPluginManager().isPluginEnabled("Citizens");
    }

    /**
     * Checks if PAPI is installed or not on the server
     *
     * @return Returns true if PAPI is found and false if not
     */
    private boolean hasPAPI() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }

    /**
     * Registers all the commands to be used
     */
    private void registerCommands() {
        Objects.requireNonNull(getCommand("npcmd")).setExecutor(commandHandler);
        Stream.of(
                new CMDHelp(this),
                new CMDAdd(this),
                new CMDCooldown(this),
                new CMDList(this),
                new CMDReload(this),
                new CMDRemove(this),
                new CMDEdit(this),
                new CMDPrice(this),
                new CMDPermission(this)
        ).forEach(commandHandler::register);
    }

    /**
     * Registers all the events to be used
     */
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new UpdateEvent(this), this);
        pm.registerEvents(new NPCListener(this), this);
    }

    /**
     * Sets up the economy if Vault is present
     *
     * @return returns true or false depending on if Vault is or not present
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> registeredServiceProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {
            return false;
        }
        economy = registeredServiceProvider.getProvider();
        shift = getConfig().getBoolean("shift-confirm");
        return economy != null;
    }

    /**
     * Creates all the language files
     */
    private void registerLangs(CitizensCMD plugin) {
        File langFile;

        for (String registeredLangFile : REGISTERED_LANG_FILES) {
            langFile = new File(plugin.getDataFolder(), "lang/" + registeredLangFile + ".yml");

            if (!langFile.exists())
                plugin.saveResource("lang/" + registeredLangFile + ".yml", false);
        }

    }

    /**
     * Sets the language that is supposed to be used
     */
    public void setLang(String language) {
        switch (language.toLowerCase()) {
            case "pt":
            case "port":
            case "portuguese":
                lang = new LangHandler(this, "pt");
                break;

            case "ro":
            case "roma":
            case "romanian":
                lang = new LangHandler(this, "ro");
                break;

            case "bg":
            case "bulg":
            case "bulgarian":
                lang = new LangHandler(this, "bg");
                break;

            case "no":
            case "norw":
            case "norwegian":
                lang = new LangHandler(this, "no");
                break;

            case "ch":
            case "chi":
            case "chinese":
                lang = new LangHandler(this, "ch");
                break;

            default:
                lang = new LangHandler(this, "en");
                break;
        }
        lang.initialize();
    }

    /**
     * Checks is PAPI is present or not
     *
     * @return Returns true if PAPI is being used
     */
    public boolean papiEnabled() {
        return papi;
    }
}
