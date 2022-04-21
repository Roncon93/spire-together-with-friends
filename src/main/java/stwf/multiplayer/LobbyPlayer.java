package stwf.multiplayer;

public class LobbyPlayer
{
    public boolean isReady;
    public Player player;

    public LobbyPlayer()
    {
        isReady = false;        
    }

    public LobbyPlayer(Player player)
    {
        this();
        
        this.player = player;      
    }
}
