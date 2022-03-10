package stwf.screens.mainMenu;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.core.*;
import stwf.util.RichPresencePatch;
import java.lang.reflect.Field;

/**
 * Patches the MenuButton class to set custom labels and click logic.
 */
public class MenuButtonsPatch
{
    @SpireEnum
    static MenuButton.ClickResult SINGLE_PLAYER;

    @SpireEnum
    static MenuButton.ClickResult COOP;

    @SpireEnum
    static MenuButton.ClickResult JOIN_GAME;

    @SpireEnum
    static MenuButton.ClickResult HOST_GAME;

    @SpireEnum
    static MenuButton.ClickResult BACK;

    /**
     * Patches the setLabel method to use the localization UI strings for the buttons.
     */
    @SpirePatch(clz=MenuButton.class, method="setLabel")
    public static class SetLabel 
    {
        /**
         * Updates the button labels depending on which button is being labeled.
         * @param __instance The MenuButton being modified.
         */
        public static void Postfix(MenuButton __instance)
        {
            try 
            {
                if (__instance.result == SINGLE_PLAYER) 
                {
                    SetMenuButtonLabel(__instance, GetMainMenuUIString(0));
                }
                else if (__instance.result == COOP) 
                {
                    SetMenuButtonLabel(__instance, GetMainMenuUIString(1));
                }
                else if (__instance.result == JOIN_GAME) 
                {
                    SetMenuButtonLabel(__instance, GetMainMenuUIString(3));
                }
                else if (__instance.result == HOST_GAME) 
                {
                    SetMenuButtonLabel(__instance, GetMainMenuUIString(4));
                }
                else if (__instance.result == BACK) 
                {
                    SetMenuButtonLabel(__instance, GetMainMenuUIString(5));
                }
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }

        /**
         * Sets the button's label.
         * @param button The button being modified.
         * @param label The label to use for the button.
         * @throws NoSuchFieldException
         * @throws IllegalAccessException
         */
        private static void SetMenuButtonLabel(MenuButton button, String label) throws NoSuchFieldException, IllegalAccessException
        {
            Field f_label = MenuButton.class.getDeclaredField("label");
            f_label.setAccessible(true);
            f_label.set(button, label);
        }

        /**
         * Retrieves localization strings under the "MainMenu" portion.
         * @param index The index of string to retrieve.
         * @return The string associated with the given index.
         */
        private static String GetMainMenuUIString(int index)
        {
            return CardCrawlGame.languagePack.getUIString("MainMenu").TEXT[index];
        }
    }

    /**
     * Patches the buttonEffect method.
     */
    @SpirePatch(clz=MenuButton.class, method="buttonEffect")
    public static class ButtonEffect 
    {
        /**
         * Inserts the click logic for every patched button.
         * This is used to tell MainMenuScreen to change which menu
         * should be displayed.
         * @param __instance The button being modified.
         */
        public static void Postfix(MenuButton __instance)
        {
            if (__instance.result == SINGLE_PLAYER)
            {  
                RichPresencePatch.setRP(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[2]);
                MainMenuScreenPatch.showMenu(Menus.SINGLE_PLAYER);
            }
            else if (__instance.result == COOP)
            {  
                RichPresencePatch.setRP(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[2]);
                MainMenuScreenPatch.showMenu(Menus.COOP);
            }
            else if (__instance.result == BACK)
            {
                RichPresencePatch.setRP(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[2]);
                MainMenuScreenPatch.showMenu(Menus.MAIN);
            }
        }
    }
}