package stwf.actions;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;

public class AbstractGameActionPatch
{
    @SpirePatch2(clz = AbstractGameAction.class, method = SpirePatch.CLASS)
    public static class AbstractGameActionFieldsPatch
    {
        public static SpireField<Boolean> process = new SpireField<>(() -> false);
    }
}
