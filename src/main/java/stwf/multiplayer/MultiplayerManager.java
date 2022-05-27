package stwf.multiplayer;

import java.util.ArrayList;
import java.util.Iterator;

import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

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

    public static void addLobbyCallback(MultiplayerServiceLobbyCallback callback, String... keys)
    {
        if (multiplayerService != null)
        {
            multiplayerService.addLobbyCallback(callback, keys);
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

    public static void sendPlayerData(String key, String value)
    {
        if (multiplayerService != null)
        {
            multiplayerService.sendPlayerData(lobby.id, key, value);
        }
    }

    public static void sendPlayerData(String key, String value, boolean persist)
    {
        if (multiplayerService != null)
        {
            multiplayerService.sendPlayerData(lobby.id, key, value, persist);
        }
    }

    public static String getLobbyData(String key)
    {
        if (multiplayerService != null)
        {
            return multiplayerService.getLobbyData(lobby.id, key);
        }

        return null;
    }

    public static String getPlayerData(MultiplayerId playerId, String key)
    {
        if (multiplayerService != null)
        {
            return multiplayerService.getPlayerData(lobby.id, playerId, key);
        }

        return null;
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

        multiplayerService.sendLobbyData(lobby.id, "lobby.phase.dungeon", Boolean.toString(true));
        return true;
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

    public static int getPlayerIndex(Player player)
    {
        int index = lobby.players.indexOf(player);
        return index != -1 ? index : 0;
    }

    public static Player getPlayer(MultiplayerId playerId)
    {
        for (Player player : lobby.players)
        {
            if (player.profile.id.equals(playerId))
            {
                return player;
            }
        }

        return null;
    }

    public static Player getLocalPlayer()
    {
        for (Player player : lobby.players)
        {
            if (player.isLocal)
            {
                return player;
            }
        }

        return null;
    }

    public static boolean isLocalPlayerHost()
    {
        return true;        
    }
}
