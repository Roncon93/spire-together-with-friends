package stwf.screens.mainMenu;

import java.util.ArrayList;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.screens.mainMenu.MainMenuScreen;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.screens.options.ConfirmPopup;
import com.megacrit.cardcrawl.screens.stats.StatsScreen;

import javassist.CtBehavior;

/**
 * Patches the MainMenuScreen class to override the default
 * menu buttons with the "Single Player" and "Cooperative" modes buttons.
 */
public class MainMenuScreenPatch
{
    private static MainMenuScreen mainMenuScreen;

    /**
     * Updates the menu buttons based on the menu value.
     * @param menu The menu buttons to show.
     */
    public static void showMenu(Menus menu)
    {
        MainMenuScreenFields.showMenu.set(mainMenuScreen, menu);
        MainMenuScreenFields.menuButtonsUpdated.set(mainMenuScreen, false);
    }

    /**
     * Adds fields to the MainMenuScreen class.
     */
    @SpirePatch(clz = MainMenuScreen.class, method = SpirePatch.CLASS)
    public static class MainMenuScreenFields
    {
        public static SpireField<Menus> showMenu = new SpireField<>(() -> Menus.MAIN);

        public static SpireField<Boolean> menuButtonsUpdated = new SpireField<>(() -> false);
    }

    /**
     * Patches the setMainMenuButtons() method.
     */
    @SpirePatch2(clz = MainMenuScreen.class, method = "setMainMenuButtons")
    public static class SetMainMenuButtonsPatch
    {
        /**
         * Saves the MainMenuScreen instance so its fields can be updated.
         * @param __instance
         */
        public static void Postfix(MainMenuScreen __instance)
        {
            mainMenuScreen = __instance;
        }
    }

    /**
     * Patches the update() methods.
     */
    @SpirePatch2(clz = MainMenuScreen.class, method = "update")
    public static class MainMenuScreenUpdatePatch
    {
        /**
         * Inserts logic to update the menu buttons.
         * @param __instance The MainMenuScreen object being patched.
         * @param ___buttons The list of buttons to modify.
         * @param ___statsScreen The StatsScreen to determine if the stats and info buttons should be shown.
         */
        @SpireInsertPatch(locator = Locator.class)
        public static void Insert(MainMenuScreen __instance, ArrayList<MenuButton> ___buttons, StatsScreen ___statsScreen)
        {
            Menus menu = MainMenuScreenFields.showMenu.get(__instance);
            boolean menuButtonsUpdated = MainMenuScreenFields.menuButtonsUpdated.get(__instance);

            if (!menuButtonsUpdated)
            {
                MainMenuScreenFields.menuButtonsUpdated.set(__instance, true);

                ___buttons.clear();
                int index = 0;

                if (menu == Menus.MAIN)
                {
                    AddMainScreenButtons(___buttons, ___statsScreen, index);
                }  
                else if (menu == Menus.SINGLE_PLAYER)
                {
                    AddSinglePlayerModeButtons(___buttons, index);
                }
                else if (menu == Menus.COOP)
                {
                    AddCoopModeButtons(___buttons, index);
                }           
            }
        }

        /**
         * Adds the main menu buttons.
         * @param buttons The list of buttons to modify.
         * @param statsScreen The StatsScreen to determine if the stats and info buttons should be shown.
         * @param index The current index of the buttons list.
         */
        public static void AddMainScreenButtons(ArrayList<MenuButton> buttons, StatsScreen statsScreen, int index)
        {
            if (!Settings.isMobile && !Settings.isConsoleBuild)
            {
                buttons.add(new MenuButton(MenuButton.ClickResult.QUIT, index++));
                buttons.add(new MenuButton(MenuButton.ClickResult.PATCH_NOTES, index++));
            } 

            buttons.add(new MenuButton(MenuButton.ClickResult.SETTINGS, index++));

            if (!Settings.isShowBuild && statsScreen.statScreenUnlocked())
            {
                buttons.add(new MenuButton(MenuButton.ClickResult.STAT, index++));
                buttons.add(new MenuButton(MenuButton.ClickResult.INFO, index++));
            }

            buttons.add(new MenuButton(MenuButtonsPatch.COOP, index++));
            buttons.add(new MenuButton(MenuButtonsPatch.SINGLE_PLAYER, index++));
        } 

        /**
         * Adds the single player mode buttons.
         * @param buttons The list of buttons to modify.
         * @param index The current index of the buttons list.
         */
        public static void AddSinglePlayerModeButtons(ArrayList<MenuButton> buttons, int index)
        {
            buttons.add(new MenuButton(MenuButtonsPatch.BACK, index++));

            if (CardCrawlGame.characterManager.anySaveFileExists())
            {
                buttons.add(new MenuButton(MenuButton.ClickResult.ABANDON_RUN, index++));
                buttons.add(new MenuButton(MenuButton.ClickResult.RESUME_GAME, index++));
            }
            else
            {
                buttons.add(new MenuButton(MenuButton.ClickResult.PLAY, index++));
            }
        }

        /**
         * Adds the co-op mode buttons.
         * @param buttons The list of buttons to modify.
         * @param index The current index of the buttons list.
         */
        public static void AddCoopModeButtons(ArrayList<MenuButton> buttons, int index)
        {
            buttons.add(new MenuButton(MenuButtonsPatch.BACK, index++));
            buttons.add(new MenuButton(MenuButtonsPatch.HOST_GAME, index++));
            buttons.add(new MenuButton(MenuButtonsPatch.JOIN_GAME, index++));
        }

        /**
         * Locator used to insert code inside the method.
         */
        public static class Locator extends SpireInsertLocator
        {
            @Override
            public int[] Locate(CtBehavior ctMethodToPatch) throws Exception
            {
                Matcher finalMatcher = new Matcher.MethodCallMatcher(ConfirmPopup.class, "update");
                return LineFinder.findInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
            }
        }
    }
}