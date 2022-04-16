package stwf.multiplayer;

public class LobbyPlayer extends Player
{
    public boolean isReady;

    public LobbyPlayer()
    {
        isReady = false;        
    }

    public LobbyPlayer(Player player)
    {
        this();
        
        profile = player.profile;
        character = player.character;        
    }
}
