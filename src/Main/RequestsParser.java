package Main;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Alex on 04.11.2015.
 *
 * Class used to parse the data we send from client to server and backward.
 */


public class RequestsParser {

    /**
     * Procedure used to build serialized string from different strings in the input list
     * and ID of the sought-for operation, in order to simplify the client-server interaction;
     * @param ar - List of strings, contains different data;
     * @param oId - ID of the operation we need to write in the output string;
     * @return - serialized string.
     */

    public String Build(ArrayList<String> ar, int oId) {
        StringBuilder res = new StringBuilder();
        res.append(oId);
        res.append(CommonData.SEP);
        if (ar.size()>0) {
            for (String anAr : ar) {
                res.append(anAr);
                res.append(CommonData.SEP);
            }
        }
        return res.toString();
    }

    /**
     * Procedure used to build serialized string from different numbers in input list
     * and ID of the sought-for operation, in order to simplify the client-server interaction;
     * @param ar - List of numbers, contains different IDs;
     * @param oId - ID of the operation we need to write in the output string;
     * @return - serialized string.
     */

    public String Build(int oId, ArrayList<Integer> ar) {
        StringBuilder res = new StringBuilder();
        res.append(oId);
        res.append(CommonData.SEP);
        if (ar.size()>0) {
            for (Integer anAr : ar) {
                res.append(anAr);
                res.append(CommonData.SEP);
            }
        }
        return res.toString();
    }

    /**
     * Procedure used to build serialized string from some data-string
     * and ID of the sought-for operation, in order to simplify the client-server interaction;
     * @param buff - common data;
     * @param oId - ID of the operation we need to write in the output string;
     * @return - serialized string.
     */

    public String Build(String buff, int oId) {
        StringBuilder res = new StringBuilder();
        res.append(oId);
        res.append(CommonData.SEP);
        res.append(buff);
        res.append(CommonData.SEP);
        return res.toString();
    }

    /**
     * Procedure used to build serialized string from some data-number
     * and ID of the sought-for operation, in order to simplify the client-server interaction;
     * @param buff - common data;
     * @param oId - ID of the operation we need to write in the output string;
     * @return - serialized string.
     */

    public String Build(int buff, int oId) {
        StringBuilder res = new StringBuilder();
        res.append(oId);
        res.append(CommonData.SEP);
        res.append(buff);
        res.append(CommonData.SEP);
        return res.toString();
    }

    /**
     * Procedure used to parse serialized string into the output list of differentiated data;
     * @param str - common data;
     * @return - differentiated output list of strings.
     */

    public ArrayList<String> ParseListOfString(String str) {
        ArrayList<String> s = new ArrayList<String>();

        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == CommonData.SEP) {
                s.add(buff.toString());
                buff.delete(0, buff.length());
            }
            else buff.append(str.charAt(i));
        }

        return s;
    }

    /**
     * Procedure used to parse serialized string into the output list of differentiated data (IDs);
     * @param str - common data;
     * @return - differentiated output list of numbers.
     */

    public ArrayList<Integer> ParseListOfInteger(String str) {
        ArrayList<Integer> n = new ArrayList<Integer>();

        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == CommonData.SEP) {
                n.add(Integer.parseInt(buff.toString()));
                buff.delete(0, buff.length());
            }
            else buff.append(str.charAt(i));
        }

        return n;
    }

    /**
     * Procedure used to parse serialized string into the output note version;
     * @param str - common data;
     * @return - output note primitive version.
     */

    public NotePrimitive ParseNotePrimitive(String str, int id) {
        //NotePrimitive np = new NotePrimitive();

        byte stage = 0;
        StringBuilder buff = new StringBuilder();

        LocalDateTime dtbuff = LocalDateTime.now();
        String ss = "";

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == CommonData.SEP) {
                switch (stage) {
                    case 0:         //*** Read creation date. ***
                        stage++;
                        dtbuff = LocalDateTime.parse(buff.toString());
                        break;
                    case 1:         //*** Read text. ***
                        stage++;
                        ss = buff.toString();
                        break;
                }
                buff.delete(0, buff.length());
            } else buff.append(str.charAt(i));
        }

        NotePrimitive np = new NotePrimitive(id, dtbuff, ss);
        return np;
    }

}
