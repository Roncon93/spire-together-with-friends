package stwf.multiplayer.Actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ReadyStatusUpdatedAction extends TypedMultiplayerAction<Boolean>
{
    public ReadyStatusUpdatedAction()
    {
        super();
    }

    public ReadyStatusUpdatedAction(boolean value)
    {
        super(value);
    }

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        value = Boolean.parseBoolean(jsonMap.getString("value"));
    }

    @Override
    public void write(Json json)
    {
        super.write(json);
        json.writeValue("value", value);
    }
}
