package stwf.screens.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import stwf.screens.coop.JoinGameScreenLobby;

public class LobbyListComponent extends BaseComponent
{
    private List<LobbyListItemComponent> lobbies;

    public LobbyListComponent()
    {
        lobbies = new CopyOnWriteArrayList<>();
    }

    public void clearLobbies()
    {
        lobbies.clear();
    }

    public void setLobbies(List<JoinGameScreenLobby> lobbies)
    {
        clearLobbies();

        for (JoinGameScreenLobby lobby : lobbies)
        {
            this.lobbies.add(new LobbyListItemComponent(lobby));
        }

        moveLobbies();
    }

    private static String GetLobbyUIString(int index)
    {
        return CardCrawlGame.languagePack.getUIString("Lobby").TEXT[index];
    }

    private void renderHeaders(SpriteBatch spriteBatch)
    {
        FontHelper.renderFontLeftTopAligned(spriteBatch, FontHelper.smallDialogOptionFont, GetLobbyUIString(14), x, y, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(spriteBatch, FontHelper.smallDialogOptionFont, GetLobbyUIString(15), x + 160, y, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(spriteBatch, FontHelper.smallDialogOptionFont, GetLobbyUIString(16), x + 500, y, Settings.CREAM_COLOR);
    }

    private void renderLobbies(SpriteBatch spriteBatch)
    {
        for (int index = 0; index < lobbies.size(); index++)
        {
            lobbies.get(index).render(spriteBatch);    
        }
    }

    private void moveLobbies()
    {
        float offsetY = 64;

        for (int index = 0; index < lobbies.size(); index++)
        {
            float spacing = 32 * index;

            lobbies.get(index).move(x, y - offsetY - spacing);            
        }
    }

    @Override
    public void move(float x, float y)
    {
        super.move(x, y);
        moveLobbies();
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        renderHeaders(spriteBatch);
        renderLobbies(spriteBatch);
    }
}
