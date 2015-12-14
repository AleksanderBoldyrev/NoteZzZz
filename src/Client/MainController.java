package Client;

/**
 * The class describes the model of the note and is used to implement JavaFX MVC model.
 */

import Main.CommonData;
import Main.Note;
import com.sun.corba.se.spi.activation.IIOP_CLEAR_TEXT;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
//import javafx.scene.control.Alert;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainController {
    @FXML
    private MenuItem aboutButton;
    @FXML
    private Button Logout;
    @FXML
    private Label infoLabel;
    @FXML
    private TextField tagList;
    @FXML
    private TextField noteCaption;
    @FXML
    private TextArea noteData;
    @FXML
    private TableView<NoteModel> noteView;
    @FXML
    private TableView<VersionInfoModel> versView;
    @FXML
    private TableColumn<NoteModel, String> noteColumn;
    @FXML
    private TableColumn<VersionInfoModel, String> versColumn;
    @FXML
    private Button saveButton;
    @FXML
    private Button undoButton;
    @FXML
    public Button NewNoteButton;
    @FXML
    private Button closeButton;

    private Client _client;
    private Stage _stage;
    private String _undoBuff;

    private int _mode; // 0 - view only notes, 1 - view notes and versions and text
    private boolean _isNewNote;
    private boolean _isNoteDel;

    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.

        _isNewNote = true;
        _isNoteDel = false;

        _mode = 0;

        noteColumn.setCellValueFactory(
                cellData -> cellData.getValue().getTitle());

        versColumn.setCellValueFactory(
                cellData -> cellData.getValue().getTitle());
        // Clear person details.
        NoteViewSelected(null);
        VersViewSelected(null);

        //noteColumn.setResizable(false);
        //versColumn.setResizable(false);

    }

    /*@FXML
    private void CloseNoteButtonClicked(Event event) {
        ShowInfo2(null);
        _mode = 0;
    }*/

    @FXML
    private void SaveButtonClicked(Event event) {
        NotifyUser("");
        _mode = 1;
        //_isNewNote = true;
        _undoBuff = noteData.getText();
        String ss = LocalDateTime.now().toString();
        if (_isNewNote) {
            _isNewNote = false;
            _client.CreateNote(noteCaption.getText(), noteData.getText(), tagList.getText(), ss);
            noteView.getSelectionModel().select(_client.getNotes().size()-1);
            _isNoteDel=true;
            Refresh();
        }
        else {
            _client.CreateVersion(noteData.getText(), ss, tagList.getText(), noteCaption.getText());
            versView.getSelectionModel().select(_client.getVersions().size()-1);
            _isNoteDel=false;
            Refresh();
        }
    }

    @FXML
    private void UndoButtonClicked(Event event) {
        NotifyUser("");
        noteData.setText(_undoBuff);
    }

    @FXML
    public void NewNoteButtonClicked(Event event) {
        NotifyUser("");
        _isNewNote = true;
        _mode = 0;

        _client.ClearVersions();
        this.noteData.setText("");
        this._undoBuff = "";
        this.noteCaption.setText("");
        this.tagList.setText("");
    }

    public void SyncData(Client mainApp, Stage mainStage){
        _client = mainApp;
        _stage = mainStage;
        noteView.setItems(mainApp.getNotes());
        versView.setItems(mainApp.getVersions());
        _stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                _client.SetStatusExit();
            }
        });

        // Listen for selection changes and show the person details when changed.
        noteView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> NoteViewSelected(newValue));

        versView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> VersViewSelected(newValue));
    }

    private void  VersViewSelected(VersionInfoModel data){
        _isNoteDel = false;
        if (data!= null) {
            if (_mode==1) {
                _isNewNote = false;
                if (_client.GetVersionsSize()>0) {
                    _client.setSelectedVersion(versView.getSelectionModel().getSelectedIndex());
                    this.noteData.setText(data.getText().get());
                    _undoBuff = this.noteData.getText();
                }
            }
        }
        else {
            this.noteData.setText("");
        }
    }

    private void  NoteViewSelected(NoteModel note){
        _isNoteDel = true;
        if (note!= null) {
            if (_client.GetNotesSize()>0) {
                _client.setSelectedNote(noteView.getSelectionModel().getSelectedIndex());
                _client.setSelectedVersion(versView.getSelectionModel().getSelectedIndex());
                this.tagList.setText(note.getTags().get());
                this.noteCaption.setText(note.getTitle().get());
                _client.SomeNoteSelected();
                _mode = 1;
            }
        }
        else {
            _mode = 0;
            this.tagList.setText("");
            this.noteCaption.setText("");
        }
    }

    public void AboutButtonClicked(Event event) {
        NotifyUser("The NoteZ application. A-13XX, 2015.");
    }

    public void Logout(Event event) {
        NotifyUser("");
        if (_client.Logout()== CommonData.SERV_YES)
        {
            _stage.close();
        }
        else
            NotifyUser("Logout unsuccessful. (:");
    }

    private void NotifyUser(final String s) {
        infoLabel.setText(s);
    }

    private void Refresh() {
        this.versColumn.setVisible(false);
        this.noteColumn.setVisible(false);
        this.versColumn.setVisible(true);
        this.noteColumn.setVisible(true);
    }

    public void DeleteButtonClicked(Event event) {
        NotifyUser("");
        if (!_isNoteDel) {
            _client.DeleteVersion();
            this.noteData.setText("");
            this._undoBuff = "";
            if (_client.getVersions().size()>1) {
                _client.DeleteVersion();
                versView.getSelectionModel().select(_client.getVersions().size() - 1);
            }
            else {
                _client.DeleteNote();
                this.noteCaption.setText("");
                this.tagList.setText("");
                if (_client.getNotes().size()>0)
                    noteView.getSelectionModel().select(_client.getNotes().size()-1);
            }
        }
        else {
            _client.DeleteNote();
            this.noteData.setText("");
            this._undoBuff = "";
            this.noteCaption.setText("");
            this.tagList.setText("");
            if (_client.getNotes().size()>0)
                noteView.getSelectionModel().select(_client.getNotes().size()-1);
        }
    }
}
