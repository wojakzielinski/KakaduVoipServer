package handler;

import model.ServerRoom;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Szymon on 23.07.2018.
 */
public enum ServerData {
    INSTANCE;
    public Map<String, ServerRoom> serverRoomMap = new TreeMap<>();
    public List<User> loggedUsers = new ArrayList<>();

    public ServerRoom getServerRoomByName(String roomName) {
        return serverRoomMap.get(roomName);
    }

    public User findUserFromNick(String nick) {
        for (User user : loggedUsers) {
            if (user.getNick().equals(nick))
                return user;
        }
        return null;
    }
}
