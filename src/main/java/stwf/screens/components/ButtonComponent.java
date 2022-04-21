package stwf.screens.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.controller.CInputActionSet;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public class ButtonComponent extends BaseButtonComponent
{
    public final Hitbox hitbox;
    public String label;

    private static final Color HOVER_BLEND_COLOR = new Color(1.0F, 1.0F, 1.0F, 0.3F);
    private static final Color BUTTON_SHADOW_COLOR = new Color(0.0F, 0.0F, 0.0F, 0.2F);
    private static final Color TEXT_DISABLED_COLOR = new Color(0.6F, 0.6F, 0.6F, 1.0F);

    private boolean isHidden;

    private float glowAlpha;  
    private Color glowColor;

    public ButtonComponent()
    {
        super();

        hitbox = new Hitbox(0.0F, 0.0F, 320.0F * Settings.scale, 100.0F * Settings.scale);
        label = "";
        glowAlpha = 0.0F;
        glowColor = Color.WHITE.cpy();
        isHidden = false;
    }

    @Override
    public void move(float x, float y)
    {
        super.move(x, y);

        if (hitbox != null)
        {
            hitbox.move(x, y);
        }
    }

    public void show()
    {
        if (isHidden)
        {
            glowAlpha = 0.0F;
            isHidden = false;
            isDisabled = false;
        } 
      }

    private void updateGlow()
    {
        glowAlpha += Gdx.graphics.getDeltaTime() * 3.0F;
        if (glowAlpha < 0.0F)
        {
            glowAlpha *= -1.0F; 
        }

        float tmp = MathUtils.cos(glowAlpha);
        if (tmp < 0.0F)
        {
            glowColor.a = -tmp / 2.0F + 0.3F;
        }
        else
        {
            glowColor.a = tmp / 2.0F + 0.3F;
        } 
    }

    @Override
    public void update()
    {
        if (!isHidden && !isDisabled)
        {
            updateGlow();
            hitbox.update();

            if (InputHelper.justClickedLeft && hitbox.hovered && !isDisabled)
            {
                hitbox.clickStarted = true;
                CardCrawlGame.sound.play("UI_CLICK_1");

                for (ButtonListenerInterface listener : listeners)
                {
                    listener.onClick(this);
                }
            }

            else if (hitbox.justHovered && !isDisabled)
            {
                CardCrawlGame.sound.play("UI_HOVER"); 
            }
            
            if (CInputActionSet.select.isJustPressed())
            {
                CInputActionSet.select.unpress();
                hitbox.clicked = true;
            }
        } 
      }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        renderShadow(spriteBatch);
        renderButton(spriteBatch);
        renderButtonLabel(spriteBatch);
    }

    private void renderShadow(SpriteBatch spriteBatch)
    {
        spriteBatch.setColor(BUTTON_SHADOW_COLOR);
        spriteBatch.draw(ImageMaster.REWARD_SCREEN_TAKE_USED_BUTTON, this.x - 256.0F, this.y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);
        spriteBatch.setColor(Color.WHITE);
    }

    private void renderButton(SpriteBatch spriteBatch)
    {
        if (!isDisabled)
        {
            spriteBatch.draw(ImageMaster.REWARD_SCREEN_TAKE_BUTTON, x - 256.0F, y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);
        }
        else
        {
            spriteBatch.draw(ImageMaster.REWARD_SCREEN_TAKE_USED_BUTTON, x - 256.0F, y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);
        }

        if (hitbox.hovered && !isDisabled && !hitbox.clickStarted)
        {
            spriteBatch.setBlendFunction(770, 1);
            spriteBatch.setColor(HOVER_BLEND_COLOR);
            spriteBatch.draw(ImageMaster.REWARD_SCREEN_TAKE_USED_BUTTON, x - 256.0F, y - 128.0F, 256.0F, 128.0F, 512.0F, 256.0F, Settings.scale, Settings.scale, 0.0F, 0, 0, 512, 256, false, false);
            spriteBatch.setBlendFunction(770, 771);
        }
    }

    private void renderButtonLabel(SpriteBatch spriteBatch)
    {
        if (isDisabled)
        {
            FontHelper.renderFontCentered(spriteBatch, FontHelper.buttonLabelFont, label, x, y, TEXT_DISABLED_COLOR);
        }
        else if (hitbox.clickStarted)
        {
            FontHelper.renderFontCentered(spriteBatch, FontHelper.buttonLabelFont, label, x, y, Color.LIGHT_GRAY);
        }
        else
        {
            FontHelper.renderFontCentered(spriteBatch, FontHelper.buttonLabelFont, label, x, y, Settings.LIGHT_YELLOW_COLOR);
        } 
    }
}
