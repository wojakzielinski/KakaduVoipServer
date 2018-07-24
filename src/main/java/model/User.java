package model;

import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String nick;
    private IoSession tcpSession;
    private IoSession udpSession;
    private List<String> mutedUserNicks = new ArrayList<>();

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public IoSession getTcpSession() {
        return tcpSession;
    }

    public void setTcpSession(IoSession tcpSession) {
        this.tcpSession = tcpSession;
    }

    public IoSession getUdpSession() {
        return udpSession;
    }

    public void setUdpSession(IoSession udpSession) {
        this.udpSession = udpSession;
    }

    public List<String> getMutedUserNicks() {
        return mutedUserNicks;
    }

    @Override
    public String toString() {
        return "User{" +
                "nick='" + nick + '\'' +
                ", mutedUserNicks.size=" + mutedUserNicks.size() +
                '}';
    }

    public void printMutedUsers(){
        for(String mutedUsersNick: mutedUserNicks)
            System.out.println(mutedUsersNick);
    }
}
