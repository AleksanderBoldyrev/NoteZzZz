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
        _data = "";
        _cdate = LocalDateTime.now();
        _id = 0;
    }

    public NotePrimitive(String s, int ident) {
        _data = s;
        _cdate = LocalDateTime.now();
        _id = ident;
    }

    public NotePrimitive(int id, LocalDateTime date, String data) {
        _data = data;
        _cdate = date;
        _id = id;
    }

    public String GetData() { return _data; }
    public LocalDateTime GetCDate() {return _cdate; }
    public void SetData(String ns)
    {
        _data = ns;
    }
    public void SetCDate(LocalDateTime nd)
    {
        _cdate = nd;
    }
    public int GetID() {
        return _id;
    }

    public void ChangeNote(int pos, String newData) {
        for (int i = 0; i < (pos+newData.length()-_data.length()); i++)
            _data+=" ";
        char arr[] = _data.toCharArray();
        for (int i = pos; i < pos + newData.length(); i++)
            arr[i] = newData.charAt(i - pos);
        _data = new String(arr);
    }

    public void DelSubstr(int beg, int end) {
        int swapper = (beg > end) ? beg : end;
        beg = (beg > end) ? end : beg;
        end = swapper;
        String ss = _data;
        _data = "";
        for (int i = 0; i<ss.length(); i++)
            if ((i<beg) && (i>end))
                _data+=ss.charAt(i);
    }
}

