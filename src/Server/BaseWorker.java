package Server;


import Main.*;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by A-13XX on 08.10.2015.
 *
 * Class used to process all the data from our database to the server,
 * in order to parse the structure of data given in DB.
 */

public class BaseWorker {
    private ArrayList<User> _users;
    private ArrayList<Note> _notes;
    private ArrayList<Tag> _tags;

    private String _usersBasePath;
    private String _notesBasePath;

    BaseWorker() {
        _users = new ArrayList<User>();
        _notes = new ArrayList<Note>();
        _tags = new ArrayList<Tag>();
    }

    /**
     * Checking for the user's account existence;
     *
     * @param log  - username;
     * @param pass - user password;
     * @return - existing user ID.
     */

    public int CheckUser(String log, String pass) {
        if (_users.size() > 0) {
            for (User _user : _users) {
                if (_user.GetName().equals(log))
                    if (_user.GetPass().equals(pass))
                        return _user.GetId();
            }
        }
        return -1;
    }

    public boolean DoesUserExist(String log) {
        if (_users.size() > 0) {
            for (User _user : _users) {
                if (_user.GetName().equals(log))
                    return true;
            }
        }
        return false;
    }

    /**
     * Parsing the whole database for all user accounts;
     *
     * @param fileName - account information database file.
     */

    private void LoadUsers(String fileName) {
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);

        try {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //*** Parsing of the file content. ***

        String str = new String(sb);

        byte stage = 0;
        StringBuilder buff = new StringBuilder();
        int buffId = 0;
        String buffLogin = "";
        String buffPass = "";
        ArrayList<Integer> buffNotesId = new ArrayList<Integer>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == CommonData.SEP) {
                switch (stage) {
                    case 0:         //*** Read user's ID. ***
                        stage++;
                        if (buff.length() != 0)
                            buffId = Integer.parseInt(buff.toString());
                        else
                            buffId = 0;
                        break;
                    case 1:         //*** Read username. ***
                        buffLogin = buff.toString();
                        stage++;
                        break;
                    case 2:         //*** Read user's password. ***
                        buffPass = buff.toString();
                        stage++;
                        break;
                    case 3:         //*** Read user's note list. ***
                        stage = 0;
                        int data = 0;
                        StringBuilder notes_id = new StringBuilder();
                        for (int j = 0; j < buff.length(); j++) {
                            if (buff.charAt(j) == CommonData.SEPID) {
                                buffNotesId.add(Integer.parseInt(notes_id.toString()));
                                notes_id.delete(0, notes_id.length());
                            } else {
                                notes_id.append(buff.charAt(j));
                            }
                        }
                        _users.add(new User(buffId, buffLogin, buffPass, buffNotesId));
                        break;
                }
                buff.delete(0, buff.length());
            } else buff.append(str.charAt(i));
        }
    }

    /**
     * Parsing the whole database for all user notes;
     *
     * @param fileName - note-list database file.
     */

    private void LoadNotes(String fileName) {
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);

        try {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //*** Parsing of the file content. ***

        String str = new String(sb);

        byte stage = 0;
        StringBuilder buff = new StringBuilder();
        int buffId = 0;
        int buffVersNum = 0;
        ArrayList<Integer> buffTagsId = new ArrayList<Integer>();
        ArrayList<NotePrimitive> buffNoteVers = new ArrayList<NotePrimitive>();
        String buffText = "";
        String buffTitle = "";
        StringBuilder lBuf = new StringBuilder();
        int readOfPrimitivesCount = 0;
        LocalDateTime buffDate = LocalDateTime.now();
        String buffNotePrimData;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == CommonData.SEP) {
                switch (stage) {
                    case 0:         //*** Read user's ID. ***
                        stage++;
                        if (buff.length() != 0)
                            buffId = Integer.parseInt(buff.toString());
                        else
                            buffId = 0;
                        break;
                    case 1:
                        stage++;
                        buffTitle = buff.toString();
                        break;
                    case 2:         //*** Read number of versions. ***
                        buffVersNum = Integer.parseInt(buff.toString());
                        stage++;
                        break;
                    case 3:         //*** Read note's tags id list. ***
                        stage++;
                        //lBuf.replace(0, lBuf.length() - 1, "");
                        for (int j = 0; j < buff.length(); j++) {
                            if (buff.charAt(j) == CommonData.SEPID) {
                                buffTagsId.add(Integer.parseInt(lBuf.toString()));
                                lBuf.delete(0, lBuf.length());
                            } else {
                                lBuf.append(buff.charAt(j));
                            }
                        }
                        break;
                    case 4:         //*** Creation date of Note Primitive. ***
                        stage++;
                        buffDate = LocalDateTime.parse(buff);
                        break;
                    case 5:         //*** Text of Note Primitive. ***
                        buffNotePrimData = buff.toString();
                        buffNoteVers.add(new NotePrimitive(readOfPrimitivesCount, buffDate, buffNotePrimData));

                        if (readOfPrimitivesCount < (buffVersNum - 1)) {
                            stage--; //read next primitive at next case
                            readOfPrimitivesCount++;
                        } else {
                            stage = 0; //end of reading note primitives
                            _notes.add(new Note(buffId, buffTagsId, buffNoteVers, buffTitle));
                            buffTagsId.clear();
                            buffNoteVers.clear();
                            readOfPrimitivesCount = 0;
                        }
                        break;
                }
                buff.delete(0, buff.length());
            } else buff.append(str.charAt(i));
        }
    }

    /**
     * Parsing the whole database for all user tags;
     *
     * @param fileName - tag-list database file.
     */

    public void LoadTags(String fileName) {
        StringBuilder sb = new StringBuilder();
        File file = new File(fileName);

        try {
            BufferedReader in = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            try {
                String s;
                while ((s = in.readLine()) != null) {
                    sb.append(s);
                    sb.append("\n");
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String str = new String(sb);

        byte stage = 0;
        StringBuilder buff = new StringBuilder();
        int buffId = 0;
        String buffData = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == CommonData.SEP) {
                if (stage == 0) {
                    stage++;
                    if (buff.length() != 0)
                        buffId = Integer.parseInt(buff.toString());
                    else
                        buffId = 0;
                } else if (stage == 1) {
                    buffData = buff.toString();
                    stage--;
                    _tags.add(new Tag(buffId, buffData));
                }
                buff.delete(0, buff.length());
            } else buff.append(str.charAt(i));
        }
    }

    /**
     * Checking for new user accounts and writing them into database;
     *
     * @param fileName - account information database file.
     */

    private void SaveUsers(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists())
                file.createNewFile();
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            for (User _user : _users) {
                out.print(_user.GetId());
                out.print(CommonData.SEP);
                out.print(_user.GetName());
                out.print(CommonData.SEP);
                out.print(_user.GetPass());
                out.print(CommonData.SEP);
                int t = _user.GetNotesCount();
                for (int j = 0; j < t; j++) {
                    out.print(_user.GetNoteByPos(j));
                    out.print(CommonData.SEPID);
                }
                out.print(CommonData.SEP);
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checking for new user notes and writing them into database;
     *
     * @param fileName - note-list database file.
     */
    private void SaveNotes(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists())
                file.createNewFile();
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            for (Note _note : _notes) {
                out.print(_note.GetId());
                out.print(CommonData.SEP);
                out.print(_note.GetTitle());
                out.print(CommonData.SEP);
                int vcount = _note.GetVersionsCount();
                out.print(vcount);
                out.print(CommonData.SEP);
                int t = _note.GetVersionsCount();
                for (int j = 0; j < t; j++) {
                    out.print(_note.GetTagById(j));
                    out.print(CommonData.SEPID);
                }
                out.print(CommonData.SEP);
                for (int j = 0; j < vcount; j++) {
                    NotePrimitive nt = _note.GetNoteByPos(j);
                    out.print(nt.GetCDate().toString());
                    out.print(CommonData.SEP);
                    out.print(nt.GetData());
                    out.print(CommonData.SEP);
                }
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checking for new user tags and writing them into database;
     *
     * @param fileName - tag-list database file.
     */
    public void SaveTags(String fileName) {
        File file = new File(fileName);
        try {
            if (!file.exists())
                file.createNewFile();
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            for (Tag _tag : _tags) {
                out.print(_tag.GetId());
                out.print(CommonData.SEP);
                out.print(_tag.GetStrData());
                out.print(CommonData.SEP);
            }
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saving the whole bunch of data into separated database files.
     */
    public void SaveData() {
        SaveNotes(CommonData.PATH_3);
        SaveUsers(CommonData.PATH_2);
        SaveTags(CommonData.PATH_1);
    }

    /**
     * Checking whether the user account ID is a natural number,
     * and it is less than the number of accounts in the list;
     *
     * @param id - user account ID;
     * @return - true in case of verification.
     */
    private boolean VerifyUserId(int id) {
        return id < _users.size() && id > 0;
    }

    /**
     * Checking whether the tag ID is a natural number,
     * and it is less than the number of tags in the list;
     *
     * @param id - tag ID;
     * @return - true in case of verification.
     */
    private boolean VerifyTagId(int id) {
        return id < _tags.size() && id > 0;
    }

    /**
     * Checking whether the user note ID is a natural number,
     * and it is less than the number of notes in the list;
     *
     * @param id - user note ID;
     * @return - true in case of verification.
     */
    private boolean VerifyNoteId(int id) {
        return id < _notes.size() && id > 0;
    }

    /**
     * Setting the note title;
     *
     * @param id   - ID of the note;
     * @param data - Title name.
     */
    public void SetNoteCaption(int id, String data) {
        if (VerifyNoteId(id)) {
            _notes.get(id).SetTitle(data);
        }
    }

    /**
     * Hashing the tag-list to the note by note ID;
     *
     * @param id - ID of the note;
     * @param t  - list of tags.
     */
    public void SetNoteTags(int id, ArrayList<Integer> t) {
        if (VerifyNoteId(id)) {
            _notes.get(id).SetTags(t);
        }
    }

    /**
     * Removing the note's last version;
     *
     * @param id  - ID of the note;
     * @param ver - version of tha note.
     */
    public void RemoveNoteVer(int id, int ver) {
        if (VerifyNoteId(id))
            _notes.get(id).DelVersion(ver);
    }

    /**
     * Setting the username of the user by user ID;
     *
     * @param id   - ID of the user;
     * @param data - username.
     */
    public void SetUserName(int id, String data) {
        if (VerifyUserId(id))
            _users.get(id).SetLogin(data);
    }

    /**
     * Setting the password of the user by user ID;
     *
     * @param id   - ID of the user;
     * @param data - password.
     */
    public void SetUserPass(int id, String data) {
        if (VerifyUserId(id))
            _users.get(id).SetPass(data);
    }

    /**
     * Getting the tag by it's title.
     *
     * @param name - tag's title;
     * @return - tag's ID.
     */
    public int GetTagByName(String name) {
        int res = -1;
        if (_tags.size() > 0)
            for (int i = 0; i < _tags.size(); i++)
                if (_tags.get(i).GetStrData().equals(name))
                    res = i;

        return res;
    }

    /**
     * Getting the tag by it's title;
     *
     * @return - tag list.
     */
    public ArrayList<Tag> GetTagList() {
        return _tags;
    }

    public void SetTagList(ArrayList<Tag> art) {
        _tags = art;
    }

    /**
     * Adds tag in the common base;
     *
     * @param t - tag's title.
     */
    public void AddTag(String t) {
        if (GetTagByName(t) >= 0) {
            int m = 0;
            if (_tags.size() > 0) m = _tags.get(0).GetId() + 1;
            Tag t1 = new Tag(m, t);
            _tags.add(t1);
        }
        //File file = new File(fileName);
        /*try
        {
            if(!file.exists())
                file.createNewFile();
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            for (int i = 0; i < _tags.size(); i++) {
                out.print(_tags.get(i).GetId());
                out.print(sep);
                out.print(_tags.get(i).GetStrData());
                out.print(sep);
            }
            out.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    /**
     * Adds note to the user's account base;
     *
     * @param userId - user account ID;
     *               //* @param _tags - tags bound with future note;
     * @param _data  - consistence of the note;
     * @param title  - note's title.
     */
    public int AddNote(int userId, String _data, String title) {
        if (VerifyUserId(userId)) {
            int m = 0;
            if (_notes.size() > 0) m = _notes.get(0).GetId() + 1;
            NotePrimitive np = new NotePrimitive(0, LocalDateTime.now(), _data);
            ArrayList<NotePrimitive> al = new ArrayList<NotePrimitive>();
            al.add(np);
            Note n1 = new Note(m, new ArrayList<Integer>(), al, title);
            _notes.add(n1);
            _users.get(userId).AddNote(m);
            return m;
        }
        //File file = new File(fileName);
        /*try
        {
            if(!file.exists())
                file.createNewFile();
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());

            out.print(_notes.get(i).GetId());
            out.print(sep);
            int vcount = _notes.get(i).GetVersionsCount();
            out.print(vcount);
            out.print(sep);
            int t = _notes.get(i).GetVersionsCount();
            for (int j = 0; j < t; j++) {
                out.print(_notes.get(i).GetTagById(j));
                out.print(sepId);
            }
            out.print(sep);
            for (int j = 0; j < vcount; j++)
            {
                NotePrimitive nt = _notes.get(i).GetNoteByPos(j);
                out.print(nt.GetCDate().toString());
                out.print(sep);
                out.print(nt.GetData());
                out.print(sep);
            }
            out.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }*/
        return -1;
    }

    /**
     * Adds user account to the base;
     *
     * @param _log  - username;
     * @param _pass - user's password.
     */
    public int AddUser(String _log, String _pass) {
        //if (CheckUser(_log, _pass) >= 0) {
        int m = 0;
        if (_users.size() > 0)
            m = _users.get(0).GetId() + 1;
        User u1 = new User(m, _log, _pass, new ArrayList<Integer>());
        _users.add(u1);
        return m;
    }

    /**
     * Deleting user account by it's ID;
     *
     * @param userId - user account ID.
     */
    public void DeleteUser(int userId) {
        if (VerifyUserId(userId)) {
            //Remove notes
            if (_users.get(userId).GetNotesCount() > 0) {
                ArrayList<Integer> un = _users.get(userId).GetNotes();
                for (int j = 0; j < _notes.size(); j++) {
                    if (un.contains(_notes.get(j).GetId()))
                        _notes.remove(j);
                }
            }
        }
    }

    /**
     * Deleting a note by user ID;
     *
     * @param userId - user account ID.
     */
    public void DeleteNote(int noteId, int userId) { //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (VerifyUserId(userId)) {
            //Remove notes
            if (_users.get(userId).GetNotesCount() > 0) {
                ArrayList<Integer> un = _users.get(userId).GetNotes();
                for (int j = 0; j < _notes.size(); j++) {
                    if (un.get(j).equals(noteId))
                        _notes.remove(j);
                }
            }
        }
    }

    /**
     * Initialising the process of loading the whole bunch of data from base.
     */
    public void Initialise() {
        LoadNotes(CommonData.PATH_3);
        LoadUsers(CommonData.PATH_2);
        LoadTags(CommonData.PATH_1);
    }

    /**
     * Deleting the tag by it's ID;
     *
     * @param id - tag's ID.
     */
    public void DeleteTagById(int id) {
        if (VerifyTagId(id)) {
            if (_tags.size() > 0)
                for (int i = 0; i < _tags.size(); i++)
                    if (_tags.get(i).GetId() == id)
                        _tags.remove(i);
        }
    }

    /**
     * Deleting the tag by it's title;
     *
     * @param name - tag's title.
     */
    public void DeleteTagByName(String name) {
        if (_tags.size() > 0)
            for (int i = 0; i < _tags.size(); i++)
                if (_tags.get(i).GetStrData().equals(name))
                    _tags.remove(i);
    }

    /**
     * Getting the number of existing notes in database belonging to the exact user;
     *
     * @param userId - user account ID;
     * @return - sought-for count.
     */
    public int GetNotesCountByUser(int userId) {
        int res = 0;
        if (_users.size() > 0)
            for (User _user : _users) {
                if (_user.GetId() == userId) {
                    res = _user.GetNotesCount();
                    break;
                }
            }
        return res;
    }

    /**
     * Getting the whole list of note's titles belonging to the exact user;
     *
     * @param userId - user account ID;
     * @return - list of note's titles for our user.
     */
    public ArrayList<String> GetNotesTitles(int userId) {
        ArrayList<String> res = new ArrayList<String>();
        if (_users.size() > 0) {
            for (User _user : _users) {
                if (_user.GetId() == userId) {
                    ArrayList<Integer> al = _user.GetNotes();
                    for (Note _note : _notes) {
                        if (al.contains(_note.GetId()))
                            res.add(_note.GetTitle());
                    }
                    break;
                }
            }
        }
        return res;
    }

    /**
     * Getting the whole list of notes ID belonging to the exact user;
     *
     * @param userId - user account ID;
     * @return - list of notes for our user.
     */
    public ArrayList<Integer> GetNotesByUserId(int userId) {
        ArrayList<Integer> res = new ArrayList<Integer>();
        if (_users.size() > 0) {
            for (User _user : _users) {
                if (_user.GetId() == userId) {
                    return _user.GetNotes();
                }
            }
        }
        return res;
    }

    /**
     * Getting all the change dates of the current version of the tag;
     *
     * @param noteId - ID of a sought-for note;
     * @return - list of dates, bound in a single string.
     */
    public ArrayList<String> GetNoteVersionsDatesById(final int noteId) {
        Note t;
        ArrayList<String> res = new ArrayList<String>();
        for (int i = 0; i < _notes.size(); i++) {
            if (_notes.get(i).GetId() == noteId) {
                t = _notes.get(i);
                for (int j = 0; j < t.GetVersionsCount(); j++) {
                    res.add(t.GetNoteVerDateByPos(j));
                }
            }
        }
        return res;
    }

    /**
     * Getting the version of the given note by it's ID.
     *
     * @param userId - ID of the logged user;
     * @param noteId - ID of the note in the global note-list;
     * @param verId  - ID of the version of sought-for note;
     * @return - the current note version string.
     */
    public String GetNoteVerById(int userId, int noteId, int verId) {
        String res = new String();
        ArrayList<Integer> n = new ArrayList<Integer>();
        if (_users.size() > 0) {
            for (User _user : _users) {
                if (_user.GetId() == userId) {
                    n = _user.GetNotes();
                    if (n.size() > 0)
                        for (Integer aN : n) {
                            if ((_notes.get(aN).GetId() == noteId) && (_notes.get(aN).GetVersionsCount() > 0)) {
                                for (int m = 0; m < _notes.get(aN).GetVersionsCount(); m++) {
                                    if (_notes.get(aN).GetNoteByPos(m).GetID() == verId) {
                                        res = _notes.get(aN).GetNoteByPos(m).GetData();
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                }
                break;
            }
        }
        return res;
    }

    /**
     * Getting the date of creation of the given note by it's ID.
     *
     * @param userId - ID of the logged user;
     * @param noteId - ID of the note in the global note-list;
     * @param verId  - ID of the version of sought-for note;
     * @return - date of creation (in LocalDateTime format).
     */
    public String GetNoteVerDateById(int userId, int noteId, int verId) {
        String res = new String(LocalDateTime.now().toString());
        ArrayList<Integer> n = new ArrayList<Integer>();
        if (_users.size() > 0) {
            for (int i = 0; i < _users.size(); i++) {
                if (_users.get(i).GetId() == userId) {
                    n = _users.get(i).GetNotes();
                    if (n.size() > 0) {
                        for (int j = 0; j < n.size(); j++) {
                            if ((_notes.get(n.get(j)).GetId() == noteId) && (_notes.get(n.get(j)).GetVersionsCount() > 0)) {
                                for (int m = 0; m < _notes.get(n.get(j)).GetVersionsCount(); m++) {
                                    if (_notes.get(n.get(j)).GetNoteByPos(m).GetID() == verId) {
                                        res = _notes.get(n.get(j)).GetNoteByPos(m).GetCDate().toString();
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                    break;
                }
                break;
            }
        }
        return res;
    }

    /**
     * Getting the ID of the note given in the common list of notes;
     *
     * @param userId - ID of the user, whose account contains this note;
     * @param id     - local ID of the note;
     * @return - the global ID of the note (in the common list, or DB).
     */
    public int GetNotePosById(int userId, int id) {
        if (_notes.size() > 0)
            for (int i = 0; i < _notes.size(); i++) {
                if (_notes.get(i).GetId() == id)
                    return i;
            }
        return 0;
    }

    /**
     * Getting the number of single note's versions;
     * @param userId - ID of the user, whose account contains current note;
     * @param noteId - ID of the current note;
     * @return - number of note's versions.
     */
    public int GetNoteVersCount(int userId, int noteId) {
        if (_users.size() > 0) {
            for (User _user : _users) {
                if (_user.GetId() == userId) {
                    return _notes.get(GetNotePosById(userId, noteId)).GetVersionsCount();
                }
            }
        }
        return 0;
    }

    public void DeleteUnusedTags() {
        /**
         * TODO
         */
    }

    public void Verify() {
        /**
         * TODO
         */
    }

    public void CreateBackup() {

    }

    public void GetFromBackup() {

    }
}

