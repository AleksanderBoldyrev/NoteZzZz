package Server;

import Main.CommonData;
import Main.RequestsParser;
import Main.Tag;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Sasha on 30.09.2015.
 *
 * Class used as a server, so it calls the DB for the data for user and has a special protocol to contact the client.
 */

public class Server extends Thread{
    //private int _port;
    private Socket _socket;
    private BufferedReader _in;
    private PrintWriter _out;
    private RequestsParser _parser;
    private int _userId;

    /*private void createNewUser() {

    }

    private void createNewNote() {

    }*/

    Server(Socket s) {
        _socket = s;
        _parser = new RequestsParser();
        //_parser.SetUserId(-1);
        _userId = -1;
        try {
            _in = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            _out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try{
            String str = "";
            String resp = "";
            while (true) {
                str = _in.readLine();
                System.out.println("Server received: "+str);
                if (str.equals(CommonData.TERMCOMMAND))
                    break;
                resp = "";
                //Parsing
                if (str.length()>0) {
                    ArrayList<String> buff = _parser.ParseListOfString(str);

                    try {
                        int command = Integer.parseInt(buff.get(0));
                        switch (command) {
                            case CommonData.O_CREATE_U:
                                resp = CreateUser(buff);
                                break;
                            case CommonData.O_CREATE_N:
                                resp = CreateNote(buff);
                                break;
                            case CommonData.O_CREATE_T:
                                resp = CreateTag(buff);
                                break;
                            case CommonData.O_DELETE_N:
                                resp = DeleteNote(buff);
                                break;
                            case CommonData.O_DELETE_N_V:
                                resp = DeleteNoteByVer(buff);
                                break;
                            case CommonData.O_DELETE_T:
                                resp = DeleteTag(buff);
                                break;
                            case CommonData.O_DELETE_U:
                                resp = DeleteUser(buff);
                                break;
                            case CommonData.O_LOGIN:
                                resp = Login(buff);
                                break;
                            case CommonData.O_LOGOUT:
                                resp = Logout(buff);
                                break;
                            case CommonData.O_SAVE_N:
                                resp = SaveNote(buff);
                                break;
                            case CommonData.O_SEARCH_N:
                                resp = SearchNote(buff);
                                break;
                            case CommonData.O_GETCAPTIONS:
                                resp = GetCaptions(buff);
                                break;
                            case CommonData.O_GETTAGS:
                                resp = GetTags(buff);
                                break;
                            case CommonData.O_SETTAGS:
                                resp = SetTags(buff);
                                break;
                            case CommonData.O_GETNOTEIDS:
                                resp = GetNoteIds(buff);
                                break;
                            case CommonData.O_GETNOTEPRIM:
                                resp = GetNotePrimitive(buff);
                                break;
                            case CommonData.O_GETVERSDATE:
                                resp = GetVersionsDate(buff);
                                break;
                            case CommonData.O_SETNOTEIDS:
                                //resp = SetNotePrimitive(buff);
                                break;
                            case CommonData.O_SETNOTEPRIM:
                                resp = SetNotePrimitive(buff);
                                break;
                            case CommonData.O_ADD_TAGS_TO_NOTE:
                                resp = AddTagsToNote(_parser.ParseListOfInteger(str));
                                break;
                            case CommonData.O_SYNC_TAG_LIST:
                                resp = SyncTagList(buff);
                                break;
                            case CommonData.O_ADD_VERSION:
                                resp = AddVersion(buff);
                                break;
                        }

                    } catch (NumberFormatException e)
                    {
                        System.out.println(e.toString());
                    }


                }

                if (!resp.equals("")) {
                    _out.println(resp);
                    System.out.println("Server send: "+resp);
                }

                FlushBases();

                try {
                    this.sleep(CommonData.SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("closing...");
            try {
                _socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String GetNotePrimitive(ArrayList<String> buff) {
        String res = "";
        return res;
    }

    public String GetVersionsDate(ArrayList<String> buff) {
        ArrayList<String> res = new ArrayList<String>();
        if (buff.size()>1) {
            res = ServerDaemon.sHelper.GetNoteVersionsListById(_userId, Integer.parseInt(buff.get(1)));
            res.add(CommonData.SERV_YES + "");
        }
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public void FlushBases(){
        ServerDaemon.sHelper.FlushBases();
    }

    /*public String SetNotesIds(ArrayList<String> buff) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        if (buff.size()>1)
        for (int i = 0; i < buff.size(); i++) {
            if (b) id = Integer.parseInt(buff.get(i));
            else {
                str = buff.get(i);
                res.add(new Tag(id, str));
            }
            b = !b;
        }
        b = ServerDaemon.sHelper.SetTagList(_userId, res);
        StringBuilder stb = new StringBuilder();
        stb.append(CommonData.SERV_YES + "");
        return _parser.Build(stb.toString(), CommonData.O_RESPOND);
    }*/

    public String SetNotePrimitive(ArrayList<String> buff) {
        String res = new String();
        return res;
    }

    public String GetNoteIds(ArrayList<String> buff) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        res = ServerDaemon.sHelper.GetNotesListByUserId(_userId);
        res.add(CommonData.SERV_YES);
        return _parser.Build(CommonData.O_RESPOND, res);
    }

    public String GetCaptions(ArrayList<String> buff) {
        ArrayList<String> res = new ArrayList<String>();
        res = ServerDaemon.sHelper.GetNotesTitlesById(_userId);
        res.add(CommonData.SERV_YES + "");
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String GetTags(ArrayList<String> buff){
        ArrayList<Tag> res = new ArrayList<Tag>();
        res = ServerDaemon.sHelper.GetTagList(_userId);
        StringBuilder stb = new StringBuilder();
        stb.append(CommonData.SERV_YES + "");
        stb.append(CommonData.SEP);
        for (Tag re : res) {
            stb.append(re.GetId());
            stb.append(CommonData.SEP);
            stb.append(re.GetStrData());
            stb.append(CommonData.SEP);
        }
        return _parser.Build(stb.toString(), CommonData.O_RESPOND);
    }

    public String SetTags(ArrayList<String> buff){
        ArrayList<Tag> res = new ArrayList<Tag>();
        boolean b = true;
        int id = 0;
        String str = "";
        for (String aBuff : buff) {
            if (b) id = Integer.parseInt(aBuff);
            else {
                str = aBuff;
                res.add(new Tag(id, str));
            }
            b = !b;
        }
        b = ServerDaemon.sHelper.SetTagList(_userId, res);
        StringBuilder stb = new StringBuilder();
        stb.append(CommonData.SERV_YES + "");
        return _parser.Build(stb.toString(), CommonData.O_RESPOND);
    }

    public String Login(ArrayList<String> buff ) {
        ArrayList<String> res = new ArrayList<String>();

        int id = CommonData.SERV_NO;
        if (buff.size()>2) {
            id = ServerDaemon.sHelper.Login(buff.get(1), buff.get(2));
        }
        if (id>=0) {
            res.add(CommonData.SERV_YES + "");
            _userId=id;
        }
        else
            res.add(CommonData.SERV_NO + "");
        //res.add(id+"");
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String Logout(ArrayList<String> buff ) {
        StringBuilder res = new StringBuilder();
        boolean suc = false;
        ArrayList<Integer> ar = new ArrayList<Integer>();
        if (buff.size()>0) {
            suc = ServerDaemon.sHelper.Logout(_userId);
        }
        if (suc)
            res.append(CommonData.SERV_YES);
        else
            res.append(CommonData.SERV_NO);
        return _parser.Build(res.toString(), CommonData.O_RESPOND);
    }

    public String CreateUser(ArrayList<String> buff ) {
        ArrayList<String> res = new ArrayList<String>();

        boolean suc = false;
        if (buff.size()>2) {
            suc = ServerDaemon.sHelper.CreateUser(buff.get(1), buff.get(2));
        }
        if (suc)
            res.add(CommonData.SERV_YES + "");
        else
            res.add(CommonData.SERV_NO + "");
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String DeleteUser(ArrayList<String> in) {
        //ArrayList<Integer> res = _parser.ParseListOfInteger(in);
        StringBuilder out = new StringBuilder();
        boolean suc = false;
        if (in.size()>2) {
            suc = ServerDaemon.sHelper.DeleteUser(_userId);
        }
        if (suc) {
            out.append(CommonData.SERV_YES);
            _userId = -1;
        }
        else
            out.append(CommonData.SERV_NO);
        return _parser.Build(out.toString(), CommonData.O_RESPOND);
    }

    public String AddVersion(ArrayList<String> buff){
        ArrayList<String> res = new ArrayList<String>();
        int suc = CommonData.SERV_NO;
        if (buff.size()> 3 ) {
            int noteId = Integer.parseInt(buff.get(1));
            String text = buff.get(2);
            LocalDateTime time = LocalDateTime.parse(buff.get(3));
            suc = ServerDaemon.sHelper.AddVersionToNote(_userId, noteId, text, time);
        }
        res.add(suc+"");
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String SyncTagList(ArrayList<String> buff){
        String res = new String();
        int suc = CommonData.SERV_NO;
        buff.remove(0); //remove operation id
        if (buff.size()>0 && (buff.size() % 2 == 0)){
            ArrayList<Tag> tags = _parser.ParseListOfTags(buff);
            ArrayList<Tag> newTags = ServerDaemon.sHelper.SyncTagList(_userId, tags);
            res = _parser.BuildTagList(newTags);
        }
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String AddTagsToNote(ArrayList<Integer> buff){
        ArrayList<Integer> res = new ArrayList<Integer>();
        int suc = -1;
        if (buff.size()> 2 ) {
            buff.remove(0); // remove operation id
            int tagId = buff.get(0);
            buff.remove(0); //remove tag id
            suc = ServerDaemon.sHelper.AddTagsToNote(_userId, tagId, buff);
        }
        res.add(suc);
        return _parser.Build(CommonData.O_RESPOND, res);
    }

    public String CreateNote(ArrayList<String> buff ) {   //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        ArrayList<Integer> res = new ArrayList<Integer>();
        int suc = CommonData.SERV_NO;
        ArrayList<Integer> ar = new ArrayList<Integer>();
        if (buff.size()>4) {
            suc = ServerDaemon.sHelper.CreateNote(_userId, buff.get(1), buff.get(2), buff.get(3), buff.get(4));
        }
        if (suc >= 0)
            res.add(CommonData.SERV_YES);
        else
            res.add(CommonData.SERV_NO);
        res.add(suc);
        return _parser.Build(CommonData.O_RESPOND, res);
    }

    public String DeleteNote(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String DeleteNoteByVer(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String ChangeUser(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String SaveNote(ArrayList<String> buff ) {
        StringBuilder res = new StringBuilder();
        boolean suc = false;
        //ArrayList<Integer> ar = new ArrayList<Integer>();
        if (buff.size()>3) {
            suc = ServerDaemon.sHelper.SaveNote(Integer.parseInt(buff.get(1)));
        }
        if (suc)
            res.append(CommonData.SERV_YES);
        else
            res.append(CommonData.SERV_NO);
        return _parser.Build(res.toString(), CommonData.O_RESPOND);
    }

    public String SearchNote(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }
    /*public String CreateRequest(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String ChangeRequest(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String DeleteRequest(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }*/

    public String CreateTag(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String DeleteTag(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String AddTagToRequest(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String GetRequestListByTags(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String GetTagList(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String HandleRequest(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }

    public String GetNoteTitleList(ArrayList<String> buff ) {
        String res = new String();
        return res;
    }
}
