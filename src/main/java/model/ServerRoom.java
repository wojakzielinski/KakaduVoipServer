package model;

import protocols.Protocols;

import java.util.ArrayList;
import java.util.List;

public class ServerRoom {
    private String name;
    private List<User> usersInRoom = new ArrayList<User>();
    private User owner;
    private String password;
    private String adminPassword;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsersInRoom() {
        return usersInRoom;
    }

    public void setUsersInRoom(List<User> usersInRoom) {
        this.usersInRoom = usersInRoom;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public void joinUserToRoom(User newUser){
        System.out.println("Handling joining room");
        //create protocols with user nicks
        Protocols.GetUsersInRoomResponse.Builder responseBuilder = Protocols.GetUsersInRoomResponse.newBuilder();
        for(User userInRoom: usersInRoom) {
            responseBuilder.addUsers(userInRoom.getNick());
        }

        responseBuilder.addUsers(newUser.getNick());
        responseBuilder.setStatus(Protocols.StatusCode.OK);
        Protocols.GetUsersInRoomResponse response = responseBuilder.build();

        //send to users actual list of users in room
        for(User userInRoom: usersInRoom) {
            System.out.println("Sending to: "+userInRoom.getNick()+", iosession="+userInRoom.getSession().toString());
            userInRoom.getSession().write(response);
        }
        //add new user to room
        usersInRoom.add(newUser);
    }

    public void leaveRoom(User userToLeave){
        System.out.println("Handling leaving room, users in room" + usersInRoom.size());
        usersInRoom.remove(userToLeave);
        System.out.println("Handling leaving room after remove, users in room" + usersInRoom.size());

        //create protocols with user nicks
        Protocols.GetUsersInRoomResponse.Builder responseBuilder = Protocols.GetUsersInRoomResponse.newBuilder();
        for(User userInRoom: usersInRoom) {
            responseBuilder.addUsers(userInRoom.getNick());
        }

        responseBuilder.setStatus(Protocols.StatusCode.OK);
        Protocols.GetUsersInRoomResponse response = responseBuilder.build();

        //send to users actual list of users in room
        for(User userInRoom: usersInRoom) {
            System.out.println("Sending to: "+userInRoom.getNick()+", iosession="+userInRoom.getSession().toString());
            userInRoom.getSession().write(response);
        }
    }

    public Protocols.Room toProtocol(){
        Protocols.Room.Builder roomBuilder = Protocols.Room.newBuilder();
        roomBuilder.setRoomName(this.name);
        roomBuilder.setOwner(this.owner.getNick());
        return  roomBuilder.build();
    }

    @Override
    public String toString() {
        return "ServerRoom{" +
                "name='" + name + '\'' +
                ", usersInRoom.size=" + usersInRoom.size() +
                ", owner=" + owner +
                ", password='" + password + '\'' +
                ", adminPassword='" + adminPassword + '\'' +
                '}';
    }
}