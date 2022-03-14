package stwf.screens.coop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuCancelButton;
import com.megacrit.cardcrawl.screens.mainMenu.PatchNotesScreen;

import stwf.screens.BaseScreenInterface;

public class JoinGameScreen implements BaseScreenInterface
{
    public MenuCancelButton returnButton;

    public JoinGameScreen()
    {
        returnButton = new MenuCancelButton();
    }

    public void open()
    {
        CardCrawlGame.mainMenuScreen.darken();
        CardCrawlGame.mainMenuScreen.screen = CurScreenPatch.JOIN_GAME;

        returnButton.show(PatchNotesScreen.TEXT[0]);
    }

    public void close()
    {
        CardCrawlGame.mainMenuScreen.screen = MainMenuScreen.CurScreen.MAIN_MENU;
        CardCrawlGame.mainMenuScreen.lighten();
        returnButton.hide();
    }

    public void update()
    {
        returnButton.update();

        if (returnButton.hb.clicked || InputHelper.pressedEscape)
        {
            returnButton.hb.clicked = false;
            InputHelper.pressedEscape = false;
            close();
        } 
    }

    public void render(SpriteBatch spriteBatch)
    {
        returnButton.render(spriteBatch);
    }
}
