package stwf.multiplayer.services.steam;

import com.codedisaster.steamworks.SteamFriends.PersonaChange;
import com.codedisaster.steamworks.SteamFriendsCallback;
import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamResult;

public class SteamServiceFriendsCallback  implements SteamFriendsCallback
{
    @Override
    public void onAvatarImageLoaded(SteamID arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFriendRichPresenceUpdate(SteamID arg0, int arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGameLobbyJoinRequested(SteamID arg0, SteamID arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGameOverlayActivated(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGameRichPresenceJoinRequested(SteamID arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onGameServerChangeRequested(String arg0, String arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPersonaStateChange(SteamID arg0, PersonaChange arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSetPersonaNameResponse(boolean arg0, boolean arg1, SteamResult arg2) {
        // TODO Auto-generated method stub
        
    }
    
}
