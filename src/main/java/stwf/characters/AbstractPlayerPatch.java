package stwf.characters;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.stances.AbstractStance;

import javassist.CtBehavior;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public class AbstractPlayerPatch
{    
    public static boolean shouldRenderPlayers = false;

    /**
     * Adds fields to the MainMenuScreen class.
     */
    @SpirePatch2(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
    public static class AbstractPlayerFields
    {
        public static SpireField<MultiplayerId> playerId = new SpireField<>(() -> null);
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "render")
    public static class RenderPatch
    {
        @SpireInsertPatch(locator = Locator.class)
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance, SpriteBatch ___sb)
        {
            if (MultiplayerManager.inMultiplayerLobby() && !shouldRenderPlayers)
            {
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractStance.class, "render");
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
