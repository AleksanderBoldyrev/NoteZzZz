package Client;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Alex on 06.12.2015.
 */
public class LoginController {
    @FXML
    private TextField serverName;
    @FXML
    private TextField userName;
    @FXML
    private PasswordField password;

    private UserModel _userData;
    private Stage _mainStage;

    @FXML
    private void initialize() {
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

    public void SetUserData(UserModel data, Stage stage) {
        _userData = data;
        _mainStage = stage;
    }
}
