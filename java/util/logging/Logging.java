package java.util.logging;

import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;

class Logging implements LoggingMXBean
{
    private static LogManager logManager;
    private static String EMPTY_STRING;
    
    @Override
    public List<String> getLoggerNames() {
        final Enumeration<String> loggerNames = Logging.logManager.getLoggerNames();
        final ArrayList list = new ArrayList();
        while (loggerNames.hasMoreElements()) {
            list.add(loggerNames.nextElement());
        }
        return list;
    }
    
    @Override
    public String getLoggerLevel(final String s) {
        final Logger logger = Logging.logManager.getLogger(s);
        if (logger == null) {
            return null;
        }
        final Level level = logger.getLevel();
        if (level == null) {
            return Logging.EMPTY_STRING;
        }
        return level.getLevelName();
    }
    
    @Override
    public void setLoggerLevel(final String s, final String s2) {
        if (s == null) {
            throw new NullPointerException("loggerName is null");
        }
        final Logger logger = Logging.logManager.getLogger(s);
        if (logger == null) {
            throw new IllegalArgumentException("Logger " + s + "does not exist");
        }
        Level level = null;
        if (s2 != null) {
            level = Level.findLevel(s2);
            if (level == null) {
                throw new IllegalArgumentException("Unknown level \"" + s2 + "\"");
            }
        }
        logger.setLevel(level);
    }
    
    @Override
    public String getParentLoggerName(final String s) {
        final Logger logger = Logging.logManager.getLogger(s);
        if (logger == null) {
            return null;
        }
        final Logger parent = logger.getParent();
        if (parent == null) {
            return Logging.EMPTY_STRING;
        }
        return parent.getName();
    }
    
    static {
        Logging.logManager = LogManager.getLogManager();
        Logging.EMPTY_STRING = "";
    }
}
