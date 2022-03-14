package stwf.screens.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class BannerComponent extends BaseComponent
{
    public final static float DEFAULT_WIDTH = 450f;
    public float width;
    public String label;

    public BannerComponent()
    {
        this(0, 0, DEFAULT_WIDTH, "");
    }

    public BannerComponent(float x, float y)
    {
        this(x, y, DEFAULT_WIDTH, "");
    }

    public BannerComponent(float x, float y, float width, String label)
    {
        super(x, y);
        this.width = width;
        this.label = label;
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        spriteBatch.setColor(Color.WHITE.cpy());
        spriteBatch.draw(ImageMaster.VICTORY_BANNER, x, y - 23.800001F, width, 0.0F, 900.0F, 238.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 1112, 238, false, false);
        FontHelper.renderFontCentered(spriteBatch, FontHelper.SCP_cardTitleFont_small, label, x + width, y + 105.0F * Settings.yScale + 22.0F * Settings.yScale, new Color(0.9F, 0.9F, 0.9F, 1.0F), 1.0F);
    }    
}
