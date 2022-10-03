package sun.rmi.runtime;

import java.rmi.server.LogStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.logging.Level;

public abstract class Log
{
    public static final Level BRIEF;
    public static final Level VERBOSE;
    private static final LogFactory logFactory;
    
    public abstract boolean isLoggable(final Level p0);
    
    public abstract void log(final Level p0, final String p1);
    
    public abstract void log(final Level p0, final String p1, final Throwable p2);
    
    public abstract void setOutputStream(final OutputStream p0);
    
    public abstract PrintStream getPrintStream();
    
    public static Log getLog(final String s, final String s2, final int n) {
        Level level;
        if (n < 0) {
            level = null;
        }
        else if (n == 0) {
            level = Level.OFF;
        }
        else if (n > 0 && n <= 10) {
            level = Log.BRIEF;
        }
        else if (n > 10 && n <= 20) {
            level = Log.VERBOSE;
        }
        else {
            level = Level.FINEST;
        }
        return Log.logFactory.createLog(s, s2, level);
    }
    
    public static Log getLog(final String s, final String s2, final boolean b) {
        return Log.logFactory.createLog(s, s2, b ? Log.VERBOSE : null);
    }
    
    private static String[] getSource() {
        final StackTraceElement[] stackTrace = new Exception().getStackTrace();
        return new String[] { stackTrace[3].getClassName(), stackTrace[3].getMethodName() };
    }
    
    static {
        BRIEF = Level.FINE;
        VERBOSE = Level.FINER;
        logFactory = (((boolean)Boolean.valueOf(AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.log.useOld")))) ? new LogStreamLogFactory() : new LoggerLogFactory());
    }
    
    private static class LoggerLogFactory implements LogFactory
    {
        LoggerLogFactory() {
        }
        
        @Override
        public Log createLog(final String s, final String s2, final Level level) {
            return new LoggerLog(Logger.getLogger(s), level);
        }
    }
    
    private static class LoggerLog extends Log
    {
        private static final Handler alternateConsole;
        private InternalStreamHandler copyHandler;
        private final Logger logger;
        private LoggerPrintStream loggerSandwich;
        
        private LoggerLog(final Logger logger, final Level level) {
            this.copyHandler = null;
            this.logger = logger;
            if (level != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        if (!logger.isLoggable(level)) {
                            logger.setLevel(level);
                        }
                        logger.addHandler(LoggerLog.alternateConsole);
                        return null;
                    }
                });
            }
        }
        
        @Override
        public boolean isLoggable(final Level level) {
            return this.logger.isLoggable(level);
        }
        
        @Override
        public void log(final Level level, final String s) {
            if (this.isLoggable(level)) {
                final String[] access$200 = getSource();
                this.logger.logp(level, access$200[0], access$200[1], Thread.currentThread().getName() + ": " + s);
            }
        }
        
        @Override
        public void log(final Level level, final String s, final Throwable t) {
            if (this.isLoggable(level)) {
                final String[] access$200 = getSource();
                this.logger.logp(level, access$200[0], access$200[1], Thread.currentThread().getName() + ": " + s, t);
            }
        }
        
        @Override
        public synchronized void setOutputStream(final OutputStream outputStream) {
            if (outputStream != null) {
                if (!this.logger.isLoggable(LoggerLog.VERBOSE)) {
                    this.logger.setLevel(LoggerLog.VERBOSE);
                }
                (this.copyHandler = new InternalStreamHandler(outputStream)).setLevel(Log.VERBOSE);
                this.logger.addHandler(this.copyHandler);
            }
            else {
                if (this.copyHandler != null) {
                    this.logger.removeHandler(this.copyHandler);
                }
                this.copyHandler = null;
            }
        }
        
        @Override
        public synchronized PrintStream getPrintStream() {
            if (this.loggerSandwich == null) {
                this.loggerSandwich = new LoggerPrintStream(this.logger);
            }
            return this.loggerSandwich;
        }
        
        static {
            alternateConsole = AccessController.doPrivileged((PrivilegedAction<Handler>)new PrivilegedAction<Handler>() {
                @Override
                public Handler run() {
                    final InternalStreamHandler internalStreamHandler = new InternalStreamHandler(System.err);
                    internalStreamHandler.setLevel(Level.ALL);
                    return internalStreamHandler;
                }
            });
        }
    }
    
    private static class InternalStreamHandler extends StreamHandler
    {
        InternalStreamHandler(final OutputStream outputStream) {
            super(outputStream, new SimpleFormatter());
        }
        
        @Override
        public void publish(final LogRecord logRecord) {
            super.publish(logRecord);
            this.flush();
        }
        
        @Override
        public void close() {
            this.flush();
        }
    }
    
    private static class LoggerPrintStream extends PrintStream
    {
        private final Logger logger;
        private int last;
        private final ByteArrayOutputStream bufOut;
        
        private LoggerPrintStream(final Logger logger) {
            super(new ByteArrayOutputStream());
            this.last = -1;
            this.bufOut = (ByteArrayOutputStream)super.out;
            this.logger = logger;
        }
        
        @Override
        public void write(final int last) {
            if (this.last == 13 && last == 10) {
                this.last = -1;
                return;
            }
            Label_0111: {
                if (last != 10) {
                    if (last != 13) {
                        super.write(last);
                        break Label_0111;
                    }
                }
                try {
                    this.logger.logp(Level.INFO, "LogStream", "print", Thread.currentThread().getName() + ": " + this.bufOut.toString());
                }
                finally {
                    this.bufOut.reset();
                }
            }
            this.last = last;
        }
        
        @Override
        public void write(final byte[] array, final int n, final int n2) {
            if (n2 < 0) {
                throw new ArrayIndexOutOfBoundsException(n2);
            }
            for (int i = 0; i < n2; ++i) {
                this.write(array[n + i]);
            }
        }
        
        @Override
        public String toString() {
            return "RMI";
        }
    }
    
    private static class LogStreamLogFactory implements LogFactory
    {
        LogStreamLogFactory() {
        }
        
        @Override
        public Log createLog(final String s, final String s2, final Level level) {
            LogStream log = null;
            if (s2 != null) {
                log = LogStream.log(s2);
            }
            return new LogStreamLog(log, level);
        }
    }
    
    private static class LogStreamLog extends Log
    {
        private final LogStream stream;
        private int levelValue;
        
        private LogStreamLog(final LogStream stream, final Level level) {
            this.levelValue = Level.OFF.intValue();
            if (stream != null && level != null) {
                this.levelValue = level.intValue();
            }
            this.stream = stream;
        }
        
        @Override
        public synchronized boolean isLoggable(final Level level) {
            return level.intValue() >= this.levelValue;
        }
        
        @Override
        public void log(final Level level, final String s) {
            if (this.isLoggable(level)) {
                final String[] access$200 = getSource();
                this.stream.println(unqualifiedName(access$200[0]) + "." + access$200[1] + ": " + s);
            }
        }
        
        @Override
        public void log(final Level level, final String s, final Throwable t) {
            if (this.isLoggable(level)) {
                synchronized (this.stream) {
                    final String[] access$200 = getSource();
                    this.stream.println(unqualifiedName(access$200[0]) + "." + access$200[1] + ": " + s);
                    t.printStackTrace(this.stream);
                }
            }
        }
        
        @Override
        public PrintStream getPrintStream() {
            return this.stream;
        }
        
        @Override
        public synchronized void setOutputStream(final OutputStream outputStream) {
            if (outputStream != null) {
                if (LogStreamLog.VERBOSE.intValue() < this.levelValue) {
                    this.levelValue = LogStreamLog.VERBOSE.intValue();
                }
                this.stream.setOutputStream(outputStream);
            }
            else {
                this.levelValue = Level.OFF.intValue();
            }
        }
        
        private static String unqualifiedName(String s) {
            final int lastIndex = s.lastIndexOf(".");
            if (lastIndex >= 0) {
                s = s.substring(lastIndex + 1);
            }
            s = s.replace('$', '.');
            return s;
        }
    }
    
    private interface LogFactory
    {
        Log createLog(final String p0, final String p1, final Level p2);
    }
}
