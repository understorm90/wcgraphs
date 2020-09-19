package it.unifi.simonesantarsiero.wcgraphs.akibacpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger( TerminalUtils.class );

    private TerminalUtils() {

    }

    public static String exeCommand(String command) {
        String output = "";
        try {
            File dir = new File(System.getProperty("user.dir"));

            Process p = Runtime.getRuntime().exec(command, null, dir);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            output = stringBuilder.toString();
            p.waitFor();

        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException: ", e);
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            LOGGER.error("IOException: ", e);
        }
        return output;
    }
}
