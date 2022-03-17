package stwf.screens.coop;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
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

import stwf.multiplayer.LobbyPlayer;
import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.services.MultiplayerLobbyType;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.MultiplayerServiceResult;
import stwf.multiplayer.services.steam.SteamService.MultiplayerServiceId;
import stwf.screens.BaseScreenInterface;
import stwf.screens.components.BaseButtonComponent;
import stwf.screens.components.ButtonListenerInterface;
import stwf.screens.components.CharacterSelectComponent;
import stwf.screens.components.LabelComponent;
import stwf.screens.components.PlayerListComponent;
import stwf.screens.components.CharacterSelectComponent.CharacterSelectComponentListener;

public class HostGameScreen implements BaseScreenInterface, CharacterSelectComponentListener, MultiplayerServiceInterface.LobbyEventListener
{
    public MenuCancelButton returnButton;
    public GridSelectConfirmButton embarkButton;
    public PlayerListComponent playerList;
    public CharacterSelectComponent characterSelect;
    public LabelComponent titleLabel;

    private Color bgCharColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    private float bg_y_offset = 0.0F;

    private Texture selectedCharacterPortraitImage;
    private LobbyPlayer localPlayer;

    private MultiplayerServiceInterface multiplayerService;
    private MultiplayerServiceId lobbyId;

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
        localPlayer.profile = multiplayerService.getLocalPlayerProfile();
        playerList.AddPlayer(localPlayer);

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

        setPlayerListReadyButtonListener();

        localPlayer.isReady = false;

        multiplayerService.createLobby(this, MultiplayerLobbyType.PUBLIC, 2);
    }

    @Override
    public void close()
    {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        
        returnButton.hide();
        embarkButton.hide();

        dispose();

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
    public void onLobbyCreated(MultiplayerServiceResult result, MultiplayerServiceId id)
    {
        if (result == MultiplayerServiceResult.OK)
        {
            lobbyId = id;

            multiplayerService.setLobbyData(id, "hostName", localPlayer.profile.username);
        }
    }

    @Override
    public void onLobbiesRequested(List<MultiplayerLobby> lobbies) {}

    private void setPlayerListReadyButtonListener()
    {
        playerList.setReadyButtonListener(new ButtonListenerInterface()
        {
            @Override
            public void onClick(BaseButtonComponent button)
            {
                localPlayer.isReady = !localPlayer.isReady;

                playerList.setReady(localPlayer.isReady);

                characterSelect.setDisabled(localPlayer.isReady);

                embarkButton.isDisabled = !localPlayer.isReady;
            }
        });
    }

    @Override
    public void onCharacterSelected(AbstractPlayer character)
    {
        dispose();

        selectedCharacterPortraitImage = ImageMaster.loadImage("images/ui/charSelect/" + character.getPortraitImageName());
        character.doCharSelectScreenSelectEffect();

        localPlayer.character = character;

        playerList.setReadyButtonDisabled(false);
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

            CardCrawlGame.chosenCharacter = localPlayer.character.chosenClass;

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
                spriteBatch.draw(selectedCharacterPortraitImage, Settings.WIDTH / 2.0F - 960.0F, Settings.HEIGHT / 2.0F - 600.0F + this.bg_y_offset, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.yScale, Settings.yScale, 0.0F, 0, 0, 1920, 1200, false, false);
            }
            else if (Settings.isLetterbox)
            {
                spriteBatch.draw(selectedCharacterPortraitImage, Settings.WIDTH / 2.0F - 960.0F, Settings.HEIGHT / 2.0F - 600.0F + this.bg_y_offset, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.xScale, Settings.xScale, 0.0F, 0, 0, 1920, 1200, false, false);
            }
            else
            {
                spriteBatch.draw(selectedCharacterPortraitImage, Settings.WIDTH / 2.0F - 960.0F, Settings.HEIGHT / 2.0F - 600.0F + this.bg_y_offset, 960.0F, 600.0F, 1920.0F, 1200.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1920, 1200, false, false);
            } 
        }

        returnButton.render(spriteBatch);
        embarkButton.render(spriteBatch);
        playerList.render(spriteBatch);
        characterSelect.render(spriteBatch);
        titleLabel.render(spriteBatch);
    }
}
