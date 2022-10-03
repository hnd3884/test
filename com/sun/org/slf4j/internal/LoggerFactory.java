package com.sun.org.slf4j.internal;

public class LoggerFactory
{
    public static Logger getLogger(final Class<?> clazz) {
        return new Logger(clazz.getName());
    }
}
