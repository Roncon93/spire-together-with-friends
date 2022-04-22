package stwf.multiplayer.Actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public abstract class MultiplayerAction implements Json.Serializable
{
    protected static final Json JSON = new Json();

    public static MultiplayerAction fromJson(String json)
    {
        try
        {
            JsonValue root = new JsonReader().parse(json);
            String className = root.getString("class");
        
            Class<?> actionClass = Class.forName(className);
            Object action = JSON.fromJson(actionClass, json);

            return (MultiplayerAction)action;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString()
    {
        return JSON.toJson(this);
    }
}
