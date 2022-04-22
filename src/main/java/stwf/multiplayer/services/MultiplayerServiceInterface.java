package stwf.multiplayer.services;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.Player;
import stwf.multiplayer.PlayerProfile;
import stwf.multiplayer.Actions.MultiplayerAction;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbiesRequestedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyCreatedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyJoinedCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public interface MultiplayerServiceInterface
{
    Player getPlayer(MultiplayerId playerId);

    PlayerProfile getLocalPlayerProfile();

    void addLobbyCallback(MultiplayerServiceLobbyCallback callback);

    void removeLobbyCallback(MultiplayerServiceLobbyCallback callback);

    void createLobby(MultiplayerLobbyType type, int maxPlayers, MultiplayerServiceOnLobbyCreatedCallback callback);

    void joinLobby(MultiplayerId id, MultiplayerServiceOnLobbyJoinedCallback callback);

    void leaveLobby(MultiplayerId id);

    void getLobbies(MultiplayerServiceOnLobbiesRequestedCallback callback);

    boolean setLobbyData(MultiplayerId id, String key, String value);

    void sendPlayerAction(MultiplayerId lobbyId, MultiplayerAction action);

    MultiplayerLobby getLobby(MultiplayerId id);
}
