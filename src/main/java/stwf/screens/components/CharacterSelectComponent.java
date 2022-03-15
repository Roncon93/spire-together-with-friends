package stwf.screens.components;

import java.util.ArrayList;
import java.util.Comparator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.custom.CustomModeCharacterButton;

public class CharacterSelectComponent extends BaseComponent
{
    public class CustomComparator implements Comparator<CustomModeCharacterButton>
    {
        public int compare(CustomModeCharacterButton a, CustomModeCharacterButton b)
        {
            return a.c.name.compareTo(b.c.name);
        }
    }

    public interface CharacterSelectComponentListener
    {
        void onCharacterSelected(AbstractPlayer character);
    }

    public LabelComponent label;
    public ArrayList<CharacterSelectButton> characterOptionButtons;
    public CharacterSelectComponentListener listener;
    public boolean hideLabelOnCharacterSelected;

    private CharacterSelectButton previouslySelectedCharacter;
    private boolean showLabel;

    public CharacterSelectComponent()
    {
        label = new LabelComponent(CardCrawlGame.languagePack.getUIString("CharacterSelectScreen").TEXT[0]);
        characterOptionButtons = new ArrayList<>();

        characterOptionButtons.add(createCharacterSelectButton(AbstractPlayer.PlayerClass.IRONCLAD));
        characterOptionButtons.add(createCharacterSelectButton(AbstractPlayer.PlayerClass.THE_SILENT));
        characterOptionButtons.add(createCharacterSelectButton(AbstractPlayer.PlayerClass.DEFECT));
        characterOptionButtons.add(createCharacterSelectButton(AbstractPlayer.PlayerClass.WATCHER));
        
        hideLabelOnCharacterSelected = true;
        showLabel = true;
    }

    private CharacterSelectButton createCharacterSelectButton(AbstractPlayer.PlayerClass characterClass)
    {
        CharacterSelectButton button = new CharacterSelectButton(CardCrawlGame.characterManager.getCharacter(characterClass));
        button.canBeDeselected = false;

        return button;
    }

    @Override
    public void move(float x, float y)
    {
        super.move(x, y);

        label.move(x, y);

        float xPos = x - 460 * Settings.scale;
        float spacing = 230 * Settings.scale;

        for (int i = 0; i < characterOptionButtons.size(); i++)
        {
            CharacterSelectButton button = characterOptionButtons.get(i);

            button.move(xPos + spacing * i, y - 250 * Settings.scale);
        } 
    }

    @Override
    public void update()
    {
        for (int i = 0; i < characterOptionButtons.size(); i++)
        {
            CharacterSelectButton characterButton = characterOptionButtons.get(i);

            characterButton.update();

            if (characterButton.isToggled && previouslySelectedCharacter != characterButton)
            {
                previouslySelectedCharacter = characterButton;

                if (listener != null)
                {
                    listener.onCharacterSelected(characterButton.character);
                }

                showLabel = false;

                deselectOtherCharacters(characterButton);
            }
        }
    }

    /**
     * Deselects the currently selected character if any.
     */
    public void deselect()
    {
        for (CharacterSelectButton button : characterOptionButtons)
        {
            button.isToggled = false; 
        }

        previouslySelectedCharacter = null;
        showLabel = true;
    }

    /**
     * Deselects all other characters besides the one given.
     * @param selectedButton The currently selected character.
     */
    private void deselectOtherCharacters(CharacterSelectButton selectedButton)
    {
        for (CharacterSelectButton button : characterOptionButtons)
        {
            if (button != selectedButton)
            {
                button.isToggled = false; 
            } 
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch)
    {
        if (showLabel)
        {
            label.render(spriteBatch);
        }

        for (CharacterSelectButton button : characterOptionButtons)
        {
            button.render(spriteBatch);
        } 
      } 
}
