package Main;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by Sasha on 30.09.2015.
 *
 * Class describes the bunch of data that belongs to the user.
 */

public class User {
    private String _login;
    private String _pass;
    private int _u_id;
    private ArrayList<Integer> _notes_u;

    public User(int id, String buffLogin, String buffPass, ArrayList<Integer> notes) {
        this._u_id = id;
        System.out.print(this._u_id + "|");
        this._login = buffLogin;
        System.out.print(this._login + "|");
        this._pass = buffPass;
        System.out.print(this._pass + "|");
        this._notes_u = new ArrayList<Integer>();
        for (int i = 0; i < notes.size(); i++) {
            this._notes_u.add(i, notes.get(i));
            System.out.print(this._notes_u.get(i) + ".");
        }
        System.out.println("|");
    }

    public int AuthUser(String l, String p) {
        if (l.equals(this._login) && p.equals(this._pass))
            return this._u_id;
        return -1;
    }

    public int GetId() {
        return this._u_id;
    }
    public String GetName() { return this._login; }
    public String GetPass() { return this._pass; }
    public int GetNotesCount() { return this._notes_u.size(); }
    public int GetNoteByPos(int i) { return this._notes_u.get(i); }
    public ArrayList<Integer> GetNotes() {return this._notes_u; }

    public void SetLogin(String name) {
        this._login = name;
    }
    public void SetId(int _id) { this._u_id = _id; }
    public void SetPass(String p) {
        this._pass = p;
    }
    public void SetNoteList(ArrayList<Integer> _n) {this._notes_u  = _n; }

    public void RemoveNote(int noteId)  {
        if (this._notes_u.size()>0)
            for (int i=0; i<this._notes_u.size(); i++)
                if (this._notes_u.get(i)==noteId) {
                    this._notes_u.remove(i);
                }
    }

    public void AddNote(int noteId)  {
        this._notes_u.add(noteId);
    }

    public boolean Verify(final String log, final String pass) {
        return ((_login.equals(log))&&(_pass.equals(pass)));
    }
}
