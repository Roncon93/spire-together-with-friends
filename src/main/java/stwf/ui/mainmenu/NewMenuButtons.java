package stwf.ui.mainmenu;

import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireEnum;
import com.megacrit.cardcrawl.screens.mainMenu.MenuButton;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.map.*;
import com.megacrit.cardcrawl.core.*;
import basemod.ReflectionHacks;
import stwf.util.RichPresencePatch;

import java.lang.reflect.Field;

public class NewMenuButtons
{
    @SpireEnum
    static MenuButton.ClickResult SINGLE_PLAYER;

    @SpireEnum
    static MenuButton.ClickResult COOP;

    // static public NewGameScreen newGameScreen = null;
    // static public MainLobbyScreen lobbyScreen = null;
    // static public CustomModePopOver customScreen = null;

    @SpirePatch(clz=MenuButton.class, method="setLabel")
    public static class SetLabel {
        public static void Postfix(MenuButton __instance)
        {
            try 
            {
                if (__instance.result == SINGLE_PLAYER) 
                {
                    Field f_label = MenuButton.class.getDeclaredField("label");
                    f_label.setAccessible(true);
                    f_label.set(__instance, CardCrawlGame.languagePack.getUIString("MainMenu").TEXT[0]);
                }
                else if (__instance.result == COOP) 
                {
                    Field f_label = MenuButton.class.getDeclaredField("label");
                    f_label.setAccessible(true);
                    f_label.set(__instance, CardCrawlGame.languagePack.getUIString("MainMenu").TEXT[1]);
                }
            } 
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
    }

    @SpirePatch(clz=MenuButton.class, method="buttonEffect")
    public static class ButtonEffect {
        public static void Postfix(MenuButton __instance)
        {
            if (__instance.result == COOP)   {  
                RichPresencePatch.setRP(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[2]);
                ReflectionHacks.setPrivateStaticFinal(Legend.class, "Y", 600.F * Settings.yScale); 
            }
        }
    }

    // public static void openNewGame() {
    //     customScreen = new CustomModePopOver();
    //     newGameScreen = new NewGameScreen();
    //     newGameScreen.open();
    // }

    // public static void joinNewGame() {
    //     customScreen = new CustomModePopOver();
    //     newGameScreen = new NewGameScreen();
    //     newGameScreen.join();
    // }

    // public static void openLobby() {
    //     TogetherManager.clearMultiplayerData();

    //     lobbyScreen = new MainLobbyScreen();
    //     lobbyScreen.open();            
    // }
}