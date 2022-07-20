package stwf.multiplayer.services.steam;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.codedisaster.steamworks.SteamException;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamNetworking.P2PSend;
import com.codedisaster.steamworks.SteamNetworking.P2PSessionError;
import com.codedisaster.steamworks.SteamNetworkingCallback;

public class SteamServiceNetworkingCallback implements SteamNetworkingCallback, Runnable
{
    private final static Logger LOGGER = LogManager.getLogger(SteamServiceNetworkingCallback.class);
    private final static Json JSON = new Json();

    public SteamServiceCallbackInterface steamServiceCallback;

    public SteamServiceNetworkingCallback()
    {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            try
            {
                readIncomingMessage();
                Thread.sleep((long) (1000.0 / Gdx.graphics.getFramesPerSecond()));
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        LOGGER.info("Thread has exited");
    };

    private void readIncomingMessage()
    {
        if (SteamService.networkingService == null)
        {
            return;
        }

        int[] messageSize = { 0 };
        while (SteamService.networkingService.isP2PPacketAvailable(0, messageSize))
        {
            // LOGGER.info("message with " + messageSize[0] + " bytes of size received");

            ByteBuffer buffer = ByteBuffer.allocateDirect(messageSize[0]);
            try
            {
                SteamID senderId = new SteamID();
                SteamService.networkingService.readP2PPacket(senderId, buffer, 0);
                byte[] bytes = new byte[messageSize[0]];
                buffer.get(bytes);
                
                String rawMessage = new String(bytes);

                SteamNetworkingMessage message = JSON.fromJson(SteamNetworkingMessage.class, rawMessage);
                LOGGER.info("Received message: " + rawMessage);

                steamServiceCallback.onPlayerDataReceived(senderId, message.key, message.value);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onP2PSessionConnectFail(SteamID arg0, P2PSessionError arg1)
    {
    }

    @Override
    public void onP2PSessionRequest(SteamID steamIDRemote)
    {
        SteamService.networkingService.acceptP2PSessionWithUser(steamIDRemote);
        sendPlayerData(steamIDRemote, "", "");
    }

    public void sendPlayerData(SteamID playerId, String key, String value)
    {
        SteamNetworkingMessage message = new SteamNetworkingMessage();
        message.key = key;
        message.value = value;

        String messageAsJson = JSON.toJson(message);
        byte[] messageBytes = messageAsJson.getBytes();

        ByteBuffer messageData = ByteBuffer.allocateDirect(messageBytes.length);
        messageData.put(messageBytes);
        
        try
        {
            messageData.position(0);
            SteamService.networkingService.sendP2PPacket(playerId, messageData, P2PSend.Reliable, 0);
        }
        catch (SteamException e1)
        {
            e1.printStackTrace();
        }
    }

    public void sendPlayerData(String key, String value)
    {
        SteamNetworkingMessage message = new SteamNetworkingMessage();
        message.key = key;
        message.value = value;

        String messageAsJson = JSON.toJson(message);
        byte[] messageBytes = messageAsJson.getBytes();

        ByteBuffer messageData = ByteBuffer.allocateDirect(messageBytes.length);
        messageData.put(messageBytes);
        
        try
        {
            LOGGER.info("Sending message: " + messageAsJson);

            for (Iterator<SteamID> playerIds = steamServiceCallback.getRemotePlayerIds(); playerIds.hasNext();)
            {
                SteamID playerId = playerIds.next();
                messageData.position(0);
                boolean sent = SteamService.networkingService.sendP2PPacket(playerId, messageData, P2PSend.Reliable, 0);
                LOGGER.info("Sent message with result: " + sent);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static class SteamNetworkingMessage
    {
        public String key;
        public String value;
    }
}
