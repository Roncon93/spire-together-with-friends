package stwf.screens.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import stwf.screens.coop.JoinGameScreen;

public class LobbyListComponent extends BaseComponent implements ButtonListenerInterface
{
    public interface LobbyListComponentEventInterface
    {
        void onLobbySelected(JoinGameScreen.Lobby lobby);
    }

    public LobbyListComponentEventInterface listener;

    private List<LobbyListItemComponent> lobbies;

    public LobbyListComponent()
    {
        lobbies = new CopyOnWriteArrayList<>();
    }

    public void clearLobbies()
    {
        lobbies.clear();
    }

    public void setLobbies(List<JoinGameScreen.Lobby> lobbies)
    {
        clearLobbies();

        for (JoinGameScreen.Lobby lobby : lobbies)
        {
            LobbyListItemComponent newLobby = new LobbyListItemComponent(lobby);
            newLobby.listener = this;

            this.lobbies.add(newLobby);
        }

        moveLobbies();
    }

    private static String GetLobbyUIString(int index)
    {
        return CardCrawlGame.languagePack.getUIString("Lobby").TEXT[index];
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

    private void updateLobbies()
    {
        for (LobbyListItemComponent lobby : lobbies)
        {
            lobby.update();
        }
    }

    @Override
    public void update()
    {
        updateLobbies();
    }

    private void renderHeaders(SpriteBatch spriteBatch)
    {
        FontHelper.renderFontLeftTopAligned(spriteBatch, FontHelper.smallDialogOptionFont, GetLobbyUIString(14), x, y, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(spriteBatch, FontHelper.smallDialogOptionFont, GetLobbyUIString(15), x + 160, y, Settings.CREAM_COLOR);
        FontHelper.renderFontLeftTopAligned(spriteBatch, FontHelper.smallDialogOptionFont, GetLobbyUIString(16), x + 500, y, Settings.CREAM_COLOR);
    }

    private void renderLobbies(SpriteBatch spriteBatch)
    {
        for (LobbyListItemComponent lobby : lobbies)
        {
            lobby.render(spriteBatch);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        renderHeaders(spriteBatch);
        renderLobbies(spriteBatch);
    }

    public void deselect()
    {
        for (LobbyListItemComponent lobby : lobbies)
        {
            lobby.isToggled = false;
        }
    }

    @Override
    public void onClick(BaseButtonComponent button)
    {
        for (LobbyListItemComponent lobby : lobbies)
        {
            if (lobby != button)
            {
                lobby.isToggled  = false;
            }
            else if (listener != null)
            {
                listener.onLobbySelected(lobby.getLobby());
            }
        }
    }
}
