package me.mattstudios.citizenscmd.utility;

public enum Messages {

    /**
     * START UP
     */

    USING_LANGUAGE("messages.start-up.using-language"),
    STARTUP_NEW_VERSION("messages.start-up.new-version"),
    PAPI_AVAILABLE("messages.start-up.papi-available"),
    VAULT_AVAILABLE("messages.start-up.vault-available"),

    /**
     * COMMANDS
     */

    NPC_ADDED("messages.commands.npc-add-command-added"),
    NPC_ADD_FAIL("messages.commands.npc-add-command-failed"),
    NPC_ADD_DELAY_FAIL("messages.commands.npc-add-command-delay-failed"),
    NPC_COOLDOWN_SET("messages.commands.npc-cooldown-set"),
    NPC_COOLDOWN_SET_ERROR("messages.commands.npc-cooldown-error"),
    NPC_PRICE_SET("messages.commands.npc-price-set"),
    LIST_COUNT_RIGHT("messages.commands.list-commands-counter-right"),
    LIST_COUNT_LEFT("messages.commands.list-commands-counter-left"),
    LIST_TOOLTIP("messages.commands.list-tooltip"),
    LIST_COOLDOWN("messages.commands.list-cooldown"),
    LIST_PRICE("messages.commands.list-price"),
    RELOAD("messages.commands.reload-command"),
    REMOVED_COMMAND("messages.commands.removed-command"),
    EDITED_COMMAND("messages.commands.edit-command"),
    PERMISSION_SET("messages.commands.set-permission"),
    PERMISSION_REMOVED("messages.commands.remove-permission"),

    /**
     * WARNINGS
     */

    NO_NPC("messages.warnings.no-npc-selected"),
    INVALID_NUMBER("messages.warnings.invalid-number"),
    INVALID_ID_NUMBER("messages.warnings.invalid-id"),
    INVALID_CLICK_TYPE("messages.warnings.invalid-click-type"),
    NO_COMMANDS("messages.warnings.no-commands"),
    INVALID_ARGUMENTS("messages.warnings.invalid-arguments"),
    INVALID_PERMISSION("messages.warnings.invalid-permission"),
    CONSOLE_NOT_ALLOWED("messages.warnings.console-not-allowed"),
    NO_PERMISSION("messages.warnings.no-permission"),
    WRONG_USAGE("messages.warnings.wrong-usage"),
    NEW_VERSION("messages.warnings.new-version"),
    DOWNLOAD_AT("messages.warnings.download-at"),

    /**
     * NPCS
     */

    ON_COOLDOWN("messages.npc.on-cooldown"),
    ONE_TIME_CLICK("messages.npc.one-time-click"),
    PAY_CONFIRM("messages.npc.pay-confirm"),
    PAY_CANCELED("messages.npc.pay-canceled"),
    PAY_NO_MONEY("messages.npc.pay-no-money"),
    PAY_COMPLETED("messages.npc.pay-completed"),
    MESSAGE_DISPLAY("messages.npc.message-display"),

    /**
     * Help
     */

    HELP_VERSION("messages.help.version"),
    HELP_INFO("messages.help.info"),
    HELP_EXAMPLE("messages.help.example"),
    HELP_DESCRIPTION_ADD("messages.help.description-add"),
    HELP_DESCRIPTION_COOLDOWN("messages.help.description-cooldown"),
    HELP_DESCRIPTION_PRICE("messages.help.description-price"),
    HELP_DESCRIPTION_LIST("messages.help.description-list"),
    HELP_DESCRIPTION_EDIT("messages.help.description-edit"),
    HELP_DESCRIPTION_REMOVE("messages.help.description-remove"),
    HELP_DESCRIPTION_RELOAD("messages.help.description-reload"),

    /**
     * Time format
     */

    SECONDS("messages.time-format.seconds"),
    MINUTES("messages.time-format.minutes"),
    HOURS("messages.time-format.hours"),
    DAYS("messages.time-format.days");

    private final String path;

    Messages(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
