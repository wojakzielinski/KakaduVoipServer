package model;

import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String nick;
    private IoSession session;
    private List<String> mutedUserNicks = new ArrayList<>();

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public List<String> getMutedUserNicks() {
        return mutedUserNicks;
    }

    public void setMutedUserNicks(List<String> mutedUserNicks) {
        this.mutedUserNicks = mutedUserNicks;
    }

    @Override
    public String toString() {
        return "User{" +
                "nick='" + nick + '\'' +
                ", session=" + session +
                ", mutedUserNicks=" + mutedUserNicks +
                '}';
    }

    public void printMutedUsers(){
        for(String mutedUsersNick: mutedUserNicks)
            System.out.println(mutedUsersNick);
    }
}
