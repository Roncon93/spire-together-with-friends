package stwf.characters;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.evacipated.cardcrawl.modthespire.lib.SpireField;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;

import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;

public class AbstractPlayerPatch
{
    public static boolean enablePreBattlePrep = false;
    public static boolean enableShowHealthBar = false;
    public static boolean enableLoseBlock = false;
    public static boolean enableDamage = false;
    public static boolean enableApplyStartOfCombatPreDrawLogic = false;
    public static boolean enableApplyEndOfTurnTriggers = true;

    private static final float[][] POSITIONS =
    {
        { Settings.WIDTH * 0.32F, AbstractDungeon.floorY * 1.1f },
        { Settings.WIDTH * 0.22f, AbstractDungeon.floorY * 0.8f }
    };

    public static boolean enableRender = false;
    public static boolean enableMovePosition = false;

    public static void initializeLocalPlayer()
    {
        Iterator<Player> players = MultiplayerManager.getPlayers();
        while (players.hasNext())
        {
            Player player = players.next();
            if (player.isLocal && AbstractDungeon.player != player.character)
            {
                AbstractPlayer temp = player.character;
                player.character = AbstractDungeon.player;
                AbstractPlayerFields.playerData.set(player.character, AbstractPlayerFields.playerData.get(temp));
                temp.dispose();

                player.character.movePosition(0, 0);
            }
        }
    }

    /**
     * Adds fields to the MainMenuScreen class.
     */
    @SpirePatch2(clz = AbstractPlayer.class, method = SpirePatch.CLASS)
    public static class AbstractPlayerFields
    {
        public static SpireField<Player> playerData = new SpireField<>(() -> null);
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "render")
    public static class RenderPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(AbstractPlayer __instance, SpriteBatch ___sb)
        {
            if (MultiplayerManager.inMultiplayerLobby() && !enableRender)
            {
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }

        @SpireInsertPatch
        public static SpireReturn<Void> Postfix(AbstractPlayer __instance, SpriteBatch ___sb)
        {
            Player playerData = AbstractPlayerFields.playerData.get(__instance);
            if (playerData != null)
            {
                float x = __instance.hb.cX;
                float y = __instance.hb.y + __instance.hb.height;
                
                FontHelper.renderFontCentered(___sb, FontHelper.tipHeaderFont, playerData.profile.username, x, y, Settings.CREAM_COLOR);
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "applyEndOfTurnTriggers")
    public static class ApplyEndOfTurnTriggersPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix()
        {
            if (MultiplayerManager.inMultiplayerLobby() && !enableApplyEndOfTurnTriggers)
            {
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "applyStartOfCombatPreDrawLogic")
    public static class ApplyStartOfCombatPreDrawLogicPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix()
        {
            if (MultiplayerManager.inMultiplayerLobby() && !enableApplyStartOfCombatPreDrawLogic)
            {
                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "preBattlePrep")
    public static class PreBattlePrepPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix()
        {
            if (MultiplayerManager.inMultiplayerLobby() && !enablePreBattlePrep)
            {
                enablePreBattlePrep = true;

                Iterator<Player> players = MultiplayerManager.getPlayers();
                while (players.hasNext())
                {
                    Player player = players.next();
                    player.character.preBattlePrep();
                }

                enablePreBattlePrep = false;

                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "showHealthBar")
    public static class ShowHealthBarPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(AbstractCreature __instance)
        {
            if (MultiplayerManager.inMultiplayerLobby() && __instance == AbstractDungeon.player && !enableShowHealthBar)
            {
                enableShowHealthBar = true;

                Iterator<Player> players = MultiplayerManager.getPlayers();
                while (players.hasNext())
                {
                    Player player = players.next();
                    player.character.showHealthBar();
                }

                enableShowHealthBar = false;

                return SpireReturn.Return();
            }

            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "loseBlock", paramtypez = { int.class, boolean.class })
    public static class LoseBlockPatch
    {
        @SpireInsertPatch
        public static void Prefix(AbstractCreature __instance, int amount, boolean noAnimation)
        {
            if (MultiplayerManager.inMultiplayerLobby() && __instance == AbstractDungeon.player && enableLoseBlock)
            {
                LoseBlockMessage message = new LoseBlockMessage(amount, noAnimation);             
                MultiplayerManager.sendPlayerData("player.lose-block", new Json().toJson(message));
            }
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "increaseMaxHp")
    public static class IncreaseMaxHpPatch
    {
        @SpireInsertPatch
        public static void Prefix(AbstractCreature __instance, int amount)
        {
            if (MultiplayerManager.inMultiplayerLobby() && __instance == AbstractDungeon.player)
            {            
                MultiplayerManager.sendPlayerData("player.increase-max-hp", Integer.toString(amount));
            }
        }
    }

    @SpirePatch2(clz = AbstractCreature.class, method = "decreaseMaxHealth")
    public static class DecreaseMaxHpPatch
    {
        @SpireInsertPatch
        public static void Prefix(AbstractCreature __instance, int amount)
        {
            if (MultiplayerManager.inMultiplayerLobby() && __instance == AbstractDungeon.player)
            {       
                MultiplayerManager.sendPlayerData("player.decrease-max-hp", Integer.toString(amount));
            }
        }
    }

    @SpirePatch2(clz = AbstractPlayer.class, method = "movePosition")
    public static class MovePositionPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix()
        {
            if (enableMovePosition)
            {
                return SpireReturn.Continue();
            }

            enableMovePosition = true;

            Iterator<Player> players = MultiplayerManager.getPlayers();
            while (players.hasNext())
            {
                Player player = players.next();
                int index = MultiplayerManager.getPlayerIndex(player);

                player.character.movePosition(POSITIONS[index][0], POSITIONS[index][1]);
            }

            enableMovePosition = false;            
            return SpireReturn.Return();
        }
    }

    public static class LoseBlockMessage
    {
        public Integer amount;
        public Boolean noAnimation;

        public LoseBlockMessage()
        {
            this(0, false);
        }

        public LoseBlockMessage(int amount, boolean noAnimation)
        {
            this.amount = amount;
            this.noAnimation = noAnimation;
        }
    }
}
