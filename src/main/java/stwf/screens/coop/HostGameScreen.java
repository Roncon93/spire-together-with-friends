package stwf.screens.coop;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.SeedHelper;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.random.Random;
import com.megacrit.cardcrawl.screens.charSelect.CharacterSelectScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.ui.buttons.GridSelectConfirmButton;

import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.services.MultiplayerLobbyType;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.MultiplayerServiceResult;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyCreatedCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.screens.components.LabelComponent;

public class HostGameScreen extends LobbyScreen implements MultiplayerServiceOnLobbyCreatedCallback
{
    private GridSelectConfirmButton embarkButton;
    private LabelComponent titleLabel;

    public HostGameScreen(MultiplayerServiceInterface multiplayerService)
    {
        super(multiplayerService);

        embarkButton = new GridSelectConfirmButton(CharacterSelectScreen.TEXT[1]);

        titleLabel = new LabelComponent(CardCrawlGame.languagePack.getUIString("Lobby").TEXT[13]);
        titleLabel.font = FontHelper.SCP_cardTitleFont_small;
        titleLabel.color = Settings.GOLD_COLOR;
        
        localPlayer = new LobbyPlayer();
        localPlayer.player.profile = multiplayerService.getLocalPlayerProfile();

        this.multiplayerService = multiplayerService;
    }

    @Override
    public void open(HashMap<String, Object> data)
    {
        super.open(data);

        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = CurScreenPatch.HOST_GAME;

        embarkButton.show();
        embarkButton.isDisabled = true;

        titleLabel.move(Settings.WIDTH / 2.0F, Settings.HEIGHT - 70.0F * Settings.scale);

        localPlayer.isReady = false;

        multiplayerService.createLobby(MultiplayerLobbyType.PUBLIC, 2, this);
    }

    @Override
    public void close()
    {
        super.close();

        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        embarkButton.hide();
    }

    @Override
    public void onLobbyCreated(MultiplayerServiceResult result, MultiplayerId id)
    {
        if (result == MultiplayerServiceResult.OK)
        {
            lobby = multiplayerService.getLobby(id);
            MultiplayerManager.setLobby(lobby);
            MultiplayerManager.setMultiplayerService(multiplayerService);

            addPlayersFromLobby();
        }
    }

    @Override
    public void onPlayerJoined(MultiplayerId lobbyId, MultiplayerId playerId)
    {
        super.onPlayerJoined(lobbyId, playerId);
        updateEmbarkButtonDisabledStatus();
    }

    @Override
    public void onPlayerLeft(MultiplayerId lobbyId, MultiplayerId playerId)
    {
        super.onPlayerLeft(lobbyId, playerId);
        updateEmbarkButtonDisabledStatus();
    }

    @Override
    protected void onPlayerReadyStatusUpdated(LobbyPlayer lobbyPlayer, boolean isReady)
    {
        super.onPlayerReadyStatusUpdated(lobbyPlayer, isReady);
        updateEmbarkButtonDisabledStatus();
    }

    private void updateEmbarkButtonDisabledStatus()
    {
        embarkButton.isDisabled = !playerList.areAllPlayersReady();
    }

    private EmbarkMessage getGameSettings()
    {
        long sourceTime = System.nanoTime();
        Random rng = new Random(Long.valueOf(sourceTime));
        long seed = Long.valueOf(SeedHelper.generateUnoffensiveSeed(rng));

        Settings.setFinalActAvailability();
        return new EmbarkMessage(sourceTime, seed, Settings.isFinalActAvailable);
    }

    @Override
    public void update()
    {
        super.update();

        if (CardCrawlGame.mainMenuScreen.fadedOut)
        {
            close();
            return;
        }

        embarkButton.update();

        if (embarkButton.hb.clicked || CInputActionSet.proceed.isJustPressed())
        {
            embarkButton.hb.clicked = false;

            EmbarkMessage seed = getGameSettings();

            multiplayerService.sendPlayerData(lobby.id, "lobby.embark", JSON.toJson(seed));
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        super.render(spriteBatch);

        embarkButton.render(spriteBatch);
        titleLabel.render(spriteBatch);
    }
}
