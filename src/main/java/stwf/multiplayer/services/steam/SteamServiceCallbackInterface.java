package stwf.multiplayer.services.steam;

import java.util.Iterator;

import com.codedisaster.steamworks.SteamID;

public interface SteamServiceCallbackInterface
{
    SteamID getLocalPlayerId();
    Iterator<SteamID> getRemotePlayerIds();
    void addRemotePlayerId(SteamID playerId);
    void removeRemotePlayerId(SteamID playerId);
    void onPlayerDataReceived(SteamID playerId, String key, String value);
}
