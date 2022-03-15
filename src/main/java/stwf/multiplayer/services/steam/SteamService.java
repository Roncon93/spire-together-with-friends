package stwf.multiplayer.services.steam;

import java.nio.ByteBuffer;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.codedisaster.steamworks.SteamApps;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamFriends;
import com.codedisaster.steamworks.SteamFriends.PersonaChange;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamMatchmaking;
import com.codedisaster.steamworks.SteamNetworking;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUser;
import com.codedisaster.steamworks.SteamUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;

import basemod.ReflectionHacks;
import stwf.multiplayer.PlayerProfile;
import stwf.multiplayer.services.MultiplayerServiceInterface;

public class SteamService implements MultiplayerServiceInterface
{
    // public static SteamCallbacks callbacks;  
    public static SteamMatchmaking matcher;    
    private SteamFriends friends;    
    public static SteamNetworking net;    
    public static SteamUtils utils;

    private PlayerProfile localPlayer;
    private SteamUser localSteamUser;

    public SteamService()
    {
        SteamApps steamApps = (SteamApps)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, com.megacrit.cardcrawl.integrations.steam.SteamIntegration.class, "steamApps");
        // callbacks = new SteamCallbacks();
        // matcher = new SteamMatchmaking(callbacks);
        // net = new SteamNetworking(callbacks);
        utils = new SteamUtils(new SteamServiceUtilsCallback());
        friends = new SteamFriends(new SteamServiceFriendsCallback());

        localSteamUser = (SteamUser)ReflectionHacks.getPrivate(CardCrawlGame.publisherIntegration, com.megacrit.cardcrawl.integrations.steam.SteamIntegration.class, "steamUser");
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
}
