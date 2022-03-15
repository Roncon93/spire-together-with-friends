package stwf.screens.coop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;

import stwf.screens.BaseScreenInterface;
import stwf.screens.components.CharacterSelectComponent;
import stwf.screens.components.PlayerListComponent;
import stwf.screens.components.CharacterSelectComponent.CharacterSelectComponentListener;

public class HostGameScreen implements BaseScreenInterface, CharacterSelectComponentListener
{
    public MenuCancelButton returnButton;
    public PlayerListComponent playerListComponent;
    public CharacterSelectComponent characterSelectComponent;

    private Color bgCharColor = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    private float bg_y_offset = 0.0F;

    private Texture selectedCharacterPortraitImage;

    public HostGameScreen()
    {
        returnButton = new MenuCancelButton();
        playerListComponent = new PlayerListComponent();
        characterSelectComponent = new CharacterSelectComponent();

        characterSelectComponent.listener = this;
    }

    @Override
    public void open()
    {
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = CurScreenPatch.HOST_GAME;

        returnButton.show(PatchNotesScreen.TEXT[0]);

        playerListComponent.move(Settings.WIDTH * 0.22f, Settings.HEIGHT * 0.82F);
        characterSelectComponent.move(Settings.WIDTH * 0.5f, Settings.HEIGHT * 0.31F);
    }

    @Override
    public void close()
    {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        
        returnButton.hide();
        characterSelectComponent.deselect();

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
    public void onCharacterSelected(AbstractPlayer character)
    {
        dispose();

        selectedCharacterPortraitImage = ImageMaster.loadImage("images/ui/charSelect/" + character.getPortraitImageName());
        character.doCharSelectScreenSelectEffect();
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

        playerListComponent.update();
        characterSelectComponent.update();
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
        playerListComponent.render(spriteBatch);
        characterSelectComponent.render(spriteBatch);
    }
}
