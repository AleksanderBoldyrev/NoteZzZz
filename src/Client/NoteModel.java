package Client;

/**
 * The class describes the model of the note and is used to implement JavaFX MVC model.
 */

import Main.NotePrimitive;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 06.12.2015.
 */
public class NoteModel {

    private final StringProperty _title;
    private final StringProperty _tags;
    private final StringProperty _cDate;
    private final StringProperty _mDate;
    private final IntegerProperty _id;


    public NoteModel(final int id, final String title, final String tags, final String cDate, final String mDate) {
        this._title = new SimpleStringProperty(title);
        this._tags = new SimpleStringProperty(tags);
        this._cDate = new SimpleStringProperty(cDate);
        this._mDate = new SimpleStringProperty(mDate);
        this._id = new SimpleIntegerProperty(id);
        //ObservableList<String> observableList = FXCollections.observableArrayList(vers);
        //this._vers = new SimpleListProperty<String>(observableList);
    }

    public NoteModel() {
        this._title = new SimpleStringProperty("");
        this._tags = new SimpleStringProperty("");
        this._cDate = new SimpleStringProperty("");
        this._mDate = new SimpleStringProperty("");
        //ObservableList<String> observableList = FXCollections.observableArrayList();
        //this._vers = new SimpleListProperty<String>(observableList);
        this._id = new SimpleIntegerProperty(-1);
    }

    public NoteModel(final NoteModel src) {
        this._title = new SimpleStringProperty(src.getTitle().get());
        this._tags = new SimpleStringProperty(src.getTags().get());
        this._cDate = new SimpleStringProperty(src.getCDate().get());
        this._mDate = new SimpleStringProperty(src.getMDate().get());
        //ObservableList<String> observableList = FXCollections.observableArrayList();
        //this._vers = new SimpleListProperty<String>(observableList);
        this._id = new SimpleIntegerProperty(src.getId().get());
    }

    public void setId(final int id){
        this._id.set(id);
    }

    public IntegerProperty getId(){
        return this._id;
    }

    public void setTitle(final String title) {
        this._title.set(title);
    }

    public StringProperty getTitle() {
        return this._title;
    }

    public void setTags(final String tags) {
        this._tags.set(tags);
    }

    public StringProperty getTags() {
        return this._tags;
    }

    public void setCDate(final String cDate) {
        this._cDate.set(cDate);
    }

    public StringProperty getCDate() {
        return this._cDate;
    }

    public void setmDate(final String mDate) {
        this._mDate.set(mDate);
    }

    public StringProperty getMDate() {
        return this._mDate;
    }


    /*public void setVers(final ArrayList<String> arr) {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        this._vers.set(observableList);
    }

    public ListProperty<String> getVers() {
        return this._vers;
    }*/
}
