package Client;

/**
 * The class describes the model of the note and is used to implement JavaFX MVC model.
 */

import Main.Note;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
//import javafx.scene.control.Alert;

import java.util.ArrayList;

public class MainController {
    public MenuItem aboutButton;
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
    private boolean _isNewNote = false;

    @FXML
    private void initialize() {
        // Initialize the person table with the two columns.

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
        if (_isNewNote) _client.SaveNote(_client.getSelectedVersion());
        else _client.CreateNote(noteData.getText(), noteCaption.getText(), new ArrayList<Integer>());
    }

    @FXML
    private void UndoButtonClicked(Event event) {
        noteData.setText(_undoBuff);
    }

    @FXML
    public void NewNoteButtonClicked(Event event) {
        _isNewNote = true;
        _client.CreateNote(noteData.getText(), noteCaption.getText(), new ArrayList<Integer>());
    }

    public void SyncData(Client mainApp, Stage mainStage, NoteModel notes, VersionInfoModel vers){
        _client = mainApp;
        _stage = mainStage;
        _noteData = notes;
        _versData = vers;
        noteView.setItems(mainApp.getNotes());
        versView.setItems(mainApp.getVersions());
    }

    private void  ShowInfo2(VersionInfoModel data){
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
    }
}
