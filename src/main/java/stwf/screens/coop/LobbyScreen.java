package stwf.screens.coop;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;

import stwf.multiplayer.LobbyPlayer;
import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.Player;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.screens.BaseScreenInterface;
import stwf.screens.components.PlayerListComponent;
import stwf.screens.mainMenu.MainMenuScreenPatch;

public class LobbyScreen implements BaseScreenInterface
{
    private MenuCancelButton returnButton;
    private PlayerListComponent playerList;
    private MultiplayerServiceInterface multiplayerService;
    private MultiplayerLobby lobby;

    public LobbyScreen(MultiplayerServiceInterface multiplayerService, HashMap<String, Object> data)
    {
        this.multiplayerService = multiplayerService;

        returnButton = new MenuCancelButton();
        playerList = new PlayerListComponent();

        if (data != null && data.containsKey("lobbyId"))
        {
            MultiplayerId lobbyId = (MultiplayerId)data.get("lobbyId");
            lobby = multiplayerService.getLobby(lobbyId);
        }
    }

    @Override
    public void open()
    {
        returnButton.show(PatchNotesScreen.TEXT[0]);

        playerList.move(Settings.WIDTH * 0.22f, Settings.HEIGHT * 0.82F);
        playerList.setReadyButtonDisabled(true);
        playerList.setReady(false);

        
        for (Player player : lobby.players)
        {
            playerList.add(new LobbyPlayer(player));    
        }
    }

    @Override
    public void close()
    {
        MainMenuScreenPatch.setCurrentScreen(CurScreenPatch.JOIN_GAME);
        returnButton.hide();
        playerList.clear();

        multiplayerService.leaveLobby(lobby.id);
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

        playerList.update();
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        returnButton.render(spriteBatch);
        playerList.render(spriteBatch);
    }
}
