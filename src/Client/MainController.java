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

    private NoteModel _noteData;
    private VersionInfoModel _versData;
    private Client _client;
    private Stage _stage;
    private String _undoBuff;

    private int _mode; // 0 - view only notes, 1 - view notes and versions and text
    private boolean _isNewNote;

    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.

        _isNewNote = true;

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
        _mode = 1;
        //_isNewNote = true;
        _undoBuff = noteData.getText();
        String ss = LocalDateTime.now().toString();
        if (_isNewNote) {
            _isNewNote = false;
            _noteData.setTitle(noteCaption.getText());
            _noteData.setTags(tagList.getText());
            _noteData.setCDate(ss);
            _noteData.setmDate(ss);

            _versData.setText(noteData.getText());
            _versData.setTitle(ss);

            _client.CreateNote();

            Refresh();
        }
        else {
            _versData.setText(noteData.getText());
            _versData.setTitle(ss);
            _noteData.setmDate(ss);

            _client.CreateVersion();
            Refresh();
        }
    }

    @FXML
    private void UndoButtonClicked(Event event) {
        noteData.setText(_undoBuff);
    }

    @FXML
    public void NewNoteButtonClicked(Event event) {
        _isNewNote = true;
        _mode = 0;

        _client.ClearVersions();
        this.noteData.setText("");
        this._undoBuff = "";
        this.noteCaption.setText("");
        this.tagList.setText("");
        //_client.CreateNote(noteData.getText(), noteCaption.getText(), new ArrayList<Integer>());
        //_noteData.
        //_client.
    }

    public void SyncData(Client mainApp, Stage mainStage, NoteModel notes, VersionInfoModel vers){
        _client = mainApp;
        _stage = mainStage;
        _noteData = notes;
        _versData = vers;
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

        //noteView.refresh();
        //versView.refresh();
    }

    private void  VersViewSelected(VersionInfoModel data){
        //_isNewNote = false;
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
        if (note!= null) {
            if (_client.GetNotesSize()>0) {
                _client.setSelectedNote(noteView.getSelectionModel().getSelectedIndex());
                this.tagList.setText(note.getTags().get());
                this.noteCaption.setText(note.getTitle().get());
                _client.SomeNoteSelected();
                //versView.
                //noteView.refresh();
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
}
