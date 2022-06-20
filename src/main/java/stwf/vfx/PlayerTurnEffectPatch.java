package stwf.vfx;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.vfx.PlayerTurnEffect;

import stwf.multiplayer.MultiplayerManager;

public class PlayerTurnEffectPatch
{
    @SpirePatch2(clz = PlayerTurnEffect.class, method = SpirePatch.CONSTRUCTOR)
    public static class Test
    {
        @SpireInsertPatch
        public static void Prefix()
        {
            if (MultiplayerManager.inMultiplayerLobby())
            {
                MultiplayerManager.sendPlayerData("player.turn-started", "");
            }
        }
    }
}
