package org.owasp.esapi.reference;

import org.owasp.esapi.User;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.owasp.esapi.ESAPI;
import java.util.logging.Level;
import org.owasp.esapi.Logger;
import java.io.Serializable;
import java.util.HashMap;
import org.owasp.esapi.LogFactory;

public class JavaLogFactory implements LogFactory
{
    private static volatile LogFactory singletonInstance;
    private HashMap<Serializable, Logger> loggersMap;
    
    public static LogFactory getInstance() {
        if (JavaLogFactory.singletonInstance == null) {
            synchronized (JavaLogFactory.class) {
                if (JavaLogFactory.singletonInstance == null) {
                    JavaLogFactory.singletonInstance = new JavaLogFactory();
                }
            }
        }
        return JavaLogFactory.singletonInstance;
    }
    
    public JavaLogFactory() {
        this.loggersMap = new HashMap<Serializable, Logger>();
    }
    
    @Override
    public Logger getLogger(final Class clazz) {
        Logger classLogger = this.loggersMap.get(clazz);
        if (classLogger == null) {
            classLogger = new JavaLogger(clazz.getName());
            this.loggersMap.put(clazz, classLogger);
        }
        return classLogger;
    }
    
    @Override
    public Logger getLogger(final String moduleName) {
        Logger moduleLogger = this.loggersMap.get(moduleName);
        if (moduleLogger == null) {
            moduleLogger = new JavaLogger(moduleName);
            this.loggersMap.put(moduleName, moduleLogger);
        }
        return moduleLogger;
    }
    
    public static class JavaLoggerLevel extends Level
    {
        protected static final long serialVersionUID = 1L;
        public static final Level ERROR_LEVEL;
        
        protected JavaLoggerLevel(final String name, final int value) {
            super(name, value);
        }
        
        static {
            ERROR_LEVEL = new JavaLoggerLevel("ERROR", Level.SEVERE.intValue() - 1);
        }
    }
    
    private static class JavaLogger implements Logger
    {
        private java.util.logging.Logger jlogger;
        private String moduleName;
        private String applicationName;
        private static boolean logAppName;
        private static boolean logServerIP;
        
        private JavaLogger(final String moduleName) {
            this.jlogger = null;
            this.moduleName = null;
            this.applicationName = ESAPI.securityConfiguration().getApplicationName();
            this.moduleName = moduleName;
            this.jlogger = java.util.logging.Logger.getLogger(this.applicationName + ":" + moduleName);
        }
        
        @Override
        public void setLevel(final int level) {
            try {
                this.jlogger.setLevel(convertESAPILeveltoLoggerLevel(level));
            }
            catch (final IllegalArgumentException e) {
                this.error(Logger.SECURITY_FAILURE, "", e);
            }
        }
        
        @Override
        public int getESAPILevel() {
            return this.jlogger.getLevel().intValue();
        }
        
        private static Level convertESAPILeveltoLoggerLevel(final int level) {
            switch (level) {
                case Integer.MAX_VALUE: {
                    return Level.OFF;
                }
                case 1000: {
                    return Level.SEVERE;
                }
                case 800: {
                    return JavaLoggerLevel.ERROR_LEVEL;
                }
                case 600: {
                    return Level.WARNING;
                }
                case 400: {
                    return Level.INFO;
                }
                case 200: {
                    return Level.FINE;
                }
                case 100: {
                    return Level.FINEST;
                }
                case Integer.MIN_VALUE: {
                    return Level.ALL;
                }
                default: {
                    throw new IllegalArgumentException("Invalid logging level. Value was: " + level);
                }
            }
        }
        
        @Override
        public void trace(final EventType type, final String message, final Throwable throwable) {
            this.log(Level.FINEST, type, message, throwable);
        }
        
        @Override
        public void trace(final EventType type, final String message) {
            this.log(Level.FINEST, type, message, null);
        }
        
        @Override
        public void debug(final EventType type, final String message, final Throwable throwable) {
            this.log(Level.FINE, type, message, throwable);
        }
        
        @Override
        public void debug(final EventType type, final String message) {
            this.log(Level.FINE, type, message, null);
        }
        
        @Override
        public void info(final EventType type, final String message) {
            this.log(Level.INFO, type, message, null);
        }
        
        @Override
        public void info(final EventType type, final String message, final Throwable throwable) {
            this.log(Level.INFO, type, message, throwable);
        }
        
        @Override
        public void warning(final EventType type, final String message, final Throwable throwable) {
            this.log(Level.WARNING, type, message, throwable);
        }
        
        @Override
        public void warning(final EventType type, final String message) {
            this.log(Level.WARNING, type, message, null);
        }
        
        @Override
        public void error(final EventType type, final String message, final Throwable throwable) {
            this.log(Level.SEVERE, type, message, throwable);
        }
        
        @Override
        public void error(final EventType type, final String message) {
            this.log(Level.SEVERE, type, message, null);
        }
        
        @Override
        public void fatal(final EventType type, final String message, final Throwable throwable) {
            this.log(Level.SEVERE, type, message, throwable);
        }
        
        @Override
        public void fatal(final EventType type, final String message) {
            this.log(Level.SEVERE, type, message, null);
        }
        
        private void log(final Level level, final EventType type, String message, final Throwable throwable) {
            if (!this.jlogger.isLoggable(level)) {
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
            if (ESAPI.currentRequest() != null && JavaLogger.logServerIP) {
                appInfo.append(ESAPI.currentRequest().getLocalAddr() + ":" + ESAPI.currentRequest().getLocalPort());
            }
            if (JavaLogger.logAppName) {
                appInfo.append("/" + this.applicationName);
            }
            appInfo.append("/" + this.moduleName);
            String typeInfo = "";
            if (type != null) {
                typeInfo = typeInfo + type + " ";
            }
            this.jlogger.log(level, "[" + typeInfo + this.getUserInfo() + " -> " + (Object)appInfo + "] " + clean, throwable);
        }
        
        @Override
        public boolean isDebugEnabled() {
            return this.jlogger.isLoggable(Level.FINE);
        }
        
        @Override
        public boolean isErrorEnabled() {
            return this.jlogger.isLoggable(JavaLoggerLevel.ERROR_LEVEL);
        }
        
        @Override
        public boolean isFatalEnabled() {
            return this.jlogger.isLoggable(Level.SEVERE);
        }
        
        @Override
        public boolean isInfoEnabled() {
            return this.jlogger.isLoggable(Level.INFO);
        }
        
        @Override
        public boolean isTraceEnabled() {
            return this.jlogger.isLoggable(Level.FINEST);
        }
        
        @Override
        public boolean isWarningEnabled() {
            return this.jlogger.isLoggable(Level.WARNING);
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
        
        @Override
        public void always(final EventType type, final String message) {
            this.always(type, message, null);
        }
        
        @Override
        public void always(final EventType type, final String message, final Throwable throwable) {
            this.log(Level.OFF, type, message, throwable);
        }
        
        static {
            JavaLogger.logAppName = ESAPI.securityConfiguration().getLogApplicationName();
            JavaLogger.logServerIP = ESAPI.securityConfiguration().getLogServerIP();
        }
    }
}
