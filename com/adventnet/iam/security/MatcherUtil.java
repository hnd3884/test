package com.adventnet.iam.security;

import com.adventnet.iam.xss.XSSUtil;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.logging.Level;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class MatcherUtil
{
    public static final Logger LOGGER;
    private ExecutorService executor;
    public static final int MATCHES = 1;
    public static final int FIND = 2;
    private long timeOutInMillis;
    
    public MatcherUtil() {
        this.executor = null;
        this.timeOutInMillis = 30000L;
        this.executor = Executors.newFixedThreadPool(5);
    }
    
    public MatcherUtil(final int threadCount, final long timeOutInMillis) {
        this.executor = null;
        this.timeOutInMillis = 30000L;
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.timeOutInMillis = timeOutInMillis;
        MatcherUtil.LOGGER.log(Level.FINE, "Initialized MatcherUtil ThreadCount : {0} TimeoutInMillis: {1} ", new Object[] { threadCount, timeOutInMillis });
    }
    
    public boolean matches(final Pattern pattern, final String value) throws TimeoutException, Exception {
        return this.checkPattern(pattern, value, 1, this.timeOutInMillis);
    }
    
    public boolean find(final Pattern pattern, final String value) throws TimeoutException, Exception {
        return this.checkPattern(pattern, value, 2, this.timeOutInMillis);
    }
    
    public boolean checkPattern(final Pattern pattern, final String value, final int operation, final long timeInMillis) throws TimeoutException, Exception {
        if (pattern == null || value == null) {
            return false;
        }
        final StringBuffer stringBuffer = new StringBuffer(value);
        final FutureTask<Boolean> future = new FutureTask<Boolean>(new Helper(pattern, stringBuffer, operation));
        this.executor.execute(future);
        try {
            return future.get(timeInMillis, TimeUnit.MILLISECONDS);
        }
        catch (final TimeoutException ex) {
            stringBuffer.delete(0, stringBuffer.length());
            throw new Exception("PATTERN MATCHER INFINITE LOOPING TIMED OUT - PATTERN:" + pattern + " - VALUE : " + XSSUtil.getLogString(value));
        }
        catch (final Exception ex2) {
            stringBuffer.delete(0, stringBuffer.length());
            throw new Exception("PATTERN MATCHER INTERNAL EXCEPTION : " + ex2.getMessage() + " - PATTERN:" + pattern + " - VALUE : " + XSSUtil.getLogString(value));
        }
    }
    
    public void cleanup() {
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(MatcherUtil.class.getName());
    }
    
    private static class Helper implements Callable<Boolean>
    {
        StringBuffer target;
        Pattern pattern;
        int operation;
        
        Helper(final Pattern pattern, final StringBuffer t, final int operation) {
            this.target = null;
            this.pattern = null;
            this.operation = -1;
            this.target = t;
            this.pattern = pattern;
            this.operation = operation;
        }
        
        @Override
        public Boolean call() {
            if (this.operation == 1) {
                return this.pattern.matcher(this.target).matches();
            }
            if (this.operation == 2) {
                return this.pattern.matcher(this.target).find();
            }
            throw new RuntimeException("PATTERN MATCHER INTERNAL EXCEPTION : INVALID_OPEARATION");
        }
    }
}
