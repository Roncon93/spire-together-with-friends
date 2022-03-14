package stwf.screens.coop;

import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;

/**
 * Patches the MainMenuScreen.CurScreen enum.
 */
public class CurScreenPatch
{
    @SpireEnum
    public static MainMenuScreen.CurScreen HOST_GAME;

    @SpireEnum
    public static MainMenuScreen.CurScreen JOIN_GAME;
}
