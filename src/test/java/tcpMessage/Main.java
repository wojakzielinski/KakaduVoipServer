package tcpMessage;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import sun.tools.jar.CommandLine;
import tcpMessage.controllers.ClientPanelController;
import tcpMessage.model.AudioSendingThread;

/**
 * Created by Szymon & Oskar on 24.07.2018.
 */
public class Main extends Application {
    boolean flag = false;
    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("views/ClientPanel.fxml"));

        Scene scene = new Scene(root,720,350);


        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(!flag){
                    if(event.getCode() == KeyCode.K && ClientPanelController.isVoiceOnClick){
                        System.out.println("Key clicked");
                        ClientPanelController.startSending();

                    }
                }
                flag = true;

            }
        });
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.K && ClientPanelController.isVoiceOnClick){
                    System.out.println("Key released");
                   // AudioSendingThread.setRunning(false);
                    try{
                        ClientPanelController.stopSending();
                        flag = false;
                    }catch (InterruptedException ex){
                        ex.printStackTrace();
                    }

                }
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setTitle("KakaduVoIP - komunikator głosowy");
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                primaryStage.close();
            }
        });
    }
    @Override
    public void stop() throws Exception{
        if(ClientPanelController.tcpRequestService != null || ClientPanelController.tcpClient != null){
            if(ClientPanelController.isUserInRoom){
                ClientPanelController.tcpRequestService.sendLeaveRoomRequest(ClientPanelController.username,ClientPanelController.selectedRoom.getName());
            }
            ClientPanelController.tcpRequestService.sendLeaveServerRequest(ClientPanelController.username);
            ClientPanelController.tcpRequestService.getTcpClient().closeConnection();
            ClientPanelController.tcpClient.closeConnection();
            ClientPanelController.udpClient.closeConnection();
            AudioSendingThread.terminate();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }
}