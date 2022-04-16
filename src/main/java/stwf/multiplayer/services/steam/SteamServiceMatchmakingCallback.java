package stwf.multiplayer.services.steam;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmaking.ChatEntryType;
import com.codedisaster.steamworks.SteamMatchmaking.ChatMemberStateChange;
import com.codedisaster.steamworks.SteamMatchmaking.ChatRoomEnterResponse;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbiesRequestedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyCreatedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyJoinedCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.multiplayer.services.steam.SteamService.SteamServiceUtils;

import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamResult;

public class SteamServiceMatchmakingCallback implements SteamMatchmakingCallback
{
    public SteamMatchmaking matchmakingService;
    public Queue<MultiplayerServiceOnLobbyCreatedCallback> onLobbyCreatedCallbacks;
    public Queue<MultiplayerServiceOnLobbyJoinedCallback> onLobbyJoinedCallbacks;
    public Queue<MultiplayerServiceOnLobbiesRequestedCallback> onLobbiesRequestedCallbacks;
    public List<MultiplayerServiceLobbyCallback> lobbyCallbacks;

    public SteamServiceMatchmakingCallback()
    {
        onLobbyCreatedCallbacks = new ArrayDeque<MultiplayerServiceOnLobbyCreatedCallback>();
        onLobbyJoinedCallbacks = new ArrayDeque<MultiplayerServiceOnLobbyJoinedCallback>();
        onLobbiesRequestedCallbacks = new ArrayDeque<MultiplayerServiceOnLobbiesRequestedCallback>();
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
    public void onLobbyCreated(SteamResult result, SteamID id)
    {
        matchmakingService.setLobbyData(id, "mod", "stwf");

        if (!onLobbyCreatedCallbacks.isEmpty())
        {
            MultiplayerServiceOnLobbyCreatedCallback callback = onLobbyCreatedCallbacks.remove();
            callback.onLobbyCreated(SteamServiceUtils.convertSteamResultToGenericResult(result), SteamServiceUtils.convertSteamIdToGenericId(id));
        }
    }

    @Override
    public void onLobbyDataUpdate(SteamID arg0, SteamID arg1, boolean arg2) {
        // TODO Auto-generated method stub
        
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
                lobby.hostName = matchmakingService.getLobbyData(lobbyId, "hostName");

                lobbies.add(lobby);
            }
        }

        if (!onLobbiesRequestedCallbacks.isEmpty())
        {
            MultiplayerServiceOnLobbiesRequestedCallback callback = onLobbiesRequestedCallbacks.remove();
            callback.onLobbiesRequested(lobbies);
        }
    }
}
