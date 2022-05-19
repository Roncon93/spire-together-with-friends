package stwf.neow;

import java.lang.reflect.Method;

import com.badlogic.gdx.math.MathUtils;
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
    private static NeowEvent instance;
    private static boolean enableTalk = false;
    private static boolean enableMiniBlessing = false;

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
                TalkMethodPatch.invoke(value);
                enableTalk = false;
            }
            else if (key.equals("event.neow.mini-blessing"))
            {
                enableMiniBlessing = true;
                MiniBlessingMethodPatch.invoke();
                enableMiniBlessing = false;
            }
        }
    };

    @SpirePatch2(clz = NeowEvent.class, method = SpirePatch.CONSTRUCTOR, paramtypez={boolean.class})
    public static class ConstructorPatch
    {
        @SpireInsertPatch
        public static void Postfix(NeowEvent __instance, boolean ___isDone)
        {
            instance = __instance;
            MultiplayerManager.addLobbyCallback(callback);
        }
    }

    public static abstract class PrivateMethodPatch
    {
        protected static SpireReturn<Void> Prefix(boolean shouldContinue, String lobbyMessageKey, String lobbyMessageValue)
        {
            if (!MultiplayerManager.inMultiplayerLobby() || shouldContinue)
            {
                return SpireReturn.Continue();
            }

            MultiplayerManager.sendLobbyData(lobbyMessageKey, lobbyMessageValue);
            return SpireReturn.Return();
        }

        private static void invoke(Class<?> cls, String methodName, Object ...arguments)
        {
            try
            {
                Class<?>[] classes = new Class<?>[arguments.length];

                for (int i = 0; i < arguments.length; i++)
                {
                    classes[i] = arguments[i].getClass();
                }

                Method method;

                if (arguments.length == 0)
                {
                    method = cls.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    method.invoke(instance);
                }
                else
                {
                    method = cls.getDeclaredMethod(methodName, classes);
                    method.setAccessible(true);
                    method.invoke(instance, arguments);
                }                
            }
            catch (Exception e)
            {
                e.printStackTrace();        
            }
        }
    }

    @SpirePatch2(clz = NeowEvent.class, method = "talk")
    public static class TalkMethodPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(NeowEvent __instance, String ___msg)
        {
            return PrivateMethodPatch.Prefix(enableTalk, "event.neow.talked", ___msg);
        }        

        private static void invoke(String message)
        {
            PrivateMethodPatch.invoke(NeowEvent.class, "talk", message);
        }
    }

    @SpirePatch2(clz = NeowEvent.class, method = "miniBlessing")
    public static class MiniBlessingMethodPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(NeowEvent __instance)
        {
            return PrivateMethodPatch.Prefix(enableMiniBlessing, "event.neow.mini-blessing", Integer.toString(MathUtils.random(4, 6)));
        }

        private static void invoke()
        {
            PrivateMethodPatch.invoke(NeowEvent.class, "miniBlessing");
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
