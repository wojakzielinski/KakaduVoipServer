package tcpMessage;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import tcpMessage.controllers.ClientPanelController;

/**
 * Created by Szymon & Oskar on 24.07.2018.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("views/ClientPanel.fxml"));

        Scene scene = new Scene(root,720,350);

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
            ClientPanelController.tcpRequestService.sendLeaveServerRequest(ClientPanelController.username);
            ClientPanelController.tcpRequestService.getTcpClient().closeConnection();
            ClientPanelController.tcpClient.closeConnection();
        }

    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }
}