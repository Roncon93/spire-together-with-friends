package stwf.screens.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class PlayerListItemComponent extends BaseComponent
{
    public float scroll;

    private static final Color EMPTY_PLAYER_SLOT = new Color(1.0F, 1.0F, 1.0F, 0.3F);

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        render(spriteBatch, 0);
    }

    /**
     * Renders the component.
     * @param spriteBatch The SpriteBatch used to render the component.
     * @param index The index of this item on the list for setting its placement.
     */
    public void render(SpriteBatch spriteBatch, int index)
    {
        spriteBatch.setColor(EMPTY_PLAYER_SLOT);
        spriteBatch.draw(ImageMaster.REWARD_SCREEN_ITEM, x - 232.0F, y + scroll - index * 75.0F * Settings.scale - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.scale, Settings.scale * 0.75F, 0.0F, 0, 0, 464, 98, false, false);
        spriteBatch.setColor(Color.WHITE);
    }
}
