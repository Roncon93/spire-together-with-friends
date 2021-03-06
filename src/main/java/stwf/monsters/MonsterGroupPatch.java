package stwf.monsters;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

import stwf.multiplayer.MultiplayerManager;

public class MonsterGroupPatch
{
    public static boolean enableApplyEndOfTurnPowers = true;

    @SpirePatch2(clz = MonsterGroup.class, method = "applyEndOfTurnPowers")
    public static class ApplyEndOfTurnPowersPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix()
        {
            if (!MultiplayerManager.inMultiplayerLobby() || enableApplyEndOfTurnPowers)
            {
                return SpireReturn.Continue();
            }

            return SpireReturn.Return();
        }
    }
}
