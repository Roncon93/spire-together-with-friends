package stwf.rooms;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import javassist.CtBehavior;
import stwf.characters.AbstractPlayerPatch;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;

public class AbstractRoomPatch
{
    public static boolean endOfTurnMessageSent = false;
    public static boolean enableEndTurn = false;

    @SpirePatch2(clz = AbstractRoom.class, method = "endTurn")
    public static class EndTurnPatch
    {
        @SpireInsertPatch(loc = 521)
        public static SpireReturn<Void> Insert()
        {
            if (!MultiplayerManager.inMultiplayerLobby() || enableEndTurn)
            {
                enableEndTurn = false;
                endOfTurnMessageSent = false;
                return SpireReturn.Continue();
            }

            if (!endOfTurnMessageSent)
            {
                MultiplayerManager.sendPlayerData("player.ended-turn", "", false, true);
                endOfTurnMessageSent = true;
            }

            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "endTurn")
    public static class EndTurnPatch2
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix()
        {
            if (!MultiplayerManager.inMultiplayerLobby() || !endOfTurnMessageSent || enableEndTurn)
            {
                return SpireReturn.Continue();
            }

            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class UpdatePatch
    {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Postfix(AbstractRoom __instance)
        {
            if (MultiplayerManager.inMultiplayerLobby())
            {
                Iterator<Player> players = MultiplayerManager.getPlayers();
                while (players.hasNext())
                {
                    Player player = players.next();
                    player.character.update();
                    player.character.updateAnimations();
                }
            }

            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "updateAnimations");
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "render")
    public static class RenderPatch
    {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Postfix(AbstractRoom __instance, SpriteBatch ___sb)
        {
            if (MultiplayerManager.inMultiplayerLobby())
            {
                AbstractPlayerPatch.enableRender = true;

                Iterator<Player> players = MultiplayerManager.getPlayers();
                while (players.hasNext())
                {
                    Player player = players.next();
                    player.character.render(___sb);
                }
                AbstractPlayerPatch.enableRender = false;
            }

            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "render");
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
