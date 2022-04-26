package stwf.multiplayer.services.callbacks;

import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public interface MultiplayerServiceLobbyCallback
{
    void onPlayerJoined(MultiplayerId lobbyId, MultiplayerId playerId);

    void onPlayerLeft(MultiplayerId lobbyId, MultiplayerId playerId);

    void onPlayerDataReceived(MultiplayerId lobbyId, MultiplayerId playerId, String key, String value);
}
