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

package me.mattmoreira.citizenscmd;

import me.mattmoreira.citizenscmd.Listeners.NPCListener;
import me.mattmoreira.citizenscmd.Listeners.UpdateEvent;
import me.mattmoreira.citizenscmd.commands.*;
import me.mattmoreira.citizenscmd.commands.base.CommandHandler;
import me.mattmoreira.citizenscmd.files.CooldownHandler;
import me.mattmoreira.citizenscmd.files.DataHandler;
import me.mattmoreira.citizenscmd.files.LangHandler;
import me.mattmoreira.citizenscmd.metrics.Metrics;
import me.mattmoreira.citizenscmd.permissions.PermissionsManager;
import me.mattmoreira.citizenscmd.schedulers.CooldownScheduler;
import me.mattmoreira.citizenscmd.schedulers.UpdateScheduler;
import me.mattmoreira.citizenscmd.updater.SpigotUpdater;
import me.mattmoreira.citizenscmd.utility.DisplayFormat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.stream.Stream;

import static me.mattmoreira.citizenscmd.utility.Util.*;

public final class CitizensCMD extends JavaPlugin {

    /**
     * Supported languages
     */
    private final String[] REGISTERED_LANG_FILES = {"en", "pt", "ro", "bg"};

    private static CitizensCMD plugin;

    private static CommandHandler commandHandler = null;
    private static LangHandler lang = null;
    private static DataHandler dataHandler = null;
    private static CooldownHandler cooldownHandler = null;
    private static PermissionsManager permissionsManager = null;
    private static Economy economy = null;

    private static boolean papi = false;
    private static boolean updateStatus = false;
    private static boolean shift = false;
    private static String newVersion;
    private static DisplayFormat displayFormat;

    private static HashMap<String, Boolean> waitingList;

    public void onEnable() {

        plugin = this;

        commandHandler = new CommandHandler();
        commandHandler.enable();

        if (!hasCitizens()) {
            info(color(TAG + "&cCitizens &7is needed for this plugin to work!"));
            info(color(TAG + "&cCitizens.jar &7is not installed on the server!"));
            info(color(TAG + "&cDisabling CitizensCMD..."));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new Metrics(this);

        info(color(TAG + "&3Citizens&cCMD &8&o" + getDescription().getVersion() + " &8By &3Mateus Moreira &c@LichtHund"));

        permissionsManager = new PermissionsManager();

        dataHandler = new DataHandler();
        dataHandler.initialize();

        cooldownHandler = new CooldownHandler();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> cooldownHandler.initialize(), 30L);

        saveDefaultConfig();
        registerCommands();
        registerEvents();

        registerLangs();
        setLang(getConfig().getString("lang"));

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
            }
            papi = true;
        }

        if (setupEconomy()) {
            switch (lang.getLanguage()) {
                case "en":
                    info(color(TAG + "&7Using &aVult&7!"));
                    break;
                case "pt":
                    info(color(TAG + "&7Usando &aVult&7!"));
                    break;
                case "ro":
                    info(color(TAG + "&7Folositi &aVult&7!"));
                    break;
                case "bg":
                    info(color(TAG + "&7Използвайки &aVult&7!"));
                    break;
            }
        }

        waitingList = new HashMap<>();

        if (getConfig().contains("cooldonw-time-display")) {
            switch (getConfig().getString("cooldonw-time-display").toLowerCase()) {
                case "short":
                    displayFormat = DisplayFormat.SHORT;
                    break;
                case "medium":
                    displayFormat = DisplayFormat.MEDIUM;
                    break;
                case "full":
                    displayFormat = DisplayFormat.FULL;
                    break;
                default:
                    displayFormat = DisplayFormat.MEDIUM;
            }
        } else
            displayFormat = DisplayFormat.MEDIUM;

        if (upCheck()) {
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
                            info(color(TAG + "&cA new version of CitizensCMD is now available:"));
                            break;
                        case "ro":
                            info(color(TAG + "&cO noua versiune a CitizensCMD este acum valabila:"));
                            break;
                        case "bg":
                            info(color(TAG + "&cНалична е нова версия на CitizensCMD:"));
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

        new UpdateScheduler().runTaskTimerAsynchronously(this, 72000L, 72000L);
        new CooldownScheduler().runTaskTimerAsynchronously(this, 36000L, 36000L);
    }

    @Override
    public void onDisable() {
        commandHandler.disable();
        cooldownHandler.saveToFile();
    }


    /**
     * Checks if Citizens is installed or not on the server
     *
     * @return Returns true if Citizens is found and false if not
     */
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
        getCommand("npcmd").setExecutor(commandHandler);
        Stream.of(new CMDHelp(), new CMDAdd(), new CMDCooldown(), new CMDList(), new CMDReload(), new CMDRemove(), new CMDEdit(), new CMDPrice()).forEach(commandHandler::register);
    }

    /**
     * Registers all the events to be used
     */
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new UpdateEvent(), this);
        pm.registerEvents(new NPCListener(), this);
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
     * Gets the plugin instance
     *
     * @return Returns instance of the plugin
     */
    public static CitizensCMD getPlugin() {
        return plugin;
    }

    /**
     * Creates all the language files
     */
    private void registerLangs() {
        File langFile;

        for (String registeredLangFile : REGISTERED_LANG_FILES) {
            langFile = new File(CitizensCMD.getPlugin().getDataFolder(), "lang/" + registeredLangFile + ".yml");

            if (!langFile.exists())
                CitizensCMD.getPlugin().saveResource("lang/" + registeredLangFile + ".yml", false);
        }

    }

    /**
     * Sets the language that is supposed to be used
     */
    public void setLang(String language) {
        switch (language.toLowerCase()) {
            case "en":
            case "eng":
            case "english":
                lang = new LangHandler("en");
                break;

            case "pt":
            case "port":
            case "portuguese":
                lang = new LangHandler("pt");
                break;

            case "ro":
            case "roma":
            case "romanian":
                lang = new LangHandler("ro");
                break;

            case "bg":
            case "bulg":
            case "bulgarian":
                lang = new LangHandler("bg");
                break;

            default:
                lang = new LangHandler("en");
                break;
        }
        lang.initialize();
    }

    /**
     * Gets the language that is selected on the config
     *
     * @return returns the language
     */
    public LangHandler getLang() {
        return lang;
    }

    /**
     * Gets if or not should alert player of new update on join
     *
     * @return Returns update status
     */
    public boolean getUpdateStatus() {
        return updateStatus;
    }

    /**
     * Sets new update status from scheduler
     *
     * @param newUpdateStatus New boolean with the update status;
     */
    public void setUpdateStatus(boolean newUpdateStatus) {
        CitizensCMD.updateStatus = newUpdateStatus;
    }

    /**
     * Gets String with new version
     *
     * @return the new version
     */
    public String getNewVersion() {
        return newVersion;
    }

    /**
     * Sets the new version string
     *
     * @param newVersion the new version to be set
     */
    public void setNewVersion(String newVersion) {
        CitizensCMD.newVersion = newVersion;
    }

    /**
     * Gets the NPC data to be used in other classes without needing to open the file
     *
     * @return returns the DataHandler class
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Gets the cooldown handler to check for cooldown informations
     *
     * @return Returns the cooldown handler
     */
    public CooldownHandler getCooldownHandler() {
        return cooldownHandler;
    }

    /**
     * Gets the permission manager to set and unset permission
     *
     * @return the permission manager class
     */
    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    /**
     * Gets the economy to be used
     *
     * @return Returns the economy
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * Gets the hashmap with the players waiting to confirm the NPC payment
     *
     * @return returns the list of players
     */
    public HashMap<String, Boolean> getWaitingList() {
        return waitingList;
    }

    /**
     * Checks if player needs to shift or not to confirm payment
     *
     * @return Returns the boolean of whether or not players should shift
     */
    public boolean shouldShift() {
        return shift;
    }

    /**
     * Sets the new shifting rule
     *
     * @param shift The new shifting rule
     */
    public void setShift(boolean shift) {
        CitizensCMD.shift = shift;
    }

    /**
     * Checks is PAPI is present or not
     *
     * @return Returns true if PAPI is being used
     */
    public boolean papiEnabled() {
        return papi;
    }

    /**
     * Gets the display format to be used
     *
     * @return Returns either SHORT, MEDIUM OR FULL
     */
    public DisplayFormat getDisplayFormat() {
        return displayFormat;
    }

    /**
     * Sets the new display format when reloading
     *
     * @param displayFormat The new display format
     */
    public void setDisplayFormat(DisplayFormat displayFormat) {
        CitizensCMD.displayFormat = displayFormat;
    }
}
