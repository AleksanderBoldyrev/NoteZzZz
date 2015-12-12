package Client;

/**
 * The class describes the model of the note primitive and is used to implement JavaFX MVC model.
 */

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Alex on 06.12.2015.
 */
public class VersionInfoModel {
    private final StringProperty _title;
    private final StringProperty _text;
    private final IntegerProperty _id;

    VersionInfoModel(final VersionInfoModel src){
        this._title = new SimpleStringProperty(src.getTitle().get());
        this._text = new SimpleStringProperty(src.getText().get());
        this._id = new SimpleIntegerProperty(src.getId().get());
    }
    VersionInfoModel(){
        this._title = new SimpleStringProperty("");
        this._text = new SimpleStringProperty("");
        this._id = new SimpleIntegerProperty(-1);
    }
    VersionInfoModel(final String title, final String text, final int id){
        this._title = new SimpleStringProperty(title);
        this._text = new SimpleStringProperty(text);
        this._id = new SimpleIntegerProperty(id);
    }

    public void setTitle(final String t){
        this._title.set(t);
    }

    public StringProperty getTitle(){
        return this._title;
    }

    public void setId(final int id){
        this._id.set(id);
    }

    public IntegerProperty getId(){
        return this._id;
    }

    public void setText(final String text) {
        this._text.set(text);
    }

    public StringProperty getText() {
        return this._text;
    }
}
