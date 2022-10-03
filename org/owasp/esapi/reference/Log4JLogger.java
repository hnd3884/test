package org.owasp.esapi.reference;

import org.owasp.esapi.User;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.ESAPI;
import org.apache.log4j.Priority;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Category;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.Logger;

public class Log4JLogger extends org.apache.log4j.Logger implements Logger
{
    private static LoggerFactory factory;
    private static String applicationName;
    private static boolean logAppName;
    private static boolean logServerIP;
    
    public Log4JLogger(final String name) {
        super(name);
    }
    
    public static Category getInstance(final String name) {
        return (Category)LogManager.getLogger(name, Log4JLogger.factory);
    }
    
    public static Category getInstance(final Class clazz) {
        return (Category)LogManager.getLogger(clazz.getName(), Log4JLogger.factory);
    }
    
    public static org.apache.log4j.Logger getLogger(final String name) {
        return LogManager.getLogger(name, Log4JLogger.factory);
    }
    
    public static org.apache.log4j.Logger getLogger(final Class clazz) {
        return LogManager.getLogger(clazz.getName(), Log4JLogger.factory);
    }
    
    public void setLevel(final int level) {
        try {
            super.setLevel(convertESAPILeveltoLoggerLevel(level));
        }
        catch (final IllegalArgumentException e) {
            this.error(Logger.SECURITY_FAILURE, "", e);
        }
    }
    
    public int getESAPILevel() {
        final Level level = super.getLevel();
        return (level == null) ? Integer.MAX_VALUE : level.toInt();
    }
    
    private static Level convertESAPILeveltoLoggerLevel(final int level) {
        switch (level) {
            case Integer.MAX_VALUE: {
                return Level.OFF;
            }
            case 1000: {
                return Level.FATAL;
            }
            case 800: {
                return Level.ERROR;
            }
            case 600: {
                return Level.WARN;
            }
            case 400: {
                return Level.INFO;
            }
            case 200: {
                return Level.DEBUG;
            }
            case 100: {
                return Level.TRACE;
            }
            case Integer.MIN_VALUE: {
                return Level.ALL;
            }
            default: {
                throw new IllegalArgumentException("Invalid logging level. Value was: " + level);
            }
        }
    }
    
    public void always(final EventType type, final String message, final Throwable throwable) {
        this.log(Level.OFF, type, message, throwable);
    }
    
    public void always(final EventType type, final String message) {
        this.always(type, message, null);
    }
    
    public void trace(final EventType type, final String message, final Throwable throwable) {
        this.log(Level.TRACE, type, message, throwable);
    }
    
    public void trace(final EventType type, final String message) {
        this.log(Level.TRACE, type, message, null);
    }
    
    public void debug(final EventType type, final String message, final Throwable throwable) {
        this.log(Level.DEBUG, type, message, throwable);
    }
    
    public void debug(final EventType type, final String message) {
        this.log(Level.DEBUG, type, message, null);
    }
    
    public void info(final EventType type, final String message) {
        this.log(Level.INFO, type, message, null);
    }
    
    public void info(final EventType type, final String message, final Throwable throwable) {
        this.log(Level.INFO, type, message, throwable);
    }
    
    public void warning(final EventType type, final String message, final Throwable throwable) {
        this.log(Level.WARN, type, message, throwable);
    }
    
    public void warning(final EventType type, final String message) {
        this.log(Level.WARN, type, message, null);
    }
    
    public void error(final EventType type, final String message, final Throwable throwable) {
        this.log(Level.ERROR, type, message, throwable);
    }
    
    public void error(final EventType type, final String message) {
        this.log(Level.ERROR, type, message, null);
    }
    
    public void fatal(final EventType type, final String message, final Throwable throwable) {
        this.log(Level.FATAL, type, message, throwable);
    }
    
    public void fatal(final EventType type, final String message) {
        this.log(Level.FATAL, type, message, null);
    }
    
    public void always(final Object message) {
        this.always(message, null);
    }
    
    public void always(final Object message, final Throwable throwable) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.always(Logger.SECURITY_AUDIT, toLog, throwable);
    }
    
    public void trace(final Object message) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.trace(Logger.EVENT_UNSPECIFIED, toLog);
    }
    
    public void trace(final Object message, final Throwable throwable) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.trace(Logger.EVENT_UNSPECIFIED, toLog, throwable);
    }
    
    public void debug(final Object message) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.debug(Logger.EVENT_UNSPECIFIED, toLog);
    }
    
    public void debug(final Object message, final Throwable throwable) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.debug(Logger.EVENT_UNSPECIFIED, toLog, throwable);
    }
    
    public void info(final Object message) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.info(Logger.EVENT_UNSPECIFIED, toLog);
    }
    
    public void info(final Object message, final Throwable throwable) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.info(Logger.EVENT_UNSPECIFIED, toLog, throwable);
    }
    
    public void warn(final Object message) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.warning(Logger.EVENT_UNSPECIFIED, toLog);
    }
    
    public void warn(final Object message, final Throwable throwable) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.warning(Logger.EVENT_UNSPECIFIED, toLog, throwable);
    }
    
    public void error(final Object message) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.error(Logger.EVENT_UNSPECIFIED, toLog);
    }
    
    public void error(final Object message, final Throwable throwable) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.error(Logger.EVENT_UNSPECIFIED, toLog, throwable);
    }
    
    public void fatal(final Object message) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.fatal(Logger.EVENT_UNSPECIFIED, toLog);
    }
    
    public void fatal(final Object message, final Throwable throwable) {
        final String toLog = (String)((message instanceof String) ? message : message.toString());
        this.fatal(Logger.EVENT_UNSPECIFIED, toLog, throwable);
    }
    
    private void log(final Level level, final EventType type, String message, final Throwable throwable) {
        if (!this.isEnabledFor((Priority)level)) {
            return;
        }
        if (message == null) {
            message = "";
        }
        String clean = message.replace('\n', '_').replace('\r', '_');
        if (ESAPI.securityConfiguration().getLogEncodingRequired()) {
            clean = ESAPI.encoder().encodeForHTML(message);
            if (!message.equals(clean)) {
                clean += " (Encoded)";
            }
        }
        final StringBuilder appInfo = new StringBuilder();
        if (ESAPI.currentRequest() != null && Log4JLogger.logServerIP) {
            appInfo.append(ESAPI.currentRequest().getLocalAddr()).append(":").append(ESAPI.currentRequest().getLocalPort());
        }
        if (Log4JLogger.logAppName) {
            appInfo.append("/").append(Log4JLogger.applicationName);
        }
        appInfo.append("/").append(this.getName());
        String typeInfo = "";
        if (type != null) {
            typeInfo = typeInfo + type + " ";
        }
        this.log(Log4JLogger.class.getName(), (Priority)level, (Object)("[" + typeInfo + this.getUserInfo() + " -> " + (Object)appInfo + "] " + clean), throwable);
    }
    
    public boolean isDebugEnabled() {
        return this.isEnabledFor((Priority)Level.DEBUG);
    }
    
    public boolean isErrorEnabled() {
        return this.isEnabledFor((Priority)Level.ERROR);
    }
    
    public boolean isFatalEnabled() {
        return this.isEnabledFor((Priority)Level.FATAL);
    }
    
    public boolean isInfoEnabled() {
        return this.isEnabledFor((Priority)Level.INFO);
    }
    
    public boolean isTraceEnabled() {
        return this.isEnabledFor((Priority)Level.TRACE);
    }
    
    public boolean isWarningEnabled() {
        return this.isEnabledFor((Priority)Level.WARN);
    }
    
    public String getUserInfo() {
        String sid = null;
        final HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        if (request != null) {
            final HttpSession session = request.getSession(false);
            if (session != null) {
                sid = (String)session.getAttribute("ESAPI_SESSION");
                if (sid == null) {
                    sid = "" + ESAPI.randomizer().getRandomInteger(0, 1000000);
                    session.setAttribute("ESAPI_SESSION", (Object)sid);
                }
            }
        }
        final User user = ESAPI.authenticator().getCurrentUser();
        String userInfo = "";
        if (user != null) {
            userInfo = userInfo + user.getAccountName() + ":" + sid + "@" + user.getLastHostAddress();
        }
        return userInfo;
    }
    
    static {
        Log4JLogger.factory = (LoggerFactory)new Log4JLoggerFactory();
        Log4JLogger.applicationName = ESAPI.securityConfiguration().getApplicationName();
        Log4JLogger.logAppName = ESAPI.securityConfiguration().getLogApplicationName();
        Log4JLogger.logServerIP = ESAPI.securityConfiguration().getLogServerIP();
    }
}
