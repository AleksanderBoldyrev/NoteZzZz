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

        FillLists();

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
    }

    private void FillLists() {
        Random random = new Random();
        for (int i = 0; i < this.getNotes().size(); i++) {
            //_notes.add(new NoteModel(random.nextInt() + "", random.nextInt() + "", random.nextInt() + "", random.nextInt() + ""));
            //_versions.add(new VersionInfoModel(random.nextInt() + "", random.nextInt() + ""));
            _notes.add(this.getNotes().get(i));
        }
        for (int i = 0; i < this.getVersions().size(); i++) {
            //_notes.add(new NoteModel(random.nextInt() + "", random.nextInt() + "", random.nextInt() + "", random.nextInt() + ""));
            //_versions.add(new VersionInfoModel(random.nextInt() + "", random.nextInt() + ""));
            _versions.add(this.getVersions().get(i));
        }
    }

    public void ReFill() {
        Random random = new Random();
        for (int i = 0; i < this.getVersions().size(); i++) {

            //_notes.add(new NoteModel(random.nextInt()+"", random.nextInt()+"", random.nextInt()+"", random.nextInt()+""));
            //_versions.set(i, new VersionInfoModel(random.nextInt()+"", random.nextInt()+""));
            _versions.set(i, this.getVersions().get(i));
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

    public void SetListView() {
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

    public void CreateVersion(){
        ArrayList<String> buf = new ArrayList<String>();
        int verId=CommonData.SERV_NO;

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
                    }
                }
        }

        this._versData.setId(verId);
        this._versions.add(this._versData);
    }

    public void CreateNote() {
        ArrayList<String> res = new ArrayList<String>();
        int newNoteId = -1;
        String st;
        //Parse new tags
        ArrayList<String> tagData = UpdateTagList(this._noteData.getTags().get());

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

        //Fill request
        res.clear();
        res.add(this._versData.getText().get());
        res.add(this._noteData.getTitle().get());
        res.add(this._noteData.getCDate().get());
        res.add(this._noteData.getMDate().get());

        st = this._parser.Build(res, CommonData.O_CREATE_N);
        SendToServer(st);
        str = WaitForServer();
        if (!str.equals("")) {
            ArrayList<Integer> buff = this._parser.ParseListOfInteger(str);
            if (buff.size() > 2)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        newNoteId = buff.get(2);
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
        str = WaitForServer();
        if (!str.equals("")) {
            ArrayList<Integer> buff = this._parser.ParseListOfInteger(str);
            if (buff.size() > 2)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {

                    }
                }
        }
        _notes.add(_noteData);
        _versions.add(_versData);

    }

    private ArrayList<String> UpdateTagList(final String tags) {
        int lastId = 0;
        ArrayList<String> res = new ArrayList<>();
        if (_tagList.size() > 0)
            lastId = _tagList.get(_tagList.size()-1).GetId();
        StringBuilder str = new StringBuilder();
        if (tags.length()>0) {
            for (int i = 0; i < tags.length(); i++) {
                if (tags.charAt(i) == CommonData.USER_INPUT_TAGS_SEP) {
                    if (str.length() > 0) {
                        Tag t = new Tag(lastId, str.toString());
                        if (!t.TagIsInArray(_tagList)) {
                            lastId++;
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
                Tag t = new Tag(lastId, str.toString());
                if (!t.TagIsInArray(_tagList)) {
                    lastId++;
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

    public void SaveNote(int note_id) {
        String st = _parser.Build(note_id, CommonData.O_SAVE_N);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        _isAuth = true;
                    }
                }
        }
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

    public void CreateTag(String tag) {
        String st = _parser.Build(tag, CommonData.O_CREATE_T);
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

    public void DeleteTag(String tag) {
        String st = _parser.Build(tag, CommonData.O_DELETE_T);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        _isAuth = true;
                    }
                }
        }
    }

    public void DeleteNoteByVersion(int ver) {
        String st = _parser.Build(ver, CommonData.O_DELETE_N_V);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 1)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {

                    }
                }
        }
    }

    public void SearchNotes(String title) {
        ArrayList<String> s = new ArrayList<String>();
        s.add(title);
        String st = _parser.Build(s, CommonData.O_SEARCH_N);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
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

    public void GetNoteIds() {
        ArrayList<String> s = new ArrayList<String>();
        String st = _parser.Build(s, CommonData.O_GETNOTEIDS);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 2)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        buff.remove(0);
                        buff.remove(0);
                        /*for (int i = 2; i < buff.size(); i++) {
                            noteIds.add(buff.get(i));
                        }*/
                    }
                }
        }
    }

    public void GetNotePrim(int notePrimId) {
        ArrayList<String> s = new ArrayList<String>();
        s.add(notePrimId + "");
        String st = _parser.Build(s, CommonData.O_GETNOTEPRIM);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<String> buff = _parser.ParseListOfString(str);
            if (buff.size() > 4)
                if (Integer.parseInt(buff.get(0)) == CommonData.O_RESPOND) {
                    if (Integer.parseInt(buff.get(1)) == CommonData.SERV_YES) {
                        int _id = Integer.parseInt(buff.get(2));
                        LocalDateTime _cdate = LocalDateTime.parse(buff.get(3));
                        String _data = buff.get(4);
                        //curVers = new NotePrimitive(_id, _cdate, _data);
                    }
                }
        }
    }

    public void GetVersDate(final int noteId) {
        ArrayList<String> s = new ArrayList<String>();
        s.add(noteId + "");
        String st = _parser.Build(s, CommonData.O_GETVERSDATE);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<String> buff = _parser.ParseListOfString(str);
            if (buff.size() > 2)
                if (Integer.parseInt(buff.get(0)) == CommonData.O_RESPOND) {
                    if (Integer.parseInt(buff.get(1)) == CommonData.SERV_YES) {
                        /*versDate.clear();
                        for (int i = 0; i<buff.size(); i++)
                            versDate.add(buff.get(i));
                        FillCaptions(versDate);*/
                    }
                }
        }
    }

    public boolean SetNoteIds() {
        ArrayList<Integer> s = new ArrayList<Integer>();
        /*for (int i = 0; i < noteIds.size(); i++) {
            s.add(noteIds.get(i));
        }*/
        String st = _parser.Build(CommonData.O_SETNOTEIDS, s);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty()) {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 2)
                if (buff.get(0) == CommonData.O_RESPOND) {
                    if (buff.get(1) == CommonData.SERV_YES) {
                        return true;
                    }
                }
        }
        return false;
    }

    public boolean SetNotePrim() {
        /*ArrayList<String> s = new ArrayList<String>();
        s.add(curVers.GetID()+"");
        s.add(curVers.GetCDate().toString()+"");
        s.add(curVers.GetData());
        String st = _parser.Build(s, CommonData.O_SETNOTEPRIM);
        SendToServer(st);
        String str = WaitForServer();
        if (!str.isEmpty())
        {
            ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
            if (buff.size() > 2)
                if (buff.get(0) == CommonData.O_RESPOND)
                {
                    if (buff.get(1) == CommonData.SERV_YES)
                    {
                        return true;
                    }
                }
        }*/
        return false;
    }

    public boolean SetTags() {
        ArrayList<String> s = new ArrayList<String>();
        if (_tagList.size() > 0) {
            for (int i = 0; i < _tagList.size(); i++) {
                s.add(_tagList.get(i).GetId() + "");
                s.add(_tagList.get(i).GetStrData());
            }
            String st = _parser.Build(s, CommonData.O_SETTAGS);
            SendToServer(st);
            String str = WaitForServer();
            if (!str.isEmpty()) {
                ArrayList<Integer> buff = _parser.ParseListOfInteger(str);
                if (buff.size() > 2)
                    if (buff.get(0) == CommonData.O_RESPOND) {
                        if (buff.get(1) == CommonData.SERV_YES) {
                            return true;
                        }
                    }
            }
        }
        return false;
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
                        if (buff.size() > 0) {
                            for (int i = 0; i < buff.size(); i++)
                                _notes.add(new NoteModel(buff.get(i), "", "", ""));
                        }
                    }
                }
        }
    }

    public void FilterNoteByTag() { //

    }

    /*private String GetData(String s) {
        String res = new String();
        return res;
    }*/

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

    private ArrayList<Integer> ParseTags(String text) {
        ArrayList<Integer> res = new ArrayList<Integer>();
     /*   ArrayList<String> buff = new ArrayList<String>();
        StringBuilder ss = new StringBuilder();
        if (text.length()>0)
            for (int i=0; i<text.length(); i++ ) {
                if (ss.length()>0 && text.charAt(i)==CommonData.USER_INPUT_TAGS_SEP)
                {
                    if (tagsList.size()>0) {
                        for (int j = 0; )
                    } else
                        tagList.add
                }
            }*/
        return res;
    }

    /*private void OpenNote(final int pos, final String text){
        if (pos > 0 && pos < noteIds.size())
            curNoteId = noteIds.get(pos);
        else
            curNoteId = 0;
        GetVersDate(curNoteId);
        currentCaption = text;
        closeButton.setText("Close note");
    }

    private void OpenNoteVersion(final int pos) {
        if (pos>0&&pos<noteIds.size())
            curNoteId = noteIds.get(pos);
        else
            curNoteId = -1;
        GetNotePrim(0);
        noteCaption.setText(currentCaption);
        noteData.setText(curVers.GetData());
        tagList.setText(curVers.GetCDate().toString());
        undoBuff = noteData.getText();
    }*/
}
  /* public void StopListener() {
        //synchronized (termFlag) {
        termFlag = true;
        //}
    }

    public String ReadBuffer() {
        String str = "";
        synchronized (_buffin) {
            if (_buffin.size() > 0) {
                str = _buffin.get(_buffin.size() - 1);
                _buffin.remove(_buffin.size() - 1);
            }
        }
        return str;
    }

    public void WriteToBuffer(String s) {
        String str = "";

    }

    public synchronized void startThread() {
        _running = true;
        _thread = new Thread(this, "Monitor");

        _thread.setDaemon(true);

        _thread.start();
    }

    public synchronized void stopThread() {
        _running = false;
        try {
            _thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
            String str = "";
            while (_running) {
                try {
                    System.out.println("-");
                    str = _in.readLine();
                    if (str.equals(CommonData.TERMCOMMAND))
                        break;

                    synchronized (_buffin) {
                        if (str.length() > 0) {
                            _buffin.add(str);
                            System.out.println("Client received: " + str);
                        }
                    }

                    synchronized (_buffout) {
                        if (_buffout.size() > 0) {
                            _out.println(_buffout.get(_buffout.size() - 1));
                            System.out.println("Client send: " + _buffout.get(_buffout.size() - 1));
                            _buffout.remove(_buffout.size() - 1);
                        }
                    }

                    if (termFlag)
                        break;


                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("closing...");
                    try {
                        _sock.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        _running = false;
    }
*/
