package tcpMessage.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import tcpMessage.controllers.TcpRequestService;

import javax.sound.sampled.Mixer;

/**
 * Created by Szymon on 18.07.2018.
 */
public class TempUser {
    private String userName;
    private Button button;
    private String appOwner;
    private String roomPassword;
    private String roomName;
    private EventHandler eventHandler;
    public int type;

    public TcpRequestService requestService;

    public TempUser(TcpRequestService tcpRequestService, String name, Button button, String appOwner, String roomName, String roomPassword) {


        this.userName = name;
        this.button = button;
        this.appOwner = appOwner;
        this.roomName = roomName;
        this.roomPassword = roomPassword;
        this.requestService = tcpRequestService;
        this.type = 0;


        this.button.setText("Mute");
        this.button.setOnAction(ActionEvent -> {
            if(this.button.getText()=="Mute"){
                type = 0;
                System.out.println("Mute user: "+this.getUserName());
                this.eventHandler = new EventHandler(tcpRequestService,roomName,appOwner,roomPassword,name,type);
                this.button.setText("Unmute");
            }
            else {
                type = 1;
                System.out.println("Unmute user: "+this.getUserName());
                this.eventHandler = new EventHandler(tcpRequestService,roomName,appOwner,roomPassword,name,type);
                this.button.setText("Mute");
            }
            this.eventHandler.handle(ActionEvent);
        });
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}