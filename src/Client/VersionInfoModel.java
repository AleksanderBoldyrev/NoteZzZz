package Client;

/**
 * The class describes the model of the note primitive and is used to implement JavaFX MVC model.
 */

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Alex on 06.12.2015.
 */
public class VersionInfoModel {
    private final StringProperty _title;
    private final StringProperty _text;


    VersionInfoModel(){
        this._title = new SimpleStringProperty("");
        this._text = new SimpleStringProperty("");
    }
    VersionInfoModel(final String title, final String text){
        this._title = new SimpleStringProperty(title);
        this._text = new SimpleStringProperty(text);
    }

    public void setTitle(final String t){
        this._title.set(t);
    }

    public StringProperty getTitle(){
        return this._title;
    }

    public void setText(final String text) {
        this._text.set(text);
    }

    public StringProperty getText() {
        return this._text;
    }
}
