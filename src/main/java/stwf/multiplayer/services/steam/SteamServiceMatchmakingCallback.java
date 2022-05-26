package stwf.multiplayer.services.steam;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.utils.Json;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmaking.ChatEntryType;
import com.codedisaster.steamworks.SteamMatchmaking.ChatMemberStateChange;
import com.codedisaster.steamworks.SteamMatchmaking.ChatRoomEnterResponse;
import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamResult;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbiesRequestedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyCreatedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyJoinedCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.multiplayer.services.steam.SteamService.SteamServiceUtils;
import stwf.utils.StringUtils;

public class SteamServiceMatchmakingCallback implements SteamMatchmakingCallback
{
    public SteamMatchmaking matchmakingService;
    public String localPlayerName;
    public SteamID localPlayerId;
    public Queue<MultiplayerServiceOnLobbyCreatedCallback> onLobbyCreatedCallbacks;
    public Queue<MultiplayerServiceOnLobbyJoinedCallback> onLobbyJoinedCallbacks;
    public Queue<MultiplayerServiceOnLobbiesRequestedCallback> onLobbiesRequestedCallbacks;
    public List<MultiplayerServiceLobbyCallback> lobbyCallbacks;

    public ConcurrentHashMap<String,String> readMessages = new ConcurrentHashMap<String,String>();

    public SteamServiceMatchmakingCallback()
    {
        onLobbyCreatedCallbacks = new ArrayDeque<MultiplayerServiceOnLobbyCreatedCallback>();
        onLobbyJoinedCallbacks = new ArrayDeque<MultiplayerServiceOnLobbyJoinedCallback>();
        onLobbiesRequestedCallbacks = new ArrayDeque<MultiplayerServiceOnLobbiesRequestedCallback>();
    }

    public void sendLobbyData(SteamID lobbyId, String key, String value)
    {
        SteamMessage message = new SteamMessage();
        message.id = UUID.randomUUID().toString();
        message.value = value;

        matchmakingService.setLobbyData(lobbyId, key, new Json().toJson(message));
    }

    public void sendPlayerData(SteamID lobbyId, String key, String value)
    {
        SteamMessage message = new SteamMessage();
        message.id = UUID.randomUUID().toString();
        message.value = value;

        matchmakingService.setLobbyMemberData(lobbyId, key, new Json().toJson(message));
    }

    public String getLobbyData(SteamID lobbyId, String key)
    {
        String rawMessage = matchmakingService.getLobbyData(lobbyId, key);

        if (StringUtils.isNullOrEmpty(rawMessage))
        {
            return "";
        }

        SteamMessage message = new Json().fromJson(SteamMessage.class, rawMessage);
        return message.value;
    }

    public String getPlayerData(SteamID lobbyId, SteamID playerId, String key)
    {
        String rawMessage = matchmakingService.getLobbyMemberData(lobbyId, playerId, key);

        if (StringUtils.isNullOrEmpty(rawMessage))
        {
            return "";
        }

        SteamMessage message = new Json().fromJson(SteamMessage.class, rawMessage);
        return message.value;
    }

    @Override
    public void onFavoritesListAccountsUpdated(SteamResult arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFavoritesListChanged(int arg0, int arg1, int arg2, int arg3, int arg4, boolean arg5, int arg6) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLobbyChatMessage(SteamID arg0, SteamID arg1, ChatEntryType arg2, int arg3) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLobbyChatUpdate(SteamID lobbyId, SteamID playerId, SteamID hostId, ChatMemberStateChange state)
    {
        if (lobbyCallbacks != null)
        {
            MultiplayerId genericLobbyId = SteamServiceUtils.convertSteamIdToGenericId(lobbyId);
            MultiplayerId genericPlayerId = SteamServiceUtils.convertSteamIdToGenericId(playerId);

            for (MultiplayerServiceLobbyCallback callback : lobbyCallbacks)
            {
                if (state == ChatMemberStateChange.Entered)
                {
                    callback.onPlayerJoined(genericLobbyId, genericPlayerId);
                }
                else
                {
                    callback.onPlayerLeft(genericLobbyId, genericPlayerId);
                }
            }
        }
    }

    @Override
    public void onLobbyCreated(SteamResult result, SteamID lobbyId)
    {
        matchmakingService.setLobbyData(lobbyId, "mod", "stwf");
        matchmakingService.setLobbyData(lobbyId, "lobby.hostname", localPlayerName);
        matchmakingService.setLobbyData(lobbyId, "lobby.host.id", localPlayerId.toString());

        if (!onLobbyCreatedCallbacks.isEmpty())
        {
            MultiplayerServiceOnLobbyCreatedCallback callback = onLobbyCreatedCallbacks.remove();
            callback.onLobbyCreated(SteamServiceUtils.convertSteamResultToGenericResult(result), SteamServiceUtils.convertSteamIdToGenericId(lobbyId));
        }
    }

    @Override
    public void onLobbyDataUpdate(SteamID lobbyId, SteamID playerId, boolean success)
    {
        MultiplayerId genericLobbyId = SteamServiceUtils.convertSteamIdToGenericId(lobbyId);
        MultiplayerId genericPlayerId = SteamServiceUtils.convertSteamIdToGenericId(playerId);

        for (String key : readMessages.keySet())
        {
            String rawMessage;

            if (!lobbyId.equals(playerId))
            {
                rawMessage = matchmakingService.getLobbyMemberData(lobbyId, playerId, key);
            }
            else
            {
                rawMessage = matchmakingService.getLobbyData(lobbyId, key);
            }

            if (StringUtils.isNullOrEmpty(rawMessage))
            {
                continue;
            }

            SteamMessage message = new Json().fromJson(SteamMessage.class, rawMessage);

            String playerKey = playerId.toString() + "." + key;

            if (readMessages.containsKey(playerKey) && readMessages.get(playerKey).equals(message.id))
            {
                continue;
            }

            readMessages.put(playerKey, message.id);

            for (MultiplayerServiceLobbyCallback callback : lobbyCallbacks)
            {
                if (!lobbyId.equals(playerId))
                {
                    callback.onPlayerDataReceived(genericLobbyId, genericPlayerId, key, message.value);
                }
                else
                {
                    callback.onLobbyDataReceived(genericLobbyId, key, message.value);
                }
            }
        }
    }

    @Override
    public void onLobbyEnter(SteamID id, int arg1, boolean arg2, ChatRoomEnterResponse response)
    {
        if (!onLobbyJoinedCallbacks.isEmpty())
        {
            MultiplayerServiceOnLobbyJoinedCallback callback = onLobbyJoinedCallbacks.remove();
            callback.onLobbyJoined(SteamServiceUtils.convertSteamResultToGenericResult(response), SteamServiceUtils.convertSteamIdToGenericId(id));
        }
    }

    @Override
    public void onLobbyGameCreated(SteamID arg0, SteamID arg1, int arg2, short arg3) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLobbyInvite(SteamID arg0, SteamID arg1, long arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLobbyKicked(SteamID arg0, SteamID arg1, boolean arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLobbyMatchList(int lobbiesMatched)
    {
        List<MultiplayerLobby> lobbies = new ArrayList<>();

        if (matchmakingService != null)
        {
            for (int index = 0; index < lobbiesMatched; index++)
            {
                SteamID lobbyId = matchmakingService.getLobbyByIndex(index);
                SteamID ownerId = matchmakingService.getLobbyOwner(lobbyId);

                MultiplayerLobby lobby = new MultiplayerLobby();
                lobby.id = SteamServiceUtils.convertSteamIdToGenericId(lobbyId);
                lobby.hostId = SteamServiceUtils.convertSteamIdToGenericId(ownerId);
                lobby.hostName = matchmakingService.getLobbyData(lobbyId, "lobby.hostname");

                lobbies.add(lobby);
            }
        }

        if (!onLobbiesRequestedCallbacks.isEmpty())
        {
            MultiplayerServiceOnLobbiesRequestedCallback callback = onLobbiesRequestedCallbacks.remove();
            callback.onLobbiesRequested(lobbies);
        }
    }

    public static class SteamMessage
    {
        public String id;
        public String value;

        public SteamMessage() {}
    }
}
