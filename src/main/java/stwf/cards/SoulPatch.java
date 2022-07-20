package stwf.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.Soul;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import stwf.cards.AbstractCardPatch.AbstractCardFields;
import stwf.characters.AbstractPlayerPatch.AbstractPlayerFields;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;

public class SoulPatch
{
    @SpirePatch2(clz = Soul.class, method = "discard", paramtypez = { AbstractCard.class, boolean.class })
    public static class DiscardPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(AbstractCard card)
        {
            Player cardPlayer = AbstractCardFields.playerData.get(card);
            Player characterPlayer = AbstractPlayerFields.playerData.get(AbstractDungeon.player);

            if (!MultiplayerManager.inMultiplayerLobby() || cardPlayer == null || characterPlayer == null || cardPlayer.profile.id.equals(characterPlayer.profile.id))
            {
                return SpireReturn.Continue();
            }

            return SpireReturn.Return();
        }
    }
}
