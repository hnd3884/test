package com.adventnet.iam.xss;

import com.adventnet.iam.security.IAMSecurityException;
import java.util.ArrayList;
import com.adventnet.iam.security.SecurityUtil;
import java.util.logging.Level;
import org.w3c.tidy.TidyMessage;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;
import org.w3c.tidy.TidyMessageListener;
import java.io.PrintWriter;

public class JTidyErrorWriter extends PrintWriter implements TidyMessageListener
{
    public static final Logger LOGGER;
    JTidyXSSFilter jtidyXssFilter;
    List<Integer> criticalParserErrorCodes;
    
    public JTidyErrorWriter(final OutputStream os, final boolean autoflush, final JTidyXSSFilter jtidyXssFilter, final String criticalParserErrorCodesStr) {
        super(os, autoflush);
        this.jtidyXssFilter = null;
        this.criticalParserErrorCodes = null;
        this.jtidyXssFilter = jtidyXssFilter;
        this.criticalParserErrorCodes = parseStringAsIntegerList(criticalParserErrorCodesStr);
    }
    
    @Override
    public void println(final String s) {
    }
    
    public void messageReceived(final TidyMessage message) {
        if (this.criticalParserErrorCodes.contains(message.getErrorCode())) {
            JTidyErrorWriter.LOGGER.log(Level.WARNING, "CRITICAL PARSER ERROR_CODE  - {0} : MESSAGE - {1} ", new Object[] { message.getErrorCode(), message.getMessage() });
            this.jtidyXssFilter.SET_ALTERED();
        }
    }
    
    public static List<Integer> parseStringAsIntegerList(final String listStr) throws IAMSecurityException {
        if (SecurityUtil.isValid(listStr)) {
            final String[] tmpVals = listStr.split(",");
            if (tmpVals != null && tmpVals.length > 0) {
                final List<Integer> intList = new ArrayList<Integer>();
                for (final String val : tmpVals) {
                    try {
                        final Integer intVal = Integer.parseInt(val);
                        intList.add(intVal);
                    }
                    catch (final NumberFormatException ne) {
                        JTidyErrorWriter.LOGGER.log(Level.WARNING, ne.getMessage());
                        throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
                    }
                }
                if (intList.size() > 0) {
                    return intList;
                }
            }
        }
        throw new IAMSecurityException("INVALID_XSSFILTER_CONFIGURATION");
    }
    
    static {
        LOGGER = Logger.getLogger(JTidyErrorWriter.class.getName());
    }
}
