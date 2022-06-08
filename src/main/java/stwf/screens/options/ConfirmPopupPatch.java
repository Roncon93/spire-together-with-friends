package stwf.screens.options;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.screens.DeathScreen;
import com.megacrit.cardcrawl.screens.options.ConfirmPopup;

import javassist.CtBehavior;
import stwf.multiplayer.MultiplayerManager;

public class ConfirmPopupPatch
{
    @SpirePatch2(clz = ConfirmPopup.class, method = "yesButtonEffect")
    public static class UpdatePatch
    {
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert()
        {
            if (MultiplayerManager.inMultiplayerLobby())
            {
                MultiplayerManager.leaveLobby();
            }
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.NewExprMatcher(DeathScreen.class);
                return LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
