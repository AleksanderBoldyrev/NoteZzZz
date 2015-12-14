package Client;

import Main.CommonData;
import Main.RequestsParser;
import Main.Tag;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * Created by Sasha on 30.09.2015.
 * <p>
 * This class describes the client's account and provides us with GUI components.
 */

public class Client extends Application {
    private static final RequestsParser _parser = new RequestsParser();
    private static boolean _isAuth;

    private static Socket _sock;
    private static BufferedReader _in;
    private static PrintWriter _out;

    private static boolean termFlag;
    private static int _stage;                           //0 - Notes captions view, 1 - List of versions view, 2 - exit
    private static String _login;
    private static String _pass;
    private static ArrayList<Tag> _tagList;

    private Stage _mainStage;
    private Parent _lNode;
    private Parent _mNode;
    private Scene _mainScene;

    //private Client.NoteModel _container;
    private ObservableList<NoteModel> _notes = FXCollections.observableArrayList();
    private ObservableList<VersionInfoModel> _versions = FXCollections.observableArrayList();
    private UserModel _userData;
    private VersionInfoModel _versData;
    private NoteModel _noteData;

    private int _selectedNote;
    private int _selectedVersion;

    public void startProcess() {
        /*Thread t = new Thread(_listener);
        t.setDaemon(true);
        t.start();*/

        try {
            _sock = new Socket(CommonData.HOST, CommonData.PORT);
            _in = new BufferedReader(new InputStreamReader(_sock.getInputStream()));
            _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_sock.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        termFlag = false;

        //CreateUser();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        startProcess();

        _isAuth = false;
        _mainStage = new Stage();

        _tagList = new ArrayList<Tag>();
        _userData = new UserModel();
        _noteData = new NoteModel();
        _versData = new VersionInfoModel();

        while (_stage != 2) {
            ShowLoginWindow();
            ShowMainWindow();
        }
    }

    private void ShowLoginWindow() throws Exception {
           /* Show login window */
        int suc = CommonData.SERV_NO;

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Client.class.getResource("LoginWindow.fxml"));
        _lNode = loader.load();
        _mainStage.setTitle(CommonData.LOG_W_CAPTION);
        _mainScene = new Scene(_lNode, CommonData.LOG_W_W, CommonData.LOG_W_H);
        _mainStage.setScene(_mainScene);
        _mainStage.setResizable(false);
        LoginController lc = loader.getController();
        lc.SetUserData(this, _userData, _mainStage);
        while (suc != CommonData.SERV_YES) {
            _mainStage.showAndWait();
            if (_userData.getToCreate().get())
                suc = CreateUser(_userData.getLogin().get(), _userData.getPass().get());
            else
                suc = Login(_userData.getLogin().get(), _userData.getPass().get());
        /*TODO: to add support of situation when user closed login window without login or create user*/
        }
    }

    public void ClearVersions(){
        this._versions.clear();
    }

    public int GetVersionsSize(){
        return _versions.size();
    }

    public int GetNotesSize(){
        return _notes.size();
    }

    private void ShowMainWindow() throws Exception {
        /*Show main window*/
        FXMLLoader loader2 = new FXMLLoader();
        loader2.setLocation(Client.class.getResource("MainWindow.fxml"));
        _mNode = loader2.load();
        MainController lc2 = loader2.getController();
        lc2.SyncData(this, _mainStage, _noteData, _versData);
        _mainStage.setTitle(CommonData.MAIN_W_CAPTION);
        _mainScene = new Scene(_mNode, CommonData.MAIN_W_W, CommonData.MAIN_W_H);
        _mainStage.setScene(_mainScene);
        _mainStage.setResizable(false);
        _mainStage.showAndWait();
    }

    public void SetStatusExit() {
        _stage = 2;
        SendToServer(CommonData.TERMCOMMAND);
    }

    public void SomeNoteSelected() {
        SyncTags();
        GetMoreNoteInfo();
        GetVersions();
    }

    // get all primitives <caption-text>
    private void GetVersions() {
        _versions.clear();
        ArrayList<String> buf = new ArrayList<String>();
        buf.add(_notes.get(_selectedNote).getId().get() + "");
        String st = _parser.Build(buf, CommonData.O_GET_VERSIONS);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.equals("")) {
            buf = _parser.ParseListOfString(str);
            if (buf.size() > 2)
                if (Integer.parseInt(buf.get(0)) == CommonData.O_RESPOND) {
                    if (Integer.parseInt(buf.get(1)) == CommonData.SERV_YES) {
                        buf.remove(0);
                        buf.remove(0);
                        if ((buf.size() % 2 == 0) && (buf.size() > 0))
                            for (int i = 0; i < buf.size(); i += 2) {
                                _versions.add(new VersionInfoModel(buf.get(i), buf.get(i + 1), i / 2));
                            }
                    }
                }
        }
        if (_versions.size() > 0)
            this._versData = this._versions.get(0);
    }

    private void SyncTags() {
        String st;
        /*Sync tag list with server*/
        st = this._parser.Build(_parser.BuildTagList(this._tagList), CommonData.O_SYNC_TAG_LIST);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.equals("")) {
            ArrayList<String> buff = this._parser.ParseListOfString(str);
            if (buff.size() > 2)
                if (Integer.parseInt(buff.get(0)) == CommonData.O_RESPOND) {
                    //remove command id
                    buff.remove(0);
                    //save new tags
                    if (buff.size() % 2 == 0)
                        this._tagList = this._parser.ParseListOfTags(buff);
                    else {
                        //TODO: do anything to preserve unsynchronysation of data between server and client
                    }
                }
        }
    }

    private String GetTagById(final int id) {
        if (_tagList.size() > 0)
            for (int i = 0; i < _tagList.size(); i++) {
                if (_tagList.get(i).GetId() == id) {
                    return _tagList.get(i).GetStrData();
                }
            }
        return new String();
    }

    // Get some info for selected note primitive: cDate - mDate - tags(ids)
    private void GetMoreNoteInfo() {
        ArrayList<String> buf = new ArrayList<String>();
        buf.add(_notes.get(_selectedNote).getId().get() + "");
        String st = _parser.Build(buf, CommonData.O_GET_MORE_INFO);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.equals("")) {
            buf = _parser.ParseListOfString(str);
            if (buf.size() > 5)
                if (Integer.parseInt(buf.get(0)) == CommonData.O_RESPOND) {
                    if (Integer.parseInt(buf.get(1)) == CommonData.SERV_YES) {
                        buf.remove(0);
                        buf.remove(0);
                        String cDate = buf.get(0);
                        buf.remove(0);
                        String mDate = buf.get(0);
                        buf.remove(0);
                        StringBuilder tags = new StringBuilder();
                        if (buf.size() > 0) {
                            for (int i = 0; i < buf.size(); i++) {
                                tags.append(GetTagById(Integer.parseInt(buf.get(i))));
                                tags.append(CommonData.USER_INPUT_TAGS_SEP);
                            }
                        }
                        _notes.get(_selectedNote).setTags(tags.toString());
                        _notes.get(_selectedNote).setCDate(cDate);
                        _notes.get(_selectedNote).setmDate(mDate);
                    }
                }
        }
    }

    public ObservableList<NoteModel> getNotes() {
        return this._notes;
    }

    public ObservableList<VersionInfoModel> getVersions() {
        return this._versions;
    }

    public int getSelectedNote() {
        return _selectedNote;
    }

    public void setSelectedNote(final int sn) {
        System.out.println("Line "+sn+" selected!");
        _selectedNote = sn;
    }

    public int getSelectedVersion() {
        return _selectedVersion;
    }

    public void setSelectedVersion(final int sv) {
        _selectedVersion = sv;
    }

    public Stage getPrimaryStage() {
        return _mainStage;
    }

    public void setAuth(final boolean flag) {
        _isAuth = flag;
    }

    public int Login(String _log, String _pass) {
        int suc = CommonData.SERV_NO;
        ArrayList<String> s = new ArrayList<String>();
        s.add(_log);
        s.add(_pass);

        String st = _parser.Build(s, CommonData.O_LOGIN);
        SendToServer(st);
        String str = WaitForServer();

        if (!str.equals("")) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        _login = _log;
                        _pass = _pass;
                        _isAuth = true;
                        _stage = 0;
                        LoadBasicDataFromServer();
                        suc = CommonData.SERV_YES;
                    }
                    //else
                    //   _uiLogin.label.showMessage("User name or password is incorrect!");
                }
        }
        return suc;
    }

    public int Logout() {
        String st = _parser.Build("", CommonData.O_LOGOUT);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.equals("")) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        _isAuth = false;
                        return CommonData.SERV_YES;
                    }
                }
        }
        return CommonData.SERV_NO;
    }

    public void LoadBasicDataFromServer() {
        GetCaptions();
        GetTags();
    }

    public int CreateUser(String _log, String _pass) {
        int suc = CommonData.SERV_NO;
        ArrayList<String> s = new ArrayList<String>();
        s.add(_log);
        boolean add = s.add(_pass);
        String st = _parser.Build(s, CommonData.O_CREATE_U);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.equals("")) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        _isAuth = true;
                        suc = Login(_log, _pass);
                    }
                }
        }
        return suc;
    }

    public void DeleteUser(int user_id) {
        String st = _parser.Build(user_id, CommonData.O_DELETE_U);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.equals("")) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        _isAuth = false;
                        _mainStage.setTitle(CommonData.LOG_W_CAPTION);
                        _mainScene = new Scene(_mNode, 600, 400);
                        _mainStage.setScene(_mainScene);
                        _mainStage.show();
                    }
                }
        }
    }

    public void CreateVersion() {
        ArrayList<String> buf = new ArrayList<String>();
        int verId = CommonData.SERV_NO;

        buf.add(this._selectedNote+"");
        buf.add(_versData.getText().get());
        buf.add(_noteData.getMDate().get());

        String st = _parser.Build(buf, CommonData.O_ADD_VERSION);

        SendToServer(st);
        String str = WaitForServer();
        if (!str.equals("")) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 2)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        verId = buff.get(2);
                        this._versData.setId(verId);
                        this._versions.add(new VersionInfoModel(_versData));
                        System.out.println("New note id = "+verId);
                    }
                }
        }
    }

    public void CreateNote() {
        ArrayList<String> res = new ArrayList<String>();
        int newNoteId = -1;
        String st;
        //Parse new tags
        ArrayList<String> tagData = UpdateTagList(this._noteData.getTags().get());

        //Sync tags with server
        SyncTags();

        //Fill request
        res.clear();
        res.add(this._versData.getText().get());
        res.add(this._noteData.getTitle().get());
        res.add(this._noteData.getCDate().get());
        res.add(this._noteData.getMDate().get());

        st = this._parser.Build(res, CommonData.O_CREATE_N);
        SendToServer(st);
        st = WaitForServer();
        if (!st.equals("")) {
            ArrayList<Integer> buff = this._parser.ParseListOfInteger(st);
            if (buff.size() > 2)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        newNoteId = buff.get(2);
                        System.out.println("New note id = "+newNoteId);
                    }
                }
        }

        // Add tags to created note
        res.clear();
        //Convert tags of new note to tag ids
        res.add(newNoteId + "");
        ArrayList<Integer> tags = ConvertTagsIntoIds(tagData);
        if (tags.size() > 0)
            for (int i = 0; i < tags.size(); i++) {
                res.add(tags.get(i).toString());
            }

        st = this._parser.Build(res, CommonData.O_ADD_TAGS_TO_NOTE);
        SendToServer(st);
        st = WaitForServer();
        if (!st.equals("")) {
            ArrayList<Integer> buff = this._parser.ParseListOfInteger(st);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {

                    }
                }
        }
        _noteData.setId(newNoteId);
        _notes.add(new NoteModel(_noteData));
        _versData.setId(0);
        _versions.add(new VersionInfoModel(_versData));
    }

    private ArrayList<String> UpdateTagList(final String tags) {
        int nextId = 0;
        ArrayList<String> res = new ArrayList<>();
        if (_tagList.size() > 0)
            nextId = _tagList.get(_tagList.size() - 1).GetId()+1;

        StringBuilder str = new StringBuilder();
        if (tags.length() > 0) {
            for (int i = 0; i < tags.length(); i++) {
                if (tags.charAt(i) == CommonData.USER_INPUT_TAGS_SEP) {
                    if (str.length() > 0) {
                        Tag t = new Tag(nextId, str.toString());
                        if (!t.TagIsInArray(_tagList)) {
                            nextId++;
                            _tagList.add(t);
                        }
                        res.add(str.toString());
                        str.delete(0, str.length());
                    }
                } else {
                    str.append(tags.charAt(i));
                }
            }
            if (str.length() > 0) {
                Tag t = new Tag(nextId, str.toString());
                if (!t.TagIsInArray(_tagList)) {
                    nextId++;
                    _tagList.add(t);
                }
                res.add(str.toString());
            }
        }
        return res;
    }

    private ArrayList<Integer> ConvertTagsIntoIds(final ArrayList<String> tagData) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        if (_tagList.size() > 0 && tagData.size() > 0) {
            for (int i = 0; i < tagData.size(); i++) {
                for (int j = 0; i < _tagList.size(); j++) {
                    if (_tagList.get(j).GetStrData().equals(tagData.get(i))) {
                        int t = _tagList.get(j).GetId();
                        if (!res.contains(t)) {
                            res.add(t);
                        }
                        break;
                    }
                }
            }
        }
        return res;
    }

    public void DeleteNote(int note) {
        String st = _parser.Build(note, CommonData.O_DELETE_N);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.equals("")) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        _isAuth = true;
                    }
                }
        }
    }

    public void GetTags() {
        ArrayList<String> s = new ArrayList<String>();
        String st = _parser.Build(s, CommonData.O_GETTAGS);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<String> buff = _parser.ParseListOfString(str);
            if ((buff.size() > 2) && (buff.size() % 2 == 0))
                if (Integer.parseInt(buff.get(0)) == CommonData.O_RESPOND) {
                    if (Integer.parseInt(buff.get(1)) == CommonData.SERV_YES) {
                        ArrayList<String> foo = buff;
                        foo.remove(0);
                        foo.remove(0);
                        int tagId = 0;
                        String tagData = new String();
                        for (int i = 0; i < (foo.size() / 2); i++) {
                            tagId = Integer.parseInt(foo.get(i * 2));
                            tagData = foo.get(i * 2 + 1);
                        }

                        _tagList.add(new Tag(tagId, tagData));
                    }
                }
        }
    }

    public void GetCaptions() {
        ArrayList<String> s = new ArrayList<String>();
        String st = _parser.Build(s, CommonData.O_GETCAPTIONS);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<String> buff = _parser.ParseListOfString(str);
            if (buff.size() > 2)
                if (Integer.parseInt(buff.get(0)) == CommonData.O_RESPOND) {
                    if (Integer.parseInt(buff.get(1)) == CommonData.SERV_YES) {
                        buff.remove(0);
                        buff.remove(0);
                        _notes.clear();
                        if ((buff.size() > 0) && (buff.size() % 2 == 0)) {
                            for (int i = 0; i < buff.size(); i += 2)
                                _notes.add(new NoteModel(Integer.parseInt(buff.get(i)), buff.get(i + 1), "", "", ""));
                        }
                    }
                }
        }
    }

    private String WaitForServer() {
        int i;
        String str = "";
        for (i = CommonData.RETRIES_COUNT; i > 0; i--) {
            try {
                sleep(CommonData.SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            str = ReceiveData();
            if (!str.isEmpty()) {
                break;
            }
        }
        return str;
    }

    private void SendToServer(String str) {
        System.out.println("Client send to server:" + str);
        _out.println(str);
    }

    private String ReceiveData() {
        String str = "";
        try {
            str = _in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client received: " + str);
        return str;
    }
}