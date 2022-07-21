package stwf.cards;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

import stwf.cards.AbstractCardPatch.AbstractCardFields;
import stwf.characters.AbstractPlayerPatch.AbstractPlayerFields;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;

public class CardGroupPatch
{
    private static AbstractPlayer tempPlayer;

    @SpirePatch2(clz = CardGroup.class, method = "moveToExhaustPile")
    public static class MoveToExhaustPilePatch
    {
        @SpireInsertPatch()
        public static void Prefix(AbstractCard c)
        {
            Player cardPlayer = AbstractCardFields.playerData.get(c);

            if (MultiplayerManager.inMultiplayerLobby() && cardPlayer != null)
            {
                tempPlayer = AbstractDungeon.player;
                AbstractDungeon.player = cardPlayer.character;
            }
        }
    }

    @SpirePatch2(clz = CardGroup.class, method = "moveToExhaustPile")
    public static class MoveToExhaustPilePatch2
    {
        @SpireInsertPatch(loc = 947)
        public static SpireReturn<Void> Insert(AbstractCard c)
        {
            if (tempPlayer != null)
            {
                AbstractDungeon.player = tempPlayer;
                tempPlayer = null;
            }

            Player cardPlayer = AbstractCardFields.playerData.get(c);
            Player characterPlayer = AbstractPlayerFields.playerData.get(AbstractDungeon.player);

            if (!MultiplayerManager.inMultiplayerLobby() || cardPlayer == null || characterPlayer == null || cardPlayer.profile.id.equals(characterPlayer.profile.id))
            {
                return SpireReturn.Continue();
            }

            return SpireReturn.Return();
        }
    }
}
