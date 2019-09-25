package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.paths.Path;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;
import static me.mattstudios.utils.MessageUtils.color;
import static me.mattstudios.utils.NumbersUtils.isDouble;

@Command("npcmd")
public class AddCommand extends CommandBase {

    private CitizensCMD plugin;

    public AddCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    /**
     * Adds a command to an NPC via ingame command
     *
     * @param player     Gets the player to check for which NPC is selected and send messages.
     * @param permission The permission node or other to add.
     * @param arguments  Gets the command to be added to the NPC.
     */
    @SubCommand("add")
    @Permission("citizenscmd.add")
    public void addCommand(Player player, @Completion("#permissions") String permission, String[] arguments) {

        if (npcNotSelected(plugin, player)) return;

        StringBuilder permissionBuilder = new StringBuilder(permission);
        boolean left = false;
        boolean displayName = false;
        boolean hasDelayError = false;

        StringBuilder stringBuilder = new StringBuilder();
        arguments[0] = arguments[0].replace("/", "");

        for (int i = 0; i < arguments.length; i++) {

            if (arguments[i].equalsIgnoreCase("")) continue;

            if (arguments[i].equalsIgnoreCase("-n")) {
                displayName = true;
                continue;
            }

            if (arguments[i].equalsIgnoreCase("-l")) {
                left = true;
                continue;
            }

            if (arguments[i].equalsIgnoreCase("-d")) {
                if (i + 1 >= arguments.length) {
                    hasDelayError = true;
                    continue;
                }

                if (!isDouble(arguments[i + 1])) {
                    hasDelayError = true;
                    continue;
                }

                permissionBuilder.append("(").append(arguments[i + 1]).append(")");
                arguments[i + 1] = "";
                continue;
            }

            if (i == arguments.length - 1) stringBuilder.append(arguments[i]);
            else stringBuilder.append(arguments[i]).append(" ");
        }

        if (hasDelayError) {
            player.sendMessage(color(HEADER));
            player.sendMessage(plugin.getLang().getMessage(Path.NPC_ADD_DELAY_FAIL));
            return;
        }

        String finalString;

        if (displayName) {
            finalString = "{display} " + stringBuilder.toString().trim();
        } else {
            finalString = stringBuilder.toString().trim();
        }

        if (permissionBuilder.toString().equalsIgnoreCase("sound")) {
            if (arguments.length < 2) {
                finalString += " 1 1";
            } else {
                if (arguments.length < 3) {
                    finalString += " 1";
                }
            }
        }

        plugin.getDataHandler().addCommand(getSelectedNpcId(player), permissionBuilder.toString(), finalString, player, left);
    }

}
