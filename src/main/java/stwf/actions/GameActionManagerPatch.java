package stwf.actions;

import java.lang.reflect.Field;

import com.badlogic.gdx.utils.Json;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

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
            MultiplayerManager.addLobbyCallback(CALLBACK);
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

                    DamageActionMessage message = new DamageActionMessage();
                    message.targetId = AbstractDungeon.currMapNode.room.monsters.monsters.indexOf(damageAction.target);
                    message.damage = ((DamageInfo)info.get(damageAction)).base;
                    message.type = ((DamageInfo)info.get(damageAction)).type;
                    message.effect = damageAction.attackEffect;
                    message.addToBottom = true;

                    MultiplayerManager.sendPlayerData("action.damage", new Json().toJson(message));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            else if (action instanceof GainBlockAction)
            {
                GainBlockAction gainBlockAction = (GainBlockAction)action;

                GainBlockActionMessage message = new GainBlockActionMessage();
                message.amount = gainBlockAction.amount;

                MultiplayerManager.sendPlayerData("action.block", new Json().toJson(message));
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

    public static class GainBlockActionMessage extends ActionMessage
    {
        public Integer amount;
    }
}
