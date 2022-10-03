package com.adventnet.db.persistence.metadata.generator;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;

public class DDXMLGeneratorException extends Exception
{
    public DDXMLGeneratorException(final String message, final Throwable exc) {
        super(message, exc);
    }
    
    public DDXMLGeneratorException(final Throwable exc) {
        super(exc);
    }
    
    public static int getErrorCode(final Exception exc) {
        int errorCode = 0;
        if (exc instanceof FileNotFoundException) {
            errorCode = 1001;
        }
        else if (exc instanceof ParserConfigurationException) {
            errorCode = 1002;
        }
        else if (exc instanceof TransformerConfigurationException) {
            errorCode = 1003;
        }
        else if (exc instanceof TransformerException) {
            errorCode = 1004;
        }
        return errorCode;
    }
}
