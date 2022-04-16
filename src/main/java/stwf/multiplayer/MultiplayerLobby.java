package stwf.multiplayer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public class MultiplayerLobby
{
    public MultiplayerId id;

    public MultiplayerId hostId;

    public String hostName;

    public List<Player> players;

    public MultiplayerLobby()
    {
        players = new CopyOnWriteArrayList<>();
    }
}
