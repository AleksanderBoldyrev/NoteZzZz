package Main;

/**
 * Created by Alex on 04.11.2015.
 *
 * This class gives us the bunch of constants used in all other classes.
 */

public final class CommonData {
    public static final String PATH_1 = "F://Base/File_1.txt";      // *
    public static final String PATH_2 = "F://Base/File_2.txt";      // These are the paths to the DB.
    public static final String PATH_3 = "F://Base/File_3.txt";      // *
    public static int PORT = 36500;                                 // Number of port we use.
    public static final String HOST = "localhost";                  // Host name.
    public static final char SEP = '|';                             // Service separator of the sent data.
    public static final char SEPID = '.';                           // Service separator of tag sequence.
    public static final char USER_INPUT_TAGS_SEP = ' ';             // Separator of input tag sequence in UI.
    public static final String TERMCOMMAND = "***";                 // Service termination command.
    public static final int SLEEP_TIME = 50;                        // Time of waiting for respond.
    public static final int RETRIES_COUNT = 200;                    // Number of steps to contact server.
    public static final int STEP_TOFLUSHBASE = 3;                   // Number of saves between DB backup.
    public static final int SERV_YES = 1;                           // Positive server respond.
    public static final int SERV_NO = 0;                            // Negative server respond.

    public static final String LOG_W_CAPTION = "Welcome to NoteZ app";
    public static final String MAIN_W_CAPTION = "NoteZ app";

    public static final int LOG_W_H = 282;
    public static final int LOG_W_W = 250;
    public static final int MAIN_W_H = 400;
    public static final int MAIN_W_W = 775;

    /*Client - server commands*/
    public static final int O_IS_SERVER_ALIVE = 1111;
    public static final int O_RESPOND = 0;
    public static final int O_LOGIN = 1;
    public static final int O_CREATE_U = 2;
    public static final int O_DELETE_U = 3;
    public static final int O_LOGOUT = 4;
    public static final int O_CREATE_N = 5;
    public static final int O_DELETE_N = 6;
    public static final int O_CREATE_T = 7;
    public static final int O_DELETE_T = 8;
    public static final int O_SAVE_N = 9;
    public static final int O_DELETE_N_V = 10;
    public static final int O_SEARCH_N = 11;
    public static final int O_GETCAPTIONS = 12;
    public static final int O_GETTAGS = 13;
    public static final int O_SETTAGS = 14;
    public static final int O_GETNOTEIDS = 15;
    public static final int O_GETNOTEPRIM = 16;
    public static final int O_GETVERSDATE = 17;
    public static final int O_SETNOTEIDS = 19;
    public static final int O_SETNOTEPRIM = 20;

}
