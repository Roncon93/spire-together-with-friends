package stwf.core;

import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.core.Settings;

import stwf.multiplayer.MultiplayerManager;

public class SettingsPatch
{
    public static boolean isFinalActAvailable = false;

    @SpirePatch2(clz = Settings.class, method = "setFinalActAvailability")
    public static class SetFinalActAvailabilityPatch
    {
        @SpireInsertPatch
        public static void Postfix()
        {
            if (MultiplayerManager.inMultiplayerLobby() && MultiplayerManager.isInDungeon())
            {
                Settings.isFinalActAvailable = isFinalActAvailable;
            }
        }
    }
}
