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

package me.mattstudios.citizenscmd;

import me.mattstudios.citizenscmd.api.CitizensCMDAPI;
import me.mattstudios.citizenscmd.commands.CMDAdd;
import me.mattstudios.citizenscmd.commands.CMDCooldown;
import me.mattstudios.citizenscmd.commands.CMDEdit;
import me.mattstudios.citizenscmd.commands.CMDHelp;
import me.mattstudios.citizenscmd.commands.CMDList;
import me.mattstudios.citizenscmd.commands.CMDPermission;
import me.mattstudios.citizenscmd.commands.CMDPrice;
import me.mattstudios.citizenscmd.commands.CMDReload;
import me.mattstudios.citizenscmd.commands.CMDRemove;
import me.mattstudios.citizenscmd.commands.base.CommandHandler;
import me.mattstudios.citizenscmd.files.CooldownHandler;
import me.mattstudios.citizenscmd.files.DataHandler;
import me.mattstudios.citizenscmd.files.LangHandler;
import me.mattstudios.citizenscmd.listeners.NPCClickListener;
import me.mattstudios.citizenscmd.listeners.NPCListener;
import me.mattstudios.citizenscmd.listeners.UpdateEvent;
import me.mattstudios.citizenscmd.metrics.Metrics;
import me.mattstudios.citizenscmd.permissions.PermissionsManager;
import me.mattstudios.citizenscmd.schedulers.CooldownScheduler;
import me.mattstudios.citizenscmd.schedulers.UpdateScheduler;
import me.mattstudios.citizenscmd.updater.SpigotUpdater;
import me.mattstudios.citizenscmd.utility.DisplayFormat;
import me.mattstudios.citizenscmd.utility.Util;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;

import static me.mattstudios.utils.MessageUtils.color;
import static me.mattstudios.utils.MessageUtils.info;
import static me.mattstudios.utils.YamlUtils.copyDefaults;

public final class CitizensCMD extends JavaPlugin {

    /**
     * Supported languages
     */
    private final String[] REGISTERED_LANG_FILES = {"en", "pt", "ro", "bg", "no", "ch"};

    private LangHandler lang = null;
    private DataHandler dataHandler = null;
    private CooldownHandler cooldownHandler = null;
    private PermissionsManager permissionsManager = null;

    private static CitizensCMDAPI api;
    private static Economy economy = null;

    private boolean papi = false;
    private CommandHandler commandHandler = null;

    private boolean updateStatus = false;
    private boolean shift = false;

    private String newVersion;
    private DisplayFormat displayFormat;

    private HashMap<String, Boolean> waitingList;

    public void onEnable() {

        saveDefaultConfig();
        copyDefaults(getClassLoader().getResourceAsStream("config.yml"), new File(getDataFolder().getPath(), "config.yml"));

        if (!hasCitizens() && getConfig().getBoolean("citizens-check")) {
            Util.disablePlugin(this);
            return;
        }

        commandHandler = new CommandHandler(this);
        commandHandler.enable();

        new Metrics(this);

        info(color(Util.TAG + "&3Citizens&cCMD &8&o" + getDescription().getVersion() + " &8By &3Mateus Moreira &c@LichtHund"));

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
                    info(color(Util.TAG + "&7Using &aPlaceholderAPI&7!"));
                    break;
                case "pt":
                    info(color(Util.TAG + "&7Usando &aPlaceholderAPI&7!"));
                    break;
                case "ro":
                    info(color(Util.TAG + "&7Folositi &aPlaceholderAPI&7!"));
                    break;
                case "bg":
                    info(color(Util.TAG + "&7Използвайки &aPlaceholderAPI&7!"));
                    break;
                case "no":
                    info(color(Util.TAG + "&7Bruk &aPlaceholderAPI&7!"));
                    break;
                case "ch":
                    info(color(Util.TAG + "&7运用 &aPlaceholderAPI&7!"));
                    break;
            }
            papi = true;
        }

        if (setupEconomy()) {
            switch (lang.getLanguage()) {
                case "en":
                    info(color(Util.TAG + "&7Using &aVault&7!"));
                    break;
                case "pt":
                    info(color(Util.TAG + "&7Usando &aVault&7!"));
                    break;
                case "ro":
                    info(color(Util.TAG + "&7Folositi &aVault&7!"));
                    break;
                case "bg":
                    info(color(Util.TAG + "&7Използвайки &aVault&7!"));
                    break;
                case "no":
                    info(color(Util.TAG + "&7Bruk &aVault&7!"));
                    break;
                case "ch":
                    info(color(Util.TAG + "&7运用 &aVault&7!"));
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

        if (Util.upCheck(this)) {
            SpigotUpdater updater = new SpigotUpdater(this, 30224);
            try {
                // If there's an update, tell the user that they can update
                if (updater.checkForUpdates()) {
                    updateStatus = true;
                    newVersion = updater.getLatestVersion();
                    switch (lang.getLanguage()) {
                        case "en":
                            info(color(Util.TAG + "&cA new version of CitizensCMD is now available:"));
                            break;
                        case "pt":
                            info(color(Util.TAG + "&cA nova versão de CitizensCMD está disponivel:"));
                            break;
                        case "ro":
                            info(color(Util.TAG + "&cO noua versiune a CitizensCMD este acum valabila:"));
                            break;
                        case "bg":
                            info(color(Util.TAG + "&cНалична е нова версия на CitizensCMD:"));
                            break;
                        case "no":
                            info(color(Util.TAG + "&cEn ny versjon av CitizensCMD er nå tilgjengelig:"));
                            break;
                        case "ch":
                            info(color(Util.TAG + "&cCitizensCMD的新版本现已推出:"));
                            break;
                    }
                    info(color(Util.TAG + "&b&o" + updater.getResourceURL()));
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
        pm.registerEvents(new NPCClickListener(this), this);

        try {
            pm.registerEvents(new NPCListener(this), this);
        } catch (Exception ex) {
            info(color("&cCould not register clone event, please update your Citizens."));
        }
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
        this.updateStatus = newUpdateStatus;
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
        this.newVersion = newVersion;
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
        this.shift = shift;
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
        this.displayFormat = displayFormat;
    }

    public CitizensCMDAPI getApi() {
        return api;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isUpdateStatus() {
        return updateStatus;
    }
}
