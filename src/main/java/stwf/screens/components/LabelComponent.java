package stwf.screens.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

public class LabelComponent extends BaseComponent
{
    public String text;
    public Color color;
    public BitmapFont font;
    public float scale;

    public LabelComponent()
    {
        this("");
    }

    public LabelComponent(String text)
    {
        super(0.0f, 0.0f);

        this.text = text;
        font = FontHelper.losePowerFont;
        color = Settings.CREAM_COLOR;
        scale = 1.0f;
    }

    public enum LabelAlignment
    {
        CENTERED
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        render(spriteBatch, LabelAlignment.CENTERED);
    }

    /**
     * Renders the label using the chosen alignment.
     * @param spriteBatch The SpriteBatch to render the label with.
     * @param alignment The alignment method to render the label with.
     */
    public void render(SpriteBatch spriteBatch, LabelAlignment alignment)
    {
        if (alignment == LabelAlignment.CENTERED)
        {
            FontHelper.renderFontCentered(spriteBatch, font, text, x, y, color, scale);
        }
    }
}
