package stwf.map;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.map.MapRoomNode;

import javassist.CtBehavior;
import stwf.multiplayer.MultiplayerManager;

public class MapRoomNodePatch
{
    public static boolean shouldContinue = false;
    public static boolean enableTimer = false;
    public static boolean messageSent = false;

    @SpirePatch2(clz = MapRoomNode.class, method = "update")
    public static class UpdatePatch
    {
        @SpireInsertPatch(locator =  Locator.class)
        public static void Insert(MapRoomNode __instance, float ___animWaitTimer)
        {
            if (AbstractDungeon.dungeonMapScreen.clicked)
            {                
                if (MultiplayerManager.isLocalPlayerHost())
                {
                    messageSent = true;
                    RoomSelectedMessage message = new RoomSelectedMessage(__instance.x, __instance.y);
                    MultiplayerManager.sendPlayerData("map.room.selected", RoomSelectedMessage.toJson(message));
                }
                else
                {
                    AbstractDungeon.dungeonMapScreen.clicked = false;
                }
            }

            if (shouldContinue)
            {
                AbstractDungeon.dungeonMapScreen.clicked = shouldContinue;
            }
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractDungeon.class, "screen");
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = MapRoomNode.class, method = "update")
    public static class UpdatePatch2
    {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Insert(MapRoomNode __instance)
        {
            if (shouldContinue)
            {
                __instance.hb.hovered = true;
            }

            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(Hitbox.class, "hovered");
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    public static class RoomSelectedMessage
    {
        private static final Json JSON = new Json();

        public Integer x;
        public Integer y;

        public RoomSelectedMessage() {}

        public RoomSelectedMessage(Integer x, Integer y)
        {
            this.x = x;
            this.y = y;
        }

        public static String toJson(RoomSelectedMessage message)
        {
            return JSON.toJson(message);
        }

        public static RoomSelectedMessage fromJson(String message)
        {
            return JSON.fromJson(RoomSelectedMessage.class, message);
        }
    }
}
