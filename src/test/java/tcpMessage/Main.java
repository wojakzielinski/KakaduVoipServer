package tcpMessage;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Szymon on 18.07.2018.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("fxml/ClientPanel.fxml"));

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
        launch(args);
    }
}