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

package me.mattstudios.citizenscmd.commands.base;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.IHandler;
import me.mattstudios.citizenscmd.utility.Path;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.mattstudios.citizenscmd.utility.Util.*;

/**
 * Thank you GlareMasters for creating this class!
 */
public class CommandHandler implements CommandExecutor, TabCompleter, IHandler {

    private CitizensCMD plugin;
    private List<CommandBase> commands;

    public CommandHandler(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable() {
        commands = new ArrayList<>();
    }

    @Override
    public void disable() {
        commands.clear();
        commands = null;
    }

    public void register(CommandBase command) {
        commands.add(command);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String commandLabel, @NotNull String[] args) {
        if (!cmd.getName().equalsIgnoreCase("npcmd")) {
            return true;
        }

        if (args.length == 0 || args[0].isEmpty()) {
            if (sender.hasPermission("citizenscmd.npcmd"))
                if (sender instanceof Player)
                    getCommand().execute((Player) sender, args);
            return true;
        }

        for (CommandBase command : commands) {
            if (!command.getName().equalsIgnoreCase(args[0]) && !command.getAliases()
                    .contains(args[0].toLowerCase())) {
                continue;
            }

            if (!command.allowConsole() && !(sender instanceof Player)) {
                sender.sendMessage(color(HEADER));
                sender.sendMessage(plugin.getLang().getMessage(Path.CONSOLE_NOT_ALLOWED));
                return true;
            }

            if (!sender.hasPermission(command.getPermission())) {
                sender.sendMessage(color(HEADER));
                sender.sendMessage(plugin.getLang().getMessage(Path.NO_PERMISSION));
                return true;
            }

            args = Arrays.copyOfRange(args, 1, args.length);

            if ((command.getMinimumArguments() != -1 && command.getMinimumArguments() > args.length)
                    || (command.getMaximumArguments() != -1
                    && command.getMaximumArguments() < args.length)) {
                sender.sendMessage(color(HEADER));
                sender.sendMessage(plugin.getLang().getMessage(Path.WRONG_USAGE));
                return true;
            }

            if (command.allowConsole()) {
                if (sender instanceof Player)
                    command.execute((Player) sender, args);
                else
                    command.execute(sender, args);
                return true;
            } else {
                command.execute((Player) sender, args);
                return true;
            }
        }
        sender.sendMessage(color(HEADER));
        sender.sendMessage(plugin.getLang().getMessage(Path.WRONG_USAGE));
        return true;
    }

    private CommandBase getCommand() {
        return commands.stream().filter(
                command -> command.getName() != null && command.getName().equalsIgnoreCase("help"))
                .findFirst().orElse(null);
    }

    public List<CommandBase> getCommands() {
        return commands;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String commandLabel, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("npcmd")) {
            if (args.length == 1) {
                List<String> commandNames = new ArrayList<>();

                if (!args[0].equals("")) {
                    for (String commandName : commands.stream().map(CommandBase::getName)
                            .collect(Collectors.toList())) {
                        if (!commandName.startsWith(args[0].toLowerCase())) continue;
                        commandNames.add(commandName);
                    }
                } else {
                    commandNames =
                            commands.stream().map(CommandBase::getName)
                                    .collect(Collectors.toList());
                }

                Collections.sort(commandNames);

                return commandNames;

            } else {
                String subCMD = args[0].toLowerCase();
                switch (subCMD) {
                    case "add":
                        if (args.length == 2) return getCommandNames(subCMD, args, 1, (Player) sender);
                        if (args[1].equalsIgnoreCase("sound")) {
                            if (args.length == 3) return getCommandNames(subCMD, args, 2, (Player) sender);
                            if (args.length == 4) return getCommandNames(subCMD, args, 3, (Player) sender);
                            if (args.length == 5) return getCommandNames(subCMD, args, 4, (Player) sender);
                        }
                        break;

                    case "permission":
                    case "cooldown":
                        if (args.length == 2) return getCommandNames(subCMD, args, 1, (Player) sender);
                        break;

                    case "remove":
                        if (npcNotSelectedNM((Player) sender)) break;
                        if (args.length == 2) return getCommandNames(subCMD, args, 1, (Player) sender);
                        if (args.length == 3) {
                            if (args[1].equalsIgnoreCase("left"))
                                return getCommandNames(subCMD, args, 2, (Player) sender);
                            else if (args[1].equalsIgnoreCase("right"))
                                return getCommandNames(subCMD, args, 3, (Player) sender);
                        }
                        break;

                    case "edit":
                        if (npcNotSelectedNM((Player) sender)) break;
                        if (args.length == 2) return getCommandNames(subCMD, args, 1, (Player) sender);
                        if (args.length == 3) return getCommandNames(subCMD, args, 2, (Player) sender);
                        if (args.length == 4) {
                            if (args[2].equalsIgnoreCase("left"))
                                return getCommandNames(subCMD, args, 3, (Player) sender);
                            else if (args[2].equalsIgnoreCase("right"))
                                return getCommandNames(subCMD, args, 4, (Player) sender);
                        }
                        if (args.length == 5) {
                            if (args[1].equalsIgnoreCase("perm"))
                                return getCommandNames(subCMD, args, 5, (Player) sender);
                        }
                        break;
                }

            }
        }

        return null;
    }

    /**
     * Gets the subcomands to tab complete
     *
     * @param subCMD Gets the sub command, example, add, list, etc
     * @param args   Arguments from the command
     * @param arg    Number of arguments to get the correct from the getTabCompleteArgs()
     * @return Returns list with Strings to the tab complete
     */
    private List<String> getCommandNames(String subCMD, String[] args, int arg, Player player) {
        List<String> commandNames = new ArrayList<>();
        String[][] argsComplete = getTabCompleteArgs(plugin, subCMD, player);

        if (!args[arg - 1].equals("")) {
            for (String commandName : argsComplete[arg - 1]) {
                if (arg + 1 > args.length) break;
                if (!commandName.toLowerCase().startsWith(args[arg].toLowerCase())) continue;
                commandNames.add(commandName);
            }
        } else {
            commandNames = Arrays.asList(argsComplete[arg - 1]);
        }

        Collections.sort(commandNames);

        return commandNames;
    }

}
