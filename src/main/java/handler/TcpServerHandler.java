package handler;

import model.ServerRoom;
import model.User;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import protocols.Protocols;

import java.util.*;

public class TcpServerHandler extends IoHandlerAdapter {
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        Object response;

        if (message instanceof Protocols.LoginToServerRequest) {
            response = handleLoginRequestMessage((Protocols.LoginToServerRequest) message, session);
        } else if (message instanceof Protocols.LeaveServerRequest) {
            response = handleLeaveServerRequest((Protocols.LeaveServerRequest) message);
        } else if (message instanceof Protocols.ManageRoomRequest) {
            response = handleManageRoomRequest((Protocols.ManageRoomRequest) message);
        } else if (message instanceof Protocols.GetRoomsRequest) {
            response = handleGetRoomsRequest((Protocols.GetRoomsRequest) message);
        } else if (message instanceof Protocols.GetUsersInRoomRequest) {
            response = handleGetUsersInRoomRequest((Protocols.GetUsersInRoomRequest) message);
        } else {
            response = message;
        }
        session.write(response);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println("IDLE " + session.getIdleCount(status));
    }

    private Protocols.LoginToServerResponse handleLoginRequestMessage(Protocols.LoginToServerRequest request,IoSession ioSession) {
        Protocols.LoginToServerResponse.Builder responseBuilder = Protocols.LoginToServerResponse.newBuilder();
        System.out.println(request.toString());

        User user = ServerData.INSTANCE.findUserFromNick(request.getNick());
        if(user != null){
            responseBuilder.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
            return responseBuilder.build();
        } else {
            User newUser = new User();
            newUser.setNick(request.getNick());
            newUser.setTcpSession(ioSession);
            ServerData.INSTANCE.loggedUsers.add(newUser);

            responseBuilder.setStatus(Protocols.StatusCode.OK);
            System.out.println("LoginToServerResponse end, loggedUsers.size = " + ServerData.INSTANCE.loggedUsers.size());

            return responseBuilder.build();
        }
    }

    private Protocols.LeaveServerResponse handleLeaveServerRequest(Protocols.LeaveServerRequest request) {
        Protocols.LeaveServerResponse.Builder response = Protocols.LeaveServerResponse.newBuilder();
        ServerData.INSTANCE.loggedUsers.removeIf(user -> user.getNick().equals(request.getNick()));
        response.setStatus(Protocols.StatusCode.OK);
        System.out.println("LeaveServerRequestHandling end, loggedUsers.size = "+ServerData.INSTANCE.loggedUsers.size());

        return response.build();
    }

    private Protocols.ManageRoomResponse handleManageRoomRequest(Protocols.ManageRoomRequest request) {
        Protocols.ManageRoomResponse response;
        System.out.println("ENUM = " + request.getManageRoomEnum());
        switch (request.getManageRoomEnum()) {
            case CREATE_ROOM:
                response = createRoom(request);
                break;
            case JOIN_ROOM:
                response = addUserToRoom(request);
                break;
            case KICK_USER:
                response = handlekickUserFromRoom(request);
                break;
            case LEAVE_ROOM:
                response = handleLeaveRoom(request);
                break;
            case DELETE_ROOM:
                response = handleDeleteRoom(request);
                break;
            case MUTE_USER:
                response = handleMuteUser(request);
                break;
            case UNMUTE_USER:
                response = handleUnmuteUser(request);
                break;
            default:
                Protocols.ManageRoomResponse.Builder responseBuilder = Protocols.ManageRoomResponse.newBuilder();
                responseBuilder.setStatus(Protocols.StatusCode.SERVER_ERROR);
                responseBuilder.setSendedManageRoomEnum(request.getManageRoomEnum());
                response = responseBuilder.build();
        }
        return response;
    }

    private Protocols.ManageRoomResponse createRoom(Protocols.ManageRoomRequest request) {
        ServerRoom serverRoom = new ServerRoom();
        serverRoom.setName(request.getRoomName());
        User owner = new User();
        owner.setNick(request.getNick());

        serverRoom.setOwner(owner);
        serverRoom.setPassword(request.getPassword());
        serverRoom.setAdminPassword(request.getAdminPassword());

        ServerData.INSTANCE.serverRoomMap.put(request.getRoomName(), serverRoom);

        this.sendNewRoomListToLoggerUsers(request.getNick());

        Protocols.ManageRoomResponse.Builder responseBuilder = Protocols.ManageRoomResponse.newBuilder();
        responseBuilder.setStatus(Protocols.StatusCode.OK);
        responseBuilder.setSendedManageRoomEnum(Protocols.ManageRoomEnum.CREATE_ROOM);
        return responseBuilder.build();
    }

    private Protocols.ManageRoomResponse addUserToRoom(Protocols.ManageRoomRequest request) {
        Protocols.ManageRoomResponse.Builder responseBuilder = Protocols.ManageRoomResponse.newBuilder();
        responseBuilder.setSendedManageRoomEnum(Protocols.ManageRoomEnum.JOIN_ROOM);

        ServerRoom serverRoom = ServerData.INSTANCE.serverRoomMap.get(request.getRoomName());
        User user = ServerData.INSTANCE.findUserFromNick(request.getNick());
        if (serverRoom != null && user != null && serverRoom.getPassword().equals(request.getPassword())) {
            serverRoom.joinUserToRoom(user);
            responseBuilder.setStatus(Protocols.StatusCode.OK);
        } else {
            responseBuilder.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
        }
        return responseBuilder.build();
    }

    private Protocols.ManageRoomResponse handleLeaveRoom(Protocols.ManageRoomRequest request) {
        Protocols.ManageRoomResponse.Builder response = Protocols.ManageRoomResponse.newBuilder();
        response.setSendedManageRoomEnum(Protocols.ManageRoomEnum.LEAVE_ROOM);
        User user = ServerData.INSTANCE.findUserFromNick(request.getNick());
        if (user != null ){
            //&& serverRoomMap.get(request.getRoomName()).getUsersInRoom().remove(user))
            ServerData.INSTANCE.serverRoomMap.get(request.getRoomName()).leaveRoom(user);
            response.setStatus(Protocols.StatusCode.OK);
        }else
            response.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
        return response.build();

    }

    private Protocols.ManageRoomResponse handlekickUserFromRoom(Protocols.ManageRoomRequest request) {
        Protocols.ManageRoomResponse.Builder response = Protocols.ManageRoomResponse.newBuilder();
        response.setSendedManageRoomEnum(Protocols.ManageRoomEnum.KICK_USER);
        ServerRoom serverRoom = ServerData.INSTANCE.serverRoomMap.get(request.getRoomName());
        User userToKick = ServerData.INSTANCE.findUserFromNick(request.getOtherUserNick());

        if (userToKick != null && serverRoom.getAdminPassword().equals(request.getAdminPassword())
                && serverRoom.kickUserFromRoom(userToKick)) {
            Protocols.ManageRoomResponse.Builder kickResponse = Protocols.ManageRoomResponse.newBuilder();
            kickResponse.setStatus(Protocols.StatusCode.OK);
            kickResponse.setSendedManageRoomEnum(Protocols.ManageRoomEnum.LEAVE_ROOM);
            userToKick.getTcpSession().write(kickResponse.build());

            response.setStatus(Protocols.StatusCode.OK);
        } else
            response.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
        return response.build();
    }

    private Protocols.ManageRoomResponse handleDeleteRoom(Protocols.ManageRoomRequest request) {
        Protocols.ManageRoomResponse.Builder response = Protocols.ManageRoomResponse.newBuilder();
        response.setSendedManageRoomEnum(Protocols.ManageRoomEnum.DELETE_ROOM);
        ServerRoom serverRoom = ServerData.INSTANCE.serverRoomMap.get(request.getRoomName());
        if (serverRoom.getAdminPassword().equals(request.getAdminPassword())) {
            System.out.println("Romoving room from map");
            serverRoom.kickAllUsersFromRoom();
            ServerData.INSTANCE.serverRoomMap.remove(request.getRoomName());
            response.setStatus(Protocols.StatusCode.OK);
            this.sendNewRoomListToLoggerUsers(request.getNick());
        } else
            response.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
        return response.build();

    }

    private Protocols.ManageRoomResponse  handleMuteUser(Protocols.ManageRoomRequest request){
        Protocols.ManageRoomResponse.Builder response = Protocols.ManageRoomResponse.newBuilder();
        response.setSendedManageRoomEnum(Protocols.ManageRoomEnum.MUTE_USER);
        User user= ServerData.INSTANCE.findUserFromNick(request.getNick());
        User userToMute = ServerData.INSTANCE.findUserFromNick(request.getOtherUserNick());
        if(user!= null && userToMute != null) {
            user.getMutedUserNicks().add(userToMute.getNick());
            response.setStatus(Protocols.StatusCode.OK);
        } else
            response.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
        return response.build();
    }

    private Protocols.ManageRoomResponse  handleUnmuteUser(Protocols.ManageRoomRequest request){
        Protocols.ManageRoomResponse.Builder response = Protocols.ManageRoomResponse.newBuilder();
        response.setSendedManageRoomEnum(Protocols.ManageRoomEnum.UNMUTE_USER);

        User user= ServerData.INSTANCE.findUserFromNick(request.getNick());
        User userToUnmute = ServerData.INSTANCE.findUserFromNick(request.getOtherUserNick());
        if(user != null && userToUnmute != null) {
            user.getMutedUserNicks().remove(userToUnmute.getNick());
            response.setStatus(Protocols.StatusCode.OK);
        } else
            response.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
        return response.build();
    }

    private Protocols.GetRoomsResponse handleGetRoomsRequest(Protocols.GetRoomsRequest request){
        Protocols.GetRoomsResponse.Builder response = Protocols.GetRoomsResponse.newBuilder();
        if(ServerData.INSTANCE.findUserFromNick(request.getNick()) != null) {
            for (Map.Entry<String, ServerRoom> serverRoomEntry : ServerData.INSTANCE.serverRoomMap.entrySet()) {
                Protocols.Room.Builder roomBuilder = Protocols.Room.newBuilder();
                roomBuilder.setOwner(serverRoomEntry.getValue().getOwner().getNick());
                roomBuilder.setRoomName(serverRoomEntry.getKey());
                response.addRooms(roomBuilder);
            }
            response.setStatus(Protocols.StatusCode.OK);
        } else
            response.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
        return response.build();
    }

    private Protocols.GetUsersInRoomResponse handleGetUsersInRoomRequest(Protocols.GetUsersInRoomRequest request){
        Protocols.GetUsersInRoomResponse.Builder response = Protocols.GetUsersInRoomResponse.newBuilder();
        ServerRoom serverRoom = ServerData.INSTANCE.serverRoomMap.get(request.getRoomName());
        User user = ServerData.INSTANCE.findUserFromNick(request.getNick());
        if(serverRoom != null && user != null && serverRoom.getUsersInRoom().contains(user)){
            for(User userInRoom: serverRoom.getUsersInRoom()){
                response.addUsers(userInRoom.getNick());
            }
            response.setStatus(Protocols.StatusCode.OK);
        } else {
            response.setStatus(Protocols.StatusCode.BAD_CREDENTIALS);
        }
        return response.build();
    }

    private void sendNewRoomListToLoggerUsers(String creatorNick){
        Protocols.GetRoomsResponse.Builder responseBuilder = Protocols.GetRoomsResponse.newBuilder();
        for(Map.Entry<String, ServerRoom> serverRoomEntry: ServerData.INSTANCE.serverRoomMap.entrySet()){
            Protocols.Room.Builder room = Protocols.Room.newBuilder();
            room.setRoomName(serverRoomEntry.getValue().getName());
            room.setOwner(serverRoomEntry.getValue().getOwner().getNick());
            responseBuilder.addRooms(room);
        }
        responseBuilder.setStatus(Protocols.StatusCode.OK);
        Protocols.GetRoomsResponse response = responseBuilder.build();
        for(User user: ServerData.INSTANCE.loggedUsers){
            if(!user.getNick().equals(creatorNick)){
                user.getTcpSession().write(response);
            }
        }
    }
}
