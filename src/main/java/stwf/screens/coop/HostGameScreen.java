package stwf.screens.coop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;

import stwf.screens.BaseScreenInterface;
import stwf.screens.components.PlayerListComponent;

public class HostGameScreen implements BaseScreenInterface
{
    public MenuCancelButton returnButton;
    public PlayerListComponent playerListComponent;

    public HostGameScreen()
    {
        returnButton = new MenuCancelButton();
        playerListComponent = new PlayerListComponent();
    }

    @Override
    public void open()
    {
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = CurScreenPatch.HOST_GAME;

        returnButton.show(PatchNotesScreen.TEXT[0]);

        playerListComponent.move(Settings.WIDTH / 2.0F, Settings.HEIGHT * 0.6875F);
    }

    @Override
    public void close()
    {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        
        returnButton.hide();
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
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        returnButton.render(spriteBatch);
        playerListComponent.render(spriteBatch);
    }
}
