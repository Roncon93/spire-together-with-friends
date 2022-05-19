package stwf.multiplayer;

import java.util.Iterator;

import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;

public class MultiplayerManager
{
    private static MultiplayerServiceInterface multiplayerService;
    private static MultiplayerLobby lobby;

    public static void setMultiplayerService(MultiplayerServiceInterface multiplayerService)
    {
        MultiplayerManager.multiplayerService = multiplayerService;
    }

    public static void addLobbyCallback(MultiplayerServiceLobbyCallback callback)
    {
        multiplayerService.addLobbyCallback(callback);
    }

    public static void removeLobbyCallback(MultiplayerServiceLobbyCallback callback)
    {
        multiplayerService.removeLobbyCallback(callback);
    }

    public static void sendLobbyData(String key, String value)
    {
        multiplayerService.sendLobbyData(lobby.id, key, value);
    }

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

    public static boolean isLocalPlayerHost()
    {
        return true;        
    }
}
