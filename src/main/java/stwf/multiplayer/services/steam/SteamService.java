package stwf.multiplayer.services.steam;

import java.nio.ByteBuffer;
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

public class SteamService implements MultiplayerServiceInterface
{
    private SteamMatchmaking matchmaking;
    private SteamFriends friends;
    private SteamUtils utils;

    private SteamServiceMatchmakingCallback matchmakingCallback;
    private List<MultiplayerServiceLobbyCallback> lobbyCallbacks;

    private PlayerProfile localPlayer;
    private SteamUser localSteamUser;

    public SteamService()
    {
        matchmakingCallback = new SteamServiceMatchmakingCallback();
        lobbyCallbacks = new CopyOnWriteArrayList<>();

        matchmaking = new SteamMatchmaking(matchmakingCallback);
        utils = new SteamUtils(new SteamServiceUtilsCallback());
        friends = new SteamFriends(new SteamServiceFriendsCallback());

        localSteamUser = (SteamUser)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, SteamIntegration.class, "steamUser");

        matchmakingCallback.matchmakingService = matchmaking;
        matchmakingCallback.localPlayerName = getLocalUserName();
        matchmakingCallback.localPlayerId = localSteamUser.getSteamID();
        matchmakingCallback.lobbyCallbacks = lobbyCallbacks;
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
        matchmaking.createLobby(SteamServiceUtils.convertGenericLobbyTypeToSteamLobbyType(type), maxPlayers);
    }

    public void joinLobby(MultiplayerId id, MultiplayerServiceOnLobbyJoinedCallback callback)
    {
        matchmakingCallback.onLobbyJoinedCallbacks.add(callback);
        matchmaking.joinLobby(SteamServiceUtils.convertGenericIdToSteamId(id));
    }

    public void leaveLobby(MultiplayerId id)
    {
        matchmaking.leaveLobby(SteamServiceUtils.convertGenericIdToSteamId(id));
    }

    @Override
    public void getLobbies(MultiplayerServiceOnLobbiesRequestedCallback callback)
    {
        matchmakingCallback.onLobbiesRequestedCallbacks.add(callback);

        matchmaking.addRequestLobbyListStringFilter("mod", "stwf", LobbyComparison.Equal);
        matchmaking.requestLobbyList();
    }

    public MultiplayerLobby getLobby(MultiplayerId id)
    {
        SteamID lobbySteamId = SteamServiceUtils.convertGenericIdToSteamId(id);

        MultiplayerLobby lobby = new MultiplayerLobby();
        lobby.id = id;

        int numOfMembers = matchmaking.getNumLobbyMembers(lobbySteamId);

        for (int index = 0; index < numOfMembers; index++)
        {
            SteamID memberId = matchmaking.getLobbyMemberByIndex(lobbySteamId, index);
            String memberUsername = friends.getFriendPersonaName(memberId);

            Player player = new Player();
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
        SteamID lobbySteamId = SteamServiceUtils.convertGenericIdToSteamId(lobbyId);

        if (!matchmaking.getLobbyData(lobbySteamId, "lobby.host.id").equals(localSteamUser.getSteamID().toString()))
        {
            return;
        }

        matchmakingCallback.sendLobbyData(lobbySteamId, key, value);
    }

    @Override
    public void sendPlayerData(MultiplayerId lobbyId, String key)
    {
        sendPlayerData(lobbyId, key, null);
    }

    @Override
    public void sendPlayerData(MultiplayerId lobbyId, String key, String value)
    {
        matchmakingCallback.sendPlayerData(SteamServiceUtils.convertGenericIdToSteamId(lobbyId), key, value);
    }

    @Override
    public void sendHostPlayerData(MultiplayerId lobbyId, String key, String value)
    {
        sendLobbyData(lobbyId, key, value);
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
        player.profile.username = friends.getFriendPersonaName(playerSteamId);

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
        return friends.getPersonaName();
    }

    public PlayerProfile getLocalPlayerProfile()
    {    
        if (localPlayer == null)
        {
            localPlayer = new PlayerProfile();
            localPlayer.id = SteamServiceUtils.convertSteamIdToGenericId(localSteamUser.getSteamID());
            localPlayer.username = friends.getPersonaName();
            localPlayer.avatar = getPlayerAvatar(localSteamUser.getSteamID());
        }

        return localPlayer;    
    }

    private Texture getPlayerAvatar(SteamID id)
    {
        int imageID = friends.getLargeFriendAvatar(id);
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
}
