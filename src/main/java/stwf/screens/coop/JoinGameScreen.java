package stwf.screens.coop;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.MultiplayerServiceResult;
import stwf.multiplayer.services.steam.SteamService.MultiplayerServiceId;
import stwf.screens.BaseScreenInterface;
import stwf.screens.components.LobbyListComponent;

public class JoinGameScreen implements BaseScreenInterface, MultiplayerServiceInterface.LobbyEventListener
{
    private MenuCancelButton returnButton;
    private LobbyListComponent lobbyList;
    private MultiplayerServiceInterface multiplayerService;

    public JoinGameScreen(MultiplayerServiceInterface multiplayerService)
    {
        returnButton = new MenuCancelButton();
        lobbyList = new LobbyListComponent();
        
        this.multiplayerService = multiplayerService;
    }

    public void open()
    {
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = CurScreenPatch.JOIN_GAME;

        returnButton.show(PatchNotesScreen.TEXT[0]);

        lobbyList.move(1000 * Settings.scale, 920 * Settings.yScale);

        multiplayerService.getLobbies(this);
    }

    public void close()
    {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        returnButton.hide();
    }

    public void update()
    {
        returnButton.update();

        if (returnButton.hb.clicked || InputHelper.pressedEscape)
        {
            returnButton.hb.clicked = false;
            InputHelper.pressedEscape = false;
            close();
        } 
    }

    public void render(SpriteBatch spriteBatch)
    {
        returnButton.render(spriteBatch);
        lobbyList.render(spriteBatch);
    }

    @Override
    public void onLobbyCreated(MultiplayerServiceResult result, MultiplayerServiceId id) {}

    @Override
    public void onLobbiesRequested(List<MultiplayerLobby> lobbies)
    {
        List<JoinGameScreenLobby> joinGameScreenLobbies = new ArrayList<>();

        for (MultiplayerLobby lobby : lobbies)
        {
            JoinGameScreenLobby newLobby = new JoinGameScreenLobby();
            newLobby.host = lobby.hostName;

            joinGameScreenLobbies.add(newLobby);
        }

        lobbyList.setLobbies(joinGameScreenLobbies);
    }
}
