package stwf.dungeons;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import stwf.map.MapRoomNodePatch;
import stwf.map.MapRoomNodePatch.RoomSelectedMessage;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.rooms.AbstractRoomPatch;

public class AbstractDungeonPatch
{
    public static final MultiplayerServiceLobbyCallback CALLBACK = new MultiplayerServiceLobbyCallback()
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
        public void onPlayerDataReceived(MultiplayerId playerId, String key, String value)
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

            else if (key.equals("player.ended-turn"))
            {
                boolean allPlayersEndedTurn = true;

                Iterator<Player> players = MultiplayerManager.getPlayers();
                while (players.hasNext())
                {
                    Player player = players.next();

                    if (player.profile.id.equals(playerId))
                    {
                        player.endedTurn = true;
                    }

                    if (!player.endedTurn)
                    {
                        allPlayersEndedTurn = false;
                    }
                }

                if (allPlayersEndedTurn)
                {
                    players = MultiplayerManager.getPlayers();
                    while (players.hasNext())
                    {
                        Player player = players.next();
                        player.endedTurn = false;
                    }

                    AbstractRoomPatch.enableEndTurn = true;
                    AbstractRoom.waitTimer = 0.1f;
                }
            }
        }
    };

    @SpirePatch2(clz =  AbstractDungeon.class, method = SpirePatch.CONSTRUCTOR, paramtypez={ String.class, String.class, AbstractPlayer.class, ArrayList.class })
    public static class ConstructorPatch
    {
        @SpireInsertPatch
        public static void Postfix()
        {
            MultiplayerManager.addLobbyCallback(CALLBACK, "map.room.selected");
        }
    }

    @SpirePatch2(clz =  AbstractDungeon.class, method = SpirePatch.CONSTRUCTOR, paramtypez={ String.class, AbstractPlayer.class, SaveFile.class })
    public static class ConstructorPatch2
    {
        @SpireInsertPatch
        public static void Postfix()
        {
            MultiplayerManager.addLobbyCallback(CALLBACK);
        }
    }
}
