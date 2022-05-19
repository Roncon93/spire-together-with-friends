package stwf.neow;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.neow.NeowReward;
import com.megacrit.cardcrawl.neow.NeowReward.NeowRewardDef;
import com.megacrit.cardcrawl.neow.NeowReward.NeowRewardDrawbackDef;

import javassist.CtBehavior;

public class NeowRewardPatch
{
    private static int indexCounter = 0;

    @SpirePatch2(clz = NeowReward.class, method = SpirePatch.CLASS)
    public static class NeowRewardFieldPatch
    {
        public static SpireField<Integer> rewardIndex = new SpireField<>(() -> 0);
        public static SpireField<Integer> drawbackIndex = new SpireField<>(() -> 0);
    }

    @SpirePatch2(clz = NeowReward.class, method = SpirePatch.CONSTRUCTOR, paramtypez={int.class})
    public static class ConstructorPatch
    {
        @SpireInsertPatch(loc=99, localvars={"possibleRewards", "reward"})
        public static SpireReturn<Void> Insert(NeowReward __instance, int category, @ByRef ArrayList<NeowRewardDef>[] possibleRewards, @ByRef NeowRewardDef[] reward)
        {
            int index = 0;

            if (NeowEventPatch.blessingRewardIndices != null)
            {
                index = indexCounter++;
                reward[0] = possibleRewards[0].get(NeowEventPatch.blessingRewardIndices.get(index));
            }
            else
            {
                indexCounter = 0;
                index = possibleRewards[0].indexOf(reward[0]);
            }

            NeowRewardFieldPatch.rewardIndex.set(__instance, index);
            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(int.class, "hp_bonus");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }

    @SpirePatch2(clz = NeowReward.class, method = "getRewardOptions")
    public static class GetRewardOptionsMethodPatch
    {
        @SpireInsertPatch(locator = Locator.class, localvars = {"drawbackOptions"})
        public static SpireReturn<Void> Insert(NeowReward __instance, @ByRef NeowRewardDrawbackDef[] ___drawbackDef, @ByRef ArrayList<NeowRewardDrawbackDef>[] drawbackOptions)
        {
            if (NeowEventPatch.blessingRewardIndices != null)
            {
                ___drawbackDef[0] = drawbackOptions[0].get(NeowEventPatch.drawbackIndex);
            }
            else
            {
                NeowRewardFieldPatch.rewardIndex.set(__instance, drawbackOptions[0].indexOf(___drawbackDef[0]));
            }

            return SpireReturn.Continue();
        }

        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.FieldAccessMatcher(NeowRewardDrawbackDef.class, "type");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}
