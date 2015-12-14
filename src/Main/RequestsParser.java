package Main;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by Alex on 04.11.2015.
 *
 * Class used to parse the data we send from client to server and backward.
 */


public class RequestsParser {

    public String BuildTagList(ArrayList<Tag> tags) {
        StringBuilder str = new StringBuilder();
        if (tags.size()>0)
            for (int i = 0; i < tags.size(); i++){
                str.append(tags.get(i).GetId());
                str.append(CommonData.SEP);
                str.append(tags.get(i).GetStrData());
                if (i != tags.size()-1)
                    str.append(CommonData.SEP);
            }
        return str.toString();
    }

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
                res.append(fixString(anAr, true));
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
        res.append(fixString(buff, true));
        res.append(CommonData.SEP);
        return res.toString();
    }

    private String fixString(final String str, final boolean dir){
        StringBuilder out = new StringBuilder();
        if (dir) {
            if (str.length() > 0) {

                for (int i = 0; i<str.length(); i++)
                    if (str.charAt(i)==CommonData.NEW_LINE_SYMB)
                        out.append(CommonData.NEW_LINE_REPLACEMENT);
                else
                        out.append(str.charAt(i));
            }
        }
        else{
            if (str.length() > 0) {
                for (int i = 0; i<str.length(); i++)
                    if (str.charAt(i)==CommonData.NEW_LINE_REPLACEMENT)
                        out.append(CommonData.NEW_LINE_SYMB);
                    else
                        out.append(str.charAt(i));
            }
        }
        return out.toString();
    }

    public String fixNoteData(final String str){
        StringBuilder out = new StringBuilder();
        if (str.length() > 0) {

            for (int i = 0; i<str.length(); i++)
                if (str.charAt(i)==CommonData.SEP)
                    out.append(CommonData.SEPARATOR_REPLACEMENT);
                else
                    out.append(str.charAt(i));

        }
        return out.toString();
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

        str = fixString(str, true);

        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == CommonData.SEP) {
                s.add(fixString(buff.toString(),false));
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

        str = fixString(str, true);

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

    public ArrayList<Tag> ParseListOfTags(ArrayList<String> str) {
        ArrayList<Tag> t = new ArrayList<Tag>();

        if ((str.size() % 2 == 0) && (str.size() > 0))
            for (int i = 0; i < str.size(); i+=2) {
                int id = Integer.parseInt(str.get(i));
                String data = str.get(i+1);
                Tag tag = new Tag(id, data);
                t.add(tag);
            }
        return t;
    }

}
