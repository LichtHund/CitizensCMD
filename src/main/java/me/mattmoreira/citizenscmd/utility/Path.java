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

package me.mattmoreira.citizenscmd.utility;

public class Path {

    /**
     * COMMANDS
     */
    private static final String MAIN_PATH_COMMANDS = "messages.commands.";

    public static final String NPC_ADDED = MAIN_PATH_COMMANDS + "npc-add-command-added";
    public static final String NPC_ADD_FAIL = MAIN_PATH_COMMANDS + "npc-add-command-failed";
    public static final String NPC_COOLDOWN_SET = MAIN_PATH_COMMANDS + "npc-cooldown-set";
    public static final String NPC_COOLDOWN_SET_ERROR = MAIN_PATH_COMMANDS + "npc-cooldown-error";
    public static final String NPC_PRICE_SET = MAIN_PATH_COMMANDS + "npc-price-set";
    public static final String LIST_COUNT_RIGHT = MAIN_PATH_COMMANDS + "list-commnads-counter-right";
    public static final String LIST_COUNT_LEFT = MAIN_PATH_COMMANDS + "list-commnads-counter-left";
    public static final String LIST_TOOLTIP = MAIN_PATH_COMMANDS + "list-tooltip";
    public static final String LIST_COOLDOWN = MAIN_PATH_COMMANDS + "list-cooldown";
    public static final String LIST_PRICE = MAIN_PATH_COMMANDS + "list-price";
    public static final String RELOAD = MAIN_PATH_COMMANDS + "reload-command";
    public static final String REMOVED_COMMAND = MAIN_PATH_COMMANDS + "removed-command";
    public static final String EDITED_COMMAND = MAIN_PATH_COMMANDS + "edit-command";

    /**
     * WARNINGS
     */
    private static final String MAIN_PATH_WARNINGS = "messages.warnings.";

    static final String NO_NPC = MAIN_PATH_WARNINGS + "no-npc-selected";
    public static final String INVALID_COOLDOWN = MAIN_PATH_WARNINGS + "invalid-cooldown";
    public static final String INVALID_PRICE = MAIN_PATH_WARNINGS + "invalid-price";
    public static final String INVALID_ID_NUMBER = MAIN_PATH_WARNINGS + "invalid-id";
    public static final String INVALID_CLICK_TYPE = MAIN_PATH_WARNINGS + "invalid-click-type";
    public static final String NO_COMMANDS = MAIN_PATH_WARNINGS + "no-commands";
    public static final String INVALID_ARGUMENTS = MAIN_PATH_WARNINGS + "invalid-arguments";
    public static final String INVALID_PERMISSION = MAIN_PATH_WARNINGS + "invalid-permission";
    public static final String CONSOLE_NOT_ALLOWED = MAIN_PATH_WARNINGS + "console-not-allowed";
    public static final String NO_PERMISSION = MAIN_PATH_WARNINGS + "no-permission";
    public static final String WRONG_USAGE = MAIN_PATH_WARNINGS + "wrong-usage";
    public static final String NEW_VERSION = MAIN_PATH_WARNINGS + "new-version";
    public static final String DOWNLOAD_AT = MAIN_PATH_WARNINGS + "download-at";

    /**
     * NPCS
     */
    private static final String MAIN_PATH_NPCS = "messages.npc.";

    public static final String ON_COOLDOWN = MAIN_PATH_NPCS + "on-cooldown";
    public static final String ONE_TIME_CLICK = MAIN_PATH_NPCS + "one-time-click";
    public static final String PAY_CONFIRM = MAIN_PATH_NPCS + "pay-confirm";
    public static final String PAY_CANCELED = MAIN_PATH_NPCS + "pay-canceled";
    public static final String PAY_NO_MONEY = MAIN_PATH_NPCS + "pay-no-money";
    public static final String PAY_COMPLETED = MAIN_PATH_NPCS + "pay-completed";

    /**
     * Help
     */
    private static final String MAIN_PATH_HELP = "messages.help.";

    public static final String HELP_VERSION = MAIN_PATH_HELP + "version";
    public static final String HELP_INFO = MAIN_PATH_HELP + "info";
    public static final String HELP_EXAMPLE = MAIN_PATH_HELP + "example";
    public static final String HELP_DESCRIPTION_ADD = MAIN_PATH_HELP + "description-add";
    public static final String HELP_DESCRIPTION_COOLDOWN = MAIN_PATH_HELP + "description-cooldown";
    public static final String HELP_DESCRIPTION_PRICE = MAIN_PATH_HELP + "description-price";
    public static final String HELP_DESCRIPTION_LIST = MAIN_PATH_HELP + "description-list";
    public static final String HELP_DESCRIPTION_EDIT = MAIN_PATH_HELP + "description-edit";
    public static final String HELP_DESCRIPTION_REMOVE = MAIN_PATH_HELP + "description-remove";
    public static final String HELP_DESCRIPTION_RELOAD = MAIN_PATH_HELP + "description-reload";

    /**
     * Time format
     */
    private static final String MAIN_PATH_TIME_FORMAT = "messages.time-format.";

    static final String SECONDS = MAIN_PATH_TIME_FORMAT + "seconds";
    static final String MINUTES = MAIN_PATH_TIME_FORMAT + "minutes";
    static final String HOURS = MAIN_PATH_TIME_FORMAT + "hours";
    static final String DAYS = MAIN_PATH_TIME_FORMAT + "days";


}

