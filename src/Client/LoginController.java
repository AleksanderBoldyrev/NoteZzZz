package Client;

import Main.CommonData;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by Alex on 06.12.2015.
 */
public class LoginController {
    @FXML
    private Text infoLabel;
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
        NotifyUser("Welcome to the NoteZ application service.");
        serverName.setText(CommonData.HOST+":"+CommonData.PORT);
    }

    @FXML
    private void LoginButtonClicked(Event event) {
        if (!serverName.getText().equals("") && !userName.getText().equals("")) {
            _userData.setServer(serverName.getText());
            _userData.setLogin(userName.getText());
            _userData.setPass(password.getText());
            _userData.setToCreate(false);
            _mainStage.close();
        }
        else NotifyUser("The login or password fields are empty. Type smth in.");
    }

    @FXML
    private void CreateUserButtonClicked(Event event) {
        if (!serverName.getText().equals("") && !userName.getText().equals("")) {
            _userData.setServer(serverName.getText());
            _userData.setLogin(userName.getText());
            _userData.setPass(password.getText());
            _userData.setToCreate(true);
            _mainStage.close();
        }
    }

    @FXML
    public void DeleteUserButtonClicked(Event event) {
        if (!serverName.getText().equals("") && !userName.getText().equals("")) {
            int res = _client.DeleteUser(userName.getText(), password.getText());
            if (res == CommonData.SERV_YES)
                NotifyUser("User account has been successfully deleted.");
            else
                NotifyUser("User account hasn't been successfully deleted. Please, try again.");
            userName.clear();
            password.clear();
        }
    }

    public void SetUserData(Client client, UserModel data, Stage stage) {
        _userData = data;
        _mainStage = stage;
        _client = client;
        if (!_client.GetLLR())
            NotifyUser("User name does not exist, or password is incorrect.");
        _mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                _client.SetStatusExit();
            }
        });
    }

    private void NotifyUser(final String s) {
        infoLabel.setText(s);
    }
}
