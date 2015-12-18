package Main;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by A-13XX on 18.12.15.
 */
public class RequestsParserTest extends TestCase {

    @Test
    public void testBuildTagList() throws Exception {
        RequestsParser tester = new RequestsParser();
        ArrayList<Tag> buff = new ArrayList<Tag>();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 3; i++){
            buff.add(new Tag(i, "Tag"+i));
        }
        assertEquals("Building string from tag list is not correct.", "0|Tag0|1|Tag1|2|Tag2", tester.BuildTagList(buff));
    }

    @Test
    public void testBuild() throws Exception {
        RequestsParser tester = new RequestsParser();
        ArrayList<String> buff = new ArrayList<String>();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 5; i++){
            buff.add("Str"+i);
        }
        assertEquals("Building string from ArrayList<String> is not correct.", "-1|Str0|Str1|Str2|Str3|Str4|", tester.Build(buff, -1));
    }

    @Test
    public void testBuild1() throws Exception {
        RequestsParser tester = new RequestsParser();
        ArrayList<Integer> buff = new ArrayList<Integer>();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < 5; i++){
            buff.add(100+i);
        }
        assertEquals("Building string from ArrayList<Integer> is correct.", "-1|100|101|102|103|104|", tester.Build( -1, buff));
    }

    @Test
    public void testBuild2() throws Exception {
        RequestsParser tester = new RequestsParser();
        String buff = new String("BigTestString");

        assertEquals("Building string from String is not correct.", "-1|BigTestString|", tester.Build(buff, -1));
    }

    @Test
    public void testFixNoteData() throws Exception {
        RequestsParser tester = new RequestsParser();
        String buff1 = new String("Big\nTest\nString");
        String buff2 = new String("Big~Test~String");
        assertEquals("Chechking string for incorrect symbols is not correct.", "Big~Test~String", tester.fixString(buff1, true));
        assertEquals("Chechking string for incorrect symbols is not correct.", "Big\nTest\nString", tester.fixString(buff1, false));
    }

    @Test
    public void testBuild3() throws Exception {
        RequestsParser tester = new RequestsParser();
        int testData = 123483;

        assertEquals("Building string from Integer is not correct.", "-1|123483|", tester.Build(testData, -1));
    }

    @Test
    public void testParseListOfString() throws Exception {
        RequestsParser tester = new RequestsParser();
        ArrayList<String> buff = new ArrayList<String>();
        for (int i = 0; i < 5; i++){
            buff.add("Str"+i);
        }
        ArrayList<String> res = tester.ParseListOfString("Str0|Str1|Str2|Str3|Str4|");
        for (int i = 0; i < 5; i++) {
            assertTrue("Parsing String to ArrayList<String> is not correct.", buff.get(i).equals(res.get(i)));
        }
    }

    @Test
    public void testParseListOfInteger() throws Exception {
        RequestsParser tester = new RequestsParser();
        ArrayList<Integer> buff = new ArrayList<Integer>();
        for (int i = 0; i < 5; i++){
            buff.add(i);
        }
        ArrayList<String> res = tester.ParseListOfString("0|1|2|3|4|");
        for (int i = 0; i < 5; i++) {
            assertTrue("Parsing String to ArrayList<Integer> is not correct.", Integer.parseInt(res.get(i))==buff.get(i));
        }
    }

    @Test
    public void testParseListOfTags() throws Exception {
        RequestsParser tester = new RequestsParser();
        ArrayList<String> buff = new ArrayList<String>();
        for (int i = 0; i < 5; i++){
            buff.add(i+"");
            buff.add("Tag"+i);
        }
        ArrayList<Tag> res = tester.ParseListOfTags(buff);
        for (int i = 0; i < 5; i++) {
            assertTrue("Parsing ArrayList<Tag> from ArrayList<String> is not correct.", buff.get(i*2).equals(res.get(i).GetId()+""));
            assertTrue("Parsing ArrayList<Tag> from ArrayList<String> is not correct.", buff.get(i*2+1).equals(res.get(i).GetStrData()));
        }
    }
}