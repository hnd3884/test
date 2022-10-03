package com.zoho.security.api.wrapper;

import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import com.zoho.security.eventfw.ExecutionTimer;
import com.zoho.security.eventfw.pojos.log.ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED;
import com.adventnet.iam.security.SecurityRequestWrapper;
import com.adventnet.iam.security.SecurityUtil;
import java.util.regex.Matcher;
import com.adventnet.iam.security.SecurityFilterProperties;
import java.util.regex.Pattern;
import java.util.logging.Logger;

public final class PatternMatcherWrapper implements PatternMatcher
{
    private static final Logger LOGGER;
    private Pattern pattern;
    private int timeOutInMillis;
    private int maxIterationCount;
    private TimeLimitedRegexCharSequence wrappedCharSeq;
    private PatternMatcherMode mode;
    private boolean isSensitiveValue;
    
    public PatternMatcherWrapper(final String regex) {
        this(Pattern.compile(regex));
    }
    
    public PatternMatcherWrapper(final Pattern regexPattern) {
        this(regexPattern, -1, -1);
    }
    
    public PatternMatcherWrapper(final String regex, final int timeoutInMillis, final int iterationCount) {
        this(Pattern.compile(regex), timeoutInMillis, iterationCount);
    }
    
    public PatternMatcherWrapper(final Pattern regexPattern, final int timeoutInMillis, final int iterationCount) {
        this.pattern = regexPattern;
        this.timeOutInMillis = ((timeoutInMillis > 0) ? timeoutInMillis : SecurityFilterProperties.getPatternMatcherTimeoutInMillis());
        this.maxIterationCount = ((iterationCount > 0) ? iterationCount : SecurityFilterProperties.getPatternMatcherMaxIterationCount());
        this.mode = SecurityFilterProperties.getPatternMatcherMode();
    }
    
    @Override
    public boolean matches(final CharSequence input) {
        final boolean result = this.getMatcher(input).matches();
        this.debugLog();
        return result;
    }
    
    @Override
    public boolean find(final CharSequence input) {
        final boolean result = this.getMatcher(input).find();
        this.debugLog();
        return result;
    }
    
    @Override
    public boolean find(final CharSequence input, final int start) {
        final boolean result = this.getMatcher(input).find(start);
        this.debugLog();
        return result;
    }
    
    @Override
    public String replaceFirst(final CharSequence input, final String replacement) {
        final String value = this.getMatcher(input).replaceFirst(replacement);
        this.debugLog();
        return value;
    }
    
    @Override
    public String replaceAll(final CharSequence input, final String replacement) {
        final String value = this.getMatcher(input).replaceAll(replacement);
        this.debugLog();
        return value;
    }
    
    @Override
    public String[] split(final String input) {
        return this.pattern.split(input, 0);
    }
    
    @Override
    public String[] split(final String input, final int limit) {
        this.initCharSequenceWrapper(input);
        final String[] values = this.pattern.split(this.wrappedCharSeq, limit);
        this.debugLog();
        return values;
    }
    
    public Matcher getMatcher(final CharSequence input) {
        this.initCharSequenceWrapper(input);
        return this.pattern.matcher(this.wrappedCharSeq);
    }
    
    private void initCharSequenceWrapper(final CharSequence input) {
        this.wrappedCharSeq = new TimeLimitedRegexCharSequence(input);
    }
    
    @Override
    public String getPattern() {
        return this.pattern.pattern();
    }
    
    public int getMaxIterationCount() {
        return this.maxIterationCount;
    }
    
    public int getTimeOut() {
        return this.timeOutInMillis;
    }
    
    public void setMode(final PatternMatcherMode mode) {
        this.mode = mode;
    }
    
    public void sensitiveValue(final boolean isSensitive) {
        this.isSensitiveValue = isSensitive;
    }
    
    private void debugLog() {
        if (this.mode == PatternMatcherMode.LEARNING) {
            this.wrappedCharSeq.debugLog();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(PatternMatcherWrapper.class.getName());
    }
    
    class TimeLimitedRegexCharSequence implements CharSequence
    {
        private CharSequence charSequence;
        private long timeOutTime;
        private long iterationCounter;
        private int tempCounter;
        private int checkTimeForEach;
        private boolean limitReached;
        private long startTime;
        
        public TimeLimitedRegexCharSequence(final CharSequence input) {
            this.iterationCounter = 0L;
            this.tempCounter = 0;
            this.checkTimeForEach = 1000;
            this.limitReached = false;
            this.charSequence = input;
            this.startTime = System.currentTimeMillis();
            this.timeOutTime = this.startTime + PatternMatcherWrapper.this.timeOutInMillis;
        }
        
        @Override
        public int length() {
            return this.charSequence.length();
        }
        
        @Override
        public char charAt(final int index) {
            if (PatternMatcherWrapper.this.mode == PatternMatcherMode.DISABLE) {
                return this.charSequence.charAt(index);
            }
            ++this.iterationCounter;
            if ((PatternMatcherWrapper.this.mode == PatternMatcherMode.LOGGING || PatternMatcherWrapper.this.mode == PatternMatcherMode.ERROR) && !this.limitReached) {
                ++this.tempCounter;
                if (this.tempCounter >= this.checkTimeForEach) {
                    this.tempCounter = 0;
                    if (System.currentTimeMillis() > this.timeOutTime) {
                        this.limitReached = true;
                        if (SecurityUtil.getCurrentRequest() != null && SecurityUtil.getCurrentRequest() instanceof SecurityRequestWrapper) {
                            final SecurityRequestWrapper srw = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
                            ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.pushTimeExceeded(srw.getRequestURI(), srw.getURLActionRulePrefix(), srw.getURLActionRulePath(), srw.getURLActionRuleMethod(), srw.getURLActionRuleOperation(), PatternMatcherWrapper.this.getPattern(), PatternMatcherWrapper.this.isSensitiveValue ? "*****" : this.charSequence, (long)PatternMatcherWrapper.this.timeOutInMillis, (ExecutionTimer)null);
                        }
                        else {
                            ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.pushTimeExceeded((String)null, (String)null, (String)null, (String)null, (String)null, PatternMatcherWrapper.this.getPattern(), PatternMatcherWrapper.this.isSensitiveValue ? "*****" : this.charSequence, (long)PatternMatcherWrapper.this.timeOutInMillis, (ExecutionTimer)null);
                        }
                        if (PatternMatcherWrapper.this.mode == PatternMatcherMode.ERROR) {
                            PatternMatcherWrapper.LOGGER.log(Level.SEVERE, "REGEX PATTTERN MATCHER - TIMED OUT :: Pattern : \"{0}\", Value : \"{1}\", TimeoutTime in Millis : \"{2}\", No. of iterations taken : \"{3}\"", new Object[] { PatternMatcherWrapper.this.getPattern(), PatternMatcherWrapper.this.isSensitiveValue ? "*****" : this.charSequence, PatternMatcherWrapper.this.timeOutInMillis, this.iterationCounter });
                            throw new IAMSecurityException("PATTERN_MATCHER_TIMEDOUT");
                        }
                    }
                }
                if (this.iterationCounter > PatternMatcherWrapper.this.maxIterationCount) {
                    this.limitReached = true;
                    if (SecurityUtil.getCurrentRequest() != null && SecurityUtil.getCurrentRequest() instanceof SecurityRequestWrapper) {
                        final SecurityRequestWrapper srw = (SecurityRequestWrapper)SecurityUtil.getCurrentRequest();
                        ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.pushIterExceeded(srw.getRequestURI(), srw.getURLActionRulePrefix(), srw.getURLActionRulePath(), srw.getURLActionRuleMethod(), srw.getURLActionRuleOperation(), PatternMatcherWrapper.this.getPattern(), PatternMatcherWrapper.this.isSensitiveValue ? "*****" : this.charSequence, this.iterationCounter, (ExecutionTimer)null);
                    }
                    else {
                        ZSEC_REGEX_MATCH_THRESHOLD_EXCEEDED.pushIterExceeded((String)null, (String)null, (String)null, (String)null, (String)null, PatternMatcherWrapper.this.getPattern(), PatternMatcherWrapper.this.isSensitiveValue ? "*****" : this.charSequence, this.iterationCounter, (ExecutionTimer)null);
                    }
                    if (PatternMatcherWrapper.this.mode == PatternMatcherMode.ERROR) {
                        PatternMatcherWrapper.LOGGER.log(Level.SEVERE, "REGEX PATTTERN MATCHER - ITERATION LIMIT EXCEEDED :: Pattern : \"{0}\", Value : \"{1}\", MaxAllowedIterations : \"{2}\"", new Object[] { PatternMatcherWrapper.this.getPattern(), PatternMatcherWrapper.this.isSensitiveValue ? "*****" : this.charSequence, PatternMatcherWrapper.this.maxIterationCount });
                        throw new IAMSecurityException("PATTERN_MATCHER_ITERATION_LIMIT_EXCEEDED");
                    }
                }
            }
            return this.charSequence.charAt(index);
        }
        
        @Override
        public CharSequence subSequence(final int start, final int end) {
            return new TimeLimitedRegexCharSequence(this.charSequence.subSequence(start, end));
        }
        
        @Override
        public String toString() {
            return this.charSequence.toString();
        }
        
        void debugLog() {
            final long timeTaken = System.currentTimeMillis() - this.startTime;
            PatternMatcherWrapper.LOGGER.log(Level.WARNING, "REGEX PATTTERN MATCHER STATS :: Time taken to match the string : \"{0}\" with this pattern : \"{1}\" is : \"{2}\" ms, No. of iterations taken : \"{3}\"", new Object[] { PatternMatcherWrapper.this.isSensitiveValue ? "*****" : this.charSequence, PatternMatcherWrapper.this.getPattern(), timeTaken, this.iterationCounter });
        }
    }
    
    public enum PatternMatcherMode
    {
        LOGGING("logging"), 
        ERROR("error"), 
        LEARNING("learning"), 
        DISABLE("disable");
        
        private String mode;
        
        private PatternMatcherMode(final String mode) {
            this.mode = null;
            this.mode = mode;
        }
        
        public String getMode() {
            return this.mode;
        }
    }
}
