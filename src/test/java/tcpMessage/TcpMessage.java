package tcpMessage;

import protocols.Protocols;

import java.util.List;

public class TcpMessage {
    private static String HOSTNAME = "localhost";
    private static int PORT = 9123;

    public static void main(String[] args) throws InterruptedException
    {
        String nick = "wojakzielinski";
        List<Protocols.Room> serverRooms = loginToServer(nick);
        String roomName = "Pokój1";
        if(serverRooms.size() == 0){
            createRoom(nick, roomName);
        }
        joinRoom(nick, roomName);
        joinRoom("niechcianyGość", roomName);
        kickUser(nick, roomName, "niechcianyGość");
        leaveRoom(nick, roomName);
        deleteRoom(nick, roomName);

    }

    private static List<Protocols.Room> loginToServer(String nick)throws InterruptedException{
        Protocols.LoginToServerRequest.Builder request = Protocols.LoginToServerRequest.newBuilder();
        request.setNick(nick);
        Object message = new TcpClient(HOSTNAME,PORT).send(request.build());
        Protocols.LoginToServerResponse response = (Protocols.LoginToServerResponse) message;

        System.out.println("LoginToServerResponse = "+response.toString());
        return response.getRoomListList();
    }

    private static void createRoom(String nick, String roomName) throws InterruptedException{
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.CREATE_ROOM);
        request.setPassword("passwordToServer");
        request.setAdminPassword("adminPassword");
        request.setRoomName(roomName);

        Object message = new TcpClient(HOSTNAME,PORT).send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void joinRoom(String nick,String roomName) throws InterruptedException{
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.JOIN_ROOM);
        request.setPassword("passwordToServer");
        request.setRoomName(roomName);

        Object message = new TcpClient(HOSTNAME,PORT).send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void leaveRoom(String nick, String roomName)throws InterruptedException{
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.LEAVE_ROOM);
        request.setRoomName(roomName);

        Object message = new TcpClient(HOSTNAME,PORT).send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void deleteRoom(String nick, String roomName) throws InterruptedException{
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.DELETE_ROOM);
        request.setPassword("passwordToServer");
        request.setAdminPassword("adminPassword");
        request.setRoomName(roomName);

        Object message = new TcpClient(HOSTNAME,PORT).send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

    private static void kickUser(String nick, String roomName, String unwantedUserNick) throws InterruptedException{
        Protocols.ManageRoomRequest.Builder request = Protocols.ManageRoomRequest.newBuilder();
        request.setNick(nick);
        request.setManageRoomEnum(Protocols.ManageRoomEnum.CREATE_ROOM);
        request.setPassword("passwordToServer");
        request.setAdminPassword("adminPassword");
        request.setRoomName(roomName);
        request.setOtherUserNick(unwantedUserNick);

        Object message = new TcpClient(HOSTNAME,PORT).send(request.build());
        Protocols.ManageRoomResponse response = (Protocols.ManageRoomResponse) message;
        System.out.println(response.toString());
    }

}
