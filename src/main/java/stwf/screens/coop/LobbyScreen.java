package stwf.screens.coop;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;

import stwf.multiplayer.LobbyPlayer;
import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.Player;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.screens.BaseScreenInterface;
import stwf.screens.components.BaseButtonComponent;
import stwf.screens.components.ButtonListenerInterface;
import stwf.screens.components.CharacterSelectComponent;
import stwf.screens.components.PlayerListComponent;
import stwf.screens.components.CharacterSelectComponent.CharacterSelectComponentListener;
import stwf.screens.mainMenu.MainMenuScreenPatch;

public class LobbyScreen implements BaseScreenInterface, CharacterSelectComponentListener
{
    private static final Color BACKGROUND_COLOR = new Color(1.0F, 1.0F, 1.0F, 1.0F);

    private MenuCancelButton returnButton;
    private PlayerListComponent playerList;
    private CharacterSelectComponent characterSelect;
    private MultiplayerServiceInterface multiplayerService;
    private MultiplayerLobby lobby;
    private Texture selectedCharacterPortraitImage;
    private LobbyPlayer localPlayer;

    public LobbyScreen(MultiplayerServiceInterface multiplayerService, HashMap<String, Object> data)
    {
        this.multiplayerService = multiplayerService;

        returnButton = new MenuCancelButton();
        playerList = new PlayerListComponent();
        characterSelect = new CharacterSelectComponent();
        characterSelect.listener = this;

        if (data != null && data.containsKey("lobbyId"))
        {
            MultiplayerId lobbyId = (MultiplayerId)data.get("lobbyId");
            lobby = multiplayerService.getLobby(lobbyId);
        }

        playerList.addReadyButtonListener(new ButtonListenerInterface()
        {
            @Override
            public void onClick(BaseButtonComponent button)
            {
                localPlayer.isReady = !localPlayer.isReady;
            }
        });
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
            if (player.isLocal)
            {
                localPlayer = new LobbyPlayer(player);
                playerList.add(localPlayer);
            }
            else
            {
                playerList.add(new LobbyPlayer(player));  
            }
        }

        characterSelect.move(Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.31F);
        characterSelect.enable();
        characterSelect.deselect();
    }

    @Override
    public void close()
    {
        MainMenuScreenPatch.setCurrentScreen(CurScreenPatch.JOIN_GAME);
        returnButton.hide();
        playerList.clear();

        multiplayerService.leaveLobby(lobby.id);

        dispose();
    }

    private void dispose()
    {
        if (selectedCharacterPortraitImage != null)
        {
            selectedCharacterPortraitImage.dispose();
            selectedCharacterPortraitImage = null;
        }
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
        characterSelect.update();
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        spriteBatch.setColor(BACKGROUND_COLOR);
        if (selectedCharacterPortraitImage != null)
        {
            if (Settings.isSixteenByTen)
            {
                spriteBatch.draw(selectedCharacterPortraitImage, Settings.WIDTH / 2.0F - 960.0F, Settings.HEIGHT / 2.0F - 600.0F, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1200, false, false);
            }
            else if (Settings.isFourByThree)
            {
                spriteBatch.draw(selectedCharacterPortraitImage, Settings.WIDTH / 2.0F - 960.0F, Settings.HEIGHT / 2.0F - 600.0F, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.yScale, Settings.yScale, 0.0F, 0, 0, 1920, 1200, false, false);
            }
            else if (Settings.isLetterbox)
            {
                spriteBatch.draw(selectedCharacterPortraitImage, Settings.WIDTH / 2.0F - 960.0F, Settings.HEIGHT / 2.0F - 600.0F, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.xScale, Settings.xScale, 0.0F, 0, 0, 1920, 1200, false, false);
            }
            else
            {
                spriteBatch.draw(selectedCharacterPortraitImage, Settings.WIDTH / 2.0F - 960.0F, Settings.HEIGHT / 2.0F - 600.0F, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1200, false, false);
            } 
        }

        returnButton.render(spriteBatch);
        playerList.render(spriteBatch);
        characterSelect.render(spriteBatch);
    }

    @Override
    public void onCharacterSelected(AbstractPlayer character)
    {
        dispose();

        selectedCharacterPortraitImage = ImageMaster.loadImage("images/ui/charSelect/" + character.getPortraitImageName());
        character.doCharSelectScreenSelectEffect();

        playerList.setReadyButtonDisabled(false);
        
        localPlayer.player.character = character;
    }
}
