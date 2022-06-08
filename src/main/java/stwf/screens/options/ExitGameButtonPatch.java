package stwf.screens.options;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.screens.options.ExitGameButton;

import stwf.multiplayer.MultiplayerManager;

public class ExitGameButtonPatch
{
    @SpirePatch2(clz = ExitGameButton.class, method = "update")
    public static class UpdatePatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix()
        {
            if (!MultiplayerManager.inMultiplayerLobby())
            {
                return SpireReturn.Continue();
            }

            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = ExitGameButton.class, method = "render")
    public static class RenderPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix()
        {
            if (!MultiplayerManager.inMultiplayerLobby())
            {
                return SpireReturn.Continue();
            }

            return SpireReturn.Return();
        }
    }
}
