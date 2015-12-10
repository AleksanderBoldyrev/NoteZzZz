package Main;

import Client.LoginController;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Sasha on 30.09.2015.
 *
 * Class describes the note itself, note consists of several versions, described by note primitives.
 */


public class Note {
    private int _id;
    private String _title;
    private ArrayList<NotePrimitive> _note;
    private LocalDateTime _n_cdate;
    private LocalDateTime _n_mdate;
    private ArrayList<Integer> _tags;

    /*Note (String data, int id) {
        _note = new ArrayList<NotePrimitive>();
        _note.clear();
        NotePrimitive n = new NotePrimitive(data, id*10000);
        _note.add(n);
        _id = id;
        _n_cdate = LocalDateTime.now();
        _n_mdate = LocalDateTime.now();

    }*/


    public Note (final int id, final String title, final String data, final LocalDateTime cDate, final LocalDateTime mDate){
        _id = id;
        _title = title;
        _tags = new ArrayList<Integer>();
        _n_cdate = cDate;
        _n_mdate = mDate;
        _note = new ArrayList<NotePrimitive>();
        _note.add(new NotePrimitive(0, mDate, data));
    }

    public Note (int id, ArrayList<Integer> tags, ArrayList<NotePrimitive> notes, String title) {
        _note = new ArrayList<NotePrimitive>();
        _note.clear();
        _id = id;
        _title = title;
        //System.out.print(id + "|");
        _tags = new ArrayList<Integer>();
        _tags.clear();
        for (int i = 0; i < tags.size(); i++) {
            _tags.add(i, tags.get(i));
            //System.out.print(_tags.get(i) + ".");
        }
        //System.out.print(" | ");
        _note = new ArrayList<NotePrimitive>();
        for (int i = 0; i < notes.size(); i++) {
            _note.add(i, notes.get(i));
            //System.out.print((_note.get(i)).GetID() + "|" + (_note.get(i)).GetData() + "|");
        }
        //System.out.println( "|");
        if (_note.size() > 0) {
            _n_cdate = (_note.get(0)).GetCDate();
            _n_mdate = (_note.get(_note.size() - 1)).GetCDate();
        }
        else {
            _n_cdate = LocalDateTime.now();
            _n_mdate = LocalDateTime.now();
        }
        //System.out.println(_n_cdate + "|" + _n_mdate + "|");
    }

    public NotePrimitive getNote() {
        if (_id > _note.size()) _id = 0;
        return _note.get(0);
    }

    public String GetNoteVerDateByPos(int pos) {
        if (_note.size()>0 && _note.size() < pos)
            return _note.get(pos).GetCDate().toString();
        return "";
    }

    /*NotePrimitive getNoteByPos(int id) {
        if (_id > _note.size()) _id = 0;
        return _note.get(id - _id*10000);
    }*/

    /*
    NotePrimitive getNoteByCreateDate(LocalDateTime date) {
        NotePrimitive res = new NotePrimitive("");
        if (_id > _note.size()) _id = 0;
        if (_note.size() > 0 && (date == (this._n_cdate) )) ////////////!!!!!!!!!!!!!!!!!!!!!!!!
        for (int i = 0; i < _note.size(); i++)
        {
            if ((_note.get(i)).GetCDate()==date)
                res = _note.get(i);
;       }
        return res;
    }

    NotePrimitive getNoteByModifyDate(LocalDateTime date) {
        NotePrimitive res = new NotePrimitive("");
        if (_id > _note.size()) _id = 0;
        if (_note.size() > 0 && (date == this._n_mdate) )
            for (int i = 0; i < _note.size(); i++)
            {
                if ((_note.get(i)).GetMDate()==date)
                    res = _note.get(i);
            }
        return res;
    }
    */

    /*long GetLastVersion()
    {
        if (_id > _note.size()) _id = 0;
        return _note.get(_note.size()-1).GetID();
    }*/

    public int GetVersionsCount() {
        return _note.size();
    }

    public NotePrimitive GetNoteByPos(int pos) {
        if (pos>(_note.size()-1))
            pos = _note.size()-1;
        if (pos<0)
            pos = 0;
        return _note.get(pos);
    }


    public int GetId() {
        return _id;
    }

    public String GetTitle() {return _title; }

    public  int GetTagsCount() {
        return _tags.size();
    }

    public int GetTagById(int id) {
        if (id>(_tags.size()-1))
            id = _tags.size()-1;
        if (id<0)
            id = 0;

        return _tags.get(id);
    }

    public void SetTitle(String data)
    {
        _title = data;
    }

    public void SetTags(ArrayList<Integer> _tagIds)
    {
        _tags = _tagIds;
    }

    public void AddTags(final ArrayList<Integer> tagList){
        if (tagList.size()>0)
            for (int i = 0; i < tagList.size(); i++)
                _tags.add(tagList.get(i));
    }

    public void DelVersion(int _targetId)
    {
        if (_note.size()>1)
            for (int i=0; i < _note.size(); i++)
            {
                if (_note.get(i).GetID()==_targetId)
                    _note.remove(i);
            }
    }
    public int AddVersion(final LocalDateTime date, final String data)
    {
        int id = _note.get(_note.size()-1).GetID()+1;
        NotePrimitive np = new NotePrimitive(id, date, data);
        _note.add(np);
        return id;
    }

    /*void DelNote(int ident) {
        if (_id > _note.size()) _id = 0;
        _note.remove(ident - _id*10000);
        _n_mdate = LocalDateTime.now();
    }

    void ModifyNote(String data, int ident, int pos) {
        if (_id > _note.size()) _id = 0;
        _note.get(ident - _id*10000).ChangeNote(pos, data);
        _n_mdate = LocalDateTime.now();
    }*/
}
