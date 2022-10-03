package org.glassfish.jersey.internal;

import java.util.Collections;
import java.util.ArrayDeque;
import java.util.concurrent.Callable;
import org.glassfish.jersey.internal.util.Producer;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import org.glassfish.jersey.Severity;
import java.util.Deque;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Errors
{
    private static final Logger LOGGER;
    private static final ThreadLocal<Errors> errors;
    private final ArrayList<ErrorMessage> issues;
    private Deque<Integer> mark;
    private int stack;
    
    public static void error(final String message, final Severity severity) {
        error(null, message, severity);
    }
    
    public static void error(final Object source, final String message, final Severity severity) {
        getInstance().issues.add(new ErrorMessage(source, message, severity));
    }
    
    public static void fatal(final Object source, final String message) {
        error(source, message, Severity.FATAL);
    }
    
    public static void warning(final Object source, final String message) {
        error(source, message, Severity.WARNING);
    }
    
    public static void hint(final Object source, final String message) {
        getInstance().issues.add(new ErrorMessage(source, message, Severity.HINT));
    }
    
    private static void processErrors(final boolean throwException) {
        final List<ErrorMessage> errors = new ArrayList<ErrorMessage>(Errors.errors.get().issues);
        final boolean isFatal = logErrors(errors);
        if (throwException && isFatal) {
            throw new ErrorMessagesException((List)errors);
        }
    }
    
    public static boolean logErrors(final boolean afterMark) {
        return logErrors(getInstance()._getErrorMessages(afterMark));
    }
    
    private static boolean logErrors(final Collection<ErrorMessage> errors) {
        boolean isFatal = false;
        if (!errors.isEmpty()) {
            final StringBuilder fatals = new StringBuilder("\n");
            final StringBuilder warnings = new StringBuilder();
            final StringBuilder hints = new StringBuilder();
            for (final ErrorMessage error : errors) {
                switch (error.getSeverity()) {
                    case FATAL: {
                        isFatal = true;
                        fatals.append(LocalizationMessages.ERROR_MSG(error.getMessage())).append('\n');
                        continue;
                    }
                    case WARNING: {
                        warnings.append(LocalizationMessages.WARNING_MSG(error.getMessage())).append('\n');
                        continue;
                    }
                    case HINT: {
                        hints.append(LocalizationMessages.HINT_MSG(error.getMessage())).append('\n');
                        continue;
                    }
                }
            }
            if (isFatal) {
                Errors.LOGGER.severe(LocalizationMessages.ERRORS_AND_WARNINGS_DETECTED(fatals.append((CharSequence)warnings).append((CharSequence)hints).toString()));
            }
            else {
                if (warnings.length() > 0) {
                    Errors.LOGGER.warning(LocalizationMessages.WARNINGS_DETECTED(warnings.toString()));
                }
                if (hints.length() > 0) {
                    Errors.LOGGER.config(LocalizationMessages.HINTS_DETECTED(hints.toString()));
                }
            }
        }
        return isFatal;
    }
    
    public static boolean fatalIssuesFound() {
        for (final ErrorMessage message : getInstance().issues) {
            if (message.getSeverity() == Severity.FATAL) {
                return true;
            }
        }
        return false;
    }
    
    public static <T> T process(final Producer<T> producer) {
        return process(producer, false);
    }
    
    public static <T> T process(final Callable<T> task) throws Exception {
        return process(task, true);
    }
    
    public static <T> T processWithException(final Producer<T> producer) {
        return process(producer, true);
    }
    
    public static void process(final Runnable task) {
        process((Producer<Object>)new Producer<Void>() {
            @Override
            public Void call() {
                task.run();
                return null;
            }
        }, false);
    }
    
    public static void processWithException(final Runnable task) {
        process((Producer<Object>)new Producer<Void>() {
            @Override
            public Void call() {
                task.run();
                return null;
            }
        }, true);
    }
    
    private static <T> T process(final Producer<T> task, final boolean throwException) {
        try {
            return process((Callable<T>)task, throwException);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final Exception ex2) {
            throw new RuntimeException(ex2);
        }
    }
    
    private static <T> T process(final Callable<T> task, final boolean throwException) throws Exception {
        Errors instance = Errors.errors.get();
        if (instance == null) {
            instance = new Errors();
            Errors.errors.set(instance);
        }
        instance.preProcess();
        Exception caught = null;
        try {
            return task.call();
        }
        catch (final Exception re) {
            caught = re;
        }
        finally {
            instance.postProcess(throwException && caught == null);
        }
        throw caught;
    }
    
    private static Errors getInstance() {
        final Errors instance = Errors.errors.get();
        if (instance == null) {
            throw new IllegalStateException(LocalizationMessages.NO_ERROR_PROCESSING_IN_SCOPE());
        }
        if (instance.stack == 0) {
            Errors.errors.remove();
            throw new IllegalStateException(LocalizationMessages.NO_ERROR_PROCESSING_IN_SCOPE());
        }
        return instance;
    }
    
    public static List<ErrorMessage> getErrorMessages() {
        return getErrorMessages(false);
    }
    
    public static List<ErrorMessage> getErrorMessages(final boolean afterMark) {
        return getInstance()._getErrorMessages(afterMark);
    }
    
    public static void mark() {
        getInstance()._mark();
    }
    
    public static void unmark() {
        getInstance()._unmark();
    }
    
    public static void reset() {
        getInstance()._reset();
    }
    
    private Errors() {
        this.issues = new ArrayList<ErrorMessage>(0);
        this.mark = new ArrayDeque<Integer>(4);
        this.stack = 0;
    }
    
    private void _mark() {
        this.mark.addLast(this.issues.size());
    }
    
    private void _unmark() {
        this.mark.pollLast();
    }
    
    private void _reset() {
        final Integer _pos = this.mark.pollLast();
        final int markedPos = (_pos == null) ? -1 : _pos;
        if (markedPos >= 0 && markedPos < this.issues.size()) {
            this.issues.subList(markedPos, this.issues.size()).clear();
        }
    }
    
    private void preProcess() {
        ++this.stack;
    }
    
    private void postProcess(final boolean throwException) {
        --this.stack;
        if (this.stack == 0) {
            try {
                if (!this.issues.isEmpty()) {
                    processErrors(throwException);
                }
            }
            finally {
                Errors.errors.remove();
            }
        }
    }
    
    private List<ErrorMessage> _getErrorMessages(final boolean afterMark) {
        if (afterMark) {
            final Integer _pos = this.mark.peekLast();
            final int markedPos = (_pos == null) ? -1 : _pos;
            if (markedPos >= 0 && markedPos < this.issues.size()) {
                return Collections.unmodifiableList((List<? extends ErrorMessage>)new ArrayList<ErrorMessage>(this.issues.subList(markedPos, this.issues.size())));
            }
        }
        return Collections.unmodifiableList((List<? extends ErrorMessage>)new ArrayList<ErrorMessage>(this.issues));
    }
    
    static {
        LOGGER = Logger.getLogger(Errors.class.getName());
        errors = new ThreadLocal<Errors>();
    }
    
    public static class ErrorMessagesException extends RuntimeException
    {
        private final List<ErrorMessage> messages;
        
        private ErrorMessagesException(final List<ErrorMessage> messages) {
            this.messages = messages;
        }
        
        public List<ErrorMessage> getMessages() {
            return this.messages;
        }
    }
    
    public static class ErrorMessage
    {
        private final Object source;
        private final String message;
        private final Severity severity;
        
        private ErrorMessage(final Object source, final String message, final Severity severity) {
            this.source = source;
            this.message = message;
            this.severity = severity;
        }
        
        public Severity getSeverity() {
            return this.severity;
        }
        
        public String getMessage() {
            return this.message;
        }
        
        public Object getSource() {
            return this.source;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ErrorMessage that = (ErrorMessage)o;
            Label_0062: {
                if (this.message != null) {
                    if (this.message.equals(that.message)) {
                        break Label_0062;
                    }
                }
                else if (that.message == null) {
                    break Label_0062;
                }
                return false;
            }
            if (this.severity != that.severity) {
                return false;
            }
            if (this.source != null) {
                if (this.source.equals(that.source)) {
                    return true;
                }
            }
            else if (that.source == null) {
                return true;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = (this.source != null) ? this.source.hashCode() : 0;
            result = 31 * result + ((this.message != null) ? this.message.hashCode() : 0);
            result = 31 * result + ((this.severity != null) ? this.severity.hashCode() : 0);
            return result;
        }
    }
}
