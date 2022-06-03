package stwf.multiplayer.services.steam;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamMatchmaking.ChatRoomEnterResponse;
import com.codedisaster.steamworks.SteamMatchmaking.LobbyComparison;
import com.codedisaster.steamworks.SteamMatchmaking.LobbyType;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUser;
import com.codedisaster.steamworks.SteamUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import basemod.ReflectionHacks;
import stwf.multiplayer.MultiplayerLobby;
import stwf.multiplayer.Player;
import stwf.multiplayer.PlayerProfile;
import stwf.multiplayer.services.MultiplayerLobbyType;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.MultiplayerServiceResult;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbiesRequestedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyCreatedCallback;
import stwf.multiplayer.services.callbacks.MultiplayerServiceOnLobbyJoinedCallback;

public class SteamService implements MultiplayerServiceInterface, SteamServiceCallbackInterface
{
    private SteamMatchmaking matchmakingService;
    private SteamFriends friendsService;
    public static SteamNetworking networkingService;
    private SteamUtils utils;

    private SteamServiceMatchmakingCallback matchmakingCallback;
    private List<MultiplayerServiceLobbyCallback> lobbyCallbacks;
    private SteamServiceNetworkingCallback networkingCallback;

    private PlayerProfile localPlayer;
    private SteamUser localSteamUser;
    private List<SteamID> remotePlayerIds;

    public SteamService()
    {
        remotePlayerIds = new ArrayList<SteamID>();

        matchmakingCallback = new SteamServiceMatchmakingCallback();
        matchmakingCallback.steamServiceCallback = this;

        lobbyCallbacks = new CopyOnWriteArrayList<>();

        

        matchmakingService = new SteamMatchmaking(matchmakingCallback);
        utils = new SteamUtils(new SteamServiceUtilsCallback());
        friendsService = new SteamFriends(new SteamServiceFriendsCallback());

        localSteamUser = (SteamUser)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, SteamIntegration.class, "steamUser");

        matchmakingCallback.matchmakingService = matchmakingService;
        matchmakingCallback.localPlayerName = getLocalUserName();
        matchmakingCallback.localPlayerId = localSteamUser.getSteamID();
        matchmakingCallback.lobbyCallbacks = lobbyCallbacks;

        networkingCallback = new SteamServiceNetworkingCallback();
        networkingCallback.steamServiceCallback = this;

        networkingService = new SteamNetworking(networkingCallback);
    }

    @Override
    public void addLobbyCallback(MultiplayerServiceLobbyCallback callback)
    {
        if (!lobbyCallbacks.contains(callback))
        {
            lobbyCallbacks.add(callback);
        }
    }

    @Override
    public void addLobbyCallback(MultiplayerServiceLobbyCallback callback, String... keys)
    {
        if (!lobbyCallbacks.contains(callback))
        {
            for (String key : keys)
            {
                matchmakingCallback.readMessages.put(key, "");
            }

            lobbyCallbacks.add(callback);
        }
    }

    @Override
    public void removeLobbyCallback(MultiplayerServiceLobbyCallback callback)
    {
        if (lobbyCallbacks.contains(callback))
        {
            lobbyCallbacks.remove(callback);
        }
    }

    @Override
    public void createLobby(MultiplayerLobbyType type, int maxPlayers, MultiplayerServiceOnLobbyCreatedCallback callback)
    {
        matchmakingCallback.onLobbyCreatedCallbacks.add(callback);
        matchmakingService.createLobby(SteamServiceUtils.convertGenericLobbyTypeToSteamLobbyType(type), maxPlayers);
    }

    @Override
    public void joinLobby(MultiplayerId id, MultiplayerServiceOnLobbyJoinedCallback callback)
    {
        matchmakingCallback.onLobbyJoinedCallbacks.add(callback);
        matchmakingService.joinLobby(SteamServiceUtils.convertGenericIdToSteamId(id));
    }

    @Override
    public void leaveLobby(MultiplayerId id)
    {
        matchmakingService.leaveLobby(SteamServiceUtils.convertGenericIdToSteamId(id));
        remotePlayerIds.clear();
    }

    @Override
    public void getLobbies(MultiplayerServiceOnLobbiesRequestedCallback callback)
    {
        matchmakingCallback.onLobbiesRequestedCallbacks.add(callback);

        matchmakingService.addRequestLobbyListStringFilter("mod", "stwf", LobbyComparison.Equal);
        matchmakingService.requestLobbyList();
    }

    public MultiplayerLobby getLobby(MultiplayerId id)
    {
        SteamID lobbySteamId = SteamServiceUtils.convertGenericIdToSteamId(id);

        MultiplayerLobby lobby = new MultiplayerLobby();
        lobby.id = id;

        int numOfMembers = matchmakingService.getNumLobbyMembers(lobbySteamId);

        for (int index = 0; index < numOfMembers; index++)
        {
            SteamID memberId = matchmakingService.getLobbyMemberByIndex(lobbySteamId, index);
            String memberUsername = friendsService.getFriendPersonaName(memberId);

            Player player = new Player();
            player.order = index;
            player.isLocal = memberId.equals(localSteamUser.getSteamID());
            player.profile.id = SteamServiceUtils.convertSteamIdToGenericId(memberId);
            player.profile.username = memberUsername;

            Gdx.app.postRunnable(new Runnable()
            {
                @Override
                public void run()
                {
                    player.profile.avatar = getPlayerAvatar(memberId);
                }
            });

            lobby.players.add(player);
        }

        return lobby;
    }

    @Override
    public String getLobbyData(MultiplayerId lobbyId, String key)
    {
        return matchmakingCallback.getLobbyData(SteamServiceUtils.convertGenericIdToSteamId(lobbyId), key);
    }

    @Override
    public void sendLobbyData(MultiplayerId lobbyId, String key)
    {
        sendLobbyData(lobbyId, key);
    }

    @Override
    public void sendLobbyData(MultiplayerId lobbyId, String key, String value)
    {
        if (!isLocalPlayerHost(lobbyId))
        {
            return;
        }

        matchmakingCallback.sendLobbyData(SteamServiceUtils.convertGenericIdToSteamId(lobbyId), key, value);
    }

    @Override
    public void sendPlayerData(MultiplayerId lobbyId, String key, String value)
    {
        sendPlayerData(lobbyId, key, value, false, false);
    }

    @Override
    public void sendPlayerData(MultiplayerId lobbyId, String key, String value, boolean persist, boolean sendLocal)
    {
        if (persist)
        {
            matchmakingCallback.sendPlayerData(SteamServiceUtils.convertGenericIdToSteamId(lobbyId), key, value);
        }

        networkingCallback.sendPlayerData(key, value);

        if (sendLocal)
        {
            onPlayerDataReceived(getLocalPlayerId(), key, value);
        }
    }

    @Override
    public void sendHostPlayerData(MultiplayerId lobbyId, String key, String value)
    {
        if (!isLocalPlayerHost(lobbyId))
        {
            return;
        }

        sendPlayerData(lobbyId, key, value);
    }

    @Override
    public boolean isLocalPlayerHost(MultiplayerId lobbyId)
    {
        SteamID lobbySteamId = SteamServiceUtils.convertGenericIdToSteamId(lobbyId);
        return matchmakingService.getLobbyData(lobbySteamId, "lobby.host.id").equals(localSteamUser.getSteamID().toString());
    }

    @Override
    public String getPlayerData(MultiplayerId lobbyId, MultiplayerId playerId, String key)
    {
        return matchmakingCallback.getPlayerData(SteamServiceUtils.convertGenericIdToSteamId(lobbyId), SteamServiceUtils.convertGenericIdToSteamId(playerId), key);
    }

    public Player getPlayer(MultiplayerId playerId)
    {
        SteamID playerSteamId = SteamServiceUtils.convertGenericIdToSteamId(playerId);

        Player player = new Player();
        player.profile.id = playerId;
        player.profile.username = friendsService.getFriendPersonaName(playerSteamId);

        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                player.profile.avatar = getPlayerAvatar(playerSteamId);
            }
        });

        return player;
    }

    public String getLocalUserName()
    {
        return friendsService.getPersonaName();
    }

    public PlayerProfile getLocalPlayerProfile()
    {    
        if (localPlayer == null)
        {
            localPlayer = new PlayerProfile();
            localPlayer.id = SteamServiceUtils.convertSteamIdToGenericId(localSteamUser.getSteamID());
            localPlayer.username = friendsService.getPersonaName();
            localPlayer.avatar = getPlayerAvatar(localSteamUser.getSteamID());
        }

        return localPlayer;    
    }

    private Texture getPlayerAvatar(SteamID id)
    {
        int imageID = friendsService.getLargeFriendAvatar(id);
        int width = utils.getImageWidth(imageID);
        int height = utils.getImageHeight(imageID);
        ByteBuffer imageBuffer = ByteBuffer.allocateDirect(width * height * 4);

        try
        {
            utils.getImageRGBA(imageID, imageBuffer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 

        final Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                pixmap.drawPixel(x, y, imageBuffer.getInt());
            }
        }

        return new Texture(pixmap);
    }

    public static class SteamServiceUtils
    {
        public static SteamID convertGenericIdToSteamId(MultiplayerId id)
        {
            return id.steamId;
        }

        public static LobbyType convertGenericLobbyTypeToSteamLobbyType(MultiplayerLobbyType type)
        {
            return LobbyType.Public;
        }

        public static MultiplayerServiceResult convertSteamResultToGenericResult(SteamResult result)
        {
            if (result == SteamResult.OK)
            {
                return MultiplayerServiceResult.OK;
            }

            return MultiplayerServiceResult.FAIL;
        }

        public static MultiplayerServiceResult convertSteamResultToGenericResult(ChatRoomEnterResponse result)
        {
            if (result == ChatRoomEnterResponse.Success)
            {
                return MultiplayerServiceResult.OK;
            }

            return MultiplayerServiceResult.FAIL;
        }

        public static MultiplayerId convertSteamIdToGenericId(SteamID id)
        {
            return new MultiplayerId(id);
        }
    }
    
    public static class MultiplayerId
    {
        private final SteamID steamId;

        public MultiplayerId(SteamID id)
        {
            steamId = id;
        }

        @Override
        public String toString()
        {
            if (steamId != null)
            {
                return steamId.toString();
            }

            return super.toString();
        }

        @Override
        public boolean equals(Object obj) {
            MultiplayerId other = (MultiplayerId)obj;
            return steamId.equals(other.steamId);            
        }
    }

    @Override
    public SteamID getLocalPlayerId()
    {
        return localSteamUser.getSteamID();
    }

    @Override
    public Iterator<SteamID> getRemotePlayerIds()
    {
        return remotePlayerIds.iterator();
    }
    
    @Override
    public void addRemotePlayerId(SteamID playerId)
    {
        if (!playerId.equals(getLocalPlayerId()))
        {
            remotePlayerIds.add(playerId);
            networkingCallback.sendPlayerData(playerId, "", "");
        }
    }

    @Override
    public void removeRemotePlayerId(SteamID playerId)
    {
        remotePlayerIds.remove(playerId);
        networkingService.closeP2PSessionWithUser(playerId);
    }

    @Override
    public void onPlayerDataReceived(SteamID playerId, String key, String value)
    {
        for (MultiplayerServiceLobbyCallback callback : lobbyCallbacks)
        {
            callback.onPlayerDataReceived(SteamServiceUtils.convertSteamIdToGenericId(playerId), key, value);
        }
    }
}
