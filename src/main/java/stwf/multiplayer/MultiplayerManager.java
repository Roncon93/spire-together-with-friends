package stwf.multiplayer;

import java.util.Iterator;

public class MultiplayerManager
{
    private static MultiplayerLobby lobby;

    public static boolean inMultiplayerLobby()
    {
        return lobby != null;
    }

    public static void setLobby(MultiplayerLobby lobby)
    {
        MultiplayerManager.lobby = lobby;
    }

    public static Iterator<Player> getPlayers()
    {
        return lobby.players.iterator();
    }
}
