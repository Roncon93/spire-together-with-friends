package stwf.multiplayer.services.steam;

import java.nio.ByteBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.codedisaster.steamworks.SteamApps;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamMatchmaking.LobbyType;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUser;
import com.codedisaster.steamworks.SteamUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.ReflectionHacks;
import stwf.multiplayer.PlayerProfile;
import stwf.multiplayer.services.MultiplayerLobbyType;
import stwf.multiplayer.services.MultiplayerServiceInterface;
import stwf.multiplayer.services.MultiplayerServiceResult;

public class SteamService implements MultiplayerServiceInterface
{
    // public static SteamCallbacks callbacks;  
    private SteamMatchmaking matchmaking;    
    private SteamFriends friends;    
    // private SteamNetworking net;    
    private SteamUtils utils;

    private SteamServiceMatchmakingCallback matchmakingCallback;

    private PlayerProfile localPlayer;
    private SteamUser localSteamUser;

    public SteamService()
    {
        matchmakingCallback = new SteamServiceMatchmakingCallback();

        SteamApps steamApps = (SteamApps)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, com.megacrit.cardcrawl.integrations.steam.SteamIntegration.class, "steamApps");
        // callbacks = new SteamCallbacks();
        // net = new SteamNetworking(callbacks);
        matchmaking = new SteamMatchmaking(matchmakingCallback);
        utils = new SteamUtils(new SteamServiceUtilsCallback());
        friends = new SteamFriends(new SteamServiceFriendsCallback());

        localSteamUser = (SteamUser)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, com.megacrit.cardcrawl.integrations.steam.SteamIntegration.class, "steamUser");
        
    }

    public void createLobby(MultiplayerServiceInterface.LobbyEventListener listener, MultiplayerLobbyType type, int maxPlayers)
    {
        matchmakingCallback.lobbyEventListener = listener;
        matchmaking.createLobby(SteamServiceUtils.convertGenericLobbyTypeToSteamLobbyType(type), maxPlayers);
    }

    public void leaveLobby(MultiplayerServiceId id)
    {
        matchmaking.leaveLobby(SteamServiceUtils.convertGenericIdToSteamId(id));
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
        public static SteamID convertGenericIdToSteamId(MultiplayerServiceId id)
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

        public static MultiplayerServiceId convertSteamIdToGenericId(SteamID id)
        {
            return new MultiplayerServiceId(id);
        }
    }
    
    public static class MultiplayerServiceId
    {
        private final SteamID steamId;

        public MultiplayerServiceId(SteamID id)
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
    }
}
