package com.sun.xml.internal.ws.config.metro.util;

import javax.xml.ws.WebServiceException;
import com.sun.istack.internal.logging.Logger;

public class ParserUtil
{
    private static final Logger LOGGER;
    
    private ParserUtil() {
    }
    
    public static boolean parseBooleanValue(final String value) throws WebServiceException {
        if ("true".equals(value) || "1".equals(value)) {
            return true;
        }
        if ("false".equals(value) || "0".equals(value)) {
            return false;
        }
        throw ParserUtil.LOGGER.logSevereException(new WebServiceException("invalid boolean value"));
    }
    
    static {
        LOGGER = Logger.getLogger(ParserUtil.class);
    }
}
