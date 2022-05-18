package stwf.characters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;

import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;

public class AbstractPlayerPatch
{    
    public static boolean shouldRenderPlayers = false;

    /**
     * Adds fields to the MainMenuScreen class.
     */
    @SpirePatch2(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
    public static class AbstractPlayerFields
    {
        public static SpireField<Player> playerData = new SpireField<>(() -> null);
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "render")
    public static class RenderPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance, SpriteBatch ___sb)
        {
            if (MultiplayerManager.inMultiplayerLobby() && !shouldRenderPlayers)
            {
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }

        @SpireInsertPatch
        public static SpireReturn<Void> Postfix(AbstractPlayer __instance, SpriteBatch ___sb)
        {
            Player playerData = AbstractPlayerFields.playerData.get(__instance);
            if (playerData != null)
            {
                float x = __instance.hb.cX;
                float y = __instance.hb.y + __instance.hb.height;
                
                FontHelper.renderFontCentered(___sb, FontHelper.tipHeaderFont, playerData.profile.username, x, y, Settings.CREAM_COLOR);
            }

            return SpireReturn.Continue();
        }
    }
}
