package stwf.screens.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class ListPanelComponent extends BaseComponent
{
    @Override
    public void render(SpriteBatch spriteBatch)
    {
        spriteBatch.setColor(Color.WHITE.cpy());
        spriteBatch.draw(ImageMaster.REWARD_SCREEN_SHEET, x - 306.0F, y - 218.0F * Settings.scale - 358.0F, 306.0F, 358.0F, 612.0F, 716.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 612, 716, false, false);
    }
}
