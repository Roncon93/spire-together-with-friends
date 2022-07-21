package stwf.actions.common;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.GainEnergyAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class GainEnergyActionPatch
{
    @SpirePatch2(clz = GainEnergyAction.class, method = "update")
    public static class GetNextActionPatch
    {
        @SpireInsertPatch()
        public static SpireReturn<Void> Prefix(GainEnergyAction __instance, AbstractCreature ___target)
        {
            if (___target == AbstractDungeon.player)
            {
                return SpireReturn.Continue();
            }

            __instance.isDone = true;
            return SpireReturn.Return();
        }
    }
}
