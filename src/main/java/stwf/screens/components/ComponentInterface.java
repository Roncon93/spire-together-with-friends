package stwf.screens.components;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * UI component interface.
 */
public interface ComponentInterface
{
    /**
     * Moves the component on the screen.
     * @param x The x axis location to move to.
     * @param y The y axis location to move to.
     */
    void move(float x, float y);

    /**
     * Updates the component.
     */
    void update();
    
    /**
     * Renders the component.
     * @param spriteBatch The SpriteBatch used to render the component.
     */
    void render(SpriteBatch spriteBatch);
}
