package handler;

import model.ServerRoom;
import model.User;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import protocols.Protocols;

import java.io.IOException;


public class UdpServerHandler extends IoHandlerAdapter {

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        System.out.println("SESSION CREATED");
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        if (message instanceof Protocols.RegisterUdpSession) {
            handleRegisterUdpSession((Protocols.RegisterUdpSession) message, session);
        }
        if (message instanceof Protocols.UdpPacket) {
            handleUdpPacket((Protocols.UdpPacket) message);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        System.out.println("Session closed...");
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    private void handleRegisterUdpSession(Protocols.RegisterUdpSession message, IoSession udpSession) throws IOException {
        User sender = ServerData.INSTANCE.findUserFromNick(message.getUsername());
        if (sender != null) {
            sender.setUdpSession(udpSession);
        } else {
            throw new IOException("User with username: " + message.getUsername() + " not found");
        }
    }

    private void handleUdpPacket(Protocols.UdpPacket udpPacket) {
        ServerRoom serverRoom = ServerData.INSTANCE.getServerRoomByName(udpPacket.getRoomName());
        String senderNick = udpPacket.getUsername();
        for (User user : serverRoom.getUsersInRoom()) {
            if (!user.getNick().equals(senderNick) && !user.getMutedUserNicks().contains(senderNick)) {
                System.out.println("Packet from: " + senderNick +" goes to: "+user.getNick() + ", session = "+ user.getUdpSession().toString());
                user.getUdpSession().write(udpPacket);
            } else {
                System.out.println("!!!Packet from: " + senderNick +" not goes to: "+user.getNick()+ ", session = "+ user.getUdpSession().toString());
            }
        }
    }
}
