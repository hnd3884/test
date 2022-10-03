package org.owasp.esapi.reference;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

public class Log4JLoggerFactory implements LoggerFactory
{
    public Logger makeNewLoggerInstance(final String name) {
        return new Log4JLogger(name);
    }
}
