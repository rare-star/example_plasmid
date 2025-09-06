package su.trap.example_plasmid;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Logging {
    public static final Logger LOGGER = LogManager.getLogger("example_plasmid");
    public static void log(String[] logs, Level level) {
        for (String log : logs) {
            LOGGER.log(level, log);
        }
    }
}
