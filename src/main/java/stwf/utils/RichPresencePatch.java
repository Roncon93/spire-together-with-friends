package stwf.utils;

import com.codedisaster.steamworks.SteamFriends;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.integrations.steam.SteamIntegration;

import basemod.ReflectionHacks;
import stwf.SpireTogetherWithFriends;

public class RichPresencePatch {

	public static String[] ord = CardCrawlGame.languagePack.getUIString("NumericOrdinals").TEXT;

    @SpirePatch(clz = SteamIntegration.class, method="setRichPresenceDisplayPlaying", paramtypez={int.class, int.class, String.class})
    public static class PerFloorRichPresenceAscension 
	{
        public static void Replace(SteamIntegration __instance, int floor, int ascension, String character) 
		{
        	if (SpireTogetherWithFriends.gameMode == SpireTogetherWithFriends.GameModes.Coop)
			{
	        	RichPresencePatch.setRP(String.format(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[2], character, 2, floor, ascension));
			}
	        else
			{
	        	RichPresencePatch.setRP(String.format(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[3], RichPresencePatch.ordinal(1), character, floor, ascension));
			}
        }
    }

    @SpirePatch(clz = SteamIntegration.class, method="setRichPresenceDisplayPlaying", paramtypez={int.class, String.class})
    public static class PerFloorRichPresence 
	{
        public static void Replace(SteamIntegration __instance, int floor, String character) 
		{
        	if (SpireTogetherWithFriends.gameMode == SpireTogetherWithFriends.GameModes.Coop)
			{
				RichPresencePatch.setRP(String.format(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[4], character, 2, floor));
			}
	        else
			{
	        	RichPresencePatch.setRP(String.format(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[5], RichPresencePatch.ordinal(1), character, floor));
			}
        }
    }

    @SpirePatch(clz = SteamIntegration.class, method="setRichPresenceDisplayInMenu")
    public static class MainMenuRichPresence 
	{
        public static void Replace(SteamIntegration __instance) 
		{
	        RichPresencePatch.setRP(CardCrawlGame.languagePack.getUIString("RichPresence").TEXT[6]);
        }
    }

    public static void setRP(String value) 
	{
    	SteamFriends sf = (SteamFriends)ReflectionHacks.getPrivateStatic(SteamIntegration.class, "steamFriends");

	    if (sf != null) 
		{
	    	sf.setRichPresence("status", value);
	    	sf.setRichPresence("steam_display", "#Status");
	    }
    }

	public static String ordinal(int i) 
	{
	    String[] suffixes = new String[] { RichPresencePatch.ord[0], RichPresencePatch.ord[1], RichPresencePatch.ord[2], RichPresencePatch.ord[3], RichPresencePatch.ord[4], RichPresencePatch.ord[5], RichPresencePatch.ord[6], RichPresencePatch.ord[7], RichPresencePatch.ord[8], RichPresencePatch.ord[9] };
	    switch (i % 100) {
	    case 10:
	    case 11:
	    case 12:
	    case 13:
	    case 20:
	    case 30:
	    case 40:
	        return i + RichPresencePatch.ord[9];
	    default:
		    String ord = i + RichPresencePatch.ord[9];
			try 
			{
				ord = i + suffixes[(i % 10)-1];
			} 
			catch (Exception e) {}

	        return ord;

	    }
	}    
}