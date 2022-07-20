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
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import javassist.CtBehavior;
import stwf.actions.GameActionManagerPatch;
import stwf.characters.AbstractPlayerPatch;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;

public class AbstractRoomPatch
{
    public static boolean endOfTurnMessageSent = false;
    public static boolean enableEndTurn = false;
    public static boolean enableBattleStart = false;
    public static boolean showInitialBattleStartUI = true;

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class UpdatePatch2
    {
        @SpireInsertPatch(loc = 293)
        public static SpireReturn<Void> Insert()
        {
            if (!MultiplayerManager.inMultiplayerLobby())
            {
                return SpireReturn.Continue();
            }

            if (!showInitialBattleStartUI)
            {
                AbstractDungeon.isScreenUp = false;
                GameActionManagerPatch.enableAddToBottom = true;
                AbstractDungeon.actionManager.turnHasEnded = false;
            }
            else
            {
                AbstractPlayerPatch.enableApplyStartOfCombatPreDrawLogic = false;
                AbstractDungeon.actionManager.turnHasEnded = false;
                showInitialBattleStartUI = false;
            }

            if (GameActionManagerPatch.isLocalPlayerTurn())
            {
                AbstractDungeon.isScreenUp = false;
                showInitialBattleStartUI = true;
                GameActionManagerPatch.enableAddToBottom = true;
                AbstractPlayerPatch.enableApplyStartOfCombatPreDrawLogic = true;
                return SpireReturn.Continue();
            }

            AbstractDungeon.overlayMenu.showCombatPanels();
            AbstractRoom.waitTimer = 0.1f;
            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class UpdatePatch3
    {
        @SpireInsertPatch(loc = 289)
        public static void Insert()
        {
            if (MultiplayerManager.inMultiplayerLobby() && !showInitialBattleStartUI)
            {
                AbstractDungeon.isScreenUp = true;
                GameActionManagerPatch.enableAddToBottom = false;
            }
        }
    }

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
                AbstractPlayerPatch.enableApplyEndOfTurnTriggers = true;
                return SpireReturn.Continue();
            }

            if (!endOfTurnMessageSent)
            {
                MultiplayerManager.sendPlayerData("player.turn-ended", "", false, true);
                endOfTurnMessageSent = true;
                AbstractPlayerPatch.enableLoseBlock = false;
                AbstractPlayerPatch.enableApplyEndOfTurnTriggers = false;
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
