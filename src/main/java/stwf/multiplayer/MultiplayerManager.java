package stwf.multiplayer;

import java.util.ArrayList;
import java.util.Iterator;

import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;

public class MultiplayerManager
{
    private static MultiplayerServiceInterface multiplayerService;
    private static MultiplayerLobby lobby;

    public static boolean isFinalActAvailable = false;

    public static void setMultiplayerService(MultiplayerServiceInterface multiplayerService)
    {
        MultiplayerManager.multiplayerService = multiplayerService;
    }

    public static void addLobbyCallback(MultiplayerServiceLobbyCallback callback)
    {
        if (multiplayerService != null)
        {
            multiplayerService.addLobbyCallback(callback);
        }
    }

    public static void removeLobbyCallback(MultiplayerServiceLobbyCallback callback)
    {
        if (multiplayerService != null)
        {
            multiplayerService.removeLobbyCallback(callback);
        }
    }

    public static void sendLobbyData(String key, String value)
    {
        if (multiplayerService != null)
        {
            multiplayerService.sendLobbyData(lobby.id, key, value);
        }
    }

    public static boolean inMultiplayerLobby()
    {
        return lobby != null;
    }

    public static boolean isInDungeon()
    {
        if (lobby == null)
        {
            return false;
        }

        return Boolean.parseBoolean(multiplayerService.getLobbyData(lobby.id, "lobby.phase.dungeon"));
    }

    public static boolean setInDungeon()
    {
        if (lobby == null)
        {
            return false;
        }

        return multiplayerService.sendLobbyData(lobby.id, "lobby.phase.dungeon", Boolean.toString(true));
    }

    public static void setLobby(MultiplayerLobby lobby)
    {
        MultiplayerManager.lobby = lobby;
    }

    public static Iterator<Player> getPlayers()
    {
        if (lobby == null)
        {
            return new ArrayList<Player>().iterator();
        }

        return lobby.players.iterator();
    }

    public static boolean isLocalPlayerHost()
    {
        return true;        
    }
}
