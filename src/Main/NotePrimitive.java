package Main;

import java.time.LocalDateTime;

/**
 * Created by Sasha on 30.09.2015.
 *
 * Class describes the data which the note is consisted of. The NotePrimitive is a single part of the whole note,
 * which could consist of many versions.
 */

public class NotePrimitive {
    private String _data;
    private LocalDateTime _cdate;
    private int _id;

    public NotePrimitive() {
        this._data = "";
        this._cdate = LocalDateTime.now();
        this._id = 0;
    }

    public NotePrimitive(String s, int ident) {
        this._data = s;
        this._cdate = LocalDateTime.now();
        this._id = ident;
    }

    public NotePrimitive(int id, LocalDateTime date, String data) {
        this._data = data;
        this._cdate = date;
        this._id = id;
    }

    public String GetData() { return this._data; }
    public LocalDateTime GetCDate() {return this._cdate; }
    public void SetData(String ns)
    {
        this._data = ns;
    }
    public void SetCDate(LocalDateTime nd)
    {
        this._cdate = nd;
    }
    public int GetID() {
        return this._id;
    }

    public void ChangeNote(int pos, String newData) {
        for (int i = 0; i < (pos+newData.length()-this._data.length()); i++)
            this._data+=" ";
        char arr[] = this._data.toCharArray();
        for (int i = pos; i < pos + newData.length(); i++)
            arr[i] = newData.charAt(i - pos);
        this._data = new String(arr);
    }

    public void DelSubstr(int beg, int end) {
        int swapper = (beg > end) ? beg : end;
        beg = (beg > end) ? end : beg;
        end = swapper;
        String ss = this._data;
        this._data = "";
        for (int i = 0; i<ss.length(); i++)
            if ((i<beg) && (i>end))
                this._data+=ss.charAt(i);
    }
}

