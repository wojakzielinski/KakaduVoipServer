package tcpMessage;

import protocols.Protocols;

import java.util.List;

public class TcpMessage {
    private static String HOSTNAME = "localhost";
    private static int PORT = 9123;

    public static void main(String[] args) throws InterruptedException {
        TcpClient tcpClient = new TcpClient(HOSTNAME, PORT);
        String nick = "wojakzielinski";
        String otherUser = "niechcianyGość";
        String roomName = "Pokój1";

        loginToServer(tcpClient,nick);
        List<Protocols.Room> serverRooms = getServerRooms(tcpClient,nick);
        System.out.println("Server rooms size="+serverRooms.size());
        if (serverRooms.size() == 0) {
            createRoom(tcpClient,nick, roomName);
        }
        joinRoom(tcpClient,nick, roomName);

        //other users log to room
        TcpClient tcpClient2 = new TcpClient(HOSTNAME, PORT);
        loginToServer(tcpClient2,otherUser);
        serverRooms = getServerRooms(tcpClient2,otherUser);
        joinRoom(tcpClient2,otherUser, serverRooms.get(0).getRoomName());

        //mute user
        muteUser(tcpClient,nick, roomName, otherUser);
        //unmute user
        unmuteUser(tcpClient,nick, roomName, otherUser);
        //kick user from room
        kickUser(tcpClient,nick, roomName, otherUser);
        //leaveroom
        leaveRoom(tcpClient,nick, roomName);
        //deleteRoom
        deleteRoom(tcpClient,nick, roomName);
        //leave server
        leaveServer(tcpClient,nick);
        leaveServer(tcpClient2,otherUser);
    }

    private static void loginToServer(TcpClient tcpClient, String nick) throws InterruptedException {
        Protocols.LoginToServerRequest.Builder request = Protocols.LoginToServerRequest.newBuilder();
        request.setNick(nick);
        Object message = tcpClient.send(request.build());
        Protocols.LoginToServerResponse response = (Protocols.LoginToServerResponse) message;

        System.out.println("LoginToServerResponse = " + response.toString());
    }

    private static List<Protocols.Room> getServerRooms(TcpClient tcpClient, String nick) throws InterruptedException {
        Protocols.GetRoomsRequest.Builder request = Protocols.GetRoomsRequest.newBuilder();
        request.setNick(nick);
        Protocols.GetRoomsResponse response = (Protocols.GetRoomsResponse) tcpClient.send(request.build());
        //if(response )
        return response.getRoomsList();
    }

    private static void createRoom(TcpClient tcpClient, String nick, String roomName) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.CREATE_ROOM);
        request.setPassword("passwordToServer");
        request.setAdminPassword("adminPassword");
        request.setRoomName(roomName);

        Object message = tcpClient.send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void joinRoom(TcpClient tcpClient, String nick, String roomName) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.JOIN_ROOM);
        request.setPassword("passwordToServer");
        request.setRoomName(roomName);

        Object message = tcpClient.send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void leaveRoom(TcpClient tcpClient, String nick, String roomName) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.LEAVE_ROOM);
        request.setRoomName(roomName);

        Object message = tcpClient.send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void deleteRoom(TcpClient tcpClient, String nick, String roomName) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.DELETE_ROOM);
        request.setPassword("passwordToServer");
        request.setAdminPassword("adminPassword");
        request.setRoomName(roomName);

        Object message = tcpClient.send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void muteUser(TcpClient tcpClient, String nick, String roomName, String userToMute) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.MUTE_USER);
        request.setPassword("passwordToServer");
        request.setRoomName(roomName);
        request.setOtherUserNick(userToMute);

        Object message = tcpClient.send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void unmuteUser(TcpClient tcpClient, String nick, String roomName, String userToUnmute) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.UNMUTE_USER);
        request.setPassword("passwordToServer");
        request.setRoomName(roomName);
        request.setOtherUserNick(userToUnmute);

        Object message = tcpClient.send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void kickUser(TcpClient tcpClient, String nick, String roomName, String unwantedUserNick) throws InterruptedException {
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.KICK_USER);
        request.setPassword("passwordToServer");
        request.setAdminPassword("adminPassword");
        request.setRoomName(roomName);
        request.setOtherUserNick(unwantedUserNick);

        Object message = tcpClient.send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void leaveServer(TcpClient tcpClient, String nick) throws InterruptedException {
        Protocols.LeaveServerRequest.Builder requestBuilder = Protocols.LeaveServerRequest.newBuilder();
        requestBuilder.setNick(nick);

        Object message = tcpClient.send(requestBuilder.build());
        Protocols.LeaveServerResponse response = (Protocols.LeaveServerResponse) message;
        if(response.getStatus().equals(Protocols.StatusCode.OK))
            tcpClient.closeConnection();
        System.out.println(response.toString());
    }

}
