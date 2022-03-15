package stwf.screens.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class CharacterSelectButton extends ToggleButtonComponent
{
    public AbstractPlayer character;

    private static final Color BLACK_OUTLINE_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.5F);
    private Color glowColor = new Color(1.0F, 0.8F, 0.2F, 0.0F);
    private Texture buttonImage;

    public CharacterSelectButton(AbstractPlayer character)
    {
        super();
        resize(220, 220, Settings.scale);

        this.character = character;

        if (character.chosenClass == PlayerClass.THE_SILENT)
        {
            buttonImage = ImageMaster.CHAR_SELECT_SILENT;
        }
        else if (character.chosenClass == PlayerClass.DEFECT)
        {
            buttonImage = ImageMaster.CHAR_SELECT_DEFECT;
        }
        else if (character.chosenClass == PlayerClass.WATCHER)
        {
            buttonImage = ImageMaster.CHAR_SELECT_WATCHER;
        }
        else
        {
            buttonImage = ImageMaster.CHAR_SELECT_IRONCLAD;
        }
    }

    @Override
    public void move(float x, float y)
    {
        super.move(x, y);
        hitbox.translate(x + 20, y + 40);
    }

    @Override
    public void resize(float width, float height, float scale)
    {
        super.resize(width, height, scale);
        hitbox.resize(width - 60, height - 60);
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if (isToggled)
        {
            glowColor.a = 0.25F + (MathUtils.cosDeg((float)(System.currentTimeMillis() / 4L % 360L)) + 1.25F) / 3.5F;
            spriteBatch.setColor(glowColor);
        }
        else
        {
            spriteBatch.setColor(BLACK_OUTLINE_COLOR);
        } 
        
        // spriteBatch.draw(ImageMaster.CHAR_OPT_HIGHLIGHT, hitbox.cX - 110.0F, hitbox.cY - 110.0F, 110.0F, 110.0F, 220.0F, 220.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 220, 220, false, false);
        spriteBatch.draw(ImageMaster.CHAR_OPT_HIGHLIGHT, x, y, width / 2, height / 2, width, height, scale, scale, 0.0F, 0, 0, (int)width, (int)height, false, false);

        if (isToggled || hitbox.hovered)
        {
            spriteBatch.setColor(Color.WHITE);
        }
        else
        {
            spriteBatch.setColor(Color.LIGHT_GRAY);
        } 

        spriteBatch.draw(buttonImage, x, y, width / 2, height / 2, width, height, scale, scale, 0.0F, 0, 0, (int)width, (int)height, false, false);
        hitbox.render(spriteBatch);
    }
}
