package stwf.multiplayer;

import com.megacrit.cardcrawl.characters.AbstractPlayer;

public class Player
{
    public PlayerProfile profile;
    public AbstractPlayer character;
    public boolean isLocal;
    public boolean endedTurn;
    public int order;

    public Player()
    {
        profile = new PlayerProfile();
    }
}
