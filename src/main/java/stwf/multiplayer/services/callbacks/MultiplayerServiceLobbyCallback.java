package stwf.multiplayer.services.callbacks;

import stwf.multiplayer.Actions.MultiplayerAction;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public interface MultiplayerServiceLobbyCallback
{
    void onPlayerJoined(MultiplayerId lobbyId, MultiplayerId playerId);

    void onPlayerLeft(MultiplayerId lobbyId, MultiplayerId playerId);

    void onPlayerActionReceived(MultiplayerId lobbyId, MultiplayerId playerId, MultiplayerAction action);
}
