package stwf.dungeons;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import stwf.map.MapRoomNodePatch;
import stwf.map.MapRoomNodePatch.RoomSelectedMessage;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public class AbstractDungeonPatch
{
    private static MultiplayerServiceLobbyCallback callback = new MultiplayerServiceLobbyCallback()
    {
        @Override
        public void onPlayerJoined(MultiplayerId lobbyId, MultiplayerId playerId)
        {
        }

        @Override
        public void onPlayerLeft(MultiplayerId lobbyId, MultiplayerId playerId)
        {
        }

        @Override
        public void onPlayerDataReceived(MultiplayerId lobbyId, MultiplayerId playerId, String key, String value)
        {
        }

        @Override
        public void onLobbyDataReceived(MultiplayerId lobbyId, String key, String value)
        {
            if (key.equals("map.room.selected"))
            {
                RoomSelectedMessage message = RoomSelectedMessage.fromJson(value);
                
                Gdx.app.postRunnable(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        MapRoomNodePatch.shouldContinue = true;
                        MapRoomNode node = AbstractDungeon.map.get(message.y).get(message.x);
                        node.update();
                        MapRoomNodePatch.messageSent = false;
                        MapRoomNodePatch.shouldContinue = false;
                    }
                });  
            }    
        }
    };

    @SpirePatch2(clz =  AbstractDungeon.class, method = SpirePatch.CONSTRUCTOR, paramtypez={ String.class, String.class, AbstractPlayer.class, ArrayList.class })
    public static class ConstructorPatch
    {
        @SpireInsertPatch
        public static void Postfix()
        {
            MultiplayerManager.addLobbyCallback(callback, "map.room.selected");
        }
    }

    @SpirePatch2(clz =  AbstractDungeon.class, method = SpirePatch.CONSTRUCTOR, paramtypez={ String.class, AbstractPlayer.class, SaveFile.class })
    public static class ConstructorPatch2
    {
        @SpireInsertPatch
        public static void Postfix()
        {
            MultiplayerManager.addLobbyCallback(callback);
        }
    }
}
