package stwf.actions;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardQueueItem;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import stwf.actions.AbstractGameActionPatch.AbstractGameActionFieldsPatch;
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
    public static boolean skipMessage = false;
    private static boolean showIntent = true;

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
                card.costForTurn = 0;
                
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

    private static SpireReturn<Void> addToTopOrBottomPatch(AbstractGameAction action, boolean addToBottom)
    {
        if (skipMessage || AbstractGameActionFieldsPatch.process.get(action))
        {
            return SpireReturn.Continue();
        }

        return SpireReturn.Continue();
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

    @SpirePatch2(clz = GameActionManager.class, method = "addToTop")
    public static class AddToTopPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(AbstractGameAction action)
        {
            if (MultiplayerManager.inMultiplayerLobby() && !enableAddToTop)
            {
                return SpireReturn.Return();
            }

            return addToTopOrBottomPatch(action, false);
        }
    }

    @SpirePatch2(clz = GameActionManager.class, method = "addToBottom")
    public static class AddToBottomPatch
    {
        @SpireInsertPatch
        public static SpireReturn<Void> Prefix(AbstractGameAction action)
        {
            if (MultiplayerManager.inMultiplayerLobby() && !enableAddToBottom)
            {
                return SpireReturn.Return();
            }

            return addToTopOrBottomPatch(action, true);
        }
    }

    public static abstract class ActionMessage
    {
        public Boolean addToBottom = true;
    }

    public static class UseCardActionMessage extends ActionMessage
    {
        public String cardId;
        public Boolean isTargetPlayer;
        public Integer monsterId;
        public MultiplayerId playerId;
    }
}
