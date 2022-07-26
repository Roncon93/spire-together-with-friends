package stwf.vfx.campfire;

import com.badlogic.gdx.utils.Json;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.vfx.campfire.CampfireSleepEffect;

import stwf.actions.GameActionManagerPatch.HealMessage;
import stwf.multiplayer.MultiplayerManager;

public class CampfireSleepEffectPatch
{
    private static final Json JSON = new Json();

    @SpirePatch2(clz = CampfireSleepEffect.class, method = "update")
    public static class HealPatch
    {
        @SpireInsertPatch(loc = 82)
        public static void Insert(int ___healAmount)
        {
            if (MultiplayerManager.inMultiplayerLobby())
            {            
                HealMessage message = new HealMessage();
                message.amount = ___healAmount;
                message.showEffect = false;

                MultiplayerManager.sendPlayerData("player.heal", JSON.toJson(message));
            }
        }
    }
}
