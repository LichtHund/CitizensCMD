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

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.bukkit.message.BukkitMessageKey;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import me.mattstudios.citizenscmd.api.CitizensCMDAPI;
import me.mattstudios.citizenscmd.commands.AddCommand;
import me.mattstudios.citizenscmd.commands.CooldownCommand;
import me.mattstudios.citizenscmd.commands.EditCommand;
import me.mattstudios.citizenscmd.commands.HelpCommand;
import me.mattstudios.citizenscmd.commands.ListCommand;
import me.mattstudios.citizenscmd.commands.PermissionCommand;
import me.mattstudios.citizenscmd.commands.PriceCommand;
import me.mattstudios.citizenscmd.commands.ReloadCommand;
import me.mattstudios.citizenscmd.commands.RemoveCommand;
import me.mattstudios.citizenscmd.files.CooldownHandler;
import me.mattstudios.citizenscmd.files.DataHandler;
import me.mattstudios.citizenscmd.files.LangHandler;
import me.mattstudios.citizenscmd.listeners.NPCClickListener;
import me.mattstudios.citizenscmd.listeners.NPCListener;
import me.mattstudios.citizenscmd.listeners.UpdateEvent;
import me.mattstudios.citizenscmd.permissions.PermissionsManager;
import me.mattstudios.citizenscmd.schedulers.CooldownScheduler;
import me.mattstudios.citizenscmd.schedulers.UpdateScheduler;
import me.mattstudios.citizenscmd.updater.SpigotUpdater;
import me.mattstudios.citizenscmd.utility.DisplayFormat;
import me.mattstudios.citizenscmd.utility.Messages;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.LEGACY;
import static me.mattstudios.citizenscmd.utility.Util.TAG;
import static me.mattstudios.citizenscmd.utility.Util.color;
import static me.mattstudios.citizenscmd.utility.Util.disablePlugin;
import static me.mattstudios.citizenscmd.utility.Util.info;

public final class CitizensCMD extends JavaPlugin {

    private LangHandler lang;
    private DataHandler dataHandler;
    private CooldownHandler cooldownHandler;
    private PermissionsManager permissionsManager;

    private BukkitAudiences audiences;

    private static CitizensCMDAPI api;
    private static Economy economy;

    private boolean papi = false;
    private BukkitCommandManager<CommandSender> commandManager;
    private SettingsManager settings;

    private boolean updateStatus = false;
    private boolean shift = false;

    private String newVersion;
    private DisplayFormat displayFormat;

    private Map<String, Boolean> waitingList;

    @Override
    public void onEnable() {
        settings = SettingsManagerBuilder
                .withYamlFile(Paths.get(getDataFolder().getPath(), "config.yml"))
                .configurationData(Settings.class)
                .useDefaultMigrationService()
                .create();

        audiences = BukkitAudiences.create(this);

        setLang(settings.getProperty(Settings.LANG));

        if (!hasCitizens() && settings.getProperty(Settings.CITIZENS_CHECK)) {
            disablePlugin(this);
            return;
        }

        commandManager = BukkitCommandManager.create(this);

        //Metrics metrics = new Metrics(this);
        //Util.setUpMetrics(metrics, settings);

        final Audience console = audiences.console();
        console.sendMessage(TAG.append(LEGACY.deserialize("&3Citizens&cCMD &8&o" + getDescription().getVersion() + " &8By &3Mateus Moreira &c@LichtHund")));

        permissionsManager = new PermissionsManager(this);

        dataHandler = new DataHandler(this);
        dataHandler.initialize();

        cooldownHandler = new CooldownHandler(this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> cooldownHandler.initialize(), 30L);

        registerCommands();
        registerEvents();

        console.sendMessage(TAG.append(lang.getMessage(Messages.USING_LANGUAGE)));

        if (hasPAPI()) {
            console.sendMessage(TAG.append(lang.getMessage(Messages.PAPI_AVAILABLE)));
            papi = true;
        }

        if (setupEconomy()) {
            console.sendMessage(TAG.append(lang.getMessage(Messages.VAULT_AVAILABLE)));
        }

        waitingList = new HashMap<>();

        setShift(settings.getProperty(Settings.SHIT_CONFIRM));

        switch (settings.getProperty(Settings.TIME_DISPLAY).toLowerCase()) {
            case "short":
                displayFormat = DisplayFormat.SHORT;
                break;

            case "full":
                displayFormat = DisplayFormat.FULL;
                break;

            default:
                displayFormat = DisplayFormat.MEDIUM;
                break;
        }

        if (settings.getProperty(Settings.CHECK_UPDATES)) {
            SpigotUpdater updater = new SpigotUpdater(this, 30224);
            try {
                // If there's an update, tell the user that they can update
                if (updater.checkForUpdates()) {
                    updateStatus = true;
                    newVersion = updater.getLatestVersion();
                    console.sendMessage(TAG.append(lang.getMessage(Messages.STARTUP_NEW_VERSION).style(Style.style(NamedTextColor.AQUA, TextDecoration.ITALIC))));
                    console.sendMessage(TAG.append(Component.text(updater.getResourceURL()).style(Style.style(NamedTextColor.AQUA, TextDecoration.ITALIC))));
                }
            } catch (Exception ignored) {
            }
        }

        api = new CitizensCMDAPI(dataHandler);

        new UpdateScheduler(this).runTaskTimerAsynchronously(this, 72000L, 72000L);
        new CooldownScheduler(this).runTaskTimerAsynchronously(this, 36000L, 36000L);
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
        commandManager.registerSuggestion(SuggestionKey.of("permissions"), (sender, context) -> Arrays.asList("console", "player", "permission", "server", "message", "sound"));
        commandManager.registerSuggestion(SuggestionKey.of("type"), (sender, context) -> Arrays.asList("cmd", "perm"));
        commandManager.registerSuggestion(SuggestionKey.of("click"), (sender, context) -> Arrays.asList("left", "right"));
        commandManager.registerSuggestion(SuggestionKey.of("set"), (sender, context) -> Arrays.asList("set", "remove"));
        commandManager.registerSuggestion(SuggestionKey.of("range"), (sender, context) -> IntStream.rangeClosed(1, 9).mapToObj(String::valueOf).collect(Collectors.toList()));

        commandManager.registerMessage(BukkitMessageKey.NO_PERMISSION, (sender, context) -> {
            final Audience audience = audiences.sender(sender);
            audience.sendMessage(HEADER);
            audience.sendMessage(lang.getMessage(Messages.NO_PERMISSION));
        });
        commandManager.registerMessage(BukkitMessageKey.PLAYER_ONLY, (sender, context) -> {
            final Audience audience = audiences.sender(sender);
            audience.sendMessage(HEADER);
            audience.sendMessage(lang.getMessage(Messages.CONSOLE_NOT_ALLOWED));
        });
        commandManager.registerMessage(MessageKey.UNKNOWN_COMMAND, (sender, context) -> {
            final Audience audience = audiences.sender(sender);
            audience.sendMessage(HEADER);
            audience.sendMessage(lang.getMessage(Messages.WRONG_USAGE));
        });
        commandManager.registerMessage(MessageKey.INVALID_ARGUMENT, (sender, context) -> {
            final Audience audience = audiences.sender(sender);
            audience.sendMessage(HEADER);
            audience.sendMessage(lang.getMessage(Messages.WRONG_USAGE));
        });
        commandManager.registerMessage(MessageKey.TOO_MANY_ARGUMENTS, (sender, context) -> {
            final Audience audience = audiences.sender(sender);
            audience.sendMessage(HEADER);
            audience.sendMessage(lang.getMessage(Messages.WRONG_USAGE));
        });
        commandManager.registerMessage(MessageKey.NOT_ENOUGH_ARGUMENTS, (sender, context) -> {
            final Audience audience = audiences.sender(sender);
            audience.sendMessage(HEADER);
            audience.sendMessage(lang.getMessage(Messages.WRONG_USAGE));
        });

        Stream.of(
                new AddCommand(this),
                new HelpCommand(this),
                new EditCommand(this),
                new ListCommand(this),
                new CooldownCommand(this),
                new PermissionCommand(this),
                new PriceCommand(this),
                new ReloadCommand(this),
                new RemoveCommand(this)
        ).forEach(commandManager::registerCommand);
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
        shift = settings.getProperty(Settings.SHIT_CONFIRM);
        return economy != null;
    }

    /**
     * Settings manager, should use this instead of default config.
     *
     * @return The settings manager.
     */
    public SettingsManager getSettings() {
        return settings;
    }

    /**
     * Gets adventure's audiences for JSON messages.
     *
     * @return The BukkitAudiences.
     */
    public BukkitAudiences getAudiences() {
        return audiences;
    }

    /**
     * Sets the language that is supposed to be used
     */
    public void setLang(String language) {
        lang = new LangHandler(this, language);
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
    public Map<String, Boolean> getWaitingList() {
        return waitingList;
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

    public static CitizensCMDAPI getApi() {
        return api;
    }

    /**
     * Checks if player needs to shift or not to confirm payment
     *
     * @return Returns the boolean of whether or not players should shift
     */
    public boolean isShift() {
        return shift;
    }

    /**
     * Gets if or not should alert player of new update on join
     *
     * @return Returns update status
     */
    public boolean isUpdateStatus() {
        return updateStatus;
    }
}
