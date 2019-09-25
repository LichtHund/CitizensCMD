package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Completion;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import org.bukkit.entity.Player;

import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;

@Command("npcmd")
public class PriceCommand extends CommandBase {

    private CitizensCMD plugin;

    public PriceCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("price")
    @Permission("citizenscmd.price")
    @Completion("#range:9")
    public void price(Player player, double price) {

        if (npcNotSelected(plugin, player)) return;

        plugin.getDataHandler().setPrice(getSelectedNpcId(player), price, player);
    }

}
