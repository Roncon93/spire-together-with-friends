package stwf.multiplayer.services.steam;

import java.util.UUID;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class MessageMetadata implements Json.Serializable
{
    private static final Json JSON = new Json();

    public UUID id;
    public String key;

    @Override
    public void read(Json json, JsonValue jsonMap)
    {
        id = UUID.fromString(jsonMap.getString("id"));
        key = jsonMap.getString("key");
    }

    @Override
    public void write(Json json)
    {
        json.writeValue("id", id.toString());
        json.writeValue("key", key);
    }

    @Override
    public String toString() {
        return JSON.toJson(this);
    }
}
