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
    private ArrayList<NotePrimitive> _note_n;
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
        this._id = id;
        this._title = title;
        this._tags = new ArrayList<Integer>();
        this._n_cdate = cDate;
        this._n_mdate = mDate;
        this._note_n = new ArrayList<NotePrimitive>();
        this._note_n.add(new NotePrimitive(0, mDate, data));
    }

    public Note (int id, ArrayList<Integer> tags, ArrayList<NotePrimitive> notes, String title) {
        this._note_n = new ArrayList<NotePrimitive>();
        this._note_n.clear();
        this._id = id;
        this._title = title;
        //System.out.print(id + "|");
        this._tags = new ArrayList<Integer>();
        this._tags.clear();
        for (int i = 0; i < tags.size(); i++) {
            this._tags.add(i, tags.get(i));
            //System.out.print(_tags.get(i) + ".");
        }
        //System.out.print(" | ");
        this._note_n = new ArrayList<NotePrimitive>();
        for (int i = 0; i < notes.size(); i++) {
            this._note_n.add(i, notes.get(i));
            //System.out.print((_note.get(i)).GetID() + "|" + (_note.get(i)).GetData() + "|");
        }
        //System.out.println( "|");
        if (this._note_n.size() > 0) {
            this._n_cdate = (this._note_n.get(0)).GetCDate();
            this._n_mdate = (this._note_n.get(this._note_n.size() - 1)).GetCDate();
        }
        else {
            this._n_cdate = LocalDateTime.now();
            this._n_mdate = LocalDateTime.now();
        }
        //System.out.println(_n_cdate + "|" + _n_mdate + "|");
    }

    public NotePrimitive getNote() {
        if (this._id > this._note_n.size()) this._id = 0;
        return this._note_n.get(0);
    }

    public String GetNoteVerDateByPos(int pos) {
        if (this._note_n.size()>0 && this._note_n.size() < pos)
            return this._note_n.get(pos).GetCDate().toString();
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
        return this._note_n.size();
    }

    public NotePrimitive GetNoteByPos(int pos) {
        if (pos>(this._note_n.size()-1))
            pos = this._note_n.size()-1;
        if (pos<0)
            pos = 0;
        return this._note_n.get(pos);
    }


    public int GetId() {
        return this._id;
    }

    public String GetTitle() {return this._title; }

    public  int GetTagsCount() {
        return this._tags.size();
    }

    public LocalDateTime GetCDate(){
        return this._n_cdate;
    }

    public LocalDateTime GetMDate(){
        return this._n_mdate;
    }

    public int GetTagById(int id) {
        if (id>(this._tags.size()-1))
            id = this._tags.size()-1;
        if (id<0)
            id = 0;

        if (this._tags.size() > 0) return this._tags.get(id);
        else return 0;
    }

    public void SetTitle(String data)
    {
        this._title = data;
    }

    public void SetTags(ArrayList<Integer> _tagIds)
    {
        this._tags = _tagIds;
    }

    public void AddTags(final ArrayList<Integer> tagList){
        if (tagList.size()>0)
            for (int i = 0; i < tagList.size(); i++)
                this._tags.add(tagList.get(i));
    }

    public int DelVersion(int _targetId) {
        if (this._note_n.size()>1)
            for (int i=0; i < this._note_n.size(); i++) {
                if (this._note_n.get(i).GetID()==_targetId) {
                    this._note_n.remove(i);
                    return CommonData.SERV_YES;
                }

            }
        return CommonData.SERV_NO;
    }
    public int AddVersion(final LocalDateTime date, final String data)
    {
        int id = this._note_n.get(this._note_n.size()-1).GetID()+1;
        NotePrimitive np = new NotePrimitive(id, date, data);
        this._note_n.add(np);
        return id;
    }

    /*void DelNote(int ident) {
        if (_id > _note_n.size()) _id = 0;
        _note_n.remove(ident - _id*10000);
        _n_mdate = LocalDateTime.now();
    }

    void ModifyNote(String data, int ident, int pos) {
        if (_id > _note_n.size()) _id = 0;
        _note_n.get(ident - _id*10000).ChangeNote(pos, data);
        _n_mdate = LocalDateTime.now();
    }*/
}
