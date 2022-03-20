package me.mattstudios.citizenscmd;

import ch.jalu.configme.Comment;
import ch.jalu.configme.SettingsHolder;
import ch.jalu.configme.configurationdata.CommentsConfiguration;
import ch.jalu.configme.properties.Property;
import ch.jalu.configme.properties.PropertyInitializer;

public final class Settings implements SettingsHolder {

    private Settings() {}

    @Comment({"", "Enables Checking for update."})
    public static final Property<Boolean> CHECK_UPDATES = PropertyInitializer
            .newProperty("check-updates", true);

    @Comment({"", "Available languages EN, PT, BG, RO, NO, CH"})
    public static final Property<String> LANG = PropertyInitializer
            .newProperty("lang", "EN");

    @Comment({"", "Toggle this on to enable using Minimessage style for message commands.", "https://docs.adventure.kyori.net/minimessage/format.html"})
    public static final Property<Boolean> MINI_MESSAGE = PropertyInitializer
            .newProperty("minimessage", false);

    @Comment({"", "Toggle this on to enable using Minimessage style for your language files.", "https://docs.adventure.kyori.net/minimessage/format.html"})
    public static final Property<Boolean> MINI_MESSAGE_LANG = PropertyInitializer
            .newProperty("minimessage-lang", false);

    @Comment({"", "The default npc cooldown in seconds"})
    public static final Property<Integer> DEFAULT_COOLDOWN = PropertyInitializer
            .newProperty("default-cooldown", 0);

    @Comment({"", "When using a NPC with price, true means that to confirm the use the player needs to seek or press shift"})
    public static final Property<Boolean> SHIT_CONFIRM = PropertyInitializer
            .newProperty("shift-confirm", true);

    @Comment({"", "Select cooldown display format, SHORT = 3m 3s | MEDIUM = 3 min 3 sec | FULL - 3 minutes 3 seconds"})
    public static final Property<String> TIME_DISPLAY = PropertyInitializer
            .newProperty("cooldown-time-display", "MEDIUM");

    @Comment({"", "Disables citizens check on startup"})
    public static final Property<Boolean> CITIZENS_CHECK = PropertyInitializer
            .newProperty("citizens-check", true);

    @Override
    public void registerComments(final CommentsConfiguration conf) {
        conf.setComment(
                "",
                "Citizens CMD Plugin by Mateus Moreira",
                "@LichtHund",
                "Version ${project.version}",
                "Wiki: https://github.com/ipsk/CitizensCMD/wiki",
                "GitHub: https://github.com/ipsk/CitizensCMD",
                "Spigot: https://www.spigotmc.org/resources/citizens-cmd.30224/",
                ""
        );
    }
}
