package tcpMessage.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import protocols.Protocols;
import tcpMessage.TcpClient;
import tcpMessage.model.TempRoom;
import tcpMessage.model.TempUser;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClientPanelController implements Initializable {

    private static String HOSTNAME;//localhost
    private static int PORT;//9123
    public static TcpRequestService tcpRequestService;
    private ArrayList<TempUser> users;
    public static String username;
    private static String roomPass;
    private static TempRoom selectedRoom;
    private static TempUser selectedUser;
    public static TcpClient tcpClient;


    //tables and cols
    @FXML
    private TableView<TempRoom> room_tbl;
    @FXML
    private TableColumn<TempRoom, String> name_col, owner_col;
    @FXML
    private TableView<TempUser> user_tbl;
    @FXML
    private TableView<TempUser> user_tbl_admin;
    @FXML
    private TableColumn<TempUser, String> user_tbl_username_user_col, user_tbl_action_user_col;
    @FXML
    private TableColumn<TempUser, String> user_tbl_username_admin_col, user_tbl_action_admin_col;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name_col.setCellValueFactory(new PropertyValueFactory<>("name"));
        owner_col.setCellValueFactory(new PropertyValueFactory<>("owner"));
        user_tbl_username_user_col.setCellValueFactory(new PropertyValueFactory<>("userName"));
        user_tbl_action_user_col.setCellValueFactory(new PropertyValueFactory<>("button"));
        user_tbl_username_admin_col.setCellValueFactory(new PropertyValueFactory<>("userName"));
        user_tbl_action_admin_col.setCellValueFactory(new PropertyValueFactory<>("button"));
    }

    @FXML
    private AnchorPane
            start_pane,
            create_room_pane,
            rooms_pane,
            room_with_users_pane_admin,
            room_with_users_pane_user,
            info_pane,
            audio_pane;

    @FXML
    public void show_info(ActionEvent event) {
        info_pane.toFront();
    }

    @FXML
    public void menu_go_back(MouseEvent event) {
        info_pane.toBack();
    }

    @FXML
    public void leave_server(ActionEvent event) throws InterruptedException {
        start_pane.toFront();
        this.tcpRequestService.sendLeaveServerRequest(username);
        tcpClient.closeConnection();
    }

    public void handle_leave_server_response(Protocols.LeaveServerResponse response) {
        tcpRequestService.getTcpClient().closeConnection();
    }

    //Start view
    @FXML
    private TextField v_username;
    @FXML
    private TextField v_host;
    @FXML
    private TextField v_port;

    @FXML
    public void connect_login(MouseEvent event) throws InterruptedException {
        if(v_username.getText().equals("") || v_host.getText().equals("") || v_port.getText().equals("")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Brak danych");
            alert.setHeaderText(null);
            alert.setContentText("Aby połączyć się z serwerem KakaduVoip, należy najpierw podać nazwę użytkownika, adres serwera oraz jego port.");

            alert.showAndWait();
        }
        else {
            username = v_username.getText();
            HOSTNAME = v_host.getText();
            PORT = Integer.parseInt(v_port.getText());
            tcpClient= new TcpClient(HOSTNAME, PORT, this);
            tcpRequestService = new TcpRequestService(tcpClient);
            tcpRequestService.sendLoginToServerRequest(username);
        }
    }

    public void handle_login_to_server_response(Protocols.LoginToServerResponse response) throws InterruptedException {
        Platform.runLater(
                () -> {
                    rooms_pane.toFront();
                    tcpRequestService.sendGetRoomsRequest(username);
                    users = new ArrayList<>();

                }
        );
    }

    @FXML
    public void room_connect(MouseEvent event) throws InterruptedException {

        String room_password = "";

        //Get actual clicked row in table
        selectedRoom = room_tbl.getSelectionModel().getSelectedItem();
        //Join selected room
        if (room_tbl.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Brak wybranego pokoju");
            alert.setHeaderText(null);
            alert.setContentText("Aby dołączyć do pokoju, należy najpierw go wybrać z listy.");

            alert.showAndWait();
        } else {
            //Text Input dialog
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Podaj hasło");
            dialog.setHeaderText("Aby wejść do pokoju, należy podać hasło zabezpieczające pokój.");
            dialog.setContentText("Podaj hasło:");
            Optional<String> password = dialog.showAndWait();
            if (password.isPresent()) {
                room_password = password.get();
                roomPass = room_password;
            }

            this.tcpRequestService.sendJoinRoomRequest(username, selectedRoom.getName(), room_password);
        }
    }

    public void handle_manage_room_response(Protocols.ManageRoomResponse response) throws InterruptedException {
        switch (response.getSendedManageRoomEnum()) {
            case CREATE_ROOM:
                this.handle_create_room_response(response);
                break;
            case DELETE_ROOM:
                this.handle_delete_room_response(response);
                break;
            case JOIN_ROOM:
                this.handleJoinRoomResponse(response);
                break;
            case KICK_USER:
                break;
            case LEAVE_ROOM:
                this.handle_leave_room_response(response);
                break;
            case MUTE_USER:
                break;
            case UNMUTE_USER:
                break;
        }
    }

    public void handleJoinRoomResponse(Protocols.ManageRoomResponse response) {
        Platform.runLater(
                () -> {
                    if (response.getStatus() == Protocols.StatusCode.BAD_CREDENTIALS) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Złe hasło");
                        alert.setHeaderText(null);
                        alert.setContentText("Wprowadź poprawne hasło do pokoju.");

                        alert.showAndWait();
                    } else {
                        if (selectedRoom.getOwner().equals(username)) {
                            room_lbl_admin.setText(selectedRoom.getName());
                            user_lbl_admin.setText(username);
                            room_with_users_pane_admin.toFront();
                        } else {
                            room_lbl_user.setText(selectedRoom.getName());
                            user_lbl_user.setText(username);
                            room_with_users_pane_user.toFront();
                        }
                        this.tcpRequestService.sendGetUsersInRoomRequest(username, selectedRoom.getName());
                    }
                }
        );
    }

    @FXML
    public void room_create(MouseEvent event) {

        create_room_pane.toFront();
    }

    @FXML
    public void reload_rooms(MouseEvent event) throws InterruptedException {
        tcpRequestService.sendGetRoomsRequest(username);
    }

    public void setNewRoomList(List<Protocols.Room> rooms) {
        //Clear whole table
        room_tbl.getItems().clear();
        //Get items to refreshed table
        for (int i = 0; i < rooms.size(); i++) {
            room_tbl.getItems().add(new TempRoom(rooms.get(i).getRoomName(), rooms.get(i).getOwner()));
        }
        room_tbl.refresh();
    }


    @FXML
    public void choose_row(MouseEvent event) {

    }
    //Inside room view

    @FXML
    private Label room_lbl_user, user_lbl_user, room_lbl_admin, user_lbl_admin;



    public void handleGetUsersInRoomResponse(Protocols.GetUsersInRoomResponse response) {
        if (username.equals(selectedRoom.getOwner())) {
            user_tbl_admin.getItems().clear();
            for (String userNick : response.getUsersList()) {
                user_tbl_admin.getItems().add(new TempUser(tcpRequestService,userNick,new Button(), username, selectedRoom.getName(),roomPass));
                user_tbl_admin.refresh();
            }
            user_tbl_admin.refresh();
        } else {
            user_tbl.getItems().clear();
            for (String userNick : response.getUsersList()) {

                user_tbl.getItems().add(new TempUser(tcpRequestService, userNick, new Button(), username, selectedRoom.getName(), roomPass));
                user_tbl.refresh();
            }
            user_tbl.refresh();
        }
    }

    @FXML
    public void leave_room(MouseEvent event) throws InterruptedException {
        this.tcpRequestService.sendLeaveRoomRequest(username, selectedRoom.getName());
    }

    public void handle_leave_room_response(Protocols.ManageRoomResponse response) throws InterruptedException {
        Platform.runLater(
                () -> {
                    if (response.getStatus() == Protocols.StatusCode.OK) {
                        tcpRequestService.sendGetRoomsRequest(username);
                        rooms_pane.toFront();
                    } else {
                        System.out.println("WRONG RESPONSE STATUS FROM LeaveServerResponse, status = " + response.getStatus().toString());
                    }
                }
        );
    }

    @FXML
    private void kick_user(MouseEvent event) throws InterruptedException{


        //Get actual clicked row in table
        selectedUser = user_tbl_admin.getSelectionModel().getSelectedItem();
        //Kick HIM out!
        if (user_tbl_admin.getSelectionModel().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Brak wybranego użytkownika");
            alert.setHeaderText(null);
            alert.setContentText("Aby usunąć użytkownika, należy najpierw wybrać go z listy.");

            alert.showAndWait();
        } else {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Weryfikacja administratora");
            dialog.setHeaderText("Aby usunąć użytkownika, należy podać hasło dostępu do pokoju oraz hasło administracyjne.");

            ButtonType submitButton = new ButtonType("Potwierdź", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(submitButton, cancelButton);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField password = new PasswordField();
            password.setPromptText("Hasło dostępu");
            PasswordField adminPassword = new PasswordField();
            adminPassword.setPromptText("Hasło administracyjne");

            grid.add(new Label("Podaj hasło dostępu:"), 0, 0);
            grid.add(password, 1, 0);
            grid.add(new Label("Podaj hasło administracyjne:"), 0, 1);
            grid.add(adminPassword, 1, 1);


            dialog.getDialogPane().setContent(grid);

            // Request focus on the username field by default.
            Platform.runLater(() -> password.requestFocus());

            // Convert the result to a username-password-pair when the login button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == submitButton) {
                    return new Pair<>(password.getText(), adminPassword.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(usernamePassword -> {
                System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
            });

            if (result.isPresent()) {
                this.tcpRequestService.sendKickUserRequest(username,selectedRoom.getName(),selectedUser.getUserName(),result.get().getKey(), result.get().getValue());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Złe dane");
                alert.setHeaderText(null);
                alert.setContentText("Wprowadź hasło zabezpieczające pokój oraz hasło administracyjne.");

                alert.showAndWait();
            }

        }

    }
    @FXML
    private void delete_room(MouseEvent event) throws InterruptedException {

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Weryfikacja administratora");
        dialog.setHeaderText("Aby usunąć pokój, należy podać hasło dostępu do pokoju oraz hasło administracyjne.");

        ButtonType submitButton = new ButtonType("Potwierdź", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Anuluj", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton, cancelButton);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField password = new PasswordField();
        password.setPromptText("Hasło dostępu");
        PasswordField adminPassword = new PasswordField();
        adminPassword.setPromptText("Hasło administracyjne");

        grid.add(new Label("Podaj hasło dostępu:"), 0, 0);
        grid.add(password, 1, 0);
        grid.add(new Label("Podaj hasło administracyjne:"), 0, 1);
        grid.add(adminPassword, 1, 1);


        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> password.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButton) {
                return new Pair<>(password.getText(), adminPassword.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
        });

        if (result.isPresent()) {
            this.tcpRequestService.sendDeleteRoomRequest(username, selectedRoom.getName(), result.get().getKey(), result.get().getValue());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Złe dane");
            alert.setHeaderText(null);
            alert.setContentText("Wprowadź hasło zabezpieczające pokój oraz hasło administracyjne.");

            alert.showAndWait();
        }
    }

    public void handle_delete_room_response(Protocols.ManageRoomResponse response) {
        Platform.runLater(
                () -> {
                    if (response.getStatus() == Protocols.StatusCode.OK) {
                        rooms_pane.toFront();
                    } else if (response.getStatus() == Protocols.StatusCode.BAD_CREDENTIALS) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Złe hasła");
                        alert.setHeaderText(null);
                        alert.setContentText("Wprowadź poprawne hasła do pokoju.");

                        alert.showAndWait();
                    }
                }
        );
    }

    //Create room view
    @FXML
    private TextField v_room_passadmin, v_room_passwd, v_room_name;

    @FXML
    public void go_back(MouseEvent event) {
        rooms_pane.toFront();
    }

    @FXML
    public void create_create_room(MouseEvent event) throws InterruptedException {
        String roomname, roompass, roompassadm;
        roomname = v_room_name.getText();
        roompass = v_room_passwd.getText();
        roompassadm = v_room_passadmin.getText();

        this.tcpRequestService.sendCreateRoomRequest(username, roomname, roompass, roompassadm);
    }

    public void handle_create_room_response(Protocols.ManageRoomResponse response) throws InterruptedException {
        Platform.runLater(
                () -> {
                    if (response.getStatus() == Protocols.StatusCode.OK) {
                        tcpRequestService.sendGetRoomsRequest(username);
                        rooms_pane.toFront();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Błąd przy tworzeniu pokoju");
                        alert.setHeaderText(null);
                        alert.setContentText("StatusCode: " + response.getStatus().toString());

                        alert.showAndWait();
                    }
                }
        );
    }

    @FXML
    public void help(MouseEvent event) {

    }

    //audio pane
    @FXML
    private ChoiceBox micro_type;

    public void audio_properties(ActionEvent actionEvent) {
        Mixer.Info[] infos = AudioSystem.getMixerInfo();
        System.out.println("" + infos.length + " mixers detected");

        ObservableList mixers = FXCollections.observableArrayList();

        for (Mixer.Info inf : infos) {
            mixers.add(inf);
        }

        micro_type.setItems(mixers);
        micro_type.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //DODAC ZMIANE USTAWIEN MIKROFONU
                System.out.println(newValue);
            }
        });
        audio_pane.toFront();
    }

    public void go_back_audio(MouseEvent event) {
        audio_pane.toBack();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
