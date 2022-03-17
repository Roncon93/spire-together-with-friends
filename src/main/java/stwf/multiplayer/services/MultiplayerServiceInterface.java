package stwf.multiplayer.services;

import java.util.List;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.PlayerProfile;
import stwf.multiplayer.services.steam.SteamService.MultiplayerServiceId;

public interface MultiplayerServiceInterface
{
    public interface LobbyEventListener
    {
        void onLobbyCreated(MultiplayerServiceResult result, MultiplayerServiceId id);

        void onLobbiesRequested(List<MultiplayerLobby> lobbies);
    }

    PlayerProfile getLocalPlayerProfile();

    void createLobby(MultiplayerServiceInterface.LobbyEventListener listener, MultiplayerLobbyType type, int maxPlayers);

    void leaveLobby(MultiplayerServiceId id);

    void getLobbies(MultiplayerServiceInterface.LobbyEventListener listener);

    boolean setLobbyData(MultiplayerServiceId id, String key, String value);
}
