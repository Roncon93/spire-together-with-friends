package stwf.multiplayer.Actions;

import com.badlogic.gdx.utils.Json;

public abstract class TypedMultiplayerAction<TActionValue> extends MultiplayerAction
{
    public TActionValue value;

    public TypedMultiplayerAction()
    {
    }

    public TypedMultiplayerAction(TActionValue value)
    {
        this.value = value;
    }

    @Override
    public void write(Json json)
    {
        json.writeValue("class", getClass().getName());
    }
}
