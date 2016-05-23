package Main;

import jdk.nashorn.internal.runtime.Debug;

/**
 * Created by Alex on 04.11.2015.
 *
 * This class gives us the bunch of constants used in all other classes.
 */

public final class CommonData {
    public static final String PATH_USERS = "./Base/Users";             // *
    public static final String PATH_NOTES = "./Base/Notes";             // These are the paths to the DB.
    public static final String PATH_TAGS = "./Base/Tags";               // *
    public static int          PORT = 36550;                                     // Number of port we use.
    //public static final String HOST = "192.168.1.106";                      // Host name.
    public static final String HOST = "localhost";                      // Host name.
    public static final char   SEP = '|';                                 // Service separator of the sent data.
    public static final char   SEPID = '.';                               // Service separator of tag sequence.
    public static final char   USER_INPUT_TAGS_SEP = ' ';                 // Separator of input tag sequence in UI.
    public static final String TERMCOMMAND = "***";                     // Service termination command.
    public static final int    SLEEP_TIME = 50;                            // Time of waiting for respond.
    public static final int    RETRIES_COUNT = 200;                        // Number of steps to contact server.
    public static final int    STEP_TOFLUSHBASE = 3;                       // Number of saves between DB backup.
    public static final int    SERV_YES = 1;                               // Positive server respond.
    public static final int    SERV_NO = 0;                                // Negative server respond.
    public static final char   NEW_LINE_SYMB = '\n';                      // Symbol of the new line in escape-sequence notation.
    public static final char   NEW_LINE_REPLACEMENT = '~';                // Symbol used to replace the6 new line symbol.
    public static final String SEPARATOR_REPLACEMENT = "\\/";           // Symbol used to replace the

    public static final String LOG_W_CAPTION = "Welcome to NoteZ app";  // Login window caption text.
    public static final String MAIN_W_CAPTION = "NoteZ app";            // Main window caption text.

    public static final int    LOG_W_H = 355;                              // *
    public static final int    LOG_W_W = 250;                              // Size configurations
    public static final int    MAIN_W_H = 400;                             // for both windows.
    public static final int    MAIN_W_W = 775;                             // *

    /*Client - server commands*/
    public static final int    O_IS_SERVER_ALIVE = 1111;                   // Checking whether server is active or not //TODO: not implemented yet.
    public static final int    O_RESPOND = 0;                              // Request for server respond.
    public static final int    O_LOGIN = 1;                                // Request for login operation.
    public static final int    O_CREATE_U = 2;                             // Request for user account creation operation.
    public static final int    O_DELETE_U = 3;                             // Request for user account deletion operation.
    public static final int    O_LOGOUT = 4;                               // Request for logout operation.
    public static final int    O_CREATE_N = 5;                             // Request for note creation operation.
    public static final int    O_DELETE_N = 6;                             // Request for note deletion operation.
    public static final int    O_SAVE_N = 7;                               // Request for note saving operation.
    public static final int    O_DELETE_N_V = 8;                           // Request for version deletion operation.
    public static final int    O_GETCAPTIONS = 9;                          // Request for getting note's captions operation.
    public static final int    O_GETTAGS = 10;                             // Request for getting note's tags operation.
    public static final int    O_SETTAGS = 11;                             // Request for setting note's tags operation.
    public static final int    O_GETNOTEIDS = 12;                          // Request for getting note's IDs operation.
    public static final int    O_GETVERSDATE = 13;                         // Request for getting version creation date operation.
    public static final int    O_SETNOTEIDS = 14;                          // Request for setting note's IDs operation.
    public static final int    O_ADD_TAGS_TO_NOTE = 15;                    // Request for adding tags to the note operation.
    public static final int    O_SYNC_TAG_LIST = 16;                       // Request for synchronization of the tag list with  operation.
    public static final int    O_ADD_VERSION = 17;                         // Request for adding versions to the note operation.
    public static final int    O_GET_VERSIONS = 18;                        // Request for getting note's versions operation.
    public static final int    O_GET_MORE_INFO = 19;                       // Request for getting updated info operation.
    public static final int    O_CHANGE_CAPTION = 20;                      // Request for changing the caption operation.
    public static final int    O_SET_TAGS_TO_NOTE = 21;                    // Request for adding tags to the note operation.
}
