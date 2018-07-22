package tcpMessage.model;

import javafx.event.Event;
import tcpMessage.controllers.TcpRequestService;

public class EventHandler implements javafx.event.EventHandler {
    private TcpRequestService tcpRequestService;
    private String roomName,userName,roomPassword,mutatingUser;
    private int type;

    public EventHandler(TcpRequestService tcprs, String roomName, String userName, String roomPassword, String mutatingUser, int type){
        this.roomName = roomName;
        this.roomPassword = roomPassword;
        this.tcpRequestService = tcprs;
        this.userName = userName;
        this.mutatingUser = mutatingUser;
        this.type = type;
    }
    @Override
    public void handle(Event event) {
        try {
            if(type == 0){
                this.tcpRequestService.sendMuteUserRequest(userName,roomName,mutatingUser,roomPassword);
            }else if (type == 1){
                this.tcpRequestService.sendUnmuteUserRequest(userName,roomName,mutatingUser,roomPassword);
            }


        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
