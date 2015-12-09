package Client;

/**
 * The class describes the model of the user and is used to implement JavaFX MVC model.
 */

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Alex on 06.12.2015.
 */
public class UserModel {
    private final StringProperty _server;
    private final StringProperty _login;
    private final StringProperty _pass;
    private final BooleanProperty _toCreate;

    public UserModel(){
        this._server = new SimpleStringProperty("");
        this._login = new SimpleStringProperty("");
        this._pass = new SimpleStringProperty("");
        this._toCreate = new SimpleBooleanProperty(false);
    }

    public UserModel(final String server, final String login, final String pass, final boolean toC){
        this._server = new SimpleStringProperty(server);
        this._login = new SimpleStringProperty(login);
        this._pass = new SimpleStringProperty(pass);
        this._toCreate = new SimpleBooleanProperty(toC);
    }

    public void setServer(final String server) {
        this._server.set(server);
    }

    public StringProperty getServer() {
        return this._server;
    }

    public void setLogin(final String login) {
        this._login.set(login);
    }

    public StringProperty getLogin() {
        return this._login;
    }

    public void setPass(final String pass) {
        this._pass.set(pass);
    }

    public StringProperty getPass() {
        return this._pass;
    }

    public void setToCreate(final boolean toC) {
        this._toCreate.set(toC);
    }

    public BooleanProperty getToCreate() {
        return this._toCreate;
    }
}
