package stwf.ui.mainmenu;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.ByRef;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.characters.CharacterManager;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;

import javassist.CtBehavior;

@SpirePatch(
    clz=MainMenuScreen.class,
    method="setMainMenuButtons"
)
public class MainMenuItems
{
    @SpireInsertPatch(
        locator=Locator.class,
        localvars={"index"}
    )
    public static SpireReturn<Void> Insert(Object __obj_instance, @ByRef int[] index)
    {
        MainMenuScreen __instance = (MainMenuScreen)__obj_instance;
        __instance.buttons.add(new MenuButton(NewMenuButtons.COOP, index[0]++));
        __instance.buttons.add(new MenuButton(NewMenuButtons.SINGLE_PLAYER, index[0]++));

        return SpireReturn.Return();
    }

    private static class Locator extends SpireInsertLocator
    {
        @Override
        public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
        {
            Matcher finalMatcher = new Matcher.MethodCallMatcher(CharacterManager.class, "anySaveFileExists");
            return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
        }
    }
}