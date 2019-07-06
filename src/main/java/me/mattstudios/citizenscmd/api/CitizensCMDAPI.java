package me.mattstudios.citizenscmd.api;

import me.mattstudios.citizenscmd.files.DataHandler;

public class CitizensCMDAPI {

    private final DataHandler dataHandler;

    public CitizensCMDAPI (DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    public void addCommand(int npcID, String permission, String command, boolean left) {
        dataHandler.addCommand(npcID, permission, command, left);
    }

    public void removeNPCData(int npcID) {
        dataHandler.removeNPCData(npcID);
    }

}
