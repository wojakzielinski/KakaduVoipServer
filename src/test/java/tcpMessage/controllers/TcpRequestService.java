package tcpMessage.controllers;

import protocols.Protocols;
import tcpMessage.TcpClient;

/**
 * Created by Szymon on 18.07.2018.
 */
public class TcpRequestService {
    private TcpClient tcpClient;

    public TcpRequestService(TcpClient tcpClient) throws Exception {
        this.tcpClient = tcpClient;
    }

    public TcpClient getTcpClient() {
        return tcpClient;
    }

    public void sendLoginToServerRequest(String nick) {
        Protocols.LoginToServerRequest.Builder request = Protocols.LoginToServerRequest.newBuilder();
        request.setNick(nick);

        tcpClient.send(request.build());
    }

    public void sendLeaveServerRequest(String username) {
        Protocols.LeaveServerRequest.Builder requestBuilder = Protocols.LeaveServerRequest.newBuilder();
        requestBuilder.setNick(username);

        tcpClient.send(requestBuilder.build());
    }

    public void sendGetRoomsRequest(String nick) {
        Protocols.GetRoomsRequest.Builder request = Protocols.GetRoomsRequest.newBuilder();
        request.setNick(nick);
        tcpClient.send(request.build());
    }

    public void sendGetUsersInRoomRequest(String nick, String roomname){
        Protocols.GetUsersInRoomRequest.Builder request = Protocols.GetUsersInRoomRequest.newBuilder();
        request.setNick(nick);
        request.setRoomName(roomname);
        tcpClient.send(request.build());
    }

    public void sendCreateRoomRequest(String nick, String roomName, String password, String adminPassword) {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.CREATE_ROOM);
        request.setPassword(password);
        request.setAdminPassword(adminPassword);
        request.setRoomName(roomName);

        tcpClient.send(request.build());
    }

    public void sendJoinRoomRequest(String nick, String roomName, String password) {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.JOIN_ROOM);
        request.setPassword(password);
        request.setRoomName(roomName);

        tcpClient.send(request.build());
    }

    public void sendLeaveRoomRequest(String nick, String roomName) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.LEAVE_ROOM);
        request.setRoomName(roomName);

        tcpClient.send(request.build());
    }

    public void sendDeleteRoomRequest(String nick, String roomName, String password, String adminPassword) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.DELETE_ROOM);
        request.setPassword(password);
        request.setAdminPassword(adminPassword);
        request.setRoomName(roomName);

        tcpClient.send(request.build());
    }

    public void sendMuteUserRequest(String nick, String roomName, String userToMute, String password) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.MUTE_USER);
        request.setPassword(password);
        request.setRoomName(roomName);
        request.setOtherUserNick(userToMute);

        tcpClient.send(request.build());
    }

    public void sendUnmuteUserRequest(String nick, String roomName, String userToUnmute, String password) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.UNMUTE_USER);
        request.setPassword(password);
        request.setRoomName(roomName);
        request.setOtherUserNick(userToUnmute);

        tcpClient.send(request.build());
    }

    public void sendKickUserRequest(String nick, String roomName, String unwantedUserNick, String password, String adminPassword) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.KICK_USER);
        request.setPassword(password);
        request.setAdminPassword(adminPassword);
        request.setRoomName(roomName);
        request.setOtherUserNick(unwantedUserNick);

        tcpClient.send(request.build());
    }
}
