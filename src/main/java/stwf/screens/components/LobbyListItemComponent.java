package stwf.screens.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import stwf.screens.coop.JoinGameScreen;

public class LobbyListItemComponent extends ToggleButtonComponent
{
    private JoinGameScreen.Lobby lobby;

    public LobbyListItemComponent(JoinGameScreen.Lobby lobby)
    {
        super();
        this.lobby = lobby;

        resize(600, 32);
    }

    public JoinGameScreen.Lobby getLobby()
    {
        return lobby;
    }

    @Override
    public void move(float x, float y)
    {
        super.move(x, y);
        hitbox.translate(x, y - 24);
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        Color color = Settings.CREAM_COLOR;

        if (isToggled)
        {
            color = Settings.GREEN_TEXT_COLOR;
        }
        else if (hitbox.hovered)
        {
            color = Settings.GOLD_COLOR;
        }        

        FontHelper.renderFontLeftTopAligned(spriteBatch, FontHelper.leaderboardFont, lobby.host, x + 160, y, color);

        hitbox.render(spriteBatch);
    }   
}
