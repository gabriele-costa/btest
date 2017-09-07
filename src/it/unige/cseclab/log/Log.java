package it.unige.cseclab.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

    static Logger logger = Logger.getLogger("TestLog");
    static FileHandler fh;

    public static void log(String msg) {

        try {

            if (fh == null) {
                fh = new FileHandler("./test.log");
                logger.addHandler(fh);

                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
            }

            // the following statement is used to log any messages
            logger.info(msg);

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
