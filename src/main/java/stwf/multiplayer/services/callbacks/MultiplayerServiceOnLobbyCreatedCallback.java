package stwf.multiplayer.services.callbacks;

import stwf.multiplayer.services.MultiplayerServiceResult;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public interface MultiplayerServiceOnLobbyCreatedCallback
{
    void onLobbyCreated(MultiplayerServiceResult result, MultiplayerId id);
}
