package stwf.multiplayer.services.callbacks;

import stwf.multiplayer.services.MultiplayerServiceResult;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public interface MultiplayerServiceOnLobbyJoinedCallback
{
    void onLobbyJoined(MultiplayerServiceResult result, MultiplayerId id);
}
