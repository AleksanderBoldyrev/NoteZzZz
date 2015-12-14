package Client;

import Main.CommonData;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Alex on 06.12.2015.
 */
public class LoginController {
    @FXML
    private Label infoLabel;
    @FXML
    private TextField serverName;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField password;

    private UserModel _userData;
    private Stage _mainStage;
    private Client _client;

    @FXML
    private void initialize() {
        serverName.setText(CommonData.HOST+":"+CommonData.PORT);
    }

    @FXML
    private void LoginButtonClicked(Event event) {
        _userData.setServer(serverName.getText());
        _userData.setLogin(userName.getText());
        _userData.setPass(password.getText());
        _userData.setToCreate(false);
        _mainStage.close();
    }

    @FXML
    private void CreateUserButtonClicked(Event event) {
        _userData.setServer(serverName.getText());
        _userData.setLogin(userName.getText());
        _userData.setPass(password.getText());
        _userData.setToCreate(true);
        _mainStage.close();
    }

    public void SetUserData(Client client, UserModel data, Stage stage) {
        _userData = data;
        _mainStage = stage;
        _client = client;
        _mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                _client.SetStatusExit();
            }
        });
    }

    private void NotifyUser(final String s) {
        infoLabel.setText(s);
    }

    public void DeleteUserButtonClicked(Event event) {

    }
}
