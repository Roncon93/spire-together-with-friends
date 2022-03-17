package stwf.multiplayer.services.steam;

import java.util.ArrayList;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmaking.ChatEntryType;
import com.codedisaster.steamworks.SteamMatchmaking.ChatMemberStateChange;
import com.codedisaster.steamworks.SteamMatchmaking.ChatRoomEnterResponse;

import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.steam.SteamService.SteamServiceUtils;

import com.codedisaster.steamworks.SteamMatchmakingCallback;
import com.codedisaster.steamworks.SteamResult;

public class SteamServiceMatchmakingCallback implements SteamMatchmakingCallback
{
    public MultiplayerServiceInterface.LobbyEventListener lobbyEventListener;
    public SteamMatchmaking matchmakingService;

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
    public void onLobbyChatUpdate(SteamID arg0, SteamID arg1, SteamID arg2, ChatMemberStateChange arg3) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLobbyCreated(SteamResult result, SteamID id)
    {
        if (lobbyEventListener != null)
        {
            lobbyEventListener.onLobbyCreated(SteamServiceUtils.convertSteamResultToGenericResult(result), SteamServiceUtils.convertSteamIdToGenericId(id));
        }        
    }

    @Override
    public void onLobbyDataUpdate(SteamID arg0, SteamID arg1, boolean arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onLobbyEnter(SteamID arg0, int arg1, boolean arg2, ChatRoomEnterResponse arg3) {
        // TODO Auto-generated method stub
        
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
        ArrayList<MultiplayerLobby> lobbies = new ArrayList<>();

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

        if (lobbyEventListener != null)
        {
            lobbyEventListener.onLobbiesRequested(lobbies);
        }
    }
}
