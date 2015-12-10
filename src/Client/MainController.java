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

        noteColumn.setCellValueFactory(
                cellData -> cellData.getValue().getTitle());

        // Clear person details.
        ShowInfo(null);

        // Listen for selection changes and show the person details when changed.
        noteView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> ShowInfo(newValue));
        versView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> ShowInfo2(newValue));

    }

    /*@FXML
    private void CloseNoteButtonClicked(Event event) {
        ShowInfo2(null);
        _mode = 0;
    }*/

    @FXML
    private void SaveButtonClicked(Event event) {
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
            //_client.SaveNote(_client.getSelectedVersion());
        }
        else {
            _versData.setText(noteData.getText());
            _versData.setTitle(ss);
            _noteData.setmDate(ss);

            _client.CreateVersion();

            //_client.CreateNote(noteData.getText(), noteCaption.getText(), new ArrayList<Integer>());
        }
    }

    @FXML
    private void UndoButtonClicked(Event event) {
        noteData.setText(_undoBuff);
    }

    @FXML
    public void NewNoteButtonClicked(Event event) {
        _isNewNote = true;
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
    }

    private void  ShowInfo2(VersionInfoModel data){
        _isNewNote = false;
        if (data!= null) {
            if (_mode==1) {
                _client.setSelectedVersion(versView.getSelectionModel().getSelectedIndex());
                this.noteData.setText(data.getText().get());
                _undoBuff = noteData.getText();
            }
        }
        else {
            versColumn.setCellValueFactory(
                    cellData -> (new VersionInfoModel()).getTitle());
            this.noteData.setText("");
        }
    }

    private void  ShowInfo(NoteModel note){
        if (note!= null) {
            _client.setSelectedNote(noteView.getSelectionModel().getSelectedIndex());
            this.tagList.setText(note.getTags().get());
            this.noteCaption.setText(note.getTitle().get());
            _client.ReFill();
            versColumn.setCellValueFactory(
                    cellData -> cellData.getValue().getTitle());
            versView.refresh();
            _mode = 1;
        }
        else {
            this.tagList.setText("");
            this.noteCaption.setText("");
        }
    }

    public void AboutButtonClicked(Event event) {
        NotifyUser("Cool Code!");
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
}
