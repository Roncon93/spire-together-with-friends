package stwf.actions;

import java.lang.reflect.Field;

import com.badlogic.gdx.utils.Json;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;

import stwf.characters.AbstractPlayerPatch.LoseBlockMessage;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public class GameActionManagerPatch
{
    private static final Json JSON = new Json();

    public static boolean skipMessage = false;

    private static final MultiplayerServiceLobbyCallback CALLBACK = new MultiplayerServiceLobbyCallback()
    {
        @Override
        public void onPlayerJoined(MultiplayerId lobbyId, MultiplayerId playerId) {}

        @Override
        public void onPlayerLeft(MultiplayerId lobbyId, MultiplayerId playerId) {}

        @Override
        public void onLobbyDataReceived(MultiplayerId lobbyId, String key, String value) {}

        @Override
        public void onPlayerDataReceived(MultiplayerId lobbyId, MultiplayerId playerId, String key, String value)
        {
            if (playerId.equals(MultiplayerManager.getLocalPlayer().profile.id))
            {
                return;
            }

            if (key.equals("action.damage"))
            {
                Player player = MultiplayerManager.getPlayer(playerId);
                player.character.useFastAttackAnimation();

                DamageActionMessage message = JSON.fromJson(DamageActionMessage.class, value);

                AbstractMonster target = AbstractDungeon.currMapNode.room.monsters.monsters.get(message.targetId);
                DamageInfo info = new DamageInfo(player.character, message.damage, message.type);

                DamageAction action = new DamageAction(target, info, message.effect);

                addAction(action, message.addToBottom);
            }

            else if (key.equals("action.block"))
            {
                Player player = MultiplayerManager.getPlayer(playerId);
                GainBlockActionMessage message = JSON.fromJson(GainBlockActionMessage.class, value);

                GainBlockAction action = new GainBlockAction(player.character, message.amount);

                addAction(action, message.addToBottom);
            }

            else if (key.equals("player.lose-block"))
            {
                Player player = MultiplayerManager.getPlayer(playerId);
                LoseBlockMessage message = JSON.fromJson(LoseBlockMessage.class, value);

                player.character.loseBlock(message.amount, message.noAnimation);
            }

            else if (key.equals("action.apply-power"))
            {
                Player player = MultiplayerManager.getPlayer(playerId);

                ApplyPowerActionMessage message = JSON.fromJson(ApplyPowerActionMessage.class, value);

                AbstractMonster target = AbstractDungeon.currMapNode.room.monsters.monsters.get(message.targetId);

                AbstractPower power = null;

                if (message.powerId.equals(VulnerablePower.POWER_ID))
                {
                    power = new VulnerablePower(target, message.magicNumber, false);
                }
                else if (message.powerId.equals(WeakPower.POWER_ID))
                {
                    power = new WeakPower(target, message.magicNumber, false);
                }

                if (power != null)
                {
                    ApplyPowerAction action = new ApplyPowerAction(target, player.character, power);

                    addAction(action, message.addToBottom);
                }
            }
        }

        private void addAction(AbstractGameAction action, boolean addToBottom)
        {
            skipMessage = true;
            if (addToBottom)
            {
                AbstractDungeon.actionManager.addToBottom(action);
            }
            else
            {
                AbstractDungeon.actionManager.addToTop(action);
            }
            skipMessage = false;
        }
    };

    @SpirePatch2(clz = GameActionManager.class, method = SpirePatch.CONSTRUCTOR)
    public static class ConstructorPatch
    {
        @SpireInsertPatch
        public static void Postfix(GameActionManager __instance)
        {
            MultiplayerManager.addLobbyCallback(CALLBACK, "action.damage", "action.block", "player.lose-block", "action.apply-power");
        }
    }

    @SpirePatch2(clz = GameActionManager.class, method = "addToBottom")
    public static class AddToBottomPatch
    {
        @SpireInsertPatch
        public static void Prefix(AbstractGameAction action)
        {
            if (skipMessage)
            {
                return;
            }

            if (action instanceof DamageAction)
            {
                try
                {
                    DamageAction damageAction = (DamageAction)action;
                    Field info = DamageAction.class.getDeclaredField("info");
                    info.setAccessible(true);

                    if (damageAction.target instanceof AbstractPlayer)
                    {
                        return;
                    }

                    DamageActionMessage message = new DamageActionMessage();
                    message.targetId = AbstractDungeon.currMapNode.room.monsters.monsters.indexOf(damageAction.target);
                    message.damage = ((DamageInfo)info.get(damageAction)).base;
                    message.type = ((DamageInfo)info.get(damageAction)).type;
                    message.effect = damageAction.attackEffect;
                    message.addToBottom = true;

                    MultiplayerManager.sendPlayerData("action.damage", JSON.toJson(message));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else if (action instanceof GainBlockAction)
            {
                GainBlockAction gainBlockAction = (GainBlockAction)action;

                if (gainBlockAction.target instanceof AbstractMonster)
                {
                    return;
                }

                GainBlockActionMessage message = new GainBlockActionMessage();
                message.amount = gainBlockAction.amount;

                MultiplayerManager.sendPlayerData("action.block", JSON.toJson(message));
            }

            else if (action instanceof ApplyPowerAction)
            {
                try
                {
                    ApplyPowerAction applyPowerAction = (ApplyPowerAction)action;
                    Field powerToApply = ApplyPowerAction.class.getDeclaredField("powerToApply");
                    powerToApply.setAccessible(true);

                    if (applyPowerAction.source instanceof AbstractMonster)
                    {
                        return;
                    }

                    ApplyPowerActionMessage message = new ApplyPowerActionMessage();
                    message.targetId = AbstractDungeon.currMapNode.room.monsters.monsters.indexOf(applyPowerAction.target);
                    message.powerId = ((AbstractPower)powerToApply.get(applyPowerAction)).ID;
                    message.magicNumber = ((AbstractPower)powerToApply.get(applyPowerAction)).amount;

                    MultiplayerManager.sendPlayerData("action.apply-power", JSON.toJson(message));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static abstract class ActionMessage
    {
        public Boolean addToBottom = true;
    }

    public static class DamageActionMessage extends ActionMessage
    {
        public Integer targetId;
        public Integer damage;
        public DamageInfo.DamageType type;
        public AbstractGameAction.AttackEffect effect;
    }

    public static class ApplyPowerActionMessage extends ActionMessage
    {
        public Integer targetId;
        public String powerId;
        public Integer magicNumber;
    }

    public static class GainBlockActionMessage extends ActionMessage
    {
        public Integer amount;
    }
}
