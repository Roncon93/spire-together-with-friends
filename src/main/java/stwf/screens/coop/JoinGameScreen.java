package stwf.screens.coop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.MultiplayerServiceResult;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbiesRequestedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyJoinedCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.screens.BaseScreenInterface;
import stwf.screens.components.LobbyListComponent;
import stwf.screens.components.LobbyListComponent.LobbyListComponentEventInterface;
import stwf.screens.mainMenu.MainMenuScreenPatch;

public class JoinGameScreen implements BaseScreenInterface, LobbyListComponentEventInterface, MultiplayerServiceOnLobbiesRequestedCallback, MultiplayerServiceOnLobbyJoinedCallback
{
    private MenuCancelButton returnButton;
    private GridSelectConfirmButton joinButton;
    private LobbyListComponent lobbyList;
    private MultiplayerServiceInterface multiplayerService;
    private List<MultiplayerLobby> lobbies;
    private MultiplayerLobby selectedLobby;
    private boolean lobbyJoined;

    public JoinGameScreen(MultiplayerServiceInterface multiplayerService)
    {
        returnButton = new MenuCancelButton();
        joinButton = new GridSelectConfirmButton(CardCrawlGame.languagePack.getUIString("Lobby").TEXT[0]);
        lobbyList = new LobbyListComponent();

        lobbyList.listener = this;
        
        this.multiplayerService = multiplayerService;
    }

    @Override
    public void open(HashMap<String, Object> data)
    {
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = CurScreenPatch.JOIN_GAME;

        returnButton.show(PatchNotesScreen.TEXT[0]);
        joinButton.show();
        joinButton.isDisabled = true;

        lobbyList.move(1000 * Settings.scale, 920 * Settings.yScale);

        multiplayerService.getLobbies(this);

        lobbyJoined = false;
    }

    @Override
    public void close()
    {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        returnButton.hide();
        joinButton.hide();
        lobbyList.clearLobbies();
    }

    @Override
    public void update()
    {
        returnButton.update();
        if (returnButton.hb.clicked || InputHelper.pressedEscape)
        {
            returnButton.hb.clicked = false;
            InputHelper.pressedEscape = false;
            close();
        }

        joinButton.update();
        if (joinButton.hb.clicked)
        {
            joinButton.hb.clicked = false;

            multiplayerService.joinLobby(selectedLobby.id, this);
        }

        lobbyList.update();

        if (lobbyJoined)
        {
            MainMenuScreenPatch.setData("lobbyId", selectedLobby.id);
            MainMenuScreenPatch.setCurrentScreen(CurScreenPatch.LOBBY);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        returnButton.render(spriteBatch);
        joinButton.render(spriteBatch);
        lobbyList.render(spriteBatch);
    }

    @Override
    public void onLobbyJoined(MultiplayerServiceResult result, MultiplayerId id)
    {
        if (result == MultiplayerServiceResult.OK)
        {
            lobbyJoined = true;
        }
    }

    @Override
    public void onLobbiesRequested(List<MultiplayerLobby> lobbies)
    {
        this.lobbies = lobbies;

        List<Lobby> joinGameScreenLobbies = new ArrayList<>();

        for (MultiplayerLobby lobby : lobbies)
        {
            Lobby newLobby = new Lobby();
            newLobby.id = lobby.id;
            newLobby.host = lobby.hostName;

            joinGameScreenLobbies.add(newLobby);
        }

        lobbyList.setLobbies(joinGameScreenLobbies);
    }

    @Override
    public void onLobbySelected(Lobby lobby)
    {
        joinButton.isDisabled = false;

        for (MultiplayerLobby multiplayerLobby : lobbies)
        {
            if (lobby.id == multiplayerLobby.id)
            {
                selectedLobby = multiplayerLobby;
            }   
        }
    }

    public class Lobby
    {
        public MultiplayerId id;

        public String host;    
    }
}
