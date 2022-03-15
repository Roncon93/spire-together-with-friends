package stwf.screens.components;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

public abstract class BaseButtonComponent extends BaseComponent
{
    public static final String DEFAULT_SOUND_HOVER = "UI_HOVER";
    public static final String DEFAULT_SOUND_CLICK = "UI_CLICK_1";

    public ButtonListenerInterface listener;
    public Hitbox hitbox;
    public float width;
    public float height;
    public float scale;
    public String hoverSound;
    public String clickSound;

    public BaseButtonComponent()
    {
        super();

        width = 1.0f;
        height = 1.0f;
        scale = 1.0f;

        hitbox = new Hitbox(x, y, width, height);

        hoverSound = DEFAULT_SOUND_HOVER;
        clickSound = DEFAULT_SOUND_CLICK;
    }

    /**
     * Resizes the button and its hitbox.
     * @param width The new width of the button.
     * @param height The new height of the button.
     */
    public void resize(float width, float height)
    {
        resize(width, height, scale);
    }

    /**
     * Resizes the button and its hitbox.
     * @param width The new width of the button.
     * @param height The new height of the button.
     * @param scale The new scale of the button.
     */
    public void resize(float width, float height, float scale)
    {
        this.width = width;
        this.height = height;
        this.scale = scale;

        hitbox.resize(width, height);
    }

    @Override
    public void move(float x, float y)
    {
        super.move(x, y);
        hitbox.translate(x, y);
    }

    @Override
    public void update()
    {
        hitbox.update();

        if (hitbox.justHovered)
        {
            onHover();
        }

        if (InputHelper.justClickedLeft && hitbox.hovered)
        {
            hitbox.clickStarted = true;

            onClick();
        }

        if (hitbox.clicked) 
        {
            hitbox.clicked = false;
        }
    }

    /**
     * Called when button is being hovered over.
     */
    protected void onHover()
    {
        CardCrawlGame.sound.playA(hoverSound, -0.3F);
    }

    /**
     * Called when the button is clicked.
     */
    protected void onClick()
    {
        CardCrawlGame.sound.playA(clickSound, -0.4F);

        if (listener != null)
        {
            listener.onClick(this);
        }
    }
}
