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
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import javassist.CtBehavior;
import stwf.characters.AbstractPlayerPatch;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;

public class AbstractRoomPatch
{
    private static float[][] playerPositions =
    {
        { Settings.WIDTH * 0.25F, AbstractDungeon.floorY },
        { Settings.WIDTH * 0.20f, AbstractDungeon.floorY * 0.7f }
    };

    @SpirePatch2(clz = AbstractRoom.class, method = "render")
    public static class RenderPatch
    {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Postfix(AbstractRoom __instance, SpriteBatch ___sb)
        {
            if (MultiplayerManager.inMultiplayerLobby())
            {
                AbstractPlayerPatch.shouldRenderPlayers = true;
                int counter = 0;

                Iterator<Player> players = MultiplayerManager.getPlayers();
                while (players.hasNext())
                {
                    Player player = players.next();

                    player.character.movePosition(playerPositions[counter][0], playerPositions[counter][1]);
                    player.character.render(___sb);

                    counter++;
                }
                AbstractPlayerPatch.shouldRenderPlayers = false;
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

    @SpirePatch2(clz = AbstractRoom.class, method = "update")
    public static class UpdatePatch
    {
        @SpireInsertPatch(locator = Locator.class)
        public static void Postfix(AbstractRoom __instance)
        {
            // Initialize();

            // if (!(AbstractDungeon.getCurrRoom() instanceof com.megacrit.cardcrawl.rooms.RestRoom))
            // {
            //     player.update();
            //     player.updateAnimations();
            //     System.out.println(AbstractPlayerFields.playerId.get(player));
            // }
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "update");
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
