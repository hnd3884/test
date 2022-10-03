package org.apache.tika.config;

import org.slf4j.LoggerFactory;
import org.apache.tika.exception.TikaConfigException;

public interface InitializableProblemHandler
{
    public static final InitializableProblemHandler IGNORE = new InitializableProblemHandler() {
        @Override
        public void handleInitializableProblem(final String className, final String message) {
        }
        
        @Override
        public String toString() {
            return "IGNORE";
        }
    };
    public static final InitializableProblemHandler INFO = new InitializableProblemHandler() {
        @Override
        public void handleInitializableProblem(final String classname, final String message) {
            LoggerFactory.getLogger(classname).info(message);
        }
        
        @Override
        public String toString() {
            return "INFO";
        }
    };
    public static final InitializableProblemHandler WARN = new InitializableProblemHandler() {
        @Override
        public void handleInitializableProblem(final String classname, final String message) {
            LoggerFactory.getLogger(classname).warn(message);
        }
        
        @Override
        public String toString() {
            return "WARN";
        }
    };
    public static final InitializableProblemHandler THROW = new InitializableProblemHandler() {
        @Override
        public void handleInitializableProblem(final String classname, final String message) throws TikaConfigException {
            throw new TikaConfigException(message);
        }
        
        @Override
        public String toString() {
            return "THROW";
        }
    };
    public static final InitializableProblemHandler DEFAULT = InitializableProblemHandler.WARN;
    
    void handleInitializableProblem(final String p0, final String p1) throws TikaConfigException;
}
