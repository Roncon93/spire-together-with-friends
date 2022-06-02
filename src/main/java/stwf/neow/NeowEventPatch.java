package stwf.neow;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractEvent;
import com.megacrit.cardcrawl.neow.NeowEvent;
import com.megacrit.cardcrawl.neow.NeowReward;

import javassist.CtBehavior;
import stwf.characters.AbstractPlayerPatch;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;
import stwf.neow.NeowRewardPatch.NeowRewardFieldPatch;

public class NeowEventPatch
{
    public static ArrayList<Integer> blessingRewardIndices;
    public static int drawbackIndex;

    private static NeowEvent instance;
    private static boolean enableTalk = false;
    private static boolean enableMiniBlessing = false;
    private static boolean enableBlessing = false;

    private static final Json JSON = new Json();

    private static final MultiplayerServiceLobbyCallback CALLBACK = new MultiplayerServiceLobbyCallback()
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
            if (key.equals("event.neow.talked"))
            {
                Gdx.app.postRunnable(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        enableTalk = true;
                        TalkMethodPatch.invoke(value);
                        enableTalk = false;
                    }
                });
            }

            else if (key.equals("event.neow.mini-blessing"))
            {
                enableMiniBlessing = true;
                MiniBlessingMethodPatch.invoke();
                enableMiniBlessing = false;
            }

            else if (key.equals("event.neow.blessing"))
            {
                BlessingMethodPatch.Payload payload = JSON.fromJson(BlessingMethodPatch.Payload.class, value);
                blessingRewardIndices = payload.rewardIndices;
                drawbackIndex = payload.drawbackIndex;
                
                enableBlessing = true;
                BlessingMethodPatch.invoke();
                enableBlessing = false;
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
            blessingRewardIndices = null;

            if (MultiplayerManager.inMultiplayerLobby())
            {
                AbstractPlayerPatch.initializeLocalPlayer();
                MultiplayerManager.addLobbyCallback(CALLBACK, "event.neow.talked", "event.neow.mini-blessing", "event.neow.blessing");
            }
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
            else if (MultiplayerManager.isLocalPlayerHost())
            {
                MultiplayerManager.sendPlayerData(lobbyMessageKey, lobbyMessageValue);
                CALLBACK.onPlayerDataReceived(MultiplayerManager.getLocalPlayer().profile.id, lobbyMessageKey, lobbyMessageValue);
                return SpireReturn.Return();
            }
            
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
            catch (InvocationTargetException e)
            {
                e.getTargetException().printStackTrace();
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

    @SpirePatch2(clz = NeowEvent.class, method = "blessing")
    public static class BlessingMethodPatch
    {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert(NeowEvent __instance)
        {
            if (!MultiplayerManager.inMultiplayerLobby() || enableBlessing)
            {
                return SpireReturn.Continue();
            }
            else if (MultiplayerManager.isLocalPlayerHost())
            {
                Payload payload = new Payload();
                for (int i = 0; i < 4; i++)
                {
                    NeowReward reward = new NeowReward(i);
                    int rewardIndex = NeowRewardFieldPatch.rewardIndex.get(reward);
                    payload.rewardIndices.add(rewardIndex);
                    payload.drawbackIndex = NeowRewardFieldPatch.drawbackIndex.get(reward);
                }

                String key = "event.neow.blessing";
                String value = JSON.toJson(payload);

                MultiplayerManager.sendPlayerData(key, value);
                CALLBACK.onPlayerDataReceived(MultiplayerManager.getLocalPlayer().profile.id, key, value);
                return SpireReturn.Return();
            }
            
            return SpireReturn.Return();
        }

        private static void invoke()
        {
            PrivateMethodPatch.invoke(NeowEvent.class, "blessing");
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "bossCount");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }

        public static class Payload
        {
            public ArrayList<Integer> rewardIndices = new ArrayList<>();
            public int drawbackIndex = 0;
        }
    }

    @SpirePatch2(clz = AbstractEvent.class, method = "dispose")
    public static class DisposePatch
    {
        @SpireInsertPatch
        public static void Postfix(AbstractEvent __instance)
        {
            if (MultiplayerManager.inMultiplayerLobby())
            {
                MultiplayerManager.removeLobbyCallback(CALLBACK);
            }
        }
    }
}
