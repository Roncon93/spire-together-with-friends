package stwf.actions.common;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.common.DrawCardAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class DrawCardActionPatch
{
    @SpirePatch2(clz = DrawCardAction.class, method = "update")
    public static class GetNextActionPatch
    {
        @SpireInsertPatch()
        public static SpireReturn<Void> Prefix(DrawCardAction __instance, AbstractCreature ___target)
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
