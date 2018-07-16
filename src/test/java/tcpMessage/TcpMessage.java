package tcpMessage;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import protocols.Protocols;

import java.util.List;

public class TcpMessage extends Application {
    private static String HOSTNAME = "localhost";
    private static int PORT = 9123;
    private static TcpClient tcpClient;

    public TcpClient getConnection(){
        return tcpClient;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("./views/ClientPanel.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);


        primaryStage.setTitle("KakaduVoIP - komunikator głosowy");
        primaryStage.show();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                primaryStage.close();

            }
        });
    }

    public static void main(String[] args) throws InterruptedException {
        tcpClient = new TcpClient(HOSTNAME,PORT);
        launch(args);
        tcpClient.closeConnection();
    }



}
