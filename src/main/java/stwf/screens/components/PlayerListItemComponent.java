package stwf.screens.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;

import stwf.multiplayer.LobbyPlayer;
import stwf.multiplayer.PlayerProfile;

public class PlayerListItemComponent extends BaseComponent
{
    public static final String NONE_CHARACTER_SELECTED_NAME = "None";

    public float scroll;
    public LobbyPlayer player;

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
        renderListItemBackground(spriteBatch, index);
        renderPlayerInfo(spriteBatch, index);
        renderReadyTick(spriteBatch, index);
    }

    private String getCharacterName()
    {
        if (player != null)
        {
            return player.player.character == null ? NONE_CHARACTER_SELECTED_NAME : player.player.character.getLocalizedCharacterName();
        }

        return NONE_CHARACTER_SELECTED_NAME;
    }

    private void renderListItemBackground(SpriteBatch spriteBatch, int index)
    {
        spriteBatch.setColor(Color.WHITE);
        spriteBatch.draw(ImageMaster.REWARD_SCREEN_ITEM, x - 232.0F, y + scroll - index * 75.0F * Settings.scale - 49.0F, 232.0F, 49.0F, 464.0F, 98.0F, Settings.scale, Settings.scale * 0.75F, 0.0F, 0, 0, 464, 98, false, false);
    }

    private void renderPlayerInfo(SpriteBatch spriteBatch, int index)
    {
        PlayerProfile profile = player.player.profile;

        FontHelper.renderSmartText(spriteBatch, FontHelper.topPanelInfoFont, profile.username, x - 112.0F * Settings.scale, y + scroll - index * 75.0F * Settings.scale + 16.0F * Settings.scale, 1000.0F * Settings.scale, 0.0F, Settings.CREAM_COLOR, 1);
        FontHelper.renderSmartText(spriteBatch, FontHelper.cardTypeFont, getCharacterName(), x - 100.0F * Settings.scale, y + scroll - index * 75.0F * Settings.scale - 10.0F * Settings.scale, 1000.0F * Settings.scale, 0.0F, Color.DARK_GRAY, 1.0F);

        if (profile.avatar != null)
        {
            spriteBatch.draw(profile.avatar, x - 28.0F - 164.0F * Settings.scale, y + scroll - index * 75.0F * Settings.scale - 28.0F - 2.0F * Settings.scale, 28.0F, 28.0F, 56.0F, 56.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, profile.avatar.getWidth(), profile.avatar.getHeight(), false, false);
        }
    }

    private void renderReadyTick(SpriteBatch spriteBatch, int index)
    {
        if (player.isReady)
        {
            spriteBatch.draw(ImageMaster.TICK, x - 32.0F + 164.0F * Settings.scale, y + scroll - index * 75.0F * Settings.scale - 32.0F - 2.0F * Settings.scale, 32.0F, 32.0F, 64.0F, 64.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 64, 64, false, false);
        }
    }
}
