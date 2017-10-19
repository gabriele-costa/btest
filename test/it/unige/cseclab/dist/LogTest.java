package it.unige.cseclab.dist;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogTest {

    @Test
    public void logTest() {
        File logfile = new File("benign-sql.log");

        String log = "";

        try {
            log = new String(Files.readAllBytes(logfile.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String api = "<edu.ksu.cs.benign.MyContentProvider: android.database.Cursor query\\(android.net.Uri,java.lang.String\\[],java.lang.String,java.lang.String\\[],java.lang.String\\)>";

        // log.matches(api);

        Pattern p = Pattern.compile(api + "((?!GACALL).)*<>");
        Matcher m = p.matcher(log);

        String found = null;
        m.find();
        // while (m.find()) {
            found = m.group(0);
        // }

        System.out.println(found);
    }

}
