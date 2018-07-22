package tcpMessage.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;

/**
 * Created by Szymon on 18.07.2018.
 */
public class TempUser {
    private String userName;
    private Button button;

    public TempUser(String name) {

        this.userName = name;
        this.button = new Button("Akcja");
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