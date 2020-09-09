package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.command.CommandSender;

import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;

@Command("npcmd")
public class PriceCommand extends CommandBase {

    private final CitizensCMD plugin;

    public PriceCommand(final CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("price")
    @Permission("citizenscmd.price")
    @Completion("#range:9")
    public void price(final CommandSender sender, final Double price) {
        if (npcNotSelected(plugin, sender)) return;
        plugin.getDataHandler().setPrice(getSelectedNpcId(sender), price, sender);
    }

}
