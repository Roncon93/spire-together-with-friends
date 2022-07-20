package stwf.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.cards.AbstractCard;

import stwf.multiplayer.Player;

public class AbstractCardPatch
{
    @SpirePatch2(clz = AbstractCard.class, method = SpirePatch.CLASS)
    public static class AbstractCardFields
    {
        public static SpireField<Player> playerData = new SpireField<>(() -> null);
    }
}
