package stwf.screens.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import stwf.screens.coop.JoinGameScreenLobby;

public class LobbyListItemComponent extends BaseComponent
{
    private JoinGameScreenLobby lobby;

    public LobbyListItemComponent(JoinGameScreenLobby lobby)
    {
        this.lobby = lobby;
        System.out.println(lobby.host);
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        FontHelper.renderFontLeftTopAligned(spriteBatch, FontHelper.leaderboardFont, lobby.host, x + 160, y, Settings.CREAM_COLOR);
    }   
}
