package com.sun.istack.internal.logging;

import java.util.StringTokenizer;
import com.sun.istack.internal.NotNull;
import java.util.logging.Level;

public class Logger
{
    private static final String WS_LOGGING_SUBSYSTEM_NAME_ROOT = "com.sun.metro";
    private static final String ROOT_WS_PACKAGE = "com.sun.xml.internal.ws.";
    private static final Level METHOD_CALL_LEVEL_VALUE;
    private final String componentClassName;
    private final java.util.logging.Logger logger;
    
    protected Logger(final String systemLoggerName, final String componentName) {
        this.componentClassName = "[" + componentName + "] ";
        this.logger = java.util.logging.Logger.getLogger(systemLoggerName);
    }
    
    @NotNull
    public static Logger getLogger(@NotNull final Class<?> componentClass) {
        return new Logger(getSystemLoggerName(componentClass), componentClass.getName());
    }
    
    @NotNull
    public static Logger getLogger(@NotNull final String customLoggerName, @NotNull final Class<?> componentClass) {
        return new Logger(customLoggerName, componentClass.getName());
    }
    
    static final String getSystemLoggerName(@NotNull final Class<?> componentClass) {
        StringBuilder sb = new StringBuilder(componentClass.getPackage().getName());
        final int lastIndexOfWsPackage = sb.lastIndexOf("com.sun.xml.internal.ws.");
        if (lastIndexOfWsPackage > -1) {
            sb.replace(0, lastIndexOfWsPackage + "com.sun.xml.internal.ws.".length(), "");
            final StringTokenizer st = new StringTokenizer(sb.toString(), ".");
            sb = new StringBuilder("com.sun.metro").append(".");
            if (st.hasMoreTokens()) {
                String token = st.nextToken();
                if ("api".equals(token)) {
                    token = st.nextToken();
                }
                sb.append(token);
            }
        }
        return sb.toString();
    }
    
    public void log(final Level level, final String message) {
        if (!this.logger.isLoggable(level)) {
            return;
        }
        this.logger.logp(level, this.componentClassName, getCallerMethodName(), message);
    }
    
    public void log(final Level level, final String message, final Object param1) {
        if (!this.logger.isLoggable(level)) {
            return;
        }
        this.logger.logp(level, this.componentClassName, getCallerMethodName(), message, param1);
    }
    
    public void log(final Level level, final String message, final Object[] params) {
        if (!this.logger.isLoggable(level)) {
            return;
        }
        this.logger.logp(level, this.componentClassName, getCallerMethodName(), message, params);
    }
    
    public void log(final Level level, final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(level)) {
            return;
        }
        this.logger.logp(level, this.componentClassName, getCallerMethodName(), message, thrown);
    }
    
    public void finest(final String message) {
        if (!this.logger.isLoggable(Level.FINEST)) {
            return;
        }
        this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), message);
    }
    
    public void finest(final String message, final Object[] params) {
        if (!this.logger.isLoggable(Level.FINEST)) {
            return;
        }
        this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), message, params);
    }
    
    public void finest(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.FINEST)) {
            return;
        }
        this.logger.logp(Level.FINEST, this.componentClassName, getCallerMethodName(), message, thrown);
    }
    
    public void finer(final String message) {
        if (!this.logger.isLoggable(Level.FINER)) {
            return;
        }
        this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), message);
    }
    
    public void finer(final String message, final Object[] params) {
        if (!this.logger.isLoggable(Level.FINER)) {
            return;
        }
        this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), message, params);
    }
    
    public void finer(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.FINER)) {
            return;
        }
        this.logger.logp(Level.FINER, this.componentClassName, getCallerMethodName(), message, thrown);
    }
    
    public void fine(final String message) {
        if (!this.logger.isLoggable(Level.FINE)) {
            return;
        }
        this.logger.logp(Level.FINE, this.componentClassName, getCallerMethodName(), message);
    }
    
    public void fine(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.FINE)) {
            return;
        }
        this.logger.logp(Level.FINE, this.componentClassName, getCallerMethodName(), message, thrown);
    }
    
    public void info(final String message) {
        if (!this.logger.isLoggable(Level.INFO)) {
            return;
        }
        this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), message);
    }
    
    public void info(final String message, final Object[] params) {
        if (!this.logger.isLoggable(Level.INFO)) {
            return;
        }
        this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), message, params);
    }
    
    public void info(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.INFO)) {
            return;
        }
        this.logger.logp(Level.INFO, this.componentClassName, getCallerMethodName(), message, thrown);
    }
    
    public void config(final String message) {
        if (!this.logger.isLoggable(Level.CONFIG)) {
            return;
        }
        this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), message);
    }
    
    public void config(final String message, final Object[] params) {
        if (!this.logger.isLoggable(Level.CONFIG)) {
            return;
        }
        this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), message, params);
    }
    
    public void config(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.CONFIG)) {
            return;
        }
        this.logger.logp(Level.CONFIG, this.componentClassName, getCallerMethodName(), message, thrown);
    }
    
    public void warning(final String message) {
        if (!this.logger.isLoggable(Level.WARNING)) {
            return;
        }
        this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), message);
    }
    
    public void warning(final String message, final Object[] params) {
        if (!this.logger.isLoggable(Level.WARNING)) {
            return;
        }
        this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), message, params);
    }
    
    public void warning(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.WARNING)) {
            return;
        }
        this.logger.logp(Level.WARNING, this.componentClassName, getCallerMethodName(), message, thrown);
    }
    
    public void severe(final String message) {
        if (!this.logger.isLoggable(Level.SEVERE)) {
            return;
        }
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), message);
    }
    
    public void severe(final String message, final Object[] params) {
        if (!this.logger.isLoggable(Level.SEVERE)) {
            return;
        }
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), message, params);
    }
    
    public void severe(final String message, final Throwable thrown) {
        if (!this.logger.isLoggable(Level.SEVERE)) {
            return;
        }
        this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), message, thrown);
    }
    
    public boolean isMethodCallLoggable() {
        return this.logger.isLoggable(Logger.METHOD_CALL_LEVEL_VALUE);
    }
    
    public boolean isLoggable(final Level level) {
        return this.logger.isLoggable(level);
    }
    
    public void setLevel(final Level level) {
        this.logger.setLevel(level);
    }
    
    public void entering() {
        if (!this.logger.isLoggable(Logger.METHOD_CALL_LEVEL_VALUE)) {
            return;
        }
        this.logger.entering(this.componentClassName, getCallerMethodName());
    }
    
    public void entering(final Object... parameters) {
        if (!this.logger.isLoggable(Logger.METHOD_CALL_LEVEL_VALUE)) {
            return;
        }
        this.logger.entering(this.componentClassName, getCallerMethodName(), parameters);
    }
    
    public void exiting() {
        if (!this.logger.isLoggable(Logger.METHOD_CALL_LEVEL_VALUE)) {
            return;
        }
        this.logger.exiting(this.componentClassName, getCallerMethodName());
    }
    
    public void exiting(final Object result) {
        if (!this.logger.isLoggable(Logger.METHOD_CALL_LEVEL_VALUE)) {
            return;
        }
        this.logger.exiting(this.componentClassName, getCallerMethodName(), result);
    }
    
    public <T extends Throwable> T logSevereException(final T exception, final Throwable cause) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            if (cause == null) {
                this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage());
            }
            else {
                exception.initCause(cause);
                this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage(), cause);
            }
        }
        return exception;
    }
    
    public <T extends Throwable> T logSevereException(final T exception, final boolean logCause) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            if (logCause && exception.getCause() != null) {
                this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
            }
            else {
                this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage());
            }
        }
        return exception;
    }
    
    public <T extends Throwable> T logSevereException(final T exception) {
        if (this.logger.isLoggable(Level.SEVERE)) {
            if (exception.getCause() == null) {
                this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage());
            }
            else {
                this.logger.logp(Level.SEVERE, this.componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
            }
        }
        return exception;
    }
    
    public <T extends Throwable> T logException(final T exception, final Throwable cause, final Level level) {
        if (this.logger.isLoggable(level)) {
            if (cause == null) {
                this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage());
            }
            else {
                exception.initCause(cause);
                this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage(), cause);
            }
        }
        return exception;
    }
    
    public <T extends Throwable> T logException(final T exception, final boolean logCause, final Level level) {
        if (this.logger.isLoggable(level)) {
            if (logCause && exception.getCause() != null) {
                this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
            }
            else {
                this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage());
            }
        }
        return exception;
    }
    
    public <T extends Throwable> T logException(final T exception, final Level level) {
        if (this.logger.isLoggable(level)) {
            if (exception.getCause() == null) {
                this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage());
            }
            else {
                this.logger.logp(level, this.componentClassName, getCallerMethodName(), exception.getMessage(), exception.getCause());
            }
        }
        return exception;
    }
    
    private static String getCallerMethodName() {
        return getStackMethodName(5);
    }
    
    private static String getStackMethodName(final int methodIndexInStack) {
        final StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String methodName;
        if (stack.length > methodIndexInStack + 1) {
            methodName = stack[methodIndexInStack].getMethodName();
        }
        else {
            methodName = "UNKNOWN METHOD";
        }
        return methodName;
    }
    
    static {
        METHOD_CALL_LEVEL_VALUE = Level.FINEST;
    }
}
