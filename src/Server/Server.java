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
 * <p>
 * Class used as a server, so it calls the DB for the data for user and has a special protocol to contact the client.
 */

public class Server extends Thread {
// public class Server {
    private Socket _socket;
    private BufferedReader _in;
    private PrintWriter _out;
    private RequestsParser _parser;
    private int _userId;

    Server(Socket s) {
        _socket = s;
        _parser = new RequestsParser();
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
        try {
            String str = "";
            String resp = "";
            while (true) {
                //System.out.println("Server received: " + str);
                str = _in.readLine();
                if (str != null) {
                    System.out.println("Server received: " + str);
                    if (str.equals(CommonData.TERMCOMMAND)) {
                        ServerDaemon.sHelper.FlushBases();
                        break;
                    }
                    resp = "";
                    //Parsing
                    if (str.length() > 0) {
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
                                case CommonData.O_DELETE_N:
                                    resp = DeleteNote(buff);
                                    break;
                                case CommonData.O_DELETE_N_V:
                                    resp = DeleteNoteByVer(buff);
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
                                case CommonData.O_GETVERSDATE:
                                    resp = GetVersionsDate(buff);
                                    break;
                                case CommonData.O_SETNOTEIDS:
                                    //resp = SetNotePrimitive(buff);
                                    break;
                                case CommonData.O_ADD_TAGS_TO_NOTE:
                                    resp = AddTagsToNote(_parser.ParseListOfInteger(str));
                                    break;
                                case CommonData.O_SET_TAGS_TO_NOTE:
                                    resp = SetTagsToNote(_parser.ParseListOfInteger(str));
                                    break;
                                case CommonData.O_SYNC_TAG_LIST:
                                    resp = SyncTagList(buff);
                                    break;
                                case CommonData.O_ADD_VERSION:
                                    resp = AddVersion(buff);
                                    break;
                                case CommonData.O_GET_VERSIONS:
                                    resp = GetVersions(buff);
                                    break;
                                case CommonData.O_GET_MORE_INFO:
                                    resp = GetMoreInfo(buff);
                                    break;
                                case CommonData.O_CHANGE_CAPTION:
                                    resp = ChangeCaption(buff);
                                    break;
                            }

                        } catch (NumberFormatException e) {
                            System.out.println(e.toString());
                        }


                    }

                    if (!resp.equals("")) {
                        _out.println(resp);
                        System.out.println("Server send: " + resp);
                    }

                    FlushBases();

                    //try {
                    //    this.sleep(CommonData.SLEEP_TIME);
                    //} catch (InterruptedException e) {
                    //    e.printStackTrace();
                    //}
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

    private String ChangeCaption(ArrayList<String> buff) {
        int suc = CommonData.SERV_NO;
        if (buff.size() > 2) {
            int noteId = Integer.parseInt(buff.get(1));
            String newCaption = buff.get(2);
            suc = ServerDaemon.sHelper.ChangeCaption(_userId, noteId, newCaption);
        }
        FlushBases();
        return _parser.Build(suc, CommonData.O_RESPOND);
    }

    public String GetVersionsDate(ArrayList<String> buff) {
        ArrayList<String> res = new ArrayList<String>();
        if (buff.size() > 1) {
            res = ServerDaemon.sHelper.GetNoteVersionsListById(_userId, Integer.parseInt(buff.get(1)));
            res.add(CommonData.SERV_YES + "");
        }
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public void FlushBases() {
        ServerDaemon.sHelper.FlushBases();
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
        res.add(0, CommonData.SERV_YES+"");
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String GetTags(ArrayList<String> buff) {
        ArrayList<Tag> res = new ArrayList<Tag>();
        res = ServerDaemon.sHelper.GetTagList(_userId);
        ArrayList<String> arr = new ArrayList<String>();
        arr.add(CommonData.SERV_YES + "");
        for (Tag re : res) {
            arr.add(re.GetId()+"");
            arr.add(re.GetStrData());
        }
        return _parser.Build(arr, CommonData.O_RESPOND);
    }

    public String SetTags(ArrayList<String> buff) {
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
        FlushBases();
        return _parser.Build(stb.toString(), CommonData.O_RESPOND);
    }

    public String Login(ArrayList<String> buff) {
        ArrayList<String> res = new ArrayList<String>();

        int id = CommonData.SERV_NO;
        if (buff.size() > 2) {
            id = ServerDaemon.sHelper.Login(buff.get(1), buff.get(2));
        }
        if (id >= 0) {
            res.add(CommonData.SERV_YES + "");
            _userId = id;
        } else
            res.add(CommonData.SERV_NO + "");
        //res.add(id+"");
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String Logout(ArrayList<String> buff) {
        StringBuilder res = new StringBuilder();
        boolean suc = false;
        ArrayList<Integer> ar = new ArrayList<Integer>();
        if (buff.size() > 0) {
            suc = ServerDaemon.sHelper.Logout(_userId);
        }
        if (suc)
            res.append(CommonData.SERV_YES);
        else
            res.append(CommonData.SERV_NO);
        return _parser.Build(res.toString(), CommonData.O_RESPOND);
    }

    public String CreateUser(ArrayList<String> buff) {
        ArrayList<String> res = new ArrayList<String>();

        boolean suc = false;
        if (buff.size() > 2) {
            suc = ServerDaemon.sHelper.CreateUser(buff.get(1), buff.get(2));
        }
        if (suc)
            res.add(CommonData.SERV_YES + "");
        else
            res.add(CommonData.SERV_NO + "");
        FlushBases();
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String DeleteUser(ArrayList<String> in) {
        int suc = CommonData.SERV_NO;
        if (in.size() > 2) {
            String userName = in.get(1);
            String userPass = in.get(2);
            suc = ServerDaemon.sHelper.DeleteUser(userName, userPass);
        }
        FlushBases();
        return _parser.Build(suc, CommonData.O_RESPOND);
    }

    public String AddVersion(ArrayList<String> buff) {
        ArrayList<String> res = new ArrayList<String>();
        int suc = CommonData.SERV_NO;
        if (buff.size() > 3) {
            int noteId = Integer.parseInt(buff.get(1));
            String text = buff.get(2);
            LocalDateTime time = LocalDateTime.parse(buff.get(3));
            suc = ServerDaemon.sHelper.AddVersionToNote(_userId, noteId, text, time);
            res.add(CommonData.SERV_YES+"");
        }
        res.add(suc + "");
        FlushBases();
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    private String GetVersions(ArrayList<String> buff) {
        ArrayList<String> res= new ArrayList<String>();
        buff.remove(0); //remove operation id
        if (buff.size() > 0) {
            int noteId = Integer.parseInt(buff.get(0));
            res = ServerDaemon.sHelper.GetNoteVersionsListById(_userId, noteId);
            res.add(0, CommonData.SERV_YES+"");
        }
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    private String GetMoreInfo(ArrayList<String> buff) {
        ArrayList<String> res= new ArrayList<String>();
        buff.remove(0); //remove operation id
        if (buff.size() > 0) {
            int noteId = Integer.parseInt(buff.get(0));
            res = ServerDaemon.sHelper.GetMoreInfo(_userId, noteId);
            res.add(0, CommonData.SERV_YES+"");
        }
        return _parser.Build(res, CommonData.O_RESPOND);

    }

    public String SyncTagList(ArrayList<String> buff) {
        String res = new String();
        int suc = CommonData.SERV_NO;
        buff.remove(0); //remove operation id
        if (buff.size() > 0 && (buff.size() % 2 == 0)) {
            ArrayList<Tag> tags = _parser.ParseListOfTags(buff);
            ArrayList<Tag> newTags = ServerDaemon.sHelper.SyncTagList(_userId, tags);
            res = _parser.BuildTagList(newTags);
        }
        FlushBases();
        return _parser.Build(res, CommonData.O_RESPOND);
    }

    public String AddTagsToNote(ArrayList<Integer> buff) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        int suc = -1;
        if (buff.size() > 2) {
            buff.remove(0); // remove operation id
            int tagId = buff.get(0);
            buff.remove(0); //remove tag id
            suc = ServerDaemon.sHelper.AddTagsToNote(_userId, tagId, buff);
        }
        res.add(suc);
        FlushBases();
        return _parser.Build(CommonData.O_RESPOND, res);
    }

    public String SetTagsToNote(ArrayList<Integer> buff) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        int suc = -1;
        if (buff.size() >= 2) {
            buff.remove(0); // remove operation id
            int tagId = buff.get(0);
            buff.remove(0); //remove tag id
            suc = ServerDaemon.sHelper.SetTagsToNote(_userId, tagId, buff);
        }
        res.add(suc);
        FlushBases();
        return _parser.Build(CommonData.O_RESPOND, res);
    }

    public String CreateNote(ArrayList<String> buff) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        int suc = CommonData.SERV_NO;
        ArrayList<Integer> ar = new ArrayList<Integer>();
        if (buff.size() > 4) {
            suc = ServerDaemon.sHelper.CreateNote(_userId, buff.get(1), buff.get(2), buff.get(3), buff.get(4));
        }
        if (suc >= 0)
            res.add(CommonData.SERV_YES);
        else
            res.add(CommonData.SERV_NO);
        res.add(suc);
        FlushBases();
        return _parser.Build(CommonData.O_RESPOND, res);
    }

    public String DeleteNote(ArrayList<String> buff) {
        int suc = CommonData.SERV_NO;
        if (buff.size() > 1) {
            int noteId = Integer.parseInt(buff.get(1));
            suc = ServerDaemon.sHelper.DeleteNote(_userId, noteId);
        }
        FlushBases();
        return _parser.Build(suc, CommonData.O_RESPOND);
    }


    public String DeleteNoteByVer(ArrayList<String> buff) {
        int suc = CommonData.SERV_NO;
        if (buff.size() > 2) {
            int noteId = Integer.parseInt(buff.get(1));
            int versId = Integer.parseInt(buff.get(2));
            suc = ServerDaemon.sHelper.DeleteVersion(_userId, noteId, versId);
        }
        FlushBases();
        return _parser.Build(suc, CommonData.O_RESPOND);
    }

    public String SaveNote(ArrayList<String> buff) {
        StringBuilder res = new StringBuilder();
        boolean suc = false;
        if (buff.size() > 3) {
            suc = ServerDaemon.sHelper.SaveNote(Integer.parseInt(buff.get(1)));
        }
        if (suc)
            res.append(CommonData.SERV_YES);
        else
            res.append(CommonData.SERV_NO);
        FlushBases();
        return _parser.Build(res.toString(), CommonData.O_RESPOND);
    }
}
