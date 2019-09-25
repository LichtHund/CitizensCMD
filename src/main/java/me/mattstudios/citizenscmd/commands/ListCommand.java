package me.mattstudios.citizenscmd.commands;

import me.mattstudios.citizenscmd.CitizensCMD;
import me.mattstudios.citizenscmd.utility.EnumTypes;
import me.mattstudios.citizenscmd.utility.paths.Path;
import me.mattstudios.mf.annotations.Command;
import me.mattstudios.mf.annotations.Permission;
import me.mattstudios.mf.annotations.SubCommand;
import me.mattstudios.mf.base.CommandBase;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.mattstudios.citizenscmd.utility.Util.HEADER;
import static me.mattstudios.citizenscmd.utility.Util.getSelectedNpcId;
import static me.mattstudios.citizenscmd.utility.Util.npcNotSelected;
import static me.mattstudios.utils.MessageUtils.color;

@Command("npcmd")
public class ListCommand extends CommandBase {

    private CitizensCMD plugin;

    public ListCommand(CitizensCMD plugin) {
        this.plugin = plugin;
    }

    @SubCommand("list")
    @Permission("citizenscmd.list")
    public void list(Player player) {

        if (npcNotSelected(plugin, player)) return;

        int npc = getSelectedNpcId(player);

        List<String> leftCommands = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT) != null ? plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.LEFT) : new ArrayList<>();
        List<String> rightCommands = plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT) != null ? plugin.getDataHandler().getClickCommandsData(npc, EnumTypes.ClickType.RIGHT) : new ArrayList<>();

        player.sendMessage(color(HEADER));
        JSONMessage.create(color(plugin.getLang().getUncoloredMessage(Path.LIST_COOLDOWN) + plugin.getDataHandler().getNPCCooldown(npc))).tooltip(plugin.getLang().getMessage(Path.LIST_TOOLTIP)).suggestCommand("/npcmd cooldown ").send(player);
        JSONMessage.create(color(plugin.getLang().getUncoloredMessage(Path.LIST_PRICE) + plugin.getDataHandler().getPrice(npc))).tooltip(plugin.getLang().getMessage(Path.LIST_TOOLTIP)).suggestCommand("/npcmd price ").send(player);
        player.sendMessage("");
        player.sendMessage(plugin.getLang().getMessage(Path.LIST_COUNT_RIGHT).replace("{count}", String.valueOf(rightCommands.size())));

        int rightCount = 1;
        for (String command : rightCommands) {
            JSONMessage.create(color("&c" + rightCount + " &7- &7" + command.replace("[", "&8[&c").replace("]", "&8]&b"))).suggestCommand("/npcmd edit cmd right " + rightCount + " ").tooltip(plugin.getLang().getMessage(Path.LIST_TOOLTIP)).send(player);
            rightCount++;
        }

        player.sendMessage("");
        player.sendMessage(plugin.getLang().getMessage(Path.LIST_COUNT_LEFT).replace("{count}", String.valueOf(leftCommands.size())));

        int leftCount = 1;
        for (String command : leftCommands) {
            JSONMessage.create(color("&c" + leftCount + " &7- &7" + command.replace("[", "&8[&c").replace("]", "&8]&b"))).suggestCommand("/npcmd edit cmd left " + leftCount + " ").tooltip(plugin.getLang().getMessage(Path.LIST_TOOLTIP)).send(player);
            leftCount++;
        }
    }

}
