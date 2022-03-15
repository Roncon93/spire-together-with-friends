package stwf.multiplayer.services;

import stwf.multiplayer.PlayerProfile;
import stwf.multiplayer.services.steam.SteamService.MultiplayerServiceId;

public interface MultiplayerServiceInterface
{
    public interface LobbyEventListener
    {
        void onLobbyCreated(MultiplayerServiceResult result, MultiplayerServiceId id);
    }

    PlayerProfile getLocalPlayerProfile();

    void createLobby(MultiplayerServiceInterface.LobbyEventListener listener, MultiplayerLobbyType type, int maxPlayers);

    void leaveLobby(MultiplayerServiceId id);
}
