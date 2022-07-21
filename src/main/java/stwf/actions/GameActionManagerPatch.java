package stwf.actions;

import java.lang.reflect.Field;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

import stwf.cards.AbstractCardPatch.AbstractCardFields;
import stwf.characters.AbstractPlayerPatch;
import stwf.characters.AbstractPlayerPatch.LoseBlockMessage;
import stwf.monsters.MonsterGroupPatch;
import stwf.multiplayer.MultiplayerManager;
import stwf.multiplayer.Player;
import stwf.multiplayer.services.callbacks.MultiplayerServiceLobbyCallback;
import stwf.multiplayer.services.steam.SteamService.MultiplayerId;

public class GameActionManagerPatch
{
    private static final Json JSON = new Json();

    public static boolean enableAddToBottom = true;
    public static boolean enableAddToTop = true;
    private static boolean showIntent = true;
    private static boolean skipActionSynchronization = false;

    private static final MultiplayerServiceLobbyCallback CALLBACK = new MultiplayerServiceLobbyCallback()
    {
        @Override
        public void onPlayerJoined(MultiplayerId lobbyId, MultiplayerId playerId) {}

        @Override
        public void onPlayerLeft(MultiplayerId lobbyId, MultiplayerId playerId) {}

        @Override
        public void onPlayerDataReceived(MultiplayerId playerId, String key, String value)
        {
            if (MultiplayerManager.getPlayer(playerId).isLocal)
            {
                return;
            }

            if (key.equals("action.use-card"))
            {
                Player player = MultiplayerManager.getPlayer(playerId);

                UseCardActionMessage message = JSON.fromJson(UseCardActionMessage.class, value);

                AbstractMonster target = null;

                if (!message.isTargetPlayer && message.monsterId != -1)
                {
                    target = AbstractDungeon.currMapNode.room.monsters.monsters.get(message.monsterId);
                }

                AbstractCard card = CardLibrary.getCopy(message.cardId);
                if (message.isUpgraded)
                {
                    card.upgrade();
                }
                card.costForTurn = 0;
                card.energyOnUse = message.energyOnUse;
                
                AbstractCardFields.playerData.set(card, player);

                AbstractPlayer localCharacter = AbstractDungeon.player;

                AbstractDungeon.player = player.character;
                player.character.useCard(card, target, 0);
                AbstractDungeon.player = localCharacter;
            }

            else if (key.equals("player.round-started"))
            {
                MonsterGroupPatch.enableApplyEndOfTurnPowers = true;
                AbstractDungeon.getCurrRoom().monsters.applyEndOfTurnPowers();
            }

            else if (key.equals("player.lose-block"))
            {
                Player player = MultiplayerManager.getPlayer(playerId);
                LoseBlockMessage message = JSON.fromJson(LoseBlockMessage.class, value);

                player.character.loseBlock(message.amount, message.noAnimation);
            }

            else if (key.equals("player.increase-max-hp"))
            {
                Player player = MultiplayerManager.getPlayer(playerId);

                Gdx.app.postRunnable(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        player.character.increaseMaxHp(Integer.parseInt(value), true);
                    }   
                });
            }

            else if (key.equals("player.decrease-max-hp"))
            {
                Player player = MultiplayerManager.getPlayer(playerId);
                player.character.decreaseMaxHealth(Integer.parseInt(value));
            }
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

    public static boolean isLocalPlayerTurn()
    {
        Player localPlayer = MultiplayerManager.getLocalPlayer();
        Iterator<Player> players = MultiplayerManager.getPlayers();

        if (localPlayer.order == 0)
        {
            return true;
        }

        while (players.hasNext())
        {
            Player player = players.next();

            if (player.order == localPlayer.order - 1)
            {
                return player.endedTurn;
            }
        }

        return false;
    }

    public static boolean isLocalPlayerFirst()
    {
        return MultiplayerManager.getLocalPlayer().order == 0;
    }

    @SpirePatch2(clz = GameActionManager.class, method = "getNextAction")
    public static class GetNextActionPatch
    {
        @SpireInsertPatch(loc = 359)
        public static SpireReturn<Void> Insert(GameActionManager __instance)
        {
            if (!MultiplayerManager.inMultiplayerLobby())
            {
                return SpireReturn.Continue();
            }

            CardQueueItem cardQueueItem = AbstractDungeon.actionManager.cardQueue.get(0);

            UseCardActionMessage message = new UseCardActionMessage();
            message.cardId = cardQueueItem.card.cardID;
            message.isUpgraded = cardQueueItem.card.upgraded;
            message.energyOnUse = cardQueueItem.card.energyOnUse;
            message.isTargetPlayer = false;
            message.monsterId = AbstractDungeon.currMapNode.room.monsters.monsters.indexOf(cardQueueItem.monster);

            MultiplayerManager.sendPlayerData("action.use-card", JSON.toJson(message));

            return SpireReturn.Continue();
        }
    }

    @SpirePatch2(clz = GameActionManager.class, method = "getNextAction")
    public static class GetNextActionPatch2
    {
        @SpireInsertPatch(loc = 430)
        public static SpireReturn<Void> Insert(GameActionManager __instance)
        {
            if (!MultiplayerManager.inMultiplayerLobby())
            {
                return SpireReturn.Continue();
            }

            if (isLocalPlayerTurn())
            {
                showIntent = true;
                AbstractPlayerPatch.enableLoseBlock = true;

                if (isLocalPlayerFirst())
                {
                    MonsterGroupPatch.enableApplyEndOfTurnPowers = true;
                    MultiplayerManager.sendPlayerData("player.round-started", "");
                }

                MultiplayerManager.sendPlayerData("player.turn-started", "");
                return SpireReturn.Continue();
            }

            if (showIntent)
            {
                showIntent = false;
                AbstractDungeon.getMonsters().showIntent();
            }

            return SpireReturn.Return();
        }
    }

    @SpirePatch2(clz = GameActionManager.class, method = "addToBottom")
    public static class AddToBottomPatch
    {
        @SpireInsertPatch()
        public static void Postfix(GameActionManager __instance, AbstractGameAction action)
        {
            if (skipActionSynchronization)
            {
                return;
            }

            if (!(action.source instanceof AbstractMonster) || !(action.target instanceof AbstractPlayer))
            {
                return;
            }

            try
            {
                if (action instanceof DamageAction)
                {
                    DamageAction damageAction = (DamageAction)action;

                    Field infoField = DamageAction.class.getDeclaredField("info");
                    infoField.setAccessible(true);
                    DamageInfo info = (DamageInfo)infoField.get(damageAction);

                    Iterator<Player> players = MultiplayerManager.getPlayers();
                    while (players.hasNext())
                    {
                        Player player = players.next();

                        if (!player.isLocal)
                        {
                            DamageAction newAction = new DamageAction(player.character, info);

                            skipActionSynchronization = true;
                            AbstractDungeon.actionManager.addToBottom(newAction);
                            skipActionSynchronization = false;
                        }
                    }
                }

                if (action instanceof ApplyPowerAction)
                {
                    ApplyPowerAction applyPowerAction = (ApplyPowerAction)action;
                    
                    Field powerToApplyField = ApplyPowerAction.class.getDeclaredField("powerToApply");
                    powerToApplyField.setAccessible(true);
                    AbstractPower powerToApply = (AbstractPower)powerToApplyField.get(applyPowerAction);

                    Field startingDurationField = ApplyPowerAction.class.getDeclaredField("startingDuration");
                    startingDurationField.setAccessible(true);
                    float startingDuration = (float)startingDurationField.get(applyPowerAction);

                    Iterator<Player> players = MultiplayerManager.getPlayers();
                    while (players.hasNext())
                    {
                        Player player = players.next();

                        if (!player.isLocal)
                        {
                            ApplyPowerAction newAction = new ApplyPowerAction(player.character, applyPowerAction.source, powerToApply, applyPowerAction.amount, startingDuration == Settings.ACTION_DUR_FASTER, applyPowerAction.attackEffect);

                            skipActionSynchronization = true;
                            AbstractDungeon.actionManager.addToBottom(newAction);
                            skipActionSynchronization = false;
                        }
                    }
                }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
    }

    public static abstract class ActionMessage
    {
        public Boolean addToBottom = true;
    }

    public static class UseCardActionMessage extends ActionMessage
    {
        public String cardId;
        public Boolean isUpgraded;
        public Integer energyOnUse;
        public Boolean isTargetPlayer;
        public Integer monsterId;
        public MultiplayerId playerId;
    }
}
