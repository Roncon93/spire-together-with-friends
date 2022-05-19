package stwf.neow;

import java.lang.reflect.Method;

import com.badlogic.gdx.Gdx;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.neow.NeowEvent;

import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public class NeowEventPatch
{
    private static boolean enableTalk = false;

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
            if (key.equals("event.neow.talked"))
            {
                enableTalk = true;
                TalkPatch.talk(value);
                enableTalk = false;
            }
        }
    };

    @SpirePatch2(clz = NeowEvent.class, method = SpirePatch.CONSTRUCTOR, paramtypez={boolean.class})
    public static class ConstructorPatch
    {
        @SpireInsertPatch
        public static void Postfix(NeowEvent __instance, boolean ___isDone)
        {
            MultiplayerManager.addLobbyCallback(callback);
        }
    }

    @SpirePatch2(clz = NeowEvent.class, method = "talk")
    public static class TalkPatch
    {
        private static NeowEvent instance;

        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(NeowEvent __instance, String ___msg)
        {
            instance = __instance;

            if (!MultiplayerManager.inMultiplayerLobby() || enableTalk)
            {
                return SpireReturn.Continue();
            }

            MultiplayerManager.sendLobbyData("event.neow.talked", ___msg);
            return SpireReturn.Return();
        }

        private static void talk(String message)
        {
            try
            {
                Method talkMethod = NeowEvent.class.getDeclaredMethod("talk", String.class);
                talkMethod.setAccessible(true);
                talkMethod.invoke(instance, message);
            }
            catch (Exception e)
            {                
            }
        }
    }

    @SpirePatch2(clz = AbstractEvent.class, method = "dispose")
    public static class DisposePatch
    {
        @SpireInsertPatch
        public static void Postfix(AbstractEvent __instance)
        {
            MultiplayerManager.removeLobbyCallback(callback);
        }
    }
}
