package stwf.multiplayer.Actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;

public class CharacterSelectedAction extends TypedMultiplayerAction<PlayerClass>
{
    public CharacterSelectedAction()
    {
        super();
    }

    public CharacterSelectedAction(PlayerClass value)
    {
        super(value);
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        value = PlayerClass.valueOf(jsonMap.getString("value"));
    }

    @Override
    public void write(Json json)
    {
        super.write(json);
        json.writeValue("value", value);
    }
}
