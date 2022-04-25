package stwf.screens;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface BaseScreenInterface
{
    /**
     * Readies the screen components for showing.
     */
    void open(HashMap<String, Object> data);

    /**
     * Hides the screen components.
     */
    void close();

    /**
     * Updates the screen's components.
     */
    void update();
    
    /**
     * Renders the screen's components.
     * @param spriteBatch The SpriteBatch to use for rendering.
     */
    void render(SpriteBatch spriteBatch);
}
