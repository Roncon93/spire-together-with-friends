package stwf.screens.coop;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.Player;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.screens.BaseScreenInterface;
import stwf.screens.components.BaseButtonComponent;
import stwf.screens.components.ButtonListenerInterface;
import stwf.screens.components.CharacterSelectComponent;
import stwf.screens.components.PlayerListComponent;
import stwf.screens.components.CharacterSelectComponent.CharacterSelectComponentListener;
import stwf.screens.mainMenu.MainMenuScreenPatch;
import stwf.utils.StringUtils;

public class LobbyScreen implements BaseScreenInterface, CharacterSelectComponentListener, MultiplayerServiceLobbyCallback
{
    private static final Color BACKGROUND_COLOR = new Color(1.0F, 1.0F, 1.0F, 1.0F);

    protected MenuCancelButton returnButton;
    protected PlayerListComponent playerList;
    protected CharacterSelectComponent characterSelect;
    protected MultiplayerServiceInterface multiplayerService;
    protected MultiplayerLobby lobby;
    protected Texture selectedCharacterPortraitImage;
    protected LobbyPlayer localPlayer;

    public LobbyScreen(MultiplayerServiceInterface multiplayerService)
    {
        this.multiplayerService = multiplayerService;

        returnButton = new MenuCancelButton();
        playerList = new PlayerListComponent();
        characterSelect = new CharacterSelectComponent();
        characterSelect.listener = this;

        playerList.addReadyButtonListener(new ButtonListenerInterface()
        {
            @Override
            public void onClick(BaseButtonComponent button)
            {
                localPlayer.isReady = !localPlayer.isReady;
                characterSelect.setDisabled(localPlayer.isReady);
                multiplayerService.sendPlayerData(lobby.id, "lobby.ready", Boolean.toString(localPlayer.isReady));
            }
        });
    }

    protected void addPlayersFromLobby()
    {
        if (lobby != null)
        {
            for (Player player : lobby.players)
            {
                LobbyPlayer lobbyPlayer = new LobbyPlayer(player);
                
                String playerCharacter = multiplayerService.getPlayerData(lobby.id, player.profile.id, "lobby.character");
                String playerReadyStatus = multiplayerService.getPlayerData(lobby.id, player.profile.id, "lobby.ready");

                if (!StringUtils.isNullOrEmpty(playerCharacter))
                {
                    lobbyPlayer.player.character = parseCharacter(playerCharacter);
                }

                if (!StringUtils.isNullOrEmpty(playerReadyStatus))
                {
                    lobbyPlayer.isReady = Boolean.parseBoolean(playerReadyStatus);
                }

                if (player.isLocal)
                {
                    localPlayer = lobbyPlayer;
                    playerList.add(localPlayer);
                }
                else
                {
                    playerList.add(lobbyPlayer);  
                }
            }
        }
    }

    @Override
    public void open(HashMap<String, Object> data)
    {
        if (data != null && data.containsKey("lobbyId"))
        {
            MultiplayerId lobbyId = (MultiplayerId)data.get("lobbyId");
            lobby = multiplayerService.getLobby(lobbyId);
        }

        returnButton.show(PatchNotesScreen.TEXT[0]);

        playerList.move(Settings.WIDTH * 0.22f, Settings.HEIGHT * 0.82F);
        playerList.setReadyButtonDisabled(true);
        playerList.setReady(false);

        addPlayersFromLobby();

        characterSelect.move(Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.31F);
        characterSelect.enable();
        characterSelect.deselect();

        multiplayerService.addLobbyCallback(this);
    }

    @Override
    public void close()
    {
        MainMenuScreenPatch.setCurrentScreen(CurScreenPatch.JOIN_GAME);
        returnButton.hide();
        playerList.clear();

        multiplayerService.removeLobbyCallback(this);
        multiplayerService.leaveLobby(lobby.id);

        lobby = null;

        dispose();
    }

    protected void dispose()
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

        multiplayerService.sendPlayerData(lobby.id, "lobby.character", character.chosenClass.toString());
    }

    @Override
    public void onPlayerJoined(MultiplayerId lobbyId, MultiplayerId playerId)
    {
        playerList.add(new LobbyPlayer(multiplayerService.getPlayer(playerId)));
    }

    @Override
    public void onPlayerLeft(MultiplayerId lobbyId, MultiplayerId playerId)
    {
        LobbyPlayer player = new LobbyPlayer();
        player.player.profile.id = playerId;

        playerList.remove(player);
    }

    @Override
    public void onPlayerDataReceived(MultiplayerId lobbyId, MultiplayerId playerId, String key, String value)
    {
        LobbyPlayer lobbyPlayer = playerList.get(playerId);

        switch (key)
        {
            case "lobby.character":
                onPlayerSelectedCharacterUpdated(lobbyPlayer, parseCharacter(value));
                break;

            case "lobby.ready":
                onPlayerReadyStatusUpdated(lobbyPlayer, Boolean.parseBoolean(value));
                break;
        }
    }

    protected void onPlayerSelectedCharacterUpdated(LobbyPlayer lobbyPlayer, AbstractPlayer character)
    {
        lobbyPlayer.player.character = character;
    }

    protected void onPlayerReadyStatusUpdated(LobbyPlayer lobbyPlayer, boolean isReady)
    {
        lobbyPlayer.isReady = isReady;
    }

    protected AbstractPlayer parseCharacter(String playerClass)
    {
        return CardCrawlGame.characterManager.getCharacter(PlayerClass.valueOf(playerClass));
    }
}
