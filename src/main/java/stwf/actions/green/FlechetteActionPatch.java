package stwf.actions.green;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.unique.FlechetteAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class FlechetteActionPatch
{
    public static AbstractPlayer player;

    private static AbstractPlayer tempPlayer;

    @SpirePatch2(clz = FlechetteAction.class, method = "update")
    public static class UpdatePatch
    {
        @SpireInsertPatch
        public static void Prefix()
        {
            if (player != null)
            {
                tempPlayer = AbstractDungeon.player;
                AbstractDungeon.player = player;
            }
        }

        @SpireInsertPatch
        public static void Postfix()
        {
            if (player != null)
            {
                AbstractDungeon.player = tempPlayer;
                player = null;
            }
        }
    }
}
