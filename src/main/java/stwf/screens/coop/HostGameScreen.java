package stwf.screens.coop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;

import stwf.multiplayer.LobbyPlayer;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.screens.BaseScreenInterface;
import stwf.screens.components.BaseButtonComponent;
import stwf.screens.components.ButtonListenerInterface;
import stwf.screens.components.CharacterSelectComponent;
import stwf.screens.components.LabelComponent;
import stwf.screens.components.PlayerListComponent;
import stwf.screens.components.CharacterSelectComponent.CharacterSelectComponentListener;

public class HostGameScreen implements BaseScreenInterface, CharacterSelectComponentListener
{
    public MenuCancelButton returnButton;
    public PlayerListComponent playerList;
    public CharacterSelectComponent characterSelect;
    public LabelComponent titleLabel;

    private Color bgCharColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    private float bg_y_offset = 0.0F;

    private Texture selectedCharacterPortraitImage;
    private LobbyPlayer localPlayer;

    public HostGameScreen(MultiplayerServiceInterface multiplayerService)
    {
        returnButton = new MenuCancelButton();
        playerList = new PlayerListComponent();
        characterSelect = new CharacterSelectComponent();
        titleLabel = new LabelComponent(CardCrawlGame.languagePack.getUIString("Lobby").TEXT[13]);

        characterSelect.listener = this;

        titleLabel.font = FontHelper.SCP_cardTitleFont_small;
        titleLabel.color = Settings.GOLD_COLOR;
        
        localPlayer = new LobbyPlayer();
        localPlayer.profile = multiplayerService.getLocalPlayerProfile();
        playerList.AddPlayer(localPlayer);
    }

    @Override
    public void open()
    {
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = CurScreenPatch.HOST_GAME;

        returnButton.show(PatchNotesScreen.TEXT[0]);

        playerList.move(Settings.WIDTH * 0.22f, Settings.HEIGHT * 0.82F);
        characterSelect.move(Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.31F);
        titleLabel.move(Settings.WIDTH / 2.0F, Settings.HEIGHT - 70.0F * Settings.scale);

        playerList.setReadyButtonDisabled(true);

        setPlayerListReadyButtonListener();
    }

    @Override
    public void close()
    {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        
        returnButton.hide();
        characterSelect.deselect();

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

    private void setPlayerListReadyButtonListener()
    {
        playerList.setReadyButtonListener(new ButtonListenerInterface()
        {
            @Override
            public void onClick(BaseButtonComponent button)
            {
                localPlayer.isReady = !localPlayer.isReady;

                characterSelect.setDisabled(localPlayer.isReady);
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
        playerList.render(spriteBatch);
        characterSelect.render(spriteBatch);
        titleLabel.render(spriteBatch);
    }
}
