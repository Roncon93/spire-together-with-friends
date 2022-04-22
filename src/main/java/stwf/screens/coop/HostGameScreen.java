package stwf.screens.coop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;

import javassist.expr.Instanceof;
import stwf.multiplayer.LobbyPlayer;
import stwf.multiplayer.Actions.CharacterSelectedAction;
import stwf.multiplayer.Actions.MultiplayerAction;
import stwf.multiplayer.Actions.ReadyStatusUpdatedAction;
import stwf.multiplayer.services.MultiplayerLobbyType;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.MultiplayerServiceResult;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyCreatedCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.screens.BaseScreenInterface;
import stwf.screens.components.BaseButtonComponent;
import stwf.screens.components.ButtonListenerInterface;
import stwf.screens.components.CharacterSelectComponent;
import stwf.screens.components.LabelComponent;
import stwf.screens.components.PlayerListComponent;
import stwf.screens.components.CharacterSelectComponent.CharacterSelectComponentListener;

public class HostGameScreen implements BaseScreenInterface, CharacterSelectComponentListener, MultiplayerServiceOnLobbyCreatedCallback, MultiplayerServiceLobbyCallback
{
    public MenuCancelButton returnButton;
    public GridSelectConfirmButton embarkButton;
    public PlayerListComponent playerList;
    public CharacterSelectComponent characterSelect;
    public LabelComponent titleLabel;

    private Color bgCharColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);

    private Texture selectedCharacterPortraitImage;
    private LobbyPlayer localPlayer;

    private MultiplayerServiceInterface multiplayerService;
    private MultiplayerId lobbyId;

    public HostGameScreen(MultiplayerServiceInterface multiplayerService)
    {
        returnButton = new MenuCancelButton();
        embarkButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);
        playerList = new PlayerListComponent();
        characterSelect = new CharacterSelectComponent();
        titleLabel = new LabelComponent(CardCrawlGame.languagePack.getUIString("Lobby").TEXT[13]);

        characterSelect.listener = this;

        titleLabel.font = FontHelper.SCP_cardTitleFont_small;
        titleLabel.color = Settings.GOLD_COLOR;
        
        localPlayer = new LobbyPlayer();
        localPlayer.player.profile = multiplayerService.getLocalPlayerProfile();

        this.multiplayerService = multiplayerService;
    }

    @Override
    public void open()
    {
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = CurScreenPatch.HOST_GAME;

        returnButton.show(PatchNotesScreen.TEXT[0]);
        embarkButton.show();
        embarkButton.isDisabled = true;

        playerList.move(Settings.WIDTH * 0.22f, Settings.HEIGHT * 0.82F);
        characterSelect.move(Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.31F);
        titleLabel.move(Settings.WIDTH / 2.0F, Settings.HEIGHT - 70.0F * Settings.scale);

        characterSelect.enable();
        characterSelect.deselect();

        playerList.setReadyButtonDisabled(true);
        playerList.setReady(false);
        playerList.clear();
        playerList.add(localPlayer);

        setPlayerListReadyButtonListener();

        localPlayer.isReady = false;

        multiplayerService.addLobbyCallback(this);
        multiplayerService.createLobby(MultiplayerLobbyType.PUBLIC, 2, this);
    }

    @Override
    public void close()
    {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        
        returnButton.hide();
        embarkButton.hide();
        playerList.clear();

        dispose();

        multiplayerService.removeLobbyCallback(this);
        multiplayerService.leaveLobby(lobbyId);
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
    public void onLobbyCreated(MultiplayerServiceResult result, MultiplayerId id)
    {
        if (result == MultiplayerServiceResult.OK)
        {
            lobbyId = id;

            multiplayerService.setLobbyData(id, "hostName", localPlayer.player.profile.username);
        }
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

    private void setPlayerListReadyButtonListener()
    {
        playerList.addReadyButtonListener(new ButtonListenerInterface()
        {
            @Override
            public void onClick(BaseButtonComponent button)
            {
                localPlayer.isReady = !localPlayer.isReady;

                characterSelect.setDisabled(localPlayer.isReady);

                //embarkButton.isDisabled = !localPlayer.isReady;

                multiplayerService.sendPlayerAction(lobbyId, new ReadyStatusUpdatedAction(localPlayer.isReady));
            }
        });
    }

    @Override
    public void onPlayerActionReceived(MultiplayerId lobbyId, MultiplayerId playerId, MultiplayerAction action)
    {
        LobbyPlayer player = new LobbyPlayer();
        player.player.profile.id = playerId;
        player = playerList.get(player);

        if (player == null)
        {
            return;
        }

        if (action instanceof CharacterSelectedAction)
        {
            player.player.character = CardCrawlGame.characterManager.getCharacter(((CharacterSelectedAction)action).value);
        }
        else if (action instanceof ReadyStatusUpdatedAction)
        {
            player.isReady = ((ReadyStatusUpdatedAction)action).value;
        }
    }

    @Override
    public void onCharacterSelected(AbstractPlayer character)
    {
        dispose();

        selectedCharacterPortraitImage = ImageMaster.loadImage("images/ui/charSelect/" + character.getPortraitImageName());
        character.doCharSelectScreenSelectEffect();

        playerList.setReadyButtonDisabled(false);

        multiplayerService.sendPlayerAction(lobbyId, new CharacterSelectedAction(character.chosenClass));
    }

    @Override
    public void update()
    {
        if (CardCrawlGame.mainMenuScreen.fadedOut)
        {
            close();
            return;
        }

        returnButton.update();
        embarkButton.update();

        if (returnButton.hb.clicked || InputHelper.pressedEscape)
        {
            returnButton.hb.clicked = false;
            InputHelper.pressedEscape = false;
            close();
        }
        else if (embarkButton.hb.clicked || CInputActionSet.proceed.isJustPressed())
        {
            embarkButton.hb.clicked = false;

            setRandomSeed();

            CardCrawlGame.chosenCharacter = localPlayer.player.character.chosenClass;

            CardCrawlGame.mainMenuScreen.isFadingOut = true;
            CardCrawlGame.mainMenuScreen.fadeOutMusic();
            Settings.isDailyRun = false;

            AbstractDungeon.isAscensionMode = false;
            AbstractDungeon.ascensionLevel = 0;

            AbstractDungeon.generateSeeds();

            CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        }

        playerList.update();
        characterSelect.update();
    }

    private void setRandomSeed()
    {
        long sourceTime = System.nanoTime();
        Random rng = new Random(Long.valueOf(sourceTime));
        Settings.seedSourceTimestamp = sourceTime;
        Settings.seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));
        Settings.seedSet = false;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        spriteBatch.setColor(this.bgCharColor);

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
        embarkButton.render(spriteBatch);
        playerList.render(spriteBatch);
        characterSelect.render(spriteBatch);
        titleLabel.render(spriteBatch);
    }
}
