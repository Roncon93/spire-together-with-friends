package stwf.monsters;

import java.util.Iterator;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.powers.AbstractPower;

import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;

public class MonsterGroupPatch
{
    public static boolean enableApplyEndOfTurnPowers = false;

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

    @SpirePatch2(clz = MonsterGroup.class, method = "applyEndOfTurnPowers")
    public static class ApplyEndOfTurnPowersPatch2
    {
        @SpireInsertPatch(loc = 418)
        public static void Insert()
        {
            if (MultiplayerManager.inMultiplayerLobby())
            {
                Iterator<Player> players = MultiplayerManager.getPlayers();
                while (players.hasNext())
                {
                    Player player = players.next();                    
                    if (!player.isLocal)
                    {
                        for (AbstractPower power : player.character.powers)
                        {
                            power.atEndOfRound(); 
                        }
                    }
                }
            }
        }
    }
}
