package stwf.multiplayer.services;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.Player;
import stwf.multiplayer.PlayerProfile;
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

    void addLobbyCallback(MultiplayerServiceLobbyCallback callback, String... keys);

    void removeLobbyCallback(MultiplayerServiceLobbyCallback callback);

    void createLobby(MultiplayerLobbyType type, int maxPlayers, MultiplayerServiceOnLobbyCreatedCallback callback);

    void joinLobby(MultiplayerId id, MultiplayerServiceOnLobbyJoinedCallback callback);

    void leaveLobby(MultiplayerId id);

    void getLobbies(MultiplayerServiceOnLobbiesRequestedCallback callback);

    String getLobbyData(MultiplayerId lobbyId, String key);

    void sendLobbyData(MultiplayerId id, String key);

    void sendLobbyData(MultiplayerId id, String key, String value);

    void sendPlayerData(MultiplayerId lobbyId, String key);

    void sendPlayerData(MultiplayerId lobbyId, String key, String value);

    void sendHostPlayerData(MultiplayerId lobbyId, String key, String value);

    String getPlayerData(MultiplayerId lobbyId, MultiplayerId playerId, String key);

    MultiplayerLobby getLobby(MultiplayerId id);
}
