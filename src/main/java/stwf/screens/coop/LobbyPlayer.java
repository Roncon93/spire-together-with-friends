package stwf.screens.coop;

import stwf.multiplayer.Player;

public class LobbyPlayer
{
    public boolean isReady;
    public Player player;

    public LobbyPlayer()
    {
        this(new Player());
    }

    public LobbyPlayer(Player player)
    {
        isReady = false;
        this.player = player;      
    }
}
