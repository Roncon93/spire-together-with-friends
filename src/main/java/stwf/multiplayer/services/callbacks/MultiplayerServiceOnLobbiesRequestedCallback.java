package stwf.multiplayer.services.callbacks;

import java.util.List;

import stwf.multiplayer.MultiplayerLobby;

public interface MultiplayerServiceOnLobbiesRequestedCallback
{
    void onLobbiesRequested(List<MultiplayerLobby> lobbies);
}
