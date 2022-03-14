package stwf.screens.components;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;

import stwf.screens.components.ButtonComponent.ButtonListenerInterface;

public class PlayerListComponent extends BaseComponent
{
    private ListPanelComponent panel;
    private BannerComponent banner;
    private ButtonComponent button;
    private List<PlayerListItemComponent> playerListItems;

    public PlayerListComponent()
    {
        this(0, 0);
    }

    public PlayerListComponent(float x, float y)
    {
        super(x, y);

        panel = new ListPanelComponent();
        banner = new BannerComponent();
        button = new ButtonComponent();
        playerListItems = new CopyOnWriteArrayList<>();
        playerListItems.add(new PlayerListItemComponent());
        playerListItems.add(new PlayerListItemComponent());
        playerListItems.add(new PlayerListItemComponent());
        playerListItems.add(new PlayerListItemComponent());
        playerListItems.add(new PlayerListItemComponent());
        playerListItems.add(new PlayerListItemComponent());

        banner.label = GetLobbyUIString(12);
        button.label = GetLobbyUIString(17);

        button.show();
    }

    /**
     * Retrieves a "Lobby" UI string using the given index.
     * @param index The index of the string to retrieve.
     * @return The UI string found with the index.
     */
    private String GetLobbyUIString(int index)
    {
        return CardCrawlGame.languagePack.getUIString("Lobby").TEXT[index];
    }

    /**
     * Sets the ButtonComponentListener for the ready button.
     * @param listener
     */
    public void setReadyButtonListener(ButtonListenerInterface listener)
    {
        button.listener = listener;
    }

    @Override
    public void move(float x, float y)
    {
        super.move(x, y);
        
        if (panel != null)
        {
            panel.move(x, y);
            banner.move(x - 450.0F, y - 23.800001F);
            button.move(x, y - 450.0F * Settings.scale - 32.0F);

            for (int index = 0; index < playerListItems.size(); index++)
            {
                playerListItems.get(index).move(x, y - 15f);  
            }
        }
    }

    @Override
    public void update()
    {
        button.update();    
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        panel.render(spriteBatch);
        banner.render(spriteBatch);
        button.render(spriteBatch);
        
        for (int index = 0; index < playerListItems.size(); index++)
        {
            playerListItems.get(index).render(spriteBatch, index);   
        }
    }
}
