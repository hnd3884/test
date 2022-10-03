package org.apache.tika.config;

import org.slf4j.LoggerFactory;

public interface LoadErrorHandler
{
    public static final LoadErrorHandler IGNORE = new LoadErrorHandler() {
        @Override
        public void handleLoadError(final String classname, final Throwable throwable) {
        }
        
        @Override
        public String toString() {
            return "IGNORE";
        }
    };
    public static final LoadErrorHandler WARN = new LoadErrorHandler() {
        @Override
        public void handleLoadError(final String classname, final Throwable throwable) {
            LoggerFactory.getLogger(classname).warn("Unable to load {}", (Object)classname, (Object)throwable);
        }
        
        @Override
        public String toString() {
            return "WARN";
        }
    };
    public static final LoadErrorHandler THROW = new LoadErrorHandler() {
        @Override
        public void handleLoadError(final String classname, final Throwable throwable) {
            throw new RuntimeException("Unable to load " + classname, throwable);
        }
        
        @Override
        public String toString() {
            return "THROW";
        }
    };
    
    void handleLoadError(final String p0, final Throwable p1);
}
